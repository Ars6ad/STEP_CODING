package com.gdb.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Direct JDBC Connection Provider using DriverManager.
 */
public class JdbcConnectionProvider implements ConnectionProvider {

    private final String url;
    private final String driver;

    public JdbcConnectionProvider(String url, String driver) {
        this.url = url;
        this.driver = driver;
        try {
            if (driver != null && !driver.isEmpty()) {
                Class.forName(driver);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load JDBC driver: " + driver, e);
        }
    }

    public JdbcConnectionProvider(String url) {
        this(url, "org.sqlite.JDBC");
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

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
