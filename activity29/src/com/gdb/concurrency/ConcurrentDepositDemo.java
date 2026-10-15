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

    public static double runUnsafeDemo(IAccount account, int numThreads, double depositAmount, int iterations) {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    try {
                        // Unsynchronized read-modify-write with interleaving
                        double current = account.getBalance();
                        Thread.sleep(1);
                        account.deposit(depositAmount);
                    } catch (Exception ignored) {
                    }
                }
            });
            threads.add(t);
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return account.getBalance();
    }

    public static double runSafeDemo(ThreadSafeAccountService service, int accountNumber,
                                     int numThreads, double depositAmount, int iterations) throws Exception {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    try {
                        service.deposit(accountNumber, depositAmount);
                    } catch (Exception e) {
                        System.err.println("Deposit failed: " + e.getMessage());
                    }
                }
            });
            threads.add(t);
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return service.getAccount(accountNumber).getBalance();
    }
}
