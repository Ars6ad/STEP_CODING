package com.gdb.concurrency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Registry providing fine-grained per-account lock instances
 * to allow concurrent operations on distinct accounts.
 */
public class AccountLockRegistry {

    private final ConcurrentHashMap<Integer, ReentrantLock> accountLocks;
    private final ConcurrentHashMap<Integer, ReentrantReadWriteLock> rwLocks;

    public AccountLockRegistry() {
        this.accountLocks = new ConcurrentHashMap<>();
        this.rwLocks = new ConcurrentHashMap<>();
    }

    public ReentrantLock lockFor(int accountNumber) {
        return accountLocks.computeIfAbsent(accountNumber, k -> new ReentrantLock(true));
    }

    public ReentrantReadWriteLock rwLockFor(int accountNumber) {
        return rwLocks.computeIfAbsent(accountNumber, k -> new ReentrantReadWriteLock(true));
    }

    public AccountLock acquire(int accountNumber) {
        ReentrantLock lock = lockFor(accountNumber);
        lock.lock();
        return new AccountLock(lock);
    }
}
