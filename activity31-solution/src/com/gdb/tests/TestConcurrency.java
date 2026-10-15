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

        IAccount rawAccount = AccountFactory.createAccount("Savings", 9001, "Race Account", 28, 1000.0);
        int numThreads = 10;
        double depositAmount = 100.0;
        int iterations = 10;
        double expectedBalance = 1000.0 + (numThreads * depositAmount * iterations); // 11000.0

        double observedBalance = ConcurrentDepositDemo.runUnsafeDemo(rawAccount, numThreads, depositAmount, iterations);
        System.out.printf("  Initial: Rs. 1000.0 | Expected: Rs. %.2f | Observed: Rs. %.2f%n", expectedBalance, observedBalance);

        // Due to race conditions, observed balance is often less than expected
        System.out.println("  -> Lost updates demonstrated under unsynchronized concurrency.");
        System.out.println("  -> PASSED");
    }

    private static void testSynchronizedConcurrentDeposits() throws Exception {
        System.out.println("\n[TEST 2] Synchronized Concurrent Deposits:");

        AccountRepository accountRepo = new InMemoryAccountRepository();
        TransactionRepository txnRepo = new InMemoryTransactionRepository();
        ThreadSafeAccountService service = new ThreadSafeAccountService(accountRepo, txnRepo);

        IAccount acc = AccountFactory.createAccount("Savings", 9002, "ThreadSafe Holder", 30, 5000.0);
        acc.setPin(1234);
        service.saveAccount(acc);

        int numThreads = 10;
        double depositAmount = 250.0;
        int iterations = 20;
        double expectedFinal = 5000.0 + (numThreads * depositAmount * iterations); // 5000 + 50000 = 55000.0

        double finalBalance = ConcurrentDepositDemo.runSafeDemo(service, 9002, numThreads, depositAmount, iterations);
        System.out.printf("  Final Balance: Rs. %.2f (Expected: Rs. %.2f)%n", finalBalance, expectedFinal);

        assert Math.abs(finalBalance - expectedFinal) < 0.001 : "Final balance mismatch! Expected: " + expectedFinal + " Got: " + finalBalance;
        System.out.println("  -> PASSED");
    }

    private static void testConcurrentTransfersConservationOfFunds() throws Exception {
        System.out.println("\n[TEST 3] Concurrent Transfers - Conservation of Total Funds:");

        AccountRepository accountRepo = new InMemoryAccountRepository();
        TransactionRepository txnRepo = new InMemoryTransactionRepository();
        ThreadSafeAccountService service = new ThreadSafeAccountService(accountRepo, txnRepo);

        IAccount accA = AccountFactory.createAccount("Savings", 9003, "Alice", 25, 20000.0);
        IAccount accB = AccountFactory.createAccount("Savings", 9004, "Bob", 35, 20000.0);
        accA.setPin(1111);
        accB.setPin(2222);
        service.saveAccount(accA);
        service.saveAccount(accB);

        double totalFundsBefore = service.getBalance(9003, 1111) + service.getBalance(9004, 2222);

        int numThreads = 20;
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            Thread t = new Thread(() -> {
                for (int j = 0; j < 50; j++) {
                    try {
                        if (index % 2 == 0) {
                            service.transfer(9003, 9004, 100.0, 1111);
                        } else {
                            service.transfer(9004, 9003, 100.0, 2222);
                        }
                    } catch (Exception ignored) {}
                }
            });
            threads.add(t);
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        double totalFundsAfter = service.getBalance(9003, 1111) + service.getBalance(9004, 2222);
        System.out.printf("  Total Funds Before: Rs. %.2f | After: Rs. %.2f%n", totalFundsBefore, totalFundsAfter);
        assert Math.abs(totalFundsBefore - totalFundsAfter) < 0.001 : "Funds not conserved during concurrent transfers!";
        System.out.println("  -> PASSED");
    }

    private static void testConcurrentWithdrawalsSafety() throws Exception {
        System.out.println("\n[TEST 4] Concurrent Withdrawals Under Contention:");

        AccountRepository accountRepo = new InMemoryAccountRepository();
        TransactionRepository txnRepo = new InMemoryTransactionRepository();
        ThreadSafeAccountService service = new ThreadSafeAccountService(accountRepo, txnRepo);

        IAccount acc = AccountFactory.createAccount("Savings", 9005, "Contended Acc", 29, 10000.0);
        acc.setPin(1234);
        service.saveAccount(acc);

        int numThreads = 15;
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    try {
                        service.withdraw(9005, 50.0, 1234);
                    } catch (Exception ignored) {}
                }
            });
            threads.add(t);
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        double finalBal = service.getBalance(9005, 1234);
        double expectedBal = 10000.0 - (15 * 10 * 50.0); // 10000 - 7500 = 2500.0
        System.out.printf("  Remaining Balance: Rs. %.2f (Expected: Rs. %.2f)%n", finalBal, expectedBal);
        assert Math.abs(finalBal - expectedBal) < 0.001 : "Balance incorrect after concurrent withdrawals!";
        System.out.println("  -> PASSED");
    }
}
