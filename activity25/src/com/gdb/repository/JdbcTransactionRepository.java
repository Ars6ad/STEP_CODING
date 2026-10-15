package com.gdb.repository;

import com.gdb.db.ConnectionProvider;
import com.gdb.domain.Transaction;
import com.gdb.domain.TransactionType;
import com.gdb.exceptions.DataAccessException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcTransactionRepository implements TransactionRepository {

    private final ConnectionProvider provider;

    public JdbcTransactionRepository(ConnectionProvider provider) {
        this.provider = provider;
    }

    @Override
    public void save(Transaction transaction) {
        if (transaction == null) return;
        String sql = "INSERT INTO transactions (transaction_id, timestamp, account_number, type, amount, balance_after, status, from_account, to_account, description) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, transaction.getTransactionId());
            ps.setString(2, transaction.getTimestamp() != null ? transaction.getTimestamp().toString() : LocalDateTime.now().toString());
            ps.setInt(3, transaction.getAccountNumber());
            ps.setString(4, transaction.getType() != null ? transaction.getType().name() : "TRANSFER");
            ps.setDouble(5, transaction.getAmount());
            ps.setDouble(6, transaction.getBalanceAfter());
            ps.setString(7, transaction.getStatus() != null ? transaction.getStatus() : "SUCCESS");
            ps.setInt(8, transaction.getFromAccount());
            ps.setInt(9, transaction.getToAccount());
            ps.setString(10, transaction.getDescription());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transaction> findByAccount(int accountNumber) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? OR from_account = ? OR to_account = ?";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountNumber);
            ps.setInt(2, accountNumber);
            ps.setInt(3, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find transactions by account: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find all transactions: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void clear() {
        String sql = "DELETE FROM transactions";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear transactions: " + e.getMessage(), e);
        }
    }

    @Override
    public void transfer(int fromAccountNumber, int toAccountNumber, double amount) {
        if (amount <= 0) {
            throw new DataAccessException("Transfer amount must be positive. Provided: " + amount);
        }

        Connection conn = null;
        try {
            conn = provider.getConnection();
            conn.setAutoCommit(false);

            // 1. Check fromAccount balance
            double fromBalance = 0;
            try (PreparedStatement ps = conn.prepareStatement("SELECT balance FROM accounts WHERE account_number = ?")) {
                ps.setInt(1, fromAccountNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException("Source account #" + fromAccountNumber + " does not exist");
                    }
                    fromBalance = rs.getDouble("balance");
                }
            }

            // Check toAccount exists
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM accounts WHERE account_number = ?")) {
                ps.setInt(1, toAccountNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException("Destination account #" + toAccountNumber + " does not exist");
                    }
                }
            }

            if (fromBalance < amount) {
                throw new DataAccessException("Insufficient balance: Rs. " + fromBalance + ", Required: Rs. " + amount);
            }

            // 2. Debit source
            try (PreparedStatement ps = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE account_number = ?")) {
                ps.setDouble(1, amount);
                ps.setInt(2, fromAccountNumber);
                ps.executeUpdate();
            }

            // 3. Credit destination
            try (PreparedStatement ps = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE account_number = ?")) {
                ps.setDouble(1, amount);
                ps.setInt(2, toAccountNumber);
                ps.executeUpdate();
            }

            // 4. Record transaction in database using same connection
            String sqlTxn = "INSERT INTO transactions (transaction_id, timestamp, account_number, type, amount, balance_after, status, from_account, to_account, description) "
                          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlTxn)) {
                ps.setString(1, Transaction.generateId());
                ps.setString(2, LocalDateTime.now().toString());
                ps.setInt(3, fromAccountNumber);
                ps.setString(4, TransactionType.TRANSFER.name());
                ps.setDouble(5, amount);
                ps.setDouble(6, fromBalance - amount);
                ps.setString(7, "SUCCESS");
                ps.setInt(8, fromAccountNumber);
                ps.setInt(9, toAccountNumber);
                ps.setString(10, "Transfer of Rs. " + amount + " to Account #" + toAccountNumber);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new DataAccessException("Transfer transaction failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    public Transaction mapRow(ResultSet rs) throws SQLException {
        String id = rs.getString("transaction_id");
        String tsStr = rs.getString("timestamp");
        LocalDateTime ts = null;
        try {
            ts = LocalDateTime.parse(tsStr);
        } catch (Exception e) {
            ts = LocalDateTime.now();
        }
        int accNo = rs.getInt("account_number");
        String typeStr = rs.getString("type");
        TransactionType type = TransactionType.TRANSFER;
        try {
            type = TransactionType.valueOf(typeStr);
        } catch (Exception ignored) {}

        double amount = rs.getDouble("amount");
        double balanceAfter = rs.getDouble("balance_after");
        String status = rs.getString("status");
        String desc = rs.getString("description");
        int fromAcc = rs.getInt("from_account");
        int toAcc = rs.getInt("to_account");

        return new Transaction(id, ts, accNo, type, amount, balanceAfter, status, desc, fromAcc, toAcc);
    }
}
