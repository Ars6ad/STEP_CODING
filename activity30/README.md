# Activity 30: Async Programming – CompletableFuture

## Objective
Transition to modern, non-blocking asynchronous programming in Java using `CompletableFuture`. Master functional composition with `thenApply` and `thenCompose`, parallel fan-out aggregation using `CompletableFuture.allOf`, and resilient exception recovery with `exceptionally`.

## Key Concepts
1. **CompletableFuture<T>**: Non-blocking asynchronous promise representing a value available in the future.
2. **Async Composition**:
   - `thenApply`: Transform synchronous result (mapping $T ightarrow R$).
   - `thenCompose`: Chain dependent asynchronous tasks (flattening $T ightarrow 	ext{CompletableFuture}<R>$).
3. **Fan-Out & Aggregation (`allOf`)**: Execute operations across multiple accounts simultaneously and synchronize when all finish.
4. **Resilient Error Recovery (`exceptionally`)**: Intercept upstream exceptions and return fallback values without crashing pipelines.
5. **Timeout Guards (`completeOnTimeout`)**: Safeguard async pipelines from hanging indefinitely.

## Directory Structure
- `src/com/gdb/concurrency/AsyncUtils.java`: Async utilities and timeout fallback helpers.
- `src/com/gdb/service/AsyncAccountService.java`: Non-blocking banking service using CompletableFutures.
- `src/com/gdb/tests/TestCompletableFuture.java`: Comprehensive async pipeline verification test suite.

## How to Run
```bash
# Windows (PowerShell)
javac -encoding UTF-8 -cp "lib/*;." -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
java -ea -cp "lib/*;bin" com.gdb.tests.TestCompletableFuture

# Linux & macOS
javac -encoding UTF-8 -cp "lib/*:." -d bin $(find src -name "*.java")
java -ea -cp "lib/*:bin" com.gdb.tests.TestCompletableFuture
```
