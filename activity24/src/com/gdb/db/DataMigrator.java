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

    // ============================================================
    // 📝 STEP 6: Implement Data Migration Utility
    //
    // INSTRUCTIONS:
    //   1. Read all TransactionCommand objects from source.readAll().
    //   2. Iterate each command and extract its Transaction: cmd.getTransaction().
    //   3. If transaction is not null, save it into target TransactionRepository.
    //   4. Return the count of migrated records.
    // ============================================================
    public static int migrate(LogDestination source, TransactionRepository target) {
        // TODO: Step 6 - migrate logs to relational transaction repository
        return 0;
    }
}
