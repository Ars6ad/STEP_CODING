package com.gdb.repository;

import com.gdb.domain.IAccount;
import java.util.*;

public class InMemoryAccountRepository implements AccountRepository {

    private final Map<Integer, IAccount> storage = new HashMap<>();
    private int nextAccountNumber = 1001;

    public InMemoryAccountRepository() {
    }

    @Override
    public synchronized void save(IAccount account) {
        if (account != null) {
            storage.put(account.getAccountNumber(), account);
        }
    }

    @Override
    public synchronized IAccount findById(int accountNumber) {
        return storage.get(accountNumber);
    }

    @Override
    public synchronized List<IAccount> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public synchronized void update(IAccount account) {
        if (account != null && storage.containsKey(account.getAccountNumber())) {
            storage.put(account.getAccountNumber(), account);
        }
    }

    @Override
    public synchronized void delete(int accountNumber) {
        storage.remove(accountNumber);
    }

    @Override
    public synchronized boolean exists(int accountNumber) {
        return storage.containsKey(accountNumber);
    }

    @Override
    public synchronized int nextAccountNumber() {
        return nextAccountNumber++;
    }
}
