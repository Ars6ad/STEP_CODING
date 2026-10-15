package com.gdb.repository;

import com.gdb.domain.*;
import com.gdb.exceptions.DataAccessException;
import com.gdb.file.AtomicFileWriter;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * File-backed implementation of AccountRepository.
 * Stores account records in a CSV/text file with atomic writes.
 */
public class FileAccountRepository implements AccountRepository {

    private final String filePath;
    private final boolean autosave;
    private int nextAccountNumber;
    private final Map<Integer, IAccount> cache = new HashMap<>();

    public FileAccountRepository(String filePath, boolean autosave, int startAccountNumber) {
        this.filePath = filePath;
        this.autosave = autosave;
        this.nextAccountNumber = startAccountNumber;
        load();
    }

    public FileAccountRepository(String filePath, int startAccountNumber) {
        this(filePath, true, startAccountNumber);
    }

    public FileAccountRepository(String filePath) {
        this(filePath, true, 1001);
    }

    // ============================================================
    // 📝 STEP 1: Implement load()
    //
    // INSTRUCTIONS:
    //   Read accounts from filePath and populate the memory cache.
    // ============================================================
    public synchronized void load() {
        // TODO: Step 1 - Load accounts from file into cache
    }

    // ============================================================
    // 📝 STEP 2: Implement flush()
    //
    // INSTRUCTIONS:
    //   Serialize cache to CSV lines and write using AtomicFileWriter.
    // ============================================================
    public synchronized void flush() {
        // TODO: Step 2 - Flush cache to file atomically
    }

    // ============================================================
    // 📝 STEP 3: Implement save(IAccount account)
    // ============================================================
    @Override
    public synchronized void save(IAccount account) {
        // TODO: Step 3 - Save account to cache (and flush if autosave)
    }

    // ============================================================
    // 📝 STEP 4: Implement findById(int accountNumber)
    // ============================================================
    @Override
    public synchronized IAccount findById(int accountNumber) {
        // TODO: Step 4 - Find account by ID from cache
        return null;
    }

    // ============================================================
    // 📝 STEP 5: Implement findAll()
    // ============================================================
    @Override
    public synchronized List<IAccount> findAll() {
        // TODO: Step 5 - Return all accounts from cache
        return new ArrayList<>();
    }

    // ============================================================
    // 📝 STEP 6: Implement update(IAccount account)
    // ============================================================
    @Override
    public synchronized void update(IAccount account) {
        // TODO: Step 6 - Update account in cache
    }

    // ============================================================
    // 📝 STEP 7: Implement delete(int accountNumber)
    // ============================================================
    @Override
    public synchronized void delete(int accountNumber) {
        // TODO: Step 7 - Delete account from cache
    }

    // ============================================================
    // 📝 STEP 8: Implement exists(int accountNumber)
    // ============================================================
    @Override
    public synchronized boolean exists(int accountNumber) {
        // TODO: Step 8 - Check if account exists in cache
        return false;
    }

    // ============================================================
    // 📝 STEP 9: Implement nextAccountNumber()
    // ============================================================
    @Override
    public synchronized int nextAccountNumber() {
        // TODO: Step 9 - Return current nextAccountNumber and post-increment
        return nextAccountNumber++;
    }
}
