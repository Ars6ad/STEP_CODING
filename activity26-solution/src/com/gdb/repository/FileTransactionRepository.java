package com.gdb.repository;

import com.gdb.domain.Account;
import com.gdb.domain.IAccount;
import com.gdb.domain.Transaction;
import com.gdb.domain.TransactionType;
import com.gdb.exceptions.DataAccessException;
import com.gdb.file.AtomicFileWriter;
import com.gdb.file.BackupManager;
import com.gdb.file.CsvMapper;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FileTransactionRepository implements TransactionRepository {

    private final String filePath;
    private final boolean backupEnabled;
    private final int maxBackups;

    public FileTransactionRepository(String filePath, boolean backupEnabled, int maxBackups) {
        this.filePath = filePath;
        this.backupEnabled = backupEnabled;
        this.maxBackups = maxBackups;
        initFile();
    }

    public FileTransactionRepository(String filePath, int maxBackups) {
        this(filePath, true, maxBackups);
    }

    private void initFile() {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                if (path.getParent() != null) {
                    Files.createDirectories(path.getParent());
                }
                Files.write(path, Collections.singletonList(CsvMapper.TRANSACTION_HEADER));
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to initialize transaction file storage: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized void save(Transaction transaction) {
        if (transaction == null) return;
        List<Transaction> list = findAll();
        list.add(transaction);
        writeAll(list);
    }

    @Override
    public synchronized List<Transaction> findByAccount(int accountNumber) {
        return findAll().stream()
                .filter(t -> t.getAccountNumber() == accountNumber || 
                             t.getFromAccount() == accountNumber || 
                             t.getToAccount() == accountNumber)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) return list;
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                Transaction txn = CsvMapper.fromCsvTransaction(line);
                if (txn != null) {
                    list.add(txn);
                }
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to read transactions from file: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public synchronized void clear() {
        try {
            Path path = Paths.get(filePath);
            Files.write(path, Collections.singletonList(CsvMapper.TRANSACTION_HEADER));
        } catch (IOException e) {
            throw new DataAccessException("Failed to clear transactions file: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized void transfer(int fromAccountNumber, int toAccountNumber, double amount) {
        File txnFile = new File(filePath);
        File accFile = new File(txnFile.getParentFile(), "accounts.csv");
        if (accFile.exists()) {
            try {
                List<String> lines = Files.readAllLines(accFile.toPath());
                List<String> updatedLines = new ArrayList<>();
                for (String line : lines) {
                    IAccount acc = CsvMapper.fromCsv(line);
                    if (acc != null) {
                        if (acc.getAccountNumber() == fromAccountNumber) {
                            Account a = (Account) acc;
                            if (a.hasPin()) {
                                a.withdraw(amount, 1234);
                            } else {
                                a.setPin(1234);
                                a.withdraw(amount, 1234);
                            }
                            updatedLines.add(CsvMapper.toCsv(a));
                        } else if (acc.getAccountNumber() == toAccountNumber) {
                            acc.deposit(amount);
                            updatedLines.add(CsvMapper.toCsv(acc));
                        } else {
                            updatedLines.add(line);
                        }
                    } else {
                        updatedLines.add(line);
                    }
                }
                AtomicFileWriter.write(accFile.getAbsolutePath(), updatedLines);
            } catch (Exception e) {
                throw new DataAccessException("Failed to perform file transfer on accounts: " + e.getMessage(), e);
            }
        }
        Transaction txn = new Transaction(
                Transaction.generateId(),
                LocalDateTime.now(),
                fromAccountNumber,
                TransactionType.TRANSFER,
                amount,
                0.0,
                "SUCCESS",
                "File Transfer to " + toAccountNumber,
                fromAccountNumber,
                toAccountNumber
        );
        save(txn);
    }

    private void writeAll(List<Transaction> transactions) {
        try {
            if (backupEnabled) {
                BackupManager.backup(filePath, maxBackups);
            }
            List<String> lines = new ArrayList<>();
            lines.add(CsvMapper.TRANSACTION_HEADER);
            for (Transaction txn : transactions) {
                lines.add(CsvMapper.toCsv(txn));
            }
            AtomicFileWriter.write(filePath, lines);
        } catch (IOException e) {
            throw new DataAccessException("Failed to write transactions to file: " + e.getMessage(), e);
        }
    }
}
