package com.gdb.tests;

import com.gdb.command.*;
import com.gdb.db.*;
import com.gdb.domain.*;
import com.gdb.logging.*;
import com.gdb.repository.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class TestJdbcTransactionRepository {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 24 — JDBC TRANSACTION REPOSITORY & DATA MIGRATION");
        System.out.println("=".repeat(60));

        String testDbUrl = "jdbc:sqlite:test_txn_gdb.db";
        File dbFile = new File("test_txn_gdb.db");
        if (dbFile.exists()) dbFile.delete();

        ConnectionProvider provider = new JdbcConnectionProvider(testDbUrl);
        SchemaInitializer.initialize(provider);
        JdbcTransactionRepository txnRepo = new JdbcTransactionRepository(provider);

        // ============================================================
        // 📝 STEP 7: Test Transaction Save & Query by Account
        // ============================================================
        // TODO: create and save transactions, query by account, verify fields

        // ============================================================
        // 📝 STEP 8: Test Data Migration from MemoryLogDestination to DB
        // ============================================================
        // TODO: create source log with commands, migrate to txnRepo, verify count in DB

        provider.shutdown();
        if (dbFile.exists()) dbFile.delete();
        System.out.println("Activity 24 tests complete.");
    }
}
