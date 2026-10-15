# Activity 28: Concurrency – Locks, Atomics, Concurrent Collections

## Objective
Upgrade from coarse-grained `synchronized` locks to fine-grained concurrency utilities in `java.util.concurrent`. Implement per-account locking using `ReentrantLock` and `ConcurrentHashMap`, deadlock-free lock ordering, `ReentrantReadWriteLock` for read optimization, and lock-free CAS primitives with `AtomicLong` and `AtomicReference`.

## Key Concepts
1. **AccountLockRegistry**: Dynamically manage per-account `ReentrantLock` instances using `ConcurrentHashMap.computeIfAbsent`.
2. **Fine-Grained Locking**: Operations on Account 1001 do not block concurrent operations on Account 1002.
3. **Deadlock Prevention via Lock Ordering**: When transferring between two accounts, always acquire locks in sorted order (`Math.min` before `Math.max`).
4. **ReentrantReadWriteLock**: Allow concurrent read queries (`getBalance`) while securing exclusive access for writes (`deposit`/`withdraw`).
5. **Lock-Free Atomics & CAS**: Use `AtomicLong` and `AtomicReference.compareAndSet` for ultra-fast, lock-free coordination.

## Directory Structure
- `src/com/gdb/concurrency/AccountLockRegistry.java`: Per-account lock registry.
- `src/com/gdb/concurrency/AccountLock.java`: AutoCloseable lock helper.
- `src/com/gdb/service/LockedAccountService.java`: Fine-grained locked banking service.
- `src/com/gdb/service/AtomicAccountService.java`: Lock-free atomic service.
- `src/com/gdb/tests/TestLocksAndAtomics.java`: Comprehensive concurrency verification test suite.

## How to Run
```bash
# Windows (PowerShell)
javac -encoding UTF-8 -cp "lib/*;." -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
java -ea -cp "lib/*;bin" com.gdb.tests.TestLocksAndAtomics

# Linux & macOS
javac -encoding UTF-8 -cp "lib/*:." -d bin $(find src -name "*.java")
java -ea -cp "lib/*:bin" com.gdb.tests.TestLocksAndAtomics
```
