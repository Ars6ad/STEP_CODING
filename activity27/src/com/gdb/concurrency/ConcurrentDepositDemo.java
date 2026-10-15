package com.gdb.concurrency;

import com.gdb.domain.IAccount;
import com.gdb.service.ThreadSafeAccountService;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates the lost-update race condition when multiple threads
 * concurrently mutate an account without synchronization vs with synchronization.
 */
public class ConcurrentDepositDemo {

    // ============================================================
    // 📝 STEP 1: Implement runUnsafeDemo(...)
    //
    // INSTRUCTIONS:
    //   1. Create a list of Thread objects (size = numThreads).
    //   2. For each thread, create a Runnable that runs 'iterations' times.
    //   3. In each iteration, simulate an unsynchronized read-modify-write:
    //      - Read balance or call account.deposit(depositAmount) with a small sleep delay.
    //   4. Start all threads.
    //   5. Call join() on each thread to wait for completion.
    //   6. Return the final account balance.
    // ============================================================
    public static double runUnsafeDemo(IAccount account, int numThreads, double depositAmount, int iterations) {
        // TODO: Step 1 - Spawn threads with unsynchronized deposits & join
        return account.getBalance();
    }

    // ============================================================
    // 📝 STEP 2: Implement runSafeDemo(...)
    //
    // INSTRUCTIONS:
    //   1. Create a list of Thread objects (size = numThreads).
    //   2. For each thread, create a Runnable that runs 'iterations' times.
    //   3. In each iteration, invoke service.deposit(accountNumber, depositAmount).
    //   4. Start all threads.
    //   5. Call join() on each thread to wait for completion.
    //   6. Return the final account balance from service.
    // ============================================================
    public static double runSafeDemo(ThreadSafeAccountService service, int accountNumber,
                                     int numThreads, double depositAmount, int iterations) throws Exception {
        // TODO: Step 2 - Spawn threads with thread-safe service deposits & join
        return service.getAccount(accountNumber).getBalance();
    }
}
