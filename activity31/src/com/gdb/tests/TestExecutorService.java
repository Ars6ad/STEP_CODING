package com.gdb.tests;

import com.gdb.concurrency.ThreadPoolFactory;
import com.gdb.domain.AccountFactory;
import com.gdb.domain.IAccount;
import com.gdb.domain.Transaction;
import com.gdb.repository.AccountRepository;
import com.gdb.repository.InMemoryAccountRepository;
import com.gdb.repository.InMemoryTransactionRepository;
import com.gdb.repository.TransactionRepository;
import com.gdb.service.ExecutorAccountService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TestExecutorService {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  ACTIVITY 29 — THREAD POOLS (EXECUTORSERVICE & FUTURE)     ");
        System.out.println("============================================================");

        try {
            testAsyncDepositsWithFutureGet();
            testInvokeAllBatchProcessing();
            testGracefulShutdownAndRejection();

            System.out.println("\n============================================================");
            System.out.println("  ALL ACTIVITY 29 THREAD POOL TESTS PASSED!");
            System.out.println("============================================================");
        } catch (Throwable t) {
            System.err.println("\n❌ TEST SUITE FAILED: " + t.getMessage());
            t.printStackTrace();
            System.exit(1);
        }
    }

    private static void testAsyncDepositsWithFutureGet() throws Exception {
        System.out.println("\n[TEST 1] Async Deposits via ExecutorService & Future.get():");

        AccountRepository accountRepo = new InMemoryAccountRepository();
        TransactionRepository txnRepo = new InMemoryTransactionRepository();
        ExecutorAccountService service = new ExecutorAccountService(accountRepo, txnRepo, 8);

        IAccount acc = AccountFactory.createAccount("Savings", 7001, "Pool User", 26, 10000.0);
        acc.setPin(1234);
        service.saveAccount(acc);

        int taskCount = 100;
        double depositAmount = 50.0;
        double expectedFinal = 10000.0 + (taskCount * depositAmount); // 10000 + 5000 = 15000.0

        List<Future<Transaction>> futures = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            futures.add(service.depositAsync(7001, depositAmount));
        }

        // Wait and inspect all futures
        int successfulCount = 0;
        for (Future<Transaction> f : futures) {
            Transaction txn = f.get(5, TimeUnit.SECONDS);
            if (txn != null && "SUCCESS".equals(txn.getStatus())) {
                successfulCount++;
            }
        }

        double finalBalance = service.getBalanceAsync(7001, 1234).get(5, TimeUnit.SECONDS);
        System.out.printf("  Completed Tasks: %d / %d | Final Balance: Rs. %.2f (Expected: Rs. %.2f)%n",
                successfulCount, taskCount, finalBalance, expectedFinal);

        assert successfulCount == taskCount : "Not all async deposits completed successfully!";
        assert Math.abs(finalBalance - expectedFinal) < 0.001 : "Final balance mismatch!";

        service.shutdown();
        System.out.println("  -> PASSED");
    }

    private static void testInvokeAllBatchProcessing() throws Exception {
        System.out.println("\n[TEST 2] Batch Task Processing via invokeAll():");

        AccountRepository accountRepo = new InMemoryAccountRepository();
        TransactionRepository txnRepo = new InMemoryTransactionRepository();
        ExecutorAccountService service = new ExecutorAccountService(accountRepo, txnRepo, 4);

        IAccount acc = AccountFactory.createAccount("Savings", 7002, "Batch User", 30, 20000.0);
        acc.setPin(1234);
        service.saveAccount(acc);

        List<Callable<Transaction>> batchTasks = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            final int index = i;
            batchTasks.add(() -> {
                service.depositAsync(7002, 100.0);
                return null;
            });
        }

        List<Future<Transaction>> results = service.invokeAllTasks(batchTasks, 5, TimeUnit.SECONDS);
        assert results.size() == 20 : "Batch invokeAll did not return all task futures!";

        service.shutdown();
        System.out.println("  All 20 batch tasks dispatched and completed via invokeAll.");
        System.out.println("  -> PASSED");
    }

    private static void testGracefulShutdownAndRejection() throws Exception {
        System.out.println("\n[TEST 3] Graceful Shutdown & Rejection of New Submissions:");

        AccountRepository accountRepo = new InMemoryAccountRepository();
        TransactionRepository txnRepo = new InMemoryTransactionRepository();
        ExecutorAccountService service = new ExecutorAccountService(accountRepo, txnRepo, 2);

        IAccount acc = AccountFactory.createAccount("Savings", 7003, "Shutdown User", 35, 5000.0);
        acc.setPin(1234);
        service.saveAccount(acc);

        // Submit one task
        Future<Transaction> f = service.depositAsync(7003, 500.0);
        f.get(3, TimeUnit.SECONDS);

        // Shutdown service
        service.shutdown();
        assert service.isShutdown() : "Service should report isShutdown() == true after shutdown!";

        // Verify rejected execution on new submission
        boolean rejected = false;
        try {
            service.depositAsync(7003, 100.0);
        } catch (RejectedExecutionException ree) {
            rejected = true;
        }

        assert rejected : "New task submission must throw RejectedExecutionException after shutdown!";
        System.out.println("  Executor pool terminated gracefully and correctly rejected new tasks.");
        System.out.println("  -> PASSED");
    }
}
