# Activity 21: Repository Pattern – Interfaces, InMemory Storage, and Factory

## 1. Title
# Activity 21: Repository Pattern – Interfaces, InMemory Storage, and Factory

## 2. Description
In modern core banking platforms like HDFC Bank and State Bank of India (SBI), business services must remain decoupled from specific storage technologies. Whether account data is stored in memory during unit tests, saved into relational databases (Oracle/PostgreSQL), or cached in distributed key-value stores, the service layer should interact only with clean repository abstractions.

In this activity, you will introduce the Repository Pattern to the Global Digital Bank (GDB) system. You will define standard repository interface contracts for accounts and transaction records, build robust in-memory storage implementations backed by Java collections, and implement a configurable `RepositoryFactory` that instantiates repositories based on configuration properties.

By decoupling `AccountService` from direct data manipulation, you ensure that future persistence layers (such as JDBC and flat-file stores) can be swapped seamlessly without modifying any core business logic.

By the end of this activity, you will be able to: design decoupled data access layers using Java interfaces, implement memory-backed CRUD repositories, and dynamically instantiate repository instances using the Factory pattern.

## 3. Topic Coverage
- **Java concepts**
  - Java Interface design and contract enforcement
  - Generic Collections (`Map<K, V>`, `List<T>`, `HashMap`, `ArrayList`)
  - Stream API filtering and collection transformation (`Collectors.toList()`)
  - Method synchronization for thread-safe in-memory operations
  - Properties file loading using `Properties` and `ClassLoader` resources
- **Design patterns / architecture**
  - Repository Pattern for data access abstraction
  - Factory Pattern (`RepositoryFactory`) for configurable object creation
  - Separation of Concerns (SoC) between domain, service, and persistence layers
  - Dependency Injection via service constructors
- **Domain concepts**
  - Account lifecycle CRUD persistence (Create, Read, Update, Delete)
  - Auto-incrementing account number sequencing
  - Transaction history recording and ledger querying
  - Configuration-driven application runtime switching

## 4. Steps to Run the Activity
1. Navigate to the activity directory:
   ```powershell
   cd c:\Users\DELL\Downloads\gdb-Activities\trainee_multi-lang-activities\java-gdb-activities\activity21
   ```
2. Compile all source files into the `bin/` directory:
   ```powershell
   javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
   ```
3. Run the repository test suite with assertions enabled:
   ```powershell
   java -ea -cp "bin;lib/*" com.gdb.tests.TestRepositoryInMemory
   ```
4. Verify that all in-memory repository tests and service integration tests complete with 100% pass status.

## 5. Target Files, Classes, Methods, Interfaces, Repos, Services

| Type | Name | Package | Status | Notes |
|------|------|---------|--------|-------|
| interface | `AccountRepository` | `com.gdb.repository` | PROVIDED | Defines CRUD and query contracts for accounts |
| interface | `TransactionRepository` | `com.gdb.repository` | PROVIDED | Defines ledger persistence contracts for transactions |
| class | `InMemoryAccountRepository` | `com.gdb.repository` | UPDATE | Memory-backed implementation of `AccountRepository` |
| class | `InMemoryTransactionRepository` | `com.gdb.repository` | UPDATE | Memory-backed implementation of `TransactionRepository` |
| class | `RepositoryFactory` | `com.gdb.repository` | UPDATE | Instantiates repositories based on `persistence.properties` |
| service | `AccountService` | `com.gdb.service` | PROVIDED | Refactored to delegate persistence to repositories |
| properties | `persistence.properties` | `config` | PROVIDED | Configuration file declaring `persistence.mode=memory` |
| test | `TestRepositoryInMemory` | `com.gdb.tests` | UPDATE | Verification suite for repository CRUD operations |

## 6. Step-by-Step Instructions of Implementation

