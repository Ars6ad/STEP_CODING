# Activity 31: Enterprise Architecture – Thread-Safe Async Service and NFR Benchmarking

## 1. Title
# Activity 31: Enterprise Architecture – Thread-Safe Async Service and NFR Benchmarking

## 2. Description
In production financial core systems like SBI YONO and HDFC SmartHub, high-throughput microservices must satisfy stringent Non-Functional Requirements (NFRs): high throughput (operations/second), low latency (P95/P99 latency SLA < 50ms), and zero data race corruption. Building modern enterprise banking software requires harmonizing all architectural disciplines—thread safety, non-blocking asynchronous APIs, connection pooling, and automated NFR benchmarking.

In this capstone concurrency activity, you will combine all concepts developed across Phase 9 into a unified, enterprise-grade banking service. You will build `AsyncThreadSafeAccountService` integrating fine-grained `ReentrantLock` concurrency, lock-free atomics, managed `ExecutorService` thread pools, and non-blocking `CompletableFuture` API surfaces. You will also implement `ThroughputBenchmark` to measure and compare throughput (TPS), average latency, P95/P99 percentiles, and error rates between synchronous and asynchronous architectures.

By conducting rigorous NFR benchmarking against predefined SLAs, you validate that the banking platform can handle enterprise-scale transactional loads reliably and performantly.

By the end of this activity, you will be able to: design combined thread-safe and asynchronous service architectures, implement multi-threaded NFR benchmark suites, calculate P95/P99 latency percentiles, and verify enterprise performance SLAs.

## 3. Topic Coverage
- **Java concepts**
  - Integrated Concurrency Architecture (`ReentrantLock`, `AtomicLong`, `ExecutorService`, `CompletableFuture`)
  - Performance and Non-Functional Requirement (NFR) measurement
  - Latency distribution computation (Mean, P90, P95, P99, Max)
  - Throughput calculation (Operations Per Second / TPS)
  - Time measurement with `System.nanoTime()` and `System.currentTimeMillis()`
- **Design patterns / architecture**
  - Enterprise Service Facade Pattern
  - Asynchronous Thread-Safe Architecture
  - Automated Benchmarking and SLA Verification Pattern
- **Domain concepts**
  - Banking Non-Functional Requirements (NFR) and Service Level Agreements (SLA)
  - Peak transaction throughput and latency tail tolerance
  - Production readiness verification

## 4. Steps to Run the Activity
1. Navigate to the activity directory:
   ```powershell
   cd c:\Users\DELL\Downloads\gdb-Activities\trainee_multi-lang-activities\java-gdb-activities\activity31
   ```
2. Compile all source files into the `bin/` directory:
   ```powershell
   javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
   ```
3. Run the NFR throughput and latency benchmark test suite:
   ```powershell
   java -ea -cp "bin;lib/*" com.gdb.tests.TestNfrThroughput
   ```
4. Verify that the benchmark comparison table prints cleanly and all NFR SLA assertions pass.

## 5. Target Files, Classes, Methods, Interfaces, Repos, Services

| Type | Name | Package | Status | Notes |
|------|------|---------|--------|-------|
| service | `AsyncThreadSafeAccountService` | `com.gdb.service` | NEW | Thread-safe service with async CompletableFuture surface |
| class | `ThroughputBenchmark` | `com.gdb.concurrency` | NEW | Benchmark engine measuring throughput and P95/P99 latencies |
| test | `TestNfrThroughput` | `com.gdb.tests` | NEW | Test suite running benchmark and asserting SLA thresholds |

## 6. Step-by-Step Instructions of Implementation

### STEP 1: Implement AsyncThreadSafeAccountService
- **Where:** `src/com/gdb/service/AsyncThreadSafeAccountService.java` — methods `openAccountAsync()`, `depositAsync()`, `withdrawAsync()`, `transferAsync()`, `getAccountAsync()`
- **What:** Combine `AccountLockRegistry` with internal `ExecutorService` and return `CompletableFuture<T>` from each method. Acquire fine-grained per-account locks during operation execution to ensure complete thread safety.
- **Why:** Delivers a fully thread-safe and non-blocking service facade.
- **Hint:** `return CompletableFuture.supplyAsync(() -> { try (var l = new AccountLock(registry.getLock(accNo))) { return executeDeposit(accNo, amount); } }, executor);`.

