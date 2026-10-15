# Activity 30: Async Programming – CompletableFuture

## 1. Title
# Activity 30: Async Programming – CompletableFuture

## 2. Description
In modern microservice and reactive banking architectures like ICICI API Banking and Axis Open Banking, blocking threads while waiting for downstream database responses or third-party payment gateways severely degrades server throughput. Modern Java applications use non-blocking asynchronous programming with `CompletableFuture` to chain operations, transform results, handle failures gracefully, and fan out concurrent tasks across multiple external services.

In this activity, you will transition Global Digital Bank to a non-blocking asynchronous style using `CompletableFuture`. You will implement `AsyncUtils` with helper methods for asynchronous supplier execution and fallback defaults, build `AsyncAccountService` where all banking operations return `CompletableFuture<T>`, and chain asynchronous workflows using `supplyAsync()`, `thenApply()`, `thenCompose()`, `exceptionally()`, and `CompletableFuture.allOf()`.

Using non-blocking `CompletableFuture` pipelines enables the banking engine to compose complex multi-step workflows (e.g., validate account -> withdraw -> generate audit -> send notification) without locking execution threads.

By the end of this activity, you will be able to: create asynchronous pipelines with `CompletableFuture`, compose dependent asynchronous steps with `thenCompose` and `thenApply`, handle asynchronous exceptions with `exceptionally`, and coordinate parallel fan-out using `allOf()`.

## 3. Topic Coverage
- **Java concepts**
  - Asynchronous programming with `java.util.concurrent.CompletableFuture`
  - Asynchronous task initiation with `CompletableFuture.supplyAsync()`
  - Functional transformation pipelines (`thenApply()`, `thenApplyAsync()`)
  - Dependent async chaining with `thenCompose()`
  - Non-blocking error handling and fallbacks (`exceptionally()`, `handle()`)
  - Parallel aggregation using `CompletableFuture.allOf()` and `anyOf()`
- **Design patterns / architecture**
  - Reactive / Asynchronous Pipeline Pattern
  - Promise / Future Chaining Pattern
  - Error Handler / Fallback Circuit Pattern
- **Domain concepts**
  - Non-blocking account onboarding and transactions
  - Multi-account parallel balance aggregation
  - Timeout and fallback policies for banking operations

## 4. Steps to Run the Activity
1. Navigate to the activity directory:
   ```powershell
   cd c:\Users\DELL\Downloads\gdb-Activities\trainee_multi-lang-activities\java-gdb-activities\activity30
   ```
2. Compile all source files into the `bin/` directory:
   ```powershell
   javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
   ```
3. Run the CompletableFuture test suite:
   ```powershell
   java -ea -cp "bin;lib/*" com.gdb.tests.TestCompletableFuture
   ```
4. Verify that asynchronous chains, exception handling, and parallel aggregation execute and pass without blocking.

## 5. Target Files, Classes, Methods, Interfaces, Repos, Services

| Type | Name | Package | Status | Notes |
|------|------|---------|--------|-------|
| class | `AsyncUtils` | `com.gdb.concurrency` | NEW | Helper methods for asynchronous wrapping and defaults |
| service | `AsyncAccountService` | `com.gdb.service` | NEW | Fully asynchronous service returning `CompletableFuture<T>` |
| test | `TestCompletableFuture` | `com.gdb.tests` | NEW | Verification suite for asynchronous pipelines and error handling |

## 6. Step-by-Step Instructions of Implementation

### STEP 1: Implement AsyncUtils
- **Where:** `src/com/gdb/concurrency/AsyncUtils.java` — methods `supplyAsyncWithDefault()`, `timeoutWrapper()`
- **What:** Provide utility methods that wrap synchronous `Supplier<T>` into `CompletableFuture<T>` using an executor and attach fallback defaults on failure via `.exceptionally()`.
- **Why:** Reusable helper for resilient async execution.
- **Hint:** `return CompletableFuture.supplyAsync(supplier, executor).exceptionally(ex -> defaultValue);`.

### STEP 2: Implement AsyncAccountService
- **Where:** `src/com/gdb/service/AsyncAccountService.java` — methods `openAccountAsync()`, `depositAsync()`, `withdrawAsync()`, `transferAsync()`, `getTotalBalanceAsync()`
- **What:**
  1. `openAccountAsync(...)`: Return `CompletableFuture.supplyAsync(...)` creating the account.
  2. `depositAsync(accNo, amount)`: Supply deposit asynchronously, `thenApply` transaction extraction, `exceptionally` map error.
  3. `transferAsync(from, to, amount, pin)`: Chain transfer asynchronously with `thenApply`.
  4. `getTotalBalanceAsync(List<Integer> accountNumbers)`: Fan out `getAccount()` calls for all accounts in parallel, combine with `CompletableFuture.allOf()`, and compute sum.
- **Why:** Delivers 100% non-blocking service API for reactive clients.
- **Hint:** Use `CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))`.

### STEP 3: Implement TestCompletableFuture
- **Where:** `src/com/gdb/tests/TestCompletableFuture.java` — `main` method
- **What:**
  1. Test 1: Chain async workflow: open account -> deposit -> withdraw -> check balance.
  2. Test 2: Verify error recovery: attempt invalid withdrawal and assert `exceptionally()` handles failure.
  3. Test 3: Fan-out test: query 5 accounts in parallel using `allOf()` and verify total sum.
- **Why:** Verifies asynchronous non-blocking correctness and pipeline stability.
- **Hint:** Use `.join()` in test assertion checks.

## 7. Acceptance Criteria
- [ ] `AsyncAccountService` returns `CompletableFuture` for all public service methods.
- [ ] Asynchronous pipeline (`open -> deposit -> withdraw -> getBalance`) executes sequentially and produces correct final balance.
- [ ] Failed async operations trigger `.exceptionally()` handlers and return graceful error fallbacks without crashing the application.
- [ ] `getTotalBalanceAsync()` executes parallel queries via `CompletableFuture.allOf()` and aggregates balances accurately.
- [ ] Running `TestCompletableFuture` completes with 100% assertions passing.

## 8. Expected Output

Running `com.gdb.tests.TestCompletableFuture`:
```text
============================================================
  ACTIVITY 30 — ASYNC PROGRAMMING (COMPLETABLE FUTURE)
============================================================
[TEST 1] Chained Asynchronous Workflow (Open -> Deposit -> Withdraw):
  Initiating async pipeline...
  Async account created: #1001
  Async deposit completed. Transaction: TXN-701
  Async withdrawal completed. Transaction: TXN-702
  Pipeline completed. Final Balance: Rs. 45000.0
  -> PASSED

[TEST 2] Asynchronous Exception Handling & Fallback:
  Triggering invalid withdrawal of Rs. 999999.0...
  Captured async exception: Insufficient balance
  Fallback transaction generated successfully: FAILED
  -> PASSED

[TEST 3] Parallel Fan-Out Across Multiple Accounts (allOf):
  Querying balances for Accounts [1001, 1002, 1003, 1004] in parallel...
  Aggregated Total Balance: Rs. 185000.0 (Expected: 185000.0)
  -> PASSED

============================================================
  ALL ACTIVITY 30 COMPLETABLE FUTURE TESTS PASSED!
============================================================
```

## 9. How to Run the Commands

### Compile
```powershell
javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

### Run Test Suite
```powershell
java -ea -cp "bin;lib/*" com.gdb.tests.TestCompletableFuture
```

### Clean Up
```powershell
Remove-Item -Recurse -Force bin
```

> **Verification**: Check all checkboxes in [Section 7 (Acceptance Criteria)](#7-acceptance-criteria) once test execution passes.