### STEP 1: Declare Fields in InMemoryAccountRepository
- **Where:** `src/com/gdb/repository/InMemoryAccountRepository.java` — class body
- **What:** Declare a `private final Map<Integer, IAccount> accounts = new HashMap<>()` and a counter `private int nextAccountNumber = 1001`.
- **Why:** Stores account entities in memory indexed by their unique account number.
- **Hint:** Initialize the map during field declaration.

### STEP 2: Implement save(IAccount account)
- **Where:** `src/com/gdb/repository/InMemoryAccountRepository.java` — method `save(IAccount)`
- **What:** Add the provided `account` into the `accounts` map using `account.getAccountNumber()` as the key.
- **Why:** Persists newly created accounts in memory.
- **Hint:** Check for null account before inserting.

### STEP 3: Implement findById(int accountNumber)
- **Where:** `src/com/gdb/repository/InMemoryAccountRepository.java` — method `findById(int)`
- **What:** Retrieve and return the `IAccount` from `accounts` map matching the `accountNumber`, or return `null` if not present.
- **Why:** Enables single-account lookup by primary key.
- **Hint:** Use `accounts.get(accountNumber)`.

### STEP 4: Implement findAll()
- **Where:** `src/com/gdb/repository/InMemoryAccountRepository.java` — method `findAll()`
- **What:** Return a new `ArrayList<IAccount>` containing all values from the `accounts` map.
- **Why:** Provides a complete snapshot of all active accounts in memory.
- **Hint:** `new ArrayList<>(accounts.values())`.

### STEP 5: Implement update(IAccount account)
- **Where:** `src/com/gdb/repository/InMemoryAccountRepository.java` — method `update(IAccount)`
- **What:** If the account exists in the map, replace the existing entry with the updated `account`.
- **Why:** Updates modified account state (balances, status) in storage.
- **Hint:** Check `accounts.containsKey(account.getAccountNumber())` before putting.

### STEP 6: Implement delete(int accountNumber)
- **Where:** `src/com/gdb/repository/InMemoryAccountRepository.java` — method `delete(int)`
- **What:** Remove the entry with key `accountNumber` from the `accounts` map.
- **Why:** Supports account closure and deletion workflows.
- **Hint:** `accounts.remove(accountNumber)`.

### STEP 7: Implement exists(int accountNumber)
- **Where:** `src/com/gdb/repository/InMemoryAccountRepository.java` — method `exists(int)`
- **What:** Return `true` if `accountNumber` is present in the `accounts` map; `false` otherwise.
- **Why:** Fast existence check without retrieving full entity.
- **Hint:** `accounts.containsKey(accountNumber)`.

### STEP 8: Implement nextAccountNumber()
- **Where:** `src/com/gdb/repository/InMemoryAccountRepository.java` — method `nextAccountNumber()`
- **What:** Return the current value of `nextAccountNumber` and increment it by 1 in a thread-safe synchronized manner.
- **Why:** Generates unique, sequential account numbers.
- **Hint:** Return `nextAccountNumber++`.

### STEP 9: Declare Storage in InMemoryTransactionRepository
- **Where:** `src/com/gdb/repository/InMemoryTransactionRepository.java` — class body
- **What:** Declare a `private final List<Transaction> transactions = new ArrayList<>()`.
- **Why:** Stores historical transaction audit entries in memory.
- **Hint:** Use a standard `ArrayList`.

### STEP 10: Implement save(Transaction transaction)
- **Where:** `src/com/gdb/repository/InMemoryTransactionRepository.java` — method `save(Transaction)`
- **What:** Append the non-null `transaction` to the internal `transactions` list.
- **Why:** Records financial operations for audit and reporting.
- **Hint:** `transactions.add(transaction)`.

### STEP 11: Implement findByAccount(int accountNumber)
- **Where:** `src/com/gdb/repository/InMemoryTransactionRepository.java` — method `findByAccount(int)`
- **What:** Filter the `transactions` list to find all records where `account_number`, `from_account`, or `to_account` equals `accountNumber`.
- **Why:** Fetches statement history for a specific customer account.
- **Hint:** Use `transactions.stream().filter(...).collect(Collectors.toList())`.

