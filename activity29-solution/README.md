# Activity 29: Thread Pools – ExecutorService + Future

## Objective
Scale concurrent execution efficiently using managed thread pools (`ExecutorService`) from `ThreadPoolFactory` and decouple task submission from completion using `Future<T>`. Master task lifecycle, `Callable<T>`, batch execution with `invokeAll`, and two-phase graceful shutdown.

## Key Concepts
1. **Thread Pools (`ExecutorService`)**: Avoid overhead of creating and destroying raw OS threads by reusing a pool of worker threads.
2. **ThreadPoolFactory**: Factory creating fixed, cached, and single-thread pools with centralized config.
3. **Callable<T> & Future<T>**: Submit value-returning tasks and retrieve results or handle errors using `Future.get(timeout, unit)`.
4. **Batch Processing (`invokeAll`)**: Dispatch collections of tasks and wait for all to complete within a bounded timeout.
5. **Graceful Two-Phase Shutdown**: Calling `shutdown()` followed by `awaitTermination()` and fallback to `shutdownNow()`.

## Directory Structure
- `src/com/gdb/concurrency/ThreadPoolFactory.java`: Factory for thread pool creation and graceful shutdown.
- `src/com/gdb/service/ExecutorAccountService.java`: Asynchronous banking service using ExecutorService and Futures.
- `src/com/gdb/tests/TestExecutorService.java`: Comprehensive thread pool verification test suite.

## How to Run
```bash
# Windows (PowerShell)
javac -encoding UTF-8 -cp "lib/*;." -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
java -ea -cp "lib/*;bin" com.gdb.tests.TestExecutorService

# Linux & macOS
javac -encoding UTF-8 -cp "lib/*:." -d bin $(find src -name "*.java")
java -ea -cp "lib/*:bin" com.gdb.tests.TestExecutorService
```
