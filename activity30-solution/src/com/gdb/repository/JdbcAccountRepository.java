package com.gdb.repository;

import com.gdb.db.ConnectionProvider;
import com.gdb.domain.*;
import com.gdb.exceptions.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcAccountRepository implements AccountRepository {

    private final ConnectionProvider provider;

    public JdbcAccountRepository(ConnectionProvider provider) {
        this.provider = provider;
    }

    @Override
    public void save(IAccount account) {
        if (account == null) return;
        String sql = "INSERT INTO accounts (account_number, account_holder_name, age, balance, account_type, status, pin, opening_date, overdraft_used, interest_earned) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            Account acc = (Account) account;
            ps.setInt(1, acc.getAccountNumber());
            ps.setString(2, acc.getAccountHolderName());
            ps.setInt(3, acc.getTenureYears() > 0 ? 30 : 25);
            ps.setDouble(4, acc.getBalance());
            ps.setString(5, acc.getAccountType());
            ps.setString(6, acc.isActive() ? "Active" : "Inactive");
            ps.setInt(7, acc.hasPin() ? 1234 : 0);
            ps.setString(8, acc.getOpeningDate() != null ? acc.getOpeningDate() : "2026-08-28");
            ps.setDouble(9, 0.0);
            ps.setDouble(10, 0.0);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to save account: " + e.getMessage(), e);
        }
    }

    @Override
    public IAccount findById(int accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find account by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<IAccount> findAll() {
        List<IAccount> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                accounts.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find all accounts: " + e.getMessage(), e);
        }
        return accounts;
    }

    @Override
    public void update(IAccount account) {
        if (account == null) return;
        String sql = "UPDATE accounts SET account_holder_name = ?, balance = ?, status = ?, overdraft_used = ?, interest_earned = ? WHERE account_number = ?";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            Account acc = (Account) account;
            ps.setString(1, acc.getAccountHolderName());
            ps.setDouble(2, acc.getBalance());
            ps.setString(3, acc.isActive() ? "Active" : "Inactive");
            ps.setDouble(4, 0.0);
            ps.setDouble(5, 0.0);
            ps.setInt(6, acc.getAccountNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update account: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int accountNumber) {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountNumber);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete account: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exists(int accountNumber) {
        String sql = "SELECT 1 FROM accounts WHERE account_number = ?";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check if account exists: " + e.getMessage(), e);
        }
    }

    @Override
    public int nextAccountNumber() {
        String sql = "SELECT COALESCE(MAX(account_number), 1000) + 1 AS next_id FROM accounts";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("next_id");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to generate next account number: " + e.getMessage(), e);
        }
        return 1001;
    }

    public IAccount mapRow(ResultSet rs) throws SQLException {
        int accNo = rs.getInt("account_number");
        String name = rs.getString("account_holder_name");
        int age = rs.getInt("age");
        double balance = rs.getDouble("balance");
        String type = rs.getString("account_type");
        String status = rs.getString("status");
        int pin = rs.getInt("pin");

        try {
            Account acc = (Account) AccountFactory.createAccount(type, accNo, name, Math.max(18, age), balance);
            if (pin > 0) {
                try { acc.setPin(pin); } catch (Exception ignored) {}
            }
            if (!"Active".equalsIgnoreCase(status)) {
                try { acc.closeAccount(); } catch (Exception ignored) {}
            }
            return acc;
        } catch (Exception e) {
            throw new SQLException("Error mapping row to Account: " + e.getMessage(), e);
        }
    }
}