### STEP 12: Implement findAll() and clear()
- **Where:** `src/com/gdb/repository/InMemoryTransactionRepository.java` — methods `findAll()`, `clear()`
- **What:** `findAll()` returns a copy of `transactions`; `clear()` removes all entries from the list.
- **Why:** Allows full ledger export and resetting for automated test isolation.
- **Hint:** `transactions.clear()`.

### STEP 13: Implement transfer(int fromAccount, int toAccount, double amount)
- **Where:** `src/com/gdb/repository/InMemoryTransactionRepository.java` — method `transfer(int, int, double)`
- **What:** Create and record a `Transaction` of type `TRANSFER` linking `fromAccount` and `toAccount`.
- **Why:** Ensures atomic transfer ledger logging in the transaction repository.
- **Hint:** Instantiate `Transaction` and pass to `save(txn)`.

### STEP 14: Implement RepositoryFactory Mode Dispatch
- **Where:** `src/com/gdb/repository/RepositoryFactory.java` — method `getAccountRepository()`, `getTransactionRepository()`
- **What:** Read `persistence.mode` from `config/persistence.properties` (or default to `memory`) and return singleton instances of `InMemoryAccountRepository` and `InMemoryTransactionRepository`.
- **Why:** Centralizes repository lifecycle and decoupling from caller classes.
- **Hint:** Use static fields for cached repository instances.

## 7. Acceptance Criteria
- [ ] `InMemoryAccountRepository` stores accounts and retrieves them accurately by `accountNumber`.
- [ ] `findAll()` returns a defensive copy of all stored accounts without exposing internal collections.
- [ ] `nextAccountNumber()` increments sequentially starting from 1001.
- [ ] `InMemoryTransactionRepository` stores transactions and filters by account number correctly.
- [ ] `RepositoryFactory` creates in-memory repositories when `persistence.mode=memory`.
- [ ] `TestRepositoryInMemory` completes with 100% assertions passing without throwing unhandled exceptions.

## 8. Expected Output

Running `com.gdb.tests.TestRepositoryInMemory`:
```text
============================================================
  ACTIVITY 21 — REPOSITORY PATTERN IN-MEMORY TESTS
============================================================
[TEST 1] Account Repository CRUD:
  Saved: Account #1001 | Rajesh Sharma (30 yrs, Tenure: 0 yrs) | Savings | Rs. 50000.0 | Active
  Found: Account #1001 | Rajesh Sharma (30 yrs, Tenure: 0 yrs) | Savings | Rs. 50000.0 | Active
  Updated balance: Rs. 65000.0
  Total accounts in repository: 2
  -> PASSED

[TEST 2] Transaction Repository Operations:
  Saved 2 transaction records.
  Found transactions for #1001: 2
  -> PASSED

[TEST 3] Service Integration with Repositories:
? Loading account rules from properties files...
--------------------------------------------------
? Loaded rules for: SAVINGS (4 tenure buckets)
? Loaded rules for: CURRENT (3 tenure buckets)
? Loaded rules for: FIXEDDEPOSIT (4 tenure buckets)
? Loaded rules for: SALARY (4 tenure buckets)
--------------------------------------------------
? All rules loaded successfully!
   Loaded at: 2026-09-19T20:15:00.123456700
   Account types: [SALARY, SAVINGS, FIXEDDEPOSIT, CURRENT]
  Opened account #1003 via AccountService.
  Deposit executed. New Balance: Rs. 25000.0
  -> PASSED

============================================================
  ALL ACTIVITY 21 IN-MEMORY REPOSITORY TESTS PASSED!
============================================================
```

## 9. How to Run the Commands

### Compile
```powershell
javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

### Run Test Suite
```powershell
java -ea -cp "bin;lib/*" com.gdb.tests.TestRepositoryInMemory
```

### Clean Up
```powershell
Remove-Item -Recurse -Force bin
```

> **Verification**: Check all checkboxes in [Section 7 (Acceptance Criteria)](#7-acceptance-criteria) once test execution outputs all tests passing.
