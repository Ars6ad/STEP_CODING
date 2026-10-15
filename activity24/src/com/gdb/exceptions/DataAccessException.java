package com.gdb.exceptions;

/**
 * Unchecked runtime exception wrapping underlying database/SQL errors.
 * Preserves root cause while decoupling caller from java.sql.SQLException.
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