### STEP 2: Implement ThroughputBenchmark
- **Where:** `src/com/gdb/concurrency/ThroughputBenchmark.java` — method `runBenchmark(String serviceName, Runnable task, int totalOps, int concurrency)`
- **What:**
  1. Record start timestamp `System.nanoTime()`.
  2. Spawn worker threads and collect latency for every single operation into a thread-safe list.
  3. Wait for all operations to complete.
  4. Compute total duration, throughput (Ops/sec), mean latency, P95 latency (95th percentile), and P99 latency.
  5. Print formatted ASCII performance comparison table.
- **Why:** Provides objective, quantitative NFR verification.
- **Hint:** Sort recorded latencies to calculate percentile ranks (`latencies.get((int) (latencies.size() * 0.95))`).

### STEP 3: Implement TestNfrThroughput
- **Where:** `src/com/gdb/tests/TestNfrThroughput.java` — `main` method
- **What:**
  1. Run correctness verification on `AsyncThreadSafeAccountService`.
  2. Run benchmark on Synchronous Service vs Async Thread-Safe Service (1,000 operations, concurrency 10).
  3. Print comparison table.
  4. Assert P95 latency is within SLA threshold (< 100ms) and error rate is 0.0%.
- **Why:** Automates continuous NFR compliance checking.
- **Hint:** Assert `result.getP95LatencyMs() < 100.0`.

## 7. Acceptance Criteria
- [ ] `AsyncThreadSafeAccountService` prevents race conditions while providing a non-blocking `CompletableFuture` API.
- [ ] `ThroughputBenchmark` computes operations/second, mean latency, and P95/P99 latency percentiles.
- [ ] Benchmark comparison table prints clearly with throughput and latency metrics for sync and async modes.
- [ ] Zero transactions fail or corrupt balances under concurrent load (Error rate = 0.0%).
- [ ] Running `TestNfrThroughput` verifies that performance metrics satisfy configured SLA limits.

## 8. Expected Output

Running `com.gdb.tests.TestNfrThroughput`:
```text
============================================================
  ACTIVITY 31 — THREAD-SAFE + ASYNC + NFR MEASUREMENT
============================================================

[TEST 1] AsyncThreadSafeAccountService Correctness Verification:
? Loading account rules from properties files...
--------------------------------------------------
? All rules loaded successfully!
   Account types: [SALARY, SAVINGS, FIXEDDEPOSIT, CURRENT]
  Account created: #1001
  Deposit executed: Balance Rs. 25000.0
  Withdrawal executed: Balance Rs. 22500.0
  -> PASSED

[TEST 2] Synchronous vs Asynchronous Throughput Benchmark:
  Running 1,000 operations across 10 concurrent threads...

┌────────────────────────────┬──────────────────────┬──────────────────────┐
│ Metric                     │ Synchronous Service  │ Async Thread-Safe    │
├────────────────────────────┼──────────────────────┼──────────────────────┤
│ Total Operations           │ 1000                 │ 1000                 │
│ Total Time (ms)            │ 124                  │ 48                   │
│ Throughput (Ops/sec)       │ 8064.52              │ 20833.33             │
│ Mean Latency (ms)          │ 1.18                 │ 0.44                 │
│ P95 Latency (ms)           │ 2.40                 │ 0.85                 │
│ P99 Latency (ms)           │ 4.10                 │ 1.20                 │
│ Error Rate (%)             │ 0.00                 │ 0.00                 │
└────────────────────────────┴──────────────────────┴──────────────────────┘
  -> PASSED

[TEST 3] NFR SLA Threshold Assertions:
  Single Op Latency: 0.44 ms (SLA Limit: < 100ms)
  P95 Latency: 0.85 ms (SLA Limit: < 100ms)
  NFR Latency & SLA compliance verified.
  -> PASSED

============================================================
  ALL ACTIVITY 31 NFR BENCHMARK TESTS PASSED!
============================================================
```

## 9. How to Run the Commands

### Compile
```powershell
javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

### Run Test Suite
```powershell
java -ea -cp "bin;lib/*" com.gdb.tests.TestNfrThroughput
```

### Clean Up
```powershell
Remove-Item -Recurse -Force bin
```

> **Verification**: Check all checkboxes in [Section 7 (Acceptance Criteria)](#7-acceptance-criteria) once test execution passes.
