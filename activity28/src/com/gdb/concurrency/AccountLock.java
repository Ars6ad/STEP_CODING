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

    // ============================================================
    // 📝 STEP 4: Implement close()
    //
    // INSTRUCTIONS:
    //   If lock is non-null and held by current thread, unlock it.
    // ============================================================
    @Override
    public void close() {
        // TODO: Step 4 - unlock the lock
    }
}
