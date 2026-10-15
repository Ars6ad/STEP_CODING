package com.gdb.tests;

import com.gdb.db.ConnectionProvider;
import com.gdb.db.JdbcConnectionProvider;
import com.gdb.db.SchemaInitializer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestJdbcConnection {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 22 — JDBC FOUNDATION (CONNECTION & SCHEMA)");
        System.out.println("=".repeat(60));

        String testDbUrl = "jdbc:sqlite:test_gdb.db";
        ConnectionProvider provider = new JdbcConnectionProvider(testDbUrl);

        // ============================================================
        // 📝 STEP 8: Test Direct JDBC Connection & Simple Query
        // ============================================================
        // TODO: connect and execute "SELECT 1"

        // ============================================================
        // 📝 STEP 9: Initialize Schema & Verify Tables
        // ============================================================
        // TODO: call SchemaInitializer.initialize(provider) and query sqlite_master for table names

        provider.shutdown();
        System.out.println("Activity 22 JDBC tests complete.");
    }
}
