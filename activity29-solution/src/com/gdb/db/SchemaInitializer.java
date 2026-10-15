package com.gdb.db;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Initializes the database schema idempotently by executing schema.sql DDL statements.
 */
public class SchemaInitializer {

    public static void initialize(ConnectionProvider provider) {
        if (provider == null) {
            return;
        }
        String sql = readSchemaSql();
        if (sql == null || sql.trim().isEmpty()) {
            throw new RuntimeException("schema.sql is empty or not found");
        }

        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement()) {
            String[] statements = sql.split(";");
            for (String s : statements) {
                String trimmed = s.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database schema: " + e.getMessage(), e);
        }
    }

    public static String readSchemaSql() {
        String[] candidatePaths = {
            "schema.sql",
            "src/main/resources/schema.sql",
            "bin/schema.sql"
        };

        for (String p : candidatePaths) {
            File f = new File(p);
            if (f.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    return sb.toString();
                } catch (IOException ignored) {}
            }
        }

        try (InputStream in = SchemaInitializer.class.getResourceAsStream("/schema.sql")) {
            if (in != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    return sb.toString();
                }
            }
        } catch (IOException ignored) {}

        // Fallback default DDL
        return "CREATE TABLE IF NOT EXISTS accounts (account_number INTEGER PRIMARY KEY, account_holder_name TEXT NOT NULL, age INTEGER NOT NULL, balance REAL NOT NULL, account_type TEXT NOT NULL, status TEXT NOT NULL, pin INTEGER, opening_date TEXT NOT NULL, overdraft_used REAL DEFAULT 0, interest_earned REAL DEFAULT 0);\n" +
               "CREATE TABLE IF NOT EXISTS transactions (transaction_id TEXT PRIMARY KEY, timestamp TEXT NOT NULL, account_number INTEGER NOT NULL, type TEXT NOT NULL, amount REAL NOT NULL, balance_after REAL NOT NULL, status TEXT NOT NULL, from_account INTEGER, to_account INTEGER, description TEXT);";
    }
}
