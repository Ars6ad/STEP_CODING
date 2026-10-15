package com.gdb.repository;

import com.gdb.domain.Transaction;
import com.gdb.domain.TransactionType;
import com.gdb.exceptions.DataAccessException;
import com.gdb.file.AtomicFileWriter;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * File-backed implementation of TransactionRepository.
 * Stores transaction records in a text file and supports atomic balance transfers.
 */
public class FileTransactionRepository implements TransactionRepository {

    private final String filePath;
    private final boolean autosave;
    private final List<Transaction> cache = new ArrayList<>();

    public FileTransactionRepository(String filePath, boolean autosave, int startId) {
        this.filePath = filePath;
        this.autosave = autosave;
        load();
    }

    public FileTransactionRepository(String filePath, int startId) {
        this(filePath, true, startId);
    }

    public FileTransactionRepository(String filePath) {
        this(filePath, true, 1);
    }

    // ============================================================
    // 📝 STEP 1: Implement load() and flush()
    // ============================================================
    public synchronized void load() {
        // TODO: Step 1 - Load transactions from file into cache
    }

    public synchronized void flush() {
        // TODO: Step 1 - Flush cache to file atomically
    }

    // ============================================================
    // 📝 STEP 2: Implement save(Transaction transaction)
    // ============================================================
    @Override
    public synchronized void save(Transaction transaction) {
        // TODO: Step 2 - Save transaction
    }

    // ============================================================
    // 📝 STEP 3: Implement findByAccount(int accountNumber)
    // ============================================================
    @Override
    public synchronized List<Transaction> findByAccount(int accountNumber) {
        // TODO: Step 3 - Find transactions by account number
        return new ArrayList<>();
    }

    // ============================================================
    // 📝 STEP 4: Implement findAll()
    // ============================================================
    @Override
    public synchronized List<Transaction> findAll() {
        // TODO: Step 4 - Return all transactions
        return new ArrayList<>();
    }

    // ============================================================
    // 📝 STEP 5: Implement clear()
    // ============================================================
    @Override
    public synchronized void clear() {
        // TODO: Step 5 - Clear transactions
    }

    // ============================================================
    // 📝 STEP 6: Implement transfer(int fromAccountNumber, int toAccountNumber, double amount)
    // ============================================================
    @Override
    public synchronized void transfer(int fromAccountNumber, int toAccountNumber, double amount) {
        // TODO: Step 6 - Atomic transfer across accounts in file repository
    }
}
