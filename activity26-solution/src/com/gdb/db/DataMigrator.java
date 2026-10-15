package com.gdb.db;

import com.gdb.command.TransactionCommand;
import com.gdb.domain.Transaction;
import com.gdb.logging.LogDestination;
import com.gdb.repository.TransactionRepository;

import java.util.List;

/**
 * Utility for migrating legacy file/in-memory transaction command logs into relational SQLite database.
 */
public class DataMigrator {

    public static int migrate(LogDestination source, TransactionRepository target) {
        if (source == null || target == null) {
            return 0;
        }
        List<TransactionCommand> commands = source.readAll();
        int count = 0;
        for (TransactionCommand cmd : commands) {
            if (cmd != null && cmd.getTransaction() != null) {
                target.save(cmd.getTransaction());
                count++;
            }
        }
        return count;
    }
}
