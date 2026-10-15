package com.gdb.tests;

import com.gdb.db.*;
import com.gdb.domain.*;
import com.gdb.repository.*;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class TestConnectionPooling {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 25 — CONNECTION POOLING WITH HIKARICP (NFR)");
        System.out.println("=".repeat(60));

        Properties props = new Properties();
        props.setProperty("persistence.db.url", "jdbc:sqlite:test_pool_gdb.db");
        props.setProperty("persistence.db.driver", "org.sqlite.JDBC");
        props.setProperty("persistence.pool.enabled", "true");
        props.setProperty("persistence.pool.size", "5");
        props.setProperty("persistence.pool.timeout", "10000");

        File dbFile = new File("test_pool_gdb.db");
        if (dbFile.exists()) dbFile.delete();

        // ============================================================
        // 📝 STEP 5: Test Connection Pool Initialization
        // ============================================================
        // TODO: instantiate HikariConnectionProvider, run SchemaInitializer

        // ============================================================
        // 📝 STEP 6: Test Connection Borrowing & Returning from Pool
        // ============================================================
        // TODO: borrow connection, execute query, close connection back to pool

        // ============================================================
        // 📝 STEP 7: Benchmark Pooled vs Non-Pooled Performance
        // ============================================================
        // TODO: measure time taken for 50 queries using pool vs direct DriverManager

        if (dbFile.exists()) dbFile.delete();
        System.out.println("Activity 25 tests complete.");
    }
}
