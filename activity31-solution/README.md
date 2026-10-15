# Activity 31: Thread-safe + Async Service + NFR Measurement

## Objective
Unify all concepts developed throughout Phase 9 into a production-ready `AsyncThreadSafeAccountService` combining fine-grained per-account locks, lock-free atomic counters, non-blocking `CompletableFuture` APIs, and timeout protection. Measure and benchmark Non-Functional Requirements (NFR) including operations per second, average latency, and P95/P99 latency profiles.

## Key Concepts
1. **AsyncThreadSafeAccountService**: Unifies thread safety (`ReentrantLock`, `ConcurrentHashMap`) with non-blocking async operations (`CompletableFuture<T>`).
2. **NFR Performance Benchmarking**: Quantify system metrics:
   - **Throughput**: Operations completed per second.
   - **Average Latency**: Mean response time per transaction.
   - **P95 / P99 Tail Latency**: Response time experienced by the 95th and 99th percentile of requests.
3. **SLA Compliance**: Guarantee response times within strict Non-Functional Requirements (< 100 ms).
4. **Configuration-Driven Concurrency**: Manage thread pool sizes and timeouts via `persistence.properties`.

## Directory Structure
- `src/com/gdb/service/AsyncThreadSafeAccountService.java`: Full thread-safe asynchronous banking service.
- `src/com/gdb/concurrency/ThroughputBenchmark.java`: NFR performance metrics and latency percentile calculator.
- `src/com/gdb/tests/TestNfrThroughput.java`: End-to-end benchmark and SLA verification test suite.

## How to Run
```bash
# Windows (PowerShell)
javac -encoding UTF-8 -cp "lib/*;." -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
java -ea -cp "lib/*;bin" com.gdb.tests.TestNfrThroughput

# Linux & macOS
javac -encoding UTF-8 -cp "lib/*:." -d bin $(find src -name "*.java")
java -ea -cp "lib/*:bin" com.gdb.tests.TestNfrThroughput
```
