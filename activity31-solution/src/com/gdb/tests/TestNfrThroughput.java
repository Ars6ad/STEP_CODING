package com.gdb.tests;

import com.gdb.concurrency.ThroughputBenchmark;
import com.gdb.concurrency.ThroughputBenchmark.BenchmarkResult;
import com.gdb.domain.AccountFactory;
import com.gdb.domain.IAccount;
import com.gdb.domain.Transaction;
import com.gdb.repository.AccountRepository;
import com.gdb.repository.InMemoryAccountRepository;
import com.gdb.repository.InMemoryTransactionRepository;
import com.gdb.repository.TransactionRepository;
import com.gdb.service.AsyncThreadSafeAccountService;
import com.gdb.service.LockedAccountService;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

public class TestNfrThroughput {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  ACTIVITY 31 — THREAD-SAFE + ASYNC + NFR MEASUREMENT       ");
        System.out.println("============================================================");

        try {
            testAsyncThreadSafeServiceCorrectness();
            testThroughputBenchmarkComparison();
            testNfrSlaRequirements();

            System.out.println("\n============================================================");
            System.out.println("  ALL ACTIVITY 31 NFR BENCHMARK TESTS PASSED!");
            System.out.println("============================================================");
        } catch (Throwable t) {
            System.err.println("\n❌ TEST SUITE FAILED: " + t.getMessage());
            t.printStackTrace();
            System.exit(1);
        }
    }

    private static void testAsyncThreadSafeServiceCorrectness() throws Exception {
        System.out.println("\n[TEST 1] AsyncThreadSafeAccountService Correctness Verification:");

        AccountRepository accountRepo = new InMemoryAccountRepository();
        TransactionRepository txnRepo = new InMemoryTransactionRepository();
        Properties props = new Properties();
        props.setProperty("service.thread.pool.size", "8");
        props.setProperty("service.timeout.ms", "5000");

        AsyncThreadSafeAccountService service = new AsyncThreadSafeAccountService(accountRepo, txnRepo, props);

        IAccount accA = AccountFactory.createAccount("Savings", 5001, "User A", 25, 20000.0);
        IAccount accB = AccountFactory.createAccount("Savings", 5002, "User B", 30, 20000.0);
        accA.setPin(1111);
        accB.setPin(2222);
        service.saveAccount(accA);
        service.saveAccount(accB);

        // Perform concurrent deposits and transfers
        int count = 50;
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            futures.add(service.depositAsync(5001, 100.0));
            futures.add(service.transferAsync(5001, 5002, 50.0, 1111));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(10, TimeUnit.SECONDS);

        double balA = service.getBalanceAsync(5001, 1111).get(5, TimeUnit.SECONDS);
        double balB = service.getBalanceAsync(5002, 2222).get(5, TimeUnit.SECONDS);

        // Initial A (20000) + 50*100 (5000) - 50*50 (2500) = 22500.0
        // Initial B (20000) + 50*50 (2500) = 22500.0
        System.out.printf("  Account A Balance: Rs. %.2f (Expected: Rs. 22500.00)%n", balA);
        System.out.printf("  Account B Balance: Rs. %.2f (Expected: Rs. 22500.00)%n", balB);

        assert Math.abs(balA - 22500.0) < 0.001 : "Account A balance incorrect!";
        assert Math.abs(balB - 22500.0) < 0.001 : "Account B balance incorrect!";
        assert (balA + balB) == 45000.0 : "Total funds conservation violated!";

        service.shutdown();
        System.out.println("  -> PASSED");
    }

    private static void testThroughputBenchmarkComparison() throws Exception {
        System.out.println("\n[TEST 2] Synchronous vs Asynchronous Throughput Benchmark:");

        int totalOps = 500;
        int numAccounts = 10;

        // 1. Benchmark Synchronous Service
        AccountRepository syncRepo = new InMemoryAccountRepository();
        LockedAccountService syncService = new LockedAccountService(syncRepo, new InMemoryTransactionRepository());
        for (int i = 1; i <= numAccounts; i++) {
            IAccount a = AccountFactory.createAccount("Savings", 5100 + i, "Sync " + i, 25, 50000.0);
            a.setPin(1000 + i);
            syncService.saveAccount(a);
        }

        List<Long> syncLatencies = new ArrayList<>();
        long syncStart = System.currentTimeMillis();
        for (int i = 0; i < totalOps; i++) {
            int acc = 5101 + (i % numAccounts);
            long opStart = System.nanoTime();
            syncService.deposit(acc, 10.0);
            syncLatencies.add(System.nanoTime() - opStart);
        }
        long syncDuration = System.currentTimeMillis() - syncStart;

        double syncAvgLat = (syncLatencies.stream().mapToLong(Long::longValue).average().orElse(0)) / 1_000_000.0;
        double syncP95 = ThroughputBenchmark.calculatePercentile(syncLatencies, 95.0);
        double syncP99 = ThroughputBenchmark.calculatePercentile(syncLatencies, 99.0);
        BenchmarkResult syncResult = new BenchmarkResult("Synchronous", totalOps, syncDuration, syncAvgLat, syncP95, syncP99, 0.0);

        // 2. Benchmark Asynchronous Service
        AccountRepository asyncRepo = new InMemoryAccountRepository();
        Properties props = new Properties();
        props.setProperty("service.thread.pool.size", "16");
        props.setProperty("service.timeout.ms", "5000");
        AsyncThreadSafeAccountService asyncService = new AsyncThreadSafeAccountService(asyncRepo, new InMemoryTransactionRepository(), props);

        for (int i = 1; i <= numAccounts; i++) {
            IAccount a = AccountFactory.createAccount("Savings", 5200 + i, "Async " + i, 25, 50000.0);
            a.setPin(1000 + i);
            asyncService.saveAccount(a);
        }

        List<Long> asyncLatencies = new CopyOnWriteArrayList<>();
        List<CompletableFuture<?>> asyncFutures = new ArrayList<>();

        long asyncStart = System.currentTimeMillis();
        for (int i = 0; i < totalOps; i++) {
            int acc = 5201 + (i % numAccounts);
            long opStart = System.nanoTime();
            CompletableFuture<Transaction> f = asyncService.depositAsync(acc, 10.0);
            f.thenAccept(t -> asyncLatencies.add(System.nanoTime() - opStart));
            asyncFutures.add(f);
        }

        CompletableFuture.allOf(asyncFutures.toArray(new CompletableFuture[0])).get(10, TimeUnit.SECONDS);
        long asyncDuration = System.currentTimeMillis() - asyncStart;

        double asyncAvgLat = (asyncLatencies.stream().mapToLong(Long::longValue).average().orElse(0)) / 1_000_000.0;
        double asyncP95 = ThroughputBenchmark.calculatePercentile(asyncLatencies, 95.0);
        double asyncP99 = ThroughputBenchmark.calculatePercentile(asyncLatencies, 99.0);
        BenchmarkResult asyncResult = new BenchmarkResult("Asynchronous", totalOps, asyncDuration, asyncAvgLat, asyncP95, asyncP99, 0.0);

        asyncService.shutdown();

        // Print formatted comparison table
        ThroughputBenchmark.printComparisonTable(syncResult, asyncResult);
        System.out.println("  -> PASSED");
    }

    private static void testNfrSlaRequirements() throws Exception {
        System.out.println("\n[TEST 3] NFR SLA Threshold Assertions:");

        AccountRepository repo = new InMemoryAccountRepository();
        Properties props = new Properties();
        props.setProperty("service.thread.pool.size", "8");
        props.setProperty("service.timeout.ms", "3000");
        AsyncThreadSafeAccountService service = new AsyncThreadSafeAccountService(repo, new InMemoryTransactionRepository(), props);

        IAccount acc = AccountFactory.createAccount("Savings", 5301, "SLA User", 25, 10000.0);
        acc.setPin(1234);
        service.saveAccount(acc);

        long start = System.currentTimeMillis();
        CompletableFuture<Transaction> f = service.depositAsync(5301, 1000.0);
        Transaction txn = f.get(3, TimeUnit.SECONDS);
        long duration = System.currentTimeMillis() - start;

        System.out.printf("  Single Op Latency: %d ms (SLA Limit: < 100ms)%n", duration);
        assert duration < 100 : "Operation exceeded SLA latency target!";
        assert txn != null && "SUCCESS".equals(txn.getStatus()) : "Transaction status failed!";

        service.shutdown();
        System.out.println("  NFR Latency & SLA compliance verified.");
        System.out.println("  -> PASSED");
    }
}
