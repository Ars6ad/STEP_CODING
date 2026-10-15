package com.gdb.file;

import com.gdb.domain.*;
import com.gdb.exceptions.AccountException;

import java.time.LocalDateTime;

/**
 * Utility mapping between Domain models and CSV formatted rows.
 */
public class CsvMapper {

    public static final String ACCOUNT_HEADER = "account_number,account_holder_name,age,balance,account_type,status,pin,opening_date,overdraft_used,interest_earned";
    public static final String TRANSACTION_HEADER = "transaction_id,timestamp,account_number,type,amount,balance_after,status,from_account,to_account,description";

    // ============================================================
    // 📝 STEP 1: Implement toCsv(IAccount account)
    //
    // INSTRUCTIONS:
    //   Format account fields as comma-separated string matching ACCOUNT_HEADER.
    // ============================================================
    public static String toCsv(IAccount account) {
        // TODO: Step 1 - serialize IAccount to CSV line
        return "";
    }

    // ============================================================
    // 📝 STEP 2: Implement fromCsv(String line)
    //
    // INSTRUCTIONS:
    //   Parse CSV line and reconstruct appropriate IAccount subclass using AccountFactory.
    // ============================================================
    public static IAccount fromCsv(String line) {
        // TODO: Step 2 - deserialize CSV line to IAccount
        return null;
    }

    // ============================================================
    // 📝 STEP 3: Implement toCsv(Transaction transaction)
    //
    // INSTRUCTIONS:
    //   Format transaction fields matching TRANSACTION_HEADER.
    // ============================================================
    public static String toCsv(Transaction transaction) {
        // TODO: Step 3 - serialize Transaction to CSV line
        return "";
    }

    // ============================================================
    // 📝 STEP 4: Implement fromCsvTransaction(String line)
    //
    // INSTRUCTIONS:
    //   Parse CSV line and reconstruct Transaction model.
    // ============================================================
    public static Transaction fromCsvTransaction(String line) {
        // TODO: Step 4 - deserialize CSV line to Transaction
        return null;
    }
}
