package com.gdb.concurrency;

import java.util.concurrent.locks.ReentrantLock;

/**
 * AutoCloseable helper managing try-with-resources lock release.
 */
public class AccountLock implements AutoCloseable {

    private final ReentrantLock lock;

    public AccountLock(ReentrantLock lock) {
        this.lock = lock;
    }

    @Override
    public void close() {
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
