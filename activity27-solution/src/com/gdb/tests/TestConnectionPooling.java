package com.gdb.tests;

import com.gdb.db.*;
import com.gdb.domain.*;
import com.gdb.repository.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class TestConnectionPooling {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 25 — CONNECTION POOLING WITH HIKARICP (NFR)");
        System.out.println("=".repeat(60));

        String testDbUrl = "jdbc:sqlite:test_pool_gdb.db";
        File dbFile = new File("test_pool_gdb.db");
        if (dbFile.exists()) dbFile.delete();

        Properties props = new Properties();
        props.setProperty("persistence.db.url", testDbUrl);
        props.setProperty("persistence.db.driver", "org.sqlite.JDBC");
        props.setProperty("persistence.pool.enabled", "true");
        props.setProperty("persistence.pool.size", "5");
        props.setProperty("persistence.pool.timeout", "10000");

        ConnectionProvider poolProvider = new HikariConnectionProvider(props);
        SchemaInitializer.initialize(poolProvider);

        // TEST 1: Pool Initialization & Acquisition
        System.out.println("[TEST 1] HikariCP Pool Initialization:");
        System.out.println("  Provider: " + poolProvider.getProviderName());
        try (Connection conn = poolProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 AS test_val")) {
            assert rs.next() : "Result set should have rows";
            int val = rs.getInt("test_val");
            System.out.println("  Acquired connection from pool -> query returned: " + val);
            assert val == 1 : "Expected value 1";
        }
        System.out.println("  Connection returned to pool successfully. -> PASSED");

        // TEST 2: Repository Operations through Connection Pool
        System.out.println("\n[TEST 2] Repository CRUD Operations through Connection Pool:");
        AccountRepository accRepo = new JdbcAccountRepository(poolProvider);
        TransactionRepository txnRepo = new JdbcTransactionRepository(poolProvider);

        IAccount acc1 = AccountFactory.createAccount("SAVINGS", 1001, "Rajesh Sharma", 30, 50000.0);
        acc1.setPin(1234);
        accRepo.save(acc1);

        IAccount acc2 = AccountFactory.createAccount("CURRENT", 1002, "Priya Patel", 28, 40000.0);
        accRepo.save(acc2);

        txnRepo.transfer(1001, 1002, 10000.0);
        IAccount src = accRepo.findById(1001);
        IAccount dst = accRepo.findById(1002);

        System.out.println("  Source balance: Rs. " + src.getBalance() + " (Expected: 40000.0)");
        System.out.println("  Destination balance: Rs. " + dst.getBalance() + " (Expected: 50000.0)");
        assert src.getBalance() == 40000.0 : "Sender balance mismatch";
        assert dst.getBalance() == 50000.0 : "Receiver balance mismatch";
        System.out.println("  -> PASSED");

        // TEST 3: NFR Benchmark (Pooled vs Unpooled)
        System.out.println("\n[TEST 3] NFR Performance Benchmark (50 Operations):");
        int iterations = 50;

        // Pooled benchmark
        long startPooled = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            try (Connection conn = poolProvider.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT count(*) FROM accounts")) {
                rs.next();
            }
        }
        long durationPooled = System.currentTimeMillis() - startPooled;

        // Unpooled benchmark (Direct DriverManager connection creation on every iteration)
        long startUnpooled = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            try (Connection conn = DriverManager.getConnection(testDbUrl);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT count(*) FROM accounts")) {
                rs.next();
            }
        }
        long durationUnpooled = System.currentTimeMillis() - startUnpooled;

        System.out.println("  Pooled Execution Time (" + iterations + " queries):   " + durationPooled + " ms");
        System.out.println("  Unpooled Execution Time (" + iterations + " queries): " + durationUnpooled + " ms");
        System.out.println("  -> Connection Pool Benchmark complete.");

        // Clean up
        poolProvider.shutdown();
        if (dbFile.exists()) dbFile.delete();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ALL ACTIVITY 25 CONNECTION POOLING (NFR) TESTS PASSED!");
        System.out.println("=".repeat(60));
    }
}
