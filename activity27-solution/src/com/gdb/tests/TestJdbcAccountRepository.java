package com.gdb.tests;

import com.gdb.db.ConnectionProvider;
import com.gdb.db.JdbcConnectionProvider;
import com.gdb.db.SchemaInitializer;
import com.gdb.domain.AccountFactory;
import com.gdb.domain.IAccount;
import com.gdb.repository.JdbcAccountRepository;

import java.io.File;
import java.util.List;

public class TestJdbcAccountRepository {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 23 — JDBC ACCOUNT REPOSITORY CRUD TESTS");
        System.out.println("=".repeat(60));

        String testDbUrl = "jdbc:sqlite:test_acc_gdb.db";
        File dbFile = new File("test_acc_gdb.db");
        if (dbFile.exists()) dbFile.delete();

        ConnectionProvider provider = new JdbcConnectionProvider(testDbUrl);
        SchemaInitializer.initialize(provider);
        JdbcAccountRepository repo = new JdbcAccountRepository(provider);

        // TEST 1: Save & FindById
        System.out.println("[TEST 1] Save & FindById:");
        IAccount acc1 = AccountFactory.createAccount("SAVINGS", 1001, "Rajesh Sharma", 30, 50000.0);
        acc1.setPin(1234);
        repo.save(acc1);

        IAccount found = repo.findById(1001);
        System.out.println("  Found: " + found.getAccountInfo());
        assert found != null : "Account 1001 should be found";
        assert found.getAccountNumber() == 1001 : "Account number should match";
        assert "Rajesh Sharma".equals(found.getAccountHolderName()) : "Name should match";
        assert found.getBalance() == 50000.0 : "Balance should match";
        System.out.println("  -> PASSED");

        // TEST 2: Update & FindAll
        System.out.println("\n[TEST 2] Update & FindAll:");
        IAccount acc2 = AccountFactory.createAccount("CURRENT", 1002, "Priya Patel", 28, 40000.0);
        repo.save(acc2);

        found.deposit(15000.0);
        repo.update(found);

        IAccount updated = repo.findById(1001);
        System.out.println("  Updated balance: Rs. " + updated.getBalance() + " (Expected: 65000.0)");
        assert updated.getBalance() == 65000.0 : "Balance should be updated in DB";

        List<IAccount> all = repo.findAll();
        System.out.println("  Total accounts in DB: " + all.size() + " (Expected: 2)");
        assert all.size() == 2 : "Should have 2 accounts";
        System.out.println("  -> PASSED");

        // TEST 3: Delete & Exists
        System.out.println("\n[TEST 3] Delete & Exists:");
        assert repo.exists(1002) : "Account 1002 should exist";
        repo.delete(1002);
        assert !repo.exists(1002) : "Account 1002 should no longer exist";
        assert repo.findById(1002) == null : "findById(1002) should return null";
        System.out.println("  Account 1002 successfully deleted.");
        System.out.println("  -> PASSED");

        // Clean up
        provider.shutdown();
        if (dbFile.exists()) dbFile.delete();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ALL ACTIVITY 23 JDBC ACCOUNT REPOSITORY TESTS PASSED!");
        System.out.println("=".repeat(60));
    }
}
