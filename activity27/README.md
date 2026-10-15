# Activity 27: Threads – Basics + Race Conditions + Synchronization

## Objective
Understand the mechanics of multi-threaded execution in Java, observe the dangers of race conditions and lost updates on shared banking state, and master intrinsic locks (`synchronized`) to guarantee thread safety.

## Key Concepts
1. **Threads & Concurrency**: Creating and starting background worker threads using `Runnable` and `Thread`.
2. **Race Conditions**: When multiple threads read and write shared data without synchronization, interleaved execution leads to lost updates.
3. **Thread.join()**: Pausing the main orchestrator thread until background worker threads complete their execution.
4. **Intrinsic Synchronization**: Using `synchronized` methods/blocks to enforce mutual exclusion and ensure memory visibility across CPU caches.
5. **Conservation of Funds**: In financial applications, concurrent multi-account transactions must never create or destroy funds.

## Directory Structure
- `src/com/gdb/concurrency/ConcurrentDepositDemo.java`: Race condition demonstration driver.
- `src/com/gdb/service/ThreadSafeAccountService.java`: Synchronized banking service.
- `src/com/gdb/tests/TestConcurrency.java`: Concurrency verification test suite.

## How to Run
```bash
# Compile
javac -d bin -cp "lib/*" $(find src -name "*.java")

# Run Concurrency Test Suite
java -ea -cp "lib/*;bin" com.gdb.tests.TestConcurrency
```
