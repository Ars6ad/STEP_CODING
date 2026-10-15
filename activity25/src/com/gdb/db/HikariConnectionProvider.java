package com.gdb.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Production-grade ConnectionProvider backed by HikariCP Connection Pool.
 */
public class HikariConnectionProvider implements ConnectionProvider {

    // ============================================================
    // 📝 STEP 1: Declare DataSource Field
    // ============================================================
    private HikariDataSource dataSource;

    // ============================================================
    // 📝 STEP 2: Implement Constructor with HikariConfig
    //
    // INSTRUCTIONS:
    //   1. Create new HikariConfig().
    //   2. Set jdbcUrl from config "persistence.db.url" (default "jdbc:sqlite:gdb.db").
    //   3. Set driverClassName from config "persistence.db.driver" (default "org.sqlite.JDBC").
    //   4. Set maximumPoolSize from config "persistence.pool.size" (default "10").
    //   5. Set connectionTimeout from config "persistence.pool.timeout" (default "30000").
    //   6. Initialize this.dataSource = new HikariDataSource(hikariConfig).
    // ============================================================
    public HikariConnectionProvider(Properties config) {
        // TODO: Step 2 - configure and initialize HikariDataSource
    }

    // ============================================================
    // 📝 STEP 3: Implement getConnection()
    //
    // INSTRUCTIONS:
    //   Return a Connection acquired from dataSource.getConnection().
    // ============================================================
    @Override
    public Connection getConnection() throws SQLException {
        // TODO: Step 3 - acquire connection from pool
        return null;
    }

    // ============================================================
    // 📝 STEP 4: Implement shutdown()
    //
    // INSTRUCTIONS:
    //   Close the dataSource if not already closed.
    // ============================================================
    @Override
    public void shutdown() {
        // TODO: Step 4 - close dataSource
    }

    @Override
    public String getProviderName() {
        return "HikariConnectionProvider [Pool size: " + (dataSource != null ? dataSource.getMaximumPoolSize() : "N/A") + "]";
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
