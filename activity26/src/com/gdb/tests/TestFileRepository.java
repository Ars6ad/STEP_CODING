package com.gdb.tests;

import com.gdb.domain.*;
import com.gdb.repository.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class TestFileRepository {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 26 — FILE REPOSITORY (CSV + ATOMIC WRITES + BACKUP)");
        System.out.println("=".repeat(60));

        String accPath = "test_data/accounts.csv";
        String txnPath = "test_data/transactions.csv";

        // Clean test files
        new File(accPath).delete();
        new File(txnPath).delete();

        // ============================================================
        // 📝 STEP 18: Test FileAccountRepository CRUD & Disk Persistence
        // ============================================================
        // TODO: test save, findById, update, delete on FileAccountRepository

        // ============================================================
        // 📝 STEP 19: Test FileTransactionRepository & CSV Logging
        // ============================================================
        // TODO: test save, findByAccount, findAll, clear on FileTransactionRepository

        // ============================================================
        // 📝 STEP 20: Test Backup Creation
        // ============================================================
        // TODO: verify backup files exist in directory after operations

        System.out.println("Activity 26 tests complete.");
    }
}
