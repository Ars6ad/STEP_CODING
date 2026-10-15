package com.gdb.repository;

import com.gdb.db.ConnectionProvider;
import com.gdb.domain.Transaction;
import com.gdb.domain.TransactionType;
import com.gdb.exceptions.DataAccessException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC-backed implementation of TransactionRepository.
 * Handles transaction record persistence, retrieval, and multi-account atomic transfers.
 */
public class JdbcTransactionRepository implements TransactionRepository {

    private final ConnectionProvider provider;

    public JdbcTransactionRepository(ConnectionProvider provider) {
        this.provider = provider;
    }

    // ============================================================
    // 📝 STEP 1: Implement save(Transaction transaction)
    //
    // INSTRUCTIONS:
    //   1. Guard against null transaction.
    //   2. Prepare INSERT INTO transactions (id, timestamp, account_number, type, amount, balance_after, status, remarks, from_account, to_account)
    //   3. Bind values and executeUpdate.
    //   4. Catch SQLException and throw DataAccessException.
    // ============================================================
    @Override
    public void save(Transaction transaction) {
        // TODO: Step 1 - Insert transaction into database
    }

    // ============================================================
    // 📝 STEP 2: Implement findByAccount(int accountNumber)
    //
    // INSTRUCTIONS:
    //   1. Prepare SELECT * FROM transactions WHERE account_number = ? OR from_account = ? OR to_account = ?
    //   2. Bind accountNumber and execute query.
    //   3. Map rows to Transaction objects and return list.
    //   4. Catch SQLException and throw DataAccessException.
    // ============================================================
    @Override
    public List<Transaction> findByAccount(int accountNumber) {
        // TODO: Step 2 - Find transactions by account number
        return new ArrayList<>();
    }

    // ============================================================
    // 📝 STEP 3: Implement findAll()
    //
    // INSTRUCTIONS:
    //   1. Prepare SELECT * FROM transactions
    //   2. Map rows to Transaction objects and return list.
    // ============================================================
    @Override
    public List<Transaction> findAll() {
        // TODO: Step 3 - Retrieve all transactions
        return new ArrayList<>();
    }

    // ============================================================
    // 📝 STEP 4: Implement clear()
    //
    // INSTRUCTIONS:
    //   1. Prepare DELETE FROM transactions
    //   2. Execute update.
    // ============================================================
    @Override
    public void clear() {
        // TODO: Step 4 - Clear transactions
    }

    // ============================================================
    // 📝 STEP 5: Implement transfer(int fromAccountNumber, int toAccountNumber, double amount)
    //
    // INSTRUCTIONS:
    //   1. Acquire a connection from provider and setAutoCommit(false).
    //   2. Check sender balance and ensure sufficient funds.
    //   3. Decrement sender balance (UPDATE accounts SET balance = balance - ? WHERE account_number = ?).
    //   4. Increment receiver balance (UPDATE accounts SET balance = balance + ? WHERE account_number = ?).
    //   5. Insert transfer audit record into transactions table using the SAME connection.
    //   6. Call conn.commit().
    //   7. If any exception occurs, call conn.rollback() and throw DataAccessException.
    // ============================================================
    @Override
    public void transfer(int fromAccountNumber, int toAccountNumber, double amount) {
        // TODO: Step 5 - Implement atomic transfer transaction with commit/rollback
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String tsStr = rs.getString("timestamp");
        LocalDateTime ts = tsStr != null ? LocalDateTime.parse(tsStr) : LocalDateTime.now();
        int accNum = rs.getInt("account_number");
        String typeStr = rs.getString("type");
        TransactionType type = TransactionType.valueOf(typeStr);
        double amt = rs.getDouble("amount");
        double balanceAfter = rs.getDouble("balance_after");
        String status = rs.getString("status");
        String remarks = rs.getString("remarks");
        int fromAcc = rs.getInt("from_account");
        int toAcc = rs.getInt("to_account");

        return new Transaction(id, ts, accNum, type, amt, balanceAfter, status, remarks, fromAcc, toAcc);
    }
}
