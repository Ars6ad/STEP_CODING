package com.gdb.tests;

import com.gdb.domain.AccountFactory;
import com.gdb.domain.IAccount;
import com.gdb.repository.AccountRepository;
import com.gdb.repository.InMemoryAccountRepository;
import com.gdb.repository.InMemoryTransactionRepository;
import com.gdb.repository.TransactionRepository;
import com.gdb.service.AtomicAccountService;
import com.gdb.service.LockedAccountService;

import java.util.ArrayList;
import java.util.List;

public class TestLocksAndAtomics {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  ACTIVITY 28 — LOCKS, ATOMICS & CONCURRENT COLLECTIONS     ");
        System.out.println("============================================================");

        try {
            testConcurrentDepositsSameAccount();
            testConcurrentDepositsDifferentAccountsParallel();
            testDeadlockFreeBidirectionalTransfers();
            testAtomicServiceCountersAndCas();

            System.out.println("\n============================================================");
            System.out.println("  ALL ACTIVITY 28 LOCKS & ATOMICS TESTS PASSED!");
            System.out.println("============================================================");
        } catch (Throwable t) {
            System.err.println("\n❌ TEST SUITE FAILED: " + t.getMessage());
            t.printStackTrace();
            System.exit(1);
        }
    }

    private static void testConcurrentDepositsSameAccount() throws Exception {
        System.out.println("\n[TEST 1] Fine-Grained Locking - Concurrent Deposits to Same Account:");

        AccountRepository accountRepo = new InMemoryAccountRepository();
        TransactionRepository txnRepo = new InMemoryTransactionRepository();
        LockedAccountService service = new LockedAccountService(accountRepo, txnRepo);

        IAccount acc = AccountFactory.createAccount("Savings", 8001, "Lock Holder", 30, 5000.0);
        acc.setPin(1234);
        service.saveAccount(acc);

        int numThreads = 10;
        int iterations = 20;
        double depositAmount = 100.0;
        double expectedFinal = 5000.0 + (numThreads * iterations * depositAmount); // 5000 + 20000 = 25000.0

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    try {
                        service.deposit(8001, depositAmount);
                    } catch (Exception ignored) {}
                }
            });
            threads.add(t);
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        double finalBalance = service.getBalance(8001, 1234);
        System.out.printf("  Final Balance: Rs. %.2f (Expected: Rs. %.2f)%n", finalBalance, expectedFinal);
        assert Math.abs(finalBalance - expectedFinal) < 0.001 : "Final balance mismatch!";
        System.out.println("  -> PASSED");
    }

    private static void testConcurrentDepositsDifferentAccountsParallel() throws Exception {
        System.out.println("\n[TEST 2] Fine-Grained Locking - Distinct Accounts Run In Parallel:");

        AccountRepository accountRepo = new InMemoryAccountRepository();
        TransactionRepository txnRepo = new InMemoryTransactionRepository();
        LockedAccountService service = new LockedAccountService(accountRepo, txnRepo);

        int numAccounts = 5;
        for (int i = 1; i <= numAccounts; i++) {
            int accNo = 8100 + i;
            IAccount acc = AccountFactory.createAccount("Savings", accNo, "User " + i, 25, 1000.0);
            acc.setPin(1000 + i);
            service.saveAccount(acc);
        }

        List<Thread> threads = new ArrayList<>();
        for (int i = 1; i <= numAccounts; i++) {
            final int accNo = 8100 + i;
            Thread t = new Thread(() -> {
                for (int j = 0; j < 50; j++) {
                    try {
                        service.deposit(accNo, 10.0);
                    } catch (Exception ignored) {}
                }
            });
            threads.add(t);
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        for (int i = 1; i <= numAccounts; i++) {
            int accNo = 8100 + i;
            double bal = service.getBalance(accNo, 1000 + i);
            double expected = 1000.0 + (50 * 10.0); // 1500.0
            assert Math.abs(bal - expected) < 0.001 : "Account " + accNo + " balance mismatch!";
        }
        System.out.println("  All 5 independent accounts updated simultaneously without interference.");
        System.out.println("  -> PASSED");
    }

    private static void testDeadlockFreeBidirectionalTransfers() throws Exception {
        System.out.println("\n[TEST 3] Lock Ordering - Deadlock-Free Bidirectional Transfers:");

        AccountRepository accountRepo = new InMemoryAccountRepository();
        TransactionRepository txnRepo = new InMemoryTransactionRepository();
        LockedAccountService service = new LockedAccountService(accountRepo, txnRepo);

        IAccount accA = AccountFactory.createAccount("Savings", 8201, "Alice Lock", 28, 50000.0);
        IAccount accB = AccountFactory.createAccount("Savings", 8202, "Bob Lock", 32, 50000.0);
        accA.setPin(1111);
        accB.setPin(2222);
        service.saveAccount(accA);
        service.saveAccount(accB);

        double totalFundsBefore = service.getBalance(8201, 1111) + service.getBalance(8202, 2222);

        int numThreads = 20;
        int iterations = 100;
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            Thread t = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    try {
                        if (index % 2 == 0) {
                            // Thread A -> B
                            service.transfer(8201, 8202, 25.0, 1111);
                        } else {
                            // Thread B -> A
                            service.transfer(8202, 8201, 25.0, 2222);
                        }
                    } catch (Exception ignored) {}
                }
            });
            threads.add(t);
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        double totalFundsAfter = service.getBalance(8201, 1111) + service.getBalance(8202, 2222);
        System.out.printf("  Total Funds Before: Rs. %.2f | After: Rs. %.2f%n", totalFundsBefore, totalFundsAfter);
        assert Math.abs(totalFundsBefore - totalFundsAfter) < 0.001 : "Funds mismatch after transfers!";
        System.out.println("  No deadlock encountered across 2000 bidirectional transfer attempts.");
        System.out.println("  -> PASSED");
    }

    private static void testAtomicServiceCountersAndCas() throws Exception {
        System.out.println("\n[TEST 4] Lock-Free Atomic Primitives & CAS Status Updates:");

        AtomicAccountService atomicService = new AtomicAccountService();
        int numThreads = 20;
        int perThreadCount = 100;
        double depositAmount = 50.0;

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < perThreadCount; j++) {
                    atomicService.nextTransactionId();
                    atomicService.recordDepositVolume(depositAmount);
                }
            });
            threads.add(t);
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        long totalTxns = atomicService.getGlobalTransactionCount();
        double totalVolume = atomicService.getTotalDepositVolume();
        long expectedTxns = (long) numThreads * perThreadCount;
        double expectedVolume = expectedTxns * depositAmount;

        System.out.printf("  Total Txns: %d (Expected: %d)%n", totalTxns, expectedTxns);
        System.out.printf("  Total Volume: Rs. %.2f (Expected: Rs. %.2f)%n", totalVolume, expectedVolume);

        assert totalTxns == expectedTxns : "Atomic transaction counter mismatch!";
        assert Math.abs(totalVolume - expectedVolume) < 0.001 : "Atomic deposit volume mismatch!";

        // Test CAS State Transition
        boolean updated = atomicService.updateSystemStatus("ONLINE", "MAINTENANCE");
        assert updated : "CAS transition ONLINE -> MAINTENANCE failed!";
        assert "MAINTENANCE".equals(atomicService.getSystemStatus());

        boolean invalidUpdate = atomicService.updateSystemStatus("ONLINE", "SHUTDOWN");
        assert !invalidUpdate : "CAS transition should fail with stale expected state!";
        assert "MAINTENANCE".equals(atomicService.getSystemStatus());

        System.out.println("  CAS status transitions verified.");
        System.out.println("  -> PASSED");
    }
}
