# Activity 28: Concurrency – Locks, Atomics, and Concurrent Collections

## 1. Title
# Activity 28: Concurrency – Locks, Atomics, and Concurrent Collections

## 2. Description
While coarse-grained `synchronized` methods provide basic thread safety (as built in Activity 27), locking the entire service object creates a severe throughput bottleneck in enterprise banking systems. If customer Alice is depositing money into account #1001, customer Bob depositing into account #1002 should not be blocked waiting on Alice's lock. In enterprise banking engines (such as core banking switches at ICICI and HDFC), fine-grained per-account locks and lock-free atomic primitives are used to allow maximum parallel throughput across independent accounts.

In this activity, you will replace coarse-grained synchronization with fine-grained concurrency tools from `java.util.concurrent`. You will build `AccountLockRegistry` to manage per-account `ReentrantLock` instances, `AccountLock` helper for clean `try-finally` and `AutoCloseable` lock management, `LockedAccountService` to lock only the specific accounts involved in an operation (with ordered dual-locking to prevent deadlocks during transfers), and `AtomicAccountService` utilizing `AtomicLong` and `AtomicReference` for non-blocking CAS (Compare-And-Swap) state updates.

By isolating locks to individual accounts and leveraging atomic variables, your banking platform achieves high concurrency across different accounts while strictly eliminating deadlock risks.

By the end of this activity, you will be able to: manage fine-grained explicit locks using `ReentrantLock`, avoid deadlocks with resource ordering, implement AutoCloseable lock helpers, and use `AtomicLong` and CAS operations for lock-free counters.

## 3. Topic Coverage
- **Java concepts**
  - Explicit locking with `java.util.concurrent.locks.ReentrantLock`
  - Per-key lock management using `ConcurrentHashMap`
  - Deadlock prevention via natural ordering of account IDs
  - Lock-free atomic variables (`AtomicLong`, `AtomicReference`)
  - Compare-And-Swap (CAS) semantics
  - Automatic resource management with `AutoCloseable`
- **Design patterns / architecture**
  - Lock Registry Pattern (`AccountLockRegistry`)
  - RAII (Resource Acquisition Is Initialization) / AutoCloseable Lock Wrapper
  - Resource Ordering Pattern for Deadlock Prevention
- **Domain concepts**
  - Per-account concurrency isolation
  - Global transaction counter sequencing with `AtomicLong`
  - High-throughput parallel account processing

## 4. Steps to Run the Activity
1. Navigate to the activity directory:
   ```powershell
   cd c:\Users\DELL\Downloads\gdb-Activities\trainee_multi-lang-activities\java-gdb-activities\activity28
   ```
2. Compile all source files into the `bin/` directory:
   ```powershell
   javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
   ```
3. Run the fine-grained locks and atomics test suite:
   ```powershell
   java -ea -cp "bin;lib/*" com.gdb.tests.TestLocksAndAtomics
   ```
4. Verify that concurrent operations on independent accounts run in parallel while operations on the same account remain strictly synchronized.

## 5. Target Files, Classes, Methods, Interfaces, Repos, Services

| Type | Name | Package | Status | Notes |
|------|------|---------|--------|-------|
| class | `AccountLockRegistry` | `com.gdb.concurrency` | NEW | Manages per-account `ReentrantLock` mapping |
| class | `AccountLock` | `com.gdb.concurrency` | NEW | Helper wrapper implementing `AutoCloseable` for locks |
| service | `LockedAccountService` | `com.gdb.service` | NEW | Service using per-account locking and deadlock prevention |
| service | `AtomicAccountService` | `com.gdb.service` | NEW | Service using `AtomicLong` for global transaction counter |
| test | `TestLocksAndAtomics` | `com.gdb.tests` | NEW | Verification suite for fine-grained locking and atomics |

## 6. Step-by-Step Instructions of Implementation

