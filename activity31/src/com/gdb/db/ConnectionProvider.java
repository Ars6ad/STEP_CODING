package com.gdb.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface abstracting connection acquisition and lifecycle.
 */
public interface ConnectionProvider {
    Connection getConnection() throws SQLException;
    void shutdown();
    String getProviderName();
}
