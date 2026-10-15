package com.gdb.repository;

import com.gdb.domain.IAccount;
import com.gdb.exceptions.DataAccessException;
import com.gdb.file.AtomicFileWriter;
import com.gdb.file.BackupManager;
import com.gdb.file.CsvMapper;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileAccountRepository implements AccountRepository {

    private final String filePath;
    private final boolean backupEnabled;
    private final int maxBackups;

    public FileAccountRepository(String filePath, boolean backupEnabled, int maxBackups) {
        this.filePath = filePath;
        this.backupEnabled = backupEnabled;
        this.maxBackups = maxBackups;
        initFile();
    }

    public FileAccountRepository(String filePath, int maxBackups) {
        this(filePath, true, maxBackups);
    }

    private void initFile() {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                if (path.getParent() != null) {
                    Files.createDirectories(path.getParent());
                }
                Files.write(path, Collections.singletonList(CsvMapper.ACCOUNT_HEADER));
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to initialize account file storage: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized void save(IAccount account) {
        if (account == null) return;
        List<IAccount> accounts = findAll();
        accounts.removeIf(a -> a.getAccountNumber() == account.getAccountNumber());
        accounts.add(account);
        writeAll(accounts);
    }

    @Override
    public synchronized IAccount findById(int accountNumber) {
        return findAll().stream()
                .filter(a -> a.getAccountNumber() == accountNumber)
                .findFirst()
                .orElse(null);
    }

    @Override
    public synchronized List<IAccount> findAll() {
        List<IAccount> accounts = new ArrayList<>();
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) return accounts;
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                IAccount acc = CsvMapper.fromCsv(line);
                if (acc != null) {
                    accounts.add(acc);
                }
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to read accounts from file: " + e.getMessage(), e);
        }
        return accounts;
    }

    @Override
    public synchronized void update(IAccount account) {
        save(account);
    }

    @Override
    public synchronized void delete(int accountNumber) {
        List<IAccount> accounts = findAll();
        boolean removed = accounts.removeIf(a -> a.getAccountNumber() == accountNumber);
        if (removed) {
            writeAll(accounts);
        }
    }

    @Override
    public synchronized boolean exists(int accountNumber) {
        return findById(accountNumber) != null;
    }

    @Override
    public synchronized int nextAccountNumber() {
        return findAll().stream()
                .mapToInt(IAccount::getAccountNumber)
                .max()
                .orElse(1000) + 1;
    }

    private void writeAll(List<IAccount> accounts) {
        try {
            if (backupEnabled) {
                BackupManager.backup(filePath, maxBackups);
            }
            List<String> lines = new ArrayList<>();
            lines.add(CsvMapper.ACCOUNT_HEADER);
            for (IAccount acc : accounts) {
                lines.add(CsvMapper.toCsv(acc));
            }
            AtomicFileWriter.write(filePath, lines);
        } catch (IOException e) {
            throw new DataAccessException("Failed to write accounts to file: " + e.getMessage(), e);
        }
    }
}
