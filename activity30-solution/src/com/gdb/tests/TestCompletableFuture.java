package com.gdb.tests;

import com.gdb.concurrency.AsyncUtils;
import com.gdb.domain.Account;
import com.gdb.domain.AccountFactory;
import com.gdb.domain.IAccount;
import com.gdb.domain.Transaction;
import com.gdb.repository.AccountRepository;
import com.gdb.repository.InMemoryAccountRepository;
import com.gdb.repository.InMemoryTransactionRepository;
import com.gdb.repository.TransactionRepository;
import com.gdb.service.AsyncAccountService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class TestCompletableFuture {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  ACTIVITY 30 — ASYNC PROGRAMMING (COMPLETABLEFUTURE)       ");
        System.out.println("============================================================");

        try {
            testAsyncChainedPipeline();
            testAsyncExceptionRecovery();
            testFanOutAllOfBatchProcessing();
            testAsyncTimeoutGuard();

            System.out.println("\n============================================================");
            System.out.println("  ALL ACTIVITY 30 COMPLETABLEFUTURE TESTS PASSED!");
            System.out.println("============================================================");
        } catch (Throwable t) {
            System.err.println("\n❌ TEST SUITE FAILED: " + t.getMessage());
            t.printStackTrace();
            System.exit(1);
        }
    }

    private static void testAsyncChainedPipeline() throws Exception {
        System.out.println("\n[TEST 1] Async Pipeline: Open -> Deposit -> Withdraw -> Balance:");

        AccountRepository accountRepo = new InMemoryAccountRepository();
        TransactionRepository txnRepo = new InMemoryTransactionRepository();
        AsyncAccountService service = new AsyncAccountService(accountRepo, txnRepo, 4);

        // Chained non-blocking pipeline:
        // Open with 5000 -> Deposit 2000 (7000) -> Withdraw 1500 (5500) -> Balance (5500.0)
        CompletableFuture<Double> pipeline = service.openAccountAsync("Savings", 6001, "Pipeline User", 28, 5000.0)
                .thenApply(acc -> {
                    try { acc.setPin(1234); } catch (Exception ignored) {}
                    service.saveAccount(acc);
                    return acc;
                })
                .thenCompose(acc -> service.executePipelineAsync(6001, 2000.0, 1500.0, 1234));

        Double finalBalance = pipeline.get(5, TimeUnit.SECONDS);
        System.out.printf("  Final Balance after async pipeline: Rs. %.2f (Expected: Rs. 5500.00)%n", finalBalance);

        assert Math.abs(finalBalance - 5500.0) < 0.001 : "Pipeline result mismatch!";
        service.shutdown();
        System.out.println("  -> PASSED");
    }

    private static void testAsyncExceptionRecovery() throws Exception {
        System.out.println("\n[TEST 2] Resilient Error Handling via exceptionally():");

        AccountRepository accountRepo = new InMemoryAccountRepository();
        TransactionRepository txnRepo = new InMemoryTransactionRepository();
        AsyncAccountService service = new AsyncAccountService(accountRepo, txnRepo, 4);

        IAccount acc = AccountFactory.createAccount("Savings", 6002, "Error User", 30, 1000.0);
        acc.setPin(1234);
        service.saveAccount(acc);

        // Attempt invalid withdrawal exceeding balance (Rs. 50,000 > Rs. 1,000)
        CompletableFuture<String> resilientFlow = service.withdrawAsync(6002, 50000.0, 1234)
                .thenApply(txn -> "SUCCESS: " + txn.getTransactionId())
                .exceptionally(ex -> "RECOVERED_FALLBACK: " + ex.getCause().getMessage());

        String result = resilientFlow.get(5, TimeUnit.SECONDS);
        System.out.println("  Async exception result: " + result);

        assert result.startsWith("RECOVERED_FALLBACK") : "Async pipeline did not catch and recover from exception!";
        service.shutdown();
        System.out.println("  -> PASSED");
    }

    private static void testFanOutAllOfBatchProcessing() throws Exception {
        System.out.println("\n[TEST 3] Fan-Out & Aggregate via CompletableFuture.allOf():");

        AccountRepository accountRepo = new InMemoryAccountRepository();
        TransactionRepository txnRepo = new InMemoryTransactionRepository();
        AsyncAccountService service = new AsyncAccountService(accountRepo, txnRepo, 8);

        List<Integer> accNos = Arrays.asList(6101, 6102, 6103, 6104, 6105);
        for (int accNo : accNos) {
            IAccount a = AccountFactory.createAccount("Savings", accNo, "FanOut " + accNo, 25, 2000.0);
            a.setPin(1234);
            service.saveAccount(a);
        }

        // Fan-out: Deposit Rs. 500 into all 5 accounts concurrently
        CompletableFuture<List<Transaction>> allDeposits = service.depositAllAsync(accNos, 500.0);
        List<Transaction> transactions = allDeposits.get(5, TimeUnit.SECONDS);

        System.out.printf("  Total completed transactions: %d (Expected: 5)%n", transactions.size());
        assert transactions.size() == 5 : "allOf did not complete all transactions!";

        for (int accNo : accNos) {
            double bal = service.getBalanceAsync(accNo, 1234).get(5, TimeUnit.SECONDS);
            assert Math.abs(bal - 2500.0) < 0.001 : "Account " + accNo + " balance mismatch! Expected: 2500.0, Got: " + bal;
        }

        service.shutdown();
        System.out.println("  All accounts updated concurrently via allOf fan-out.");
        System.out.println("  -> PASSED");
    }

    private static void testAsyncTimeoutGuard() throws Exception {
        System.out.println("\n[TEST 4] Timeout Guarding with completeOnTimeout():");

        CompletableFuture<String> slowFuture = new CompletableFuture<>();
        CompletableFuture<String> guarded = AsyncUtils.withTimeout(slowFuture, 200, TimeUnit.MILLISECONDS, "DEFAULT_TIMEOUT_VALUE");

        String res = guarded.get(2, TimeUnit.SECONDS);
        System.out.println("  Guarded future returned: " + res);

        assert "DEFAULT_TIMEOUT_VALUE".equals(res) : "Timeout guard did not return fallback value!";
        System.out.println("  -> PASSED");
    }
}
