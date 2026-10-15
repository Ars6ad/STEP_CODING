# Activity 29: Thread Pools – ExecutorService and Future

## 1. Title
# Activity 29: Thread Pools – ExecutorService and Future

## 2. Description
In production banking backends like SBI YONO and ICICI iMobile, manually creating a new `new Thread()` for every incoming API request causes severe resource exhaustion and CPU context-switching overhead. High-performance enterprise systems manage concurrent workloads using managed thread pools, which reuse worker threads, bound queue sizes, and provide asynchronous `Future` handles for task coordination.

In this activity, you will modernize thread management in Global Digital Bank by eliminating manual thread creation. You will implement `ThreadPoolFactory` to create fixed, cached, and single-threaded `ExecutorService` instances, implement graceful shutdown protocols using `awaitTermination()`, and build `ExecutorAccountService` to submit tasks via `submit(Callable<T>)` and return asynchronous `Future<T>` handles.

Using managed thread pools ensures that sudden surges in banking transactions are queued and processed predictably without overwhelming server hardware.

By the end of this activity, you will be able to: create and configure thread pools with `Executors` and `ExecutorService`, submit `Callable` tasks, retrieve results asynchronously with `Future.get()`, and implement graceful shutdown routines.

## 3. Topic Coverage
- **Java concepts**
  - Java Concurrency Utilities (`java.util.concurrent.ExecutorService`, `Executors`)
  - Asynchronous task submission via `Callable<T>` vs `Runnable`
  - Future result inspection with `Future<T>.get()`, `isDone()`, `cancel()`
  - Batch execution using `invokeAll()` with timeouts
  - Graceful thread pool shutdown protocols (`shutdown()`, `awaitTermination()`, `shutdownNow()`)
- **Design patterns / architecture**
  - Thread Pool Pattern (`ThreadPoolFactory`)
  - Asynchronous Task Execution Pattern (`ExecutorAccountService`)
  - Graceful Resource Lifecycle Management
- **Domain concepts**
  - Asynchronous banking operation submission
  - Task queueing and thread reusability under heavy traffic
  - Service shutdown during banking maintenance windows

## 4. Steps to Run the Activity
1. Navigate to the activity directory:
   ```powershell
   cd c:\Users\DELL\Downloads\gdb-Activities\trainee_multi-lang-activities\java-gdb-activities\activity29
   ```
2. Compile all source files into the `bin/` directory:
   ```powershell
   javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
   ```
3. Run the executor service test suite:
   ```powershell
   java -ea -cp "bin;lib/*" com.gdb.tests.TestExecutorService
   ```
4. Verify that thread pool task submission, future retrieval, and graceful shutdown pass completely.

## 5. Target Files, Classes, Methods, Interfaces, Repos, Services

| Type | Name | Package | Status | Notes |
|------|------|---------|--------|-------|
| class | `ThreadPoolFactory` | `com.gdb.concurrency` | NEW | Factory creating fixed, cached, and single-thread pools |
| service | `ExecutorAccountService` | `com.gdb.service` | NEW | Service submitting operations to `ExecutorService` and returning `Future` |
| test | `TestExecutorService` | `com.gdb.tests` | NEW | Verification suite for thread pools and futures |

## 6. Step-by-Step Instructions of Implementation

### STEP 1: Implement ThreadPoolFactory
- **Where:** `src/com/gdb/concurrency/ThreadPoolFactory.java` — methods `newFixedPool()`, `newCachedPool()`, `newSingleThreadExecutor()`, `shutdownAndAwait()`
- **What:** Create appropriate `ExecutorService` instances using `Executors`. In `shutdownAndAwait(executor, timeoutSeconds)`, call `executor.shutdown()` followed by `executor.awaitTermination()`; if timeout occurs, call `executor.shutdownNow()`.
- **Why:** Centralizes thread pool instantiation and clean lifecycle teardown.
- **Hint:** Use `Executors.newFixedThreadPool(size)`.

### STEP 2: Implement ExecutorAccountService
- **Where:** `src/com/gdb/service/ExecutorAccountService.java` — methods `depositAsync()`, `withdrawAsync()`, `transferAsync()`
- **What:** Wrap domain operations in `Callable<Transaction>` and submit to internal `ExecutorService`, returning `Future<Transaction>`.
- **Why:** Allows callers to initiate operations without blocking the main request thread.
- **Hint:** `return executor.submit(() -> wrappedService.deposit(accountNumber, amount));`.

### STEP 3: Implement TestExecutorService
- **Where:** `src/com/gdb/tests/TestExecutorService.java` — `main` method
- **What:**
  1. Submit 100 deposit tasks to `ExecutorAccountService`.
  2. Collect all returned `Future<Transaction>` references.
  3. Wait for all futures to complete using `future.get()`.
  4. Verify final account balance equals the exact sum of all deposits.
  5. Test `invokeAll()` with timeout and verify graceful shutdown.
- **Why:** Validates thread pool scalability, future resolution, and resource release.
- **Hint:** Loop over futures calling `f.get()`.

## 7. Acceptance Criteria
- [ ] `ThreadPoolFactory` creates working Fixed, Cached, and Single-Thread executors.
- [ ] `ExecutorAccountService` returns valid `Future<Transaction>` instances for async operations.
- [ ] Submitting 100 concurrent tasks completes all operations accurately without task loss.
- [ ] Calling `Future.get()` retrieves the resulting `Transaction` accurately.
- [ ] `shutdownAndAwait()` cleanly terminates all worker threads within the configured timeout.
- [ ] Running `TestExecutorService` completes with 100% assertions passing.

## 8. Expected Output

Running `com.gdb.tests.TestExecutorService`:
```text
============================================================
  ACTIVITY 29 — THREAD POOLS (EXECUTOR SERVICE & FUTURE)
============================================================
[TEST 1] Submitting 100 Concurrent Deposit Tasks to Fixed Pool (Size 10):
  Submitting tasks...
  Waiting for 100 Futures to resolve...
  All 100 deposit futures resolved successfully.
  Final Account Balance: Rs. 110000.0 (Expected: 110000.0)
  -> PASSED

[TEST 2] Future.get() and Cancellation Check:
  Retrieved Transaction: TXN-501 | Amount: Rs. 5000.0 | Status: SUCCESS
  -> PASSED

[TEST 3] Batch Task Execution (invokeAll with Timeout):
  Executed 10 batch balance inquiry tasks.
  All tasks completed within SLA.
  -> PASSED

[TEST 4] Graceful Executor Shutdown:
  Initiating shutdown...
  Executor terminated cleanly. Is Terminated: true
  -> PASSED

============================================================
  ALL ACTIVITY 29 EXECUTOR SERVICE TESTS PASSED!
============================================================
```

## 9. How to Run the Commands

### Compile
```powershell
javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

### Run Test Suite
```powershell
java -ea -cp "bin;lib/*" com.gdb.tests.TestExecutorService
```

### Clean Up
```powershell
Remove-Item -Recurse -Force bin
```

> **Verification**: Check all checkboxes in [Section 7 (Acceptance Criteria)](#7-acceptance-criteria) once test execution passes.
