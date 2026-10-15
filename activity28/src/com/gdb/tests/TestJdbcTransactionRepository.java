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

        // TEST 1: Direct Save & Query
        System.out.println("[TEST 1] Direct Save & FindByAccount:");
        Transaction t1 = new Transaction(
            "TXN-101", LocalDateTime.now(), 1001,
            TransactionType.DEPOSIT, 5000.0, 55000.0, "SUCCESS",
            "Initial Deposit", 1001, 1001
        );
        Transaction t2 = new Transaction(
            "TXN-102", LocalDateTime.now(), 1001,
            TransactionType.TRANSFER, 2000.0, 53000.0, "SUCCESS",
            "Transfer to #1002", 1001, 1002
        );
        txnRepo.save(t1);
        txnRepo.save(t2);

        List<Transaction> acc1Txns = txnRepo.findByAccount(1001);
        System.out.println("  Found transactions for #1001: " + acc1Txns.size() + " (Expected: 2)");
        assert acc1Txns.size() == 2 : "Account 1001 should have 2 transactions";

        List<Transaction> acc2Txns = txnRepo.findByAccount(1002);
        System.out.println("  Found transactions for #1002: " + acc2Txns.size() + " (Expected: 1)");
        assert acc2Txns.size() == 1 : "Account 1002 should have 1 transfer transaction";
        System.out.println("  -> PASSED");

        // TEST 2: Data Migration from MemoryLogDestination to SQLite
        System.out.println("\n[TEST 2] Data Migration (MemoryLogDestination -> SQLite):");
        txnRepo.clear();
        assert txnRepo.findAll().isEmpty() : "Repo should be cleared before migration";

        LogDestination memorySource = new MemoryLogDestination();
        IAccount testAcc1 = AccountFactory.createAccount("SAVINGS", 1001, "Rajesh", 30, 50000.0);
        IAccount testAcc2 = AccountFactory.createAccount("CURRENT", 1002, "Priya", 28, 40000.0);
        testAcc1.setPin(1234);

        DepositCommand depCmd = new DepositCommand(testAcc1, 10000.0);
        depCmd.execute();
        memorySource.write(depCmd);

        WithdrawCommand wthCmd = new WithdrawCommand(testAcc1, 5000.0, 1234);
        wthCmd.execute();
        memorySource.write(wthCmd);

        TransferCommand trfCmd = new TransferCommand(testAcc1, testAcc2, 5000.0, 1234);
        trfCmd.execute();
        memorySource.write(trfCmd);

        int migratedCount = DataMigrator.migrate(memorySource, txnRepo);
        System.out.println("  Migrated records count: " + migratedCount + " (Expected: 3)");
        assert migratedCount == 3 : "Should have migrated 3 records";

        List<Transaction> allInDb = txnRepo.findAll();
        System.out.println("  Total records in SQLite DB: " + allInDb.size() + " (Expected: 3)");
        assert allInDb.size() == 3 : "Database should contain 3 migrated transactions";
        for (int i = 0; i < allInDb.size(); i++) {
            System.out.println("    [" + (i + 1) + "] " + allInDb.get(i));
        }
        System.out.println("  -> PASSED");

        // Clean up
        provider.shutdown();
        if (dbFile.exists()) dbFile.delete();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ALL ACTIVITY 24 JDBC TRANSACTION REPO & MIGRATION TESTS PASSED!");
        System.out.println("=".repeat(60));
    }
}
