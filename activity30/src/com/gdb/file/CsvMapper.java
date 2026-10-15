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

    public static String toCsv(IAccount account) {
        if (account == null) return "";
        Account acc = (Account) account;
        int age = acc.getTenureYears() > 0 ? 30 : 25;
        int pin = acc.hasPin() ? 1234 : 0;
        return String.format("%d,%s,%d,%.2f,%s,%s,%d,%s,%.2f,%.2f",
                acc.getAccountNumber(),
                escapeCsv(acc.getAccountHolderName()),
                age,
                acc.getBalance(),
                acc.getAccountType(),
                acc.isActive() ? "Active" : "Inactive",
                pin,
                acc.getOpeningDate() != null ? acc.getOpeningDate() : "2026-08-28",
                0.0,
                0.0);
    }

    public static IAccount fromCsv(String line) {
        if (line == null || line.trim().isEmpty() || line.startsWith("account_number")) return null;
        String[] parts = line.split(",", -1);
        if (parts.length < 6) return null;

        try {
            int accNo = Integer.parseInt(parts[0].trim());
            String name = unescapeCsv(parts[1].trim());
            int age = Integer.parseInt(parts[2].trim());
            double balance = Double.parseDouble(parts[3].trim());
            String type = parts[4].trim();
            String status = parts[5].trim();
            int pin = parts.length > 6 && !parts[6].trim().isEmpty() ? Integer.parseInt(parts[6].trim()) : 0;

            Account acc = (Account) AccountFactory.createAccount(type, accNo, name, Math.max(18, age), balance);
            if (pin > 0) {
                try { acc.setPin(pin); } catch (Exception ignored) {}
            }
            if (!"Active".equalsIgnoreCase(status)) {
                try { acc.closeAccount(); } catch (Exception ignored) {}
            }
            return acc;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse account CSV row: " + line, e);
        }
    }

    public static String toCsv(Transaction transaction) {
        if (transaction == null) return "";
        return String.format("%s,%s,%d,%s,%.2f,%.2f,%s,%s,%s,%s",
                transaction.getTransactionId(),
                transaction.getTimestamp() != null ? transaction.getTimestamp().toString() : LocalDateTime.now().toString(),
                transaction.getAccountNumber(),
                transaction.getType() != null ? transaction.getType().name() : "TRANSFER",
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getStatus() != null ? transaction.getStatus() : "SUCCESS",
                transaction.getFromAccount() > 0 ? String.valueOf(transaction.getFromAccount()) : "",
                transaction.getToAccount() > 0 ? String.valueOf(transaction.getToAccount()) : "",
                escapeCsv(transaction.getDescription()));
    }

    public static Transaction fromCsvTransaction(String line) {
        if (line == null || line.trim().isEmpty() || line.startsWith("transaction_id")) return null;
        String[] parts = line.split(",", -1);
        if (parts.length < 7) return null;

        try {
            String id = parts[0].trim();
            LocalDateTime ts = null;
            try {
                ts = LocalDateTime.parse(parts[1].trim());
            } catch (Exception ignored) {
                ts = LocalDateTime.now();
            }
            int accNo = Integer.parseInt(parts[2].trim());
            TransactionType type = TransactionType.TRANSFER;
            try {
                type = TransactionType.valueOf(parts[3].trim());
            } catch (Exception ignored) {}
            double amount = Double.parseDouble(parts[4].trim());
            double balanceAfter = Double.parseDouble(parts[5].trim());
            String status = parts[6].trim();
            int fromAcc = parts.length > 7 && !parts[7].trim().isEmpty() ? Integer.parseInt(parts[7].trim()) : 0;
            int toAcc = parts.length > 8 && !parts[8].trim().isEmpty() ? Integer.parseInt(parts[8].trim()) : 0;
            String desc = parts.length > 9 ? unescapeCsv(parts[9].trim()) : "";

            return new Transaction(id, ts, accNo, type, amount, balanceAfter, status, desc, fromAcc, toAcc);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse transaction CSV row: " + line, e);
        }
    }

    private static String escapeCsv(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }

    private static String unescapeCsv(String val) {
        if (val == null) return "";
        if (val.startsWith("\"") && val.endsWith("\"") && val.length() >= 2) {
            val = val.substring(1, val.length() - 1).replace("\"\"", "\"");
        }
        return val;
    }
}
