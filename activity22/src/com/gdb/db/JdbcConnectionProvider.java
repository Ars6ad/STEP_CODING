package com.gdb.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Direct JDBC Connection Provider using DriverManager.
 */
public class JdbcConnectionProvider implements ConnectionProvider {

    // ============================================================
    // 📝 STEP 1: Declare Fields
    // ============================================================
    private final String url;
    private final String driver;

    // ============================================================
    // 📝 STEP 2: Implement Constructor
    //
    // INSTRUCTIONS:
    //   1. Store url and driver.
    //   2. Load the driver class via Class.forName(driver).
    // ============================================================
    public JdbcConnectionProvider(String url, String driver) {
        this.url = url;
        this.driver = driver;
        // TODO: Step 2 - load driver class
    }

    public JdbcConnectionProvider(String url) {
        this(url, "org.sqlite.JDBC");
    }

    // ============================================================
    // 📝 STEP 3: Implement getConnection()
    //
    // INSTRUCTIONS:
    //   Return DriverManager.getConnection(url).
    // ============================================================
    @Override
    public Connection getConnection() throws SQLException {
        // TODO: Step 3 - obtain and return connection from DriverManager
        return null;
    }

    // ============================================================
    // 📝 STEP 4: Implement shutdown() & getProviderName()
    // ============================================================
    @Override
    public void shutdown() {
        // Direct JDBC connection requires no pool shutdown
    }

    @Override
    public String getProviderName() {
        return "JdbcConnectionProvider [" + url + "]";
    }

    public String getUrl() {
        return url;
    }
}
