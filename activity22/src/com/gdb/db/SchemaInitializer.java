package com.gdb.db;

import java.io.*;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Initializes the database schema idempotently by executing schema.sql DDL statements.
 */
public class SchemaInitializer {

    // ============================================================
    // 📝 STEP 5: Implement initialize(ConnectionProvider provider)
    //
    // INSTRUCTIONS:
    //   1. Obtain a Connection from provider.
    //   2. Read the contents of schema.sql from resource stream or file.
    //   3. Split SQL script into individual statements by semicolon ';'.
    //   4. Execute each non-empty statement using Statement.execute(sql).
    // ============================================================
    public static void initialize(ConnectionProvider provider) {
        // TODO: Step 5 - initialize database tables using schema.sql
    }

    // ============================================================
    // 📝 STEP 6: Helper Method to Read SQL Script
    // ============================================================
    public static String readSchemaSql() {
        // TODO: Step 6 - read and return schema.sql content
        return "";
    }
}
