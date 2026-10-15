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

    private final HikariDataSource dataSource;

    public HikariConnectionProvider(Properties config) {
        HikariConfig hikariConfig = new HikariConfig();
        String url = config.getProperty("persistence.db.url", "jdbc:sqlite:gdb.db");
        String driver = config.getProperty("persistence.db.driver", "org.sqlite.JDBC");
        int poolSize = Integer.parseInt(config.getProperty("persistence.pool.size", "10"));
        long timeout = Long.parseLong(config.getProperty("persistence.pool.timeout", "30000"));

        hikariConfig.setJdbcUrl(url);
        hikariConfig.setDriverClassName(driver);
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setConnectionTimeout(timeout);
        hikariConfig.setPoolName("GDB-HikariPool");

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public String getProviderName() {
        return "HikariConnectionProvider [Pool size: " + dataSource.getMaximumPoolSize() + "]";
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
