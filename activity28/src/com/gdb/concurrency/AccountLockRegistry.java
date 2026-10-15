package com.gdb.concurrency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Registry providing fine-grained per-account lock instances
 * to allow concurrent operations on distinct accounts.
 */
public class AccountLockRegistry {

    // ============================================================
    // 📝 STEP 1: Declare Concurrent Lock Maps
    //
    // INSTRUCTIONS:
    //   1. Declare private final ConcurrentHashMap<Integer, ReentrantLock> accountLocks.
    //   2. Declare private final ConcurrentHashMap<Integer, ReentrantReadWriteLock> rwLocks.
    // ============================================================
    // TODO: Step 1 - Initialize lock maps

    public AccountLockRegistry() {
        // TODO: Step 1 - Instantiate maps
    }

    // ============================================================
    // 📝 STEP 2: Implement lockFor(int accountNumber)
    //
    // INSTRUCTIONS:
    //   Return the ReentrantLock for the given account number.
    //   Use computeIfAbsent to atomically create a new ReentrantLock(true) if absent.
    // ============================================================
    public ReentrantLock lockFor(int accountNumber) {
        // TODO: Step 2 - Return per-account lock
        return null;
    }

    // ============================================================
    // 📝 STEP 3: Implement rwLockFor(int accountNumber)
    //
    // INSTRUCTIONS:
    //   Return the ReentrantReadWriteLock for the given account number.
    //   Use computeIfAbsent to atomically create a new ReentrantReadWriteLock() if absent.
    // ============================================================
    public ReentrantReadWriteLock rwLockFor(int accountNumber) {
        // TODO: Step 3 - Return per-account ReadWriteLock
        return null;
    }

    public AccountLock acquire(int accountNumber) {
        ReentrantLock lock = lockFor(accountNumber);
        lock.lock();
        return new AccountLock(lock);
    }
}
