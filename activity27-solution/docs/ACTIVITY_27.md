# Activity 27: Concurrency – Thread Safety and Synchronization

## 1. Title
# Activity 27: Concurrency – Thread Safety and Synchronization

## 2. Description
In high-concurrency banking environments like HDFC Bank and SBI UPI payment gateways, hundreds of transactions hit the same account simultaneously—such as concurrent salary credits, automated standing orders, ATM cash withdrawals, and POS payments. If multiple threads update an account balance without proper synchronization, race conditions occur, leading to lost updates, corrupted balances, and severe financial discrepancies.

In this activity, you will examine the impact of race conditions on unsynchronized banking operations and build a thread-safe service layer. You will create `ConcurrentDepositDemo` to reproduce lost updates using parallel Java threads, and implement `ThreadSafeAccountService` using Java's intrinsic locks (`synchronized` methods and blocks) and thread synchronization barriers (`CountDownLatch`).

Understanding concurrency hazards and learning how to protect shared mutable state with synchronization primitives is fundamental to building reliable multi-threaded banking systems.

By the end of this activity, you will be able to: reproduce race conditions and lost updates with concurrent threads, synchronize critical sections using Java intrinsic locks, coordinate multi-thread execution with `CountDownLatch`, and verify thread-safe balance computations.

## 3. Topic Coverage
- **Java concepts**
  - Java Multithreading (`Thread`, `Runnable`)
  - Shared mutable state and Race Conditions
  - Intrinsic locking with `synchronized` methods and `synchronized (this)` blocks
  - Thread coordination and latching with `CountDownLatch`
  - Memory visibility and thread safety guarantees
- **Design patterns / architecture**
  - Thread-Safe Wrapper / Synchronized Service Pattern
  - Stress Testing / Concurrency Verification Pattern
- **Domain concepts**
  - High-concurrency account balance updates
  - Lost update prevention in banking transactions
  - Atomic debit/credit consistency under parallel load

## 4. Steps to Run the Activity
1. Navigate to the activity directory:
   ```powershell
   cd c:\Users\DELL\Downloads\gdb-Activities\trainee_multi-lang-activities\java-gdb-activities\activity27
   ```
2. Compile all source files into the `bin/` directory:
   ```powershell
   javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
   ```
3. Run the concurrency test suite:
   ```powershell
   java -ea -cp "bin;lib/*" com.gdb.tests.TestConcurrency
   ```
4. Verify that concurrent deposits under `ThreadSafeAccountService` produce the exact expected mathematical balance without any lost updates.

## 5. Target Files, Classes, Methods, Interfaces, Repos, Services

| Type | Name | Package | Status | Notes |
|------|------|---------|--------|-------|
| class | `ConcurrentDepositDemo` | `com.gdb.concurrency` | NEW | Demonstrates race conditions in unsynchronized vs synchronized services |
| service | `ThreadSafeAccountService` | `com.gdb.service` | NEW | Thread-safe service using synchronized methods |
| test | `TestConcurrency` | `com.gdb.tests` | NEW | Verification suite for parallel operations and thread safety |

## 6. Step-by-Step Instructions of Implementation

### STEP 1: Implement ConcurrentDepositDemo
- **Where:** `src/com/gdb/concurrency/ConcurrentDepositDemo.java` — method `runDemo(AccountService service, int threadCount, int depositsPerThread, double amountPerDeposit)`
- **What:** Spawn `threadCount` threads, each executing `depositsPerThread` deposits of `amountPerDeposit` on the same account using a `CountDownLatch` to start all threads simultaneously. Wait for all threads to finish (`latch.await()` / `thread.join()`). Return final balance.
- **Why:** Simulates heavy concurrent deposit workload against an account.
- **Hint:** Use `CountDownLatch startLatch = new CountDownLatch(1)` and `CountDownLatch doneLatch = new CountDownLatch(threadCount)`.

### STEP 2: Implement ThreadSafeAccountService
- **Where:** `src/com/gdb/service/ThreadSafeAccountService.java` — class body
- **What:** Extend `AccountService` (or wrap repositories). Make `deposit()`, `withdraw()`, `transfer()`, `getAccount()`, and `openAccount()` `synchronized` methods.
- **Why:** Ensures mutual exclusion so only one thread modifies account state at any instant.
- **Hint:** Mark public methods with `synchronized`.

### STEP 3: Implement TestConcurrency
- **Where:** `src/com/gdb/tests/TestConcurrency.java` — `main` method
- **What:** 
  1. Test 1: Run 10 threads doing 100 deposits of Rs. 100 each (expected increase: Rs. 100,000) on `ThreadSafeAccountService`.
  2. Test 2: Verify concurrent transfers between two accounts maintain exact total ledger balance.
  3. Assert final balance matches exact calculation.
- **Why:** Verifies that no race conditions or lost updates occur under parallel execution.
- **Hint:** Assert `finalBalance == expectedBalance`.

## 7. Acceptance Criteria
- [ ] Concurrent execution of 10 threads making 1,000 total deposits produces the exact mathematical balance without losing updates.
- [ ] `ThreadSafeAccountService` prevents concurrent race conditions on balance updates.
- [ ] Concurrent transfers between accounts preserve total system balance invariant.
- [ ] All threads terminate cleanly without deadlocks or unhandled exceptions.
- [ ] Running `TestConcurrency` completes with 100% assertions passing.

## 8. Expected Output

Running `com.gdb.tests.TestConcurrency`:
```text
============================================================
  ACTIVITY 27 — CONCURRENCY & THREAD SAFETY TESTS
============================================================
[TEST 1] Concurrent Deposits to Same Account:
  Spawning 10 threads, each making 100 deposits of Rs. 100.0...
  Initial Balance: Rs. 10000.0
  Expected Final Balance: Rs. 110000.0
  Actual Final Balance:   Rs. 110000.0
  -> PASSED (Zero lost updates!)

[TEST 2] Concurrent Transfers Between Accounts:
  Spawning 10 threads performing reciprocal transfers...
  Total Combined Balance Before: Rs. 200000.0
  Total Combined Balance After:  Rs. 200000.0
  -> PASSED (Total invariant preserved!)

============================================================
  ALL ACTIVITY 27 CONCURRENCY TESTS PASSED!
============================================================
```

## 9. How to Run the Commands

### Compile
```powershell
javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

### Run Test Suite
```powershell
java -ea -cp "bin;lib/*" com.gdb.tests.TestConcurrency
```

### Clean Up
```powershell
Remove-Item -Recurse -Force bin
```

> **Verification**: Check all checkboxes in [Section 7 (Acceptance Criteria)](#7-acceptance-criteria) once test execution passes.
