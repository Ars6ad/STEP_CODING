package com.gdb.tests;

import com.gdb.db.ConnectionProvider;
import com.gdb.db.JdbcConnectionProvider;
import com.gdb.db.SchemaInitializer;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestJdbcConnection {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 22 — JDBC FOUNDATION (CONNECTION & SCHEMA)");
        System.out.println("=".repeat(60));

        String testDbUrl = "jdbc:sqlite:test_gdb.db";
        File dbFile = new File("test_gdb.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }

        ConnectionProvider provider = new JdbcConnectionProvider(testDbUrl);

        // TEST 1: Direct Connection & Simple Query
        System.out.println("[TEST 1] Connect to SQLite & Run Simple Query:");
        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 AS result")) {
            if (rs.next()) {
                int res = rs.getInt("result");
                System.out.println("  SELECT 1 -> " + res);
                assert res == 1 : "Expected 1";
            }
        }
        System.out.println("  -> PASSED");

        // TEST 2: Schema Initialization
        System.out.println("\n[TEST 2] Initialize Database Schema (schema.sql):");
        SchemaInitializer.initialize(provider);
        System.out.println("  SchemaInitializer executed successfully.");
        System.out.println("  -> PASSED");

        // TEST 3: Verify Tables Created
        System.out.println("\n[TEST 3] Verify Created Tables in SQLite:");
        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name")) {
            boolean hasAccounts = false;
            boolean hasTransactions = false;
            while (rs.next()) {
                String tableName = rs.getString("name");
                System.out.println("  Found table: " + tableName);
                if ("accounts".equalsIgnoreCase(tableName)) hasAccounts = true;
                if ("transactions".equalsIgnoreCase(tableName)) hasTransactions = true;
            }
            assert hasAccounts : "accounts table should exist";
            assert hasTransactions : "transactions table should exist";
        }
        System.out.println("  -> PASSED");

        // Clean up
        provider.shutdown();
        if (dbFile.exists()) {
            dbFile.delete();
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ALL ACTIVITY 22 JDBC FOUNDATION TESTS PASSED!");
        System.out.println("=".repeat(60));
    }
}