### STEP 1: Implement AccountLockRegistry
- **Where:** `src/com/gdb/concurrency/AccountLockRegistry.java` — method `getLock(int accountNumber)`
- **What:** Use a `ConcurrentHashMap<Integer, ReentrantLock>` to return an existing `ReentrantLock` for `accountNumber` or compute a new one atomically using `computeIfAbsent()`.
- **Why:** Delivers fine-grained per-account lock instances without synchronization overhead.
- **Hint:** `locks.computeIfAbsent(accountNumber, k -> new ReentrantLock())`.

### STEP 2: Implement AccountLock (AutoCloseable Helper)
- **Where:** `src/com/gdb/concurrency/AccountLock.java` — constructor and `close()`
- **What:** In constructor, acquire `lock.lock()`. In `close()`, call `lock.unlock()`.
- **Why:** Enables safe `try-with-resources` lock acquisition and release.
- **Hint:** `public class AccountLock implements AutoCloseable`.

### STEP 3: Implement LockedAccountService
- **Where:** `src/com/gdb/service/LockedAccountService.java` — `deposit()`, `withdraw()`, `transfer()`
- **What:** 
  1. For `deposit()` and `withdraw()`: Acquire lock for that specific account only.
  2. For `transfer(from, to, amount)`: Acquire locks for BOTH accounts in consistent numerical order (`min(from, to)` first, then `max(from, to)`) to strictly prevent deadlocks.
- **Why:** Allows parallel execution across independent accounts while guaranteeing deadlock-free dual-account locking.
- **Hint:** `int first = Math.min(from, to); int second = Math.max(from, to);`.

### STEP 4: Implement AtomicAccountService
- **Where:** `src/com/gdb/service/AtomicAccountService.java` — methods `nextTransactionId()`, `deposit()`
- **What:** Use `AtomicLong globalTransactionCounter = new AtomicLong(1000)` and increment atomically via `incrementAndGet()`.
- **Why:** Delivers lock-free, ultra-high-speed counter generation across threads.
- **Hint:** `globalTransactionCounter.incrementAndGet()`.

## 7. Acceptance Criteria
- [ ] Concurrent operations on the *same* account are synchronized and produce exact balance totals.
- [ ] Concurrent operations on *different* accounts proceed in parallel without blocking each other.
- [ ] Reciprocal concurrent transfers between accounts complete without encountering deadlocks.
- [ ] `AccountLock` properly releases the lock when used in `try-with-resources`.
- [ ] `AtomicAccountService` increments global transaction sequence numbers atomically.
- [ ] Running `TestLocksAndAtomics` completes with 100% assertions passing.

## 8. Expected Output

Running `com.gdb.tests.TestLocksAndAtomics`:
```text
============================================================
  ACTIVITY 28 — LOCKS, ATOMICS & CONCURRENT COLLECTIONS
============================================================
[TEST 1] Fine-Grained Per-Account Locking (Same Account):
  Spawning 10 threads depositing into Account #1001...
  Expected Balance: Rs. 110000.0, Actual: Rs. 110000.0
  -> PASSED

[TEST 2] Parallel Execution Across Different Accounts:
  Spawning threads depositing into Account #1001 and #1002 in parallel...
  Both accounts updated accurately without lock contention.
  -> PASSED

[TEST 3] Deadlock-Free Bidirectional Transfers:
  Executing 500 concurrent bidirectional transfers between #1001 and #1002...
  Deadlock avoided via ordered dual-locking.
  Total Combined Balance Preserved: Rs. 200000.0
  -> PASSED

[TEST 4] Lock-Free Atomic Sequence Counter:
  Generated 1000 unique transaction IDs concurrently.
  Final Counter Value: 2000
  -> PASSED

============================================================
  ALL ACTIVITY 28 LOCKS AND ATOMICS TESTS PASSED!
============================================================
```

## 9. How to Run the Commands

### Compile
```powershell
javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

### Run Test Suite
```powershell
java -ea -cp "bin;lib/*" com.gdb.tests.TestLocksAndAtomics
```

### Clean Up
```powershell
Remove-Item -Recurse -Force bin
```

> **Verification**: Check all checkboxes in [Section 7 (Acceptance Criteria)](#7-acceptance-criteria) once test execution passes.
