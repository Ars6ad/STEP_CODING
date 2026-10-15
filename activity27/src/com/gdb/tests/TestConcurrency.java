package com.gdb.tests;

import com.gdb.concurrency.ConcurrentDepositDemo;
import com.gdb.domain.Account;
import com.gdb.domain.AccountFactory;
import com.gdb.domain.IAccount;
import com.gdb.repository.AccountRepository;
import com.gdb.repository.InMemoryAccountRepository;
import com.gdb.repository.InMemoryTransactionRepository;
import com.gdb.repository.TransactionRepository;
import com.gdb.service.ThreadSafeAccountService;

import java.util.ArrayList;
import java.util.List;

public class TestConcurrency {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  ACTIVITY 27 — THREADS, RACE CONDITIONS & SYNCHRONIZATION ");
        System.out.println("============================================================");

        try {
            testUnsynchronizedRaceCondition();
            testSynchronizedConcurrentDeposits();
            testConcurrentTransfersConservationOfFunds();
            testConcurrentWithdrawalsSafety();

            System.out.println("\n============================================================");
            System.out.println("  ALL ACTIVITY 27 CONCURRENCY TESTS PASSED!");
            System.out.println("============================================================");
        } catch (Throwable t) {
            System.err.println("\n❌ TEST SUITE FAILED: " + t.getMessage());
            t.printStackTrace();
            System.exit(1);
        }
    }

    private static void testUnsynchronizedRaceCondition() throws Exception {
        System.out.println("\n[TEST 1] Unsynchronized Race Condition Demonstration:");
        // TODO: Test unsynchronized race condition behavior
    }

    private static void testSynchronizedConcurrentDeposits() throws Exception {
        System.out.println("\n[TEST 2] Synchronized Concurrent Deposits:");
        // TODO: Test synchronized multi-threaded deposits
    }

    private static void testConcurrentTransfersConservationOfFunds() throws Exception {
        System.out.println("\n[TEST 3] Concurrent Transfers - Conservation of Total Funds:");
        // TODO: Test multi-threaded balance transfers
    }

    private static void testConcurrentWithdrawalsSafety() throws Exception {
        System.out.println("\n[TEST 4] Concurrent Withdrawals Under Contention:");
        // TODO: Test multi-threaded balance withdrawals
    }
}
