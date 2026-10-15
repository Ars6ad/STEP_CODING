# Activity 24: JDBC – Transaction Repository and Data Migration

## 1. Title
# Activity 24: JDBC – Transaction Repository and Data Migration

## 2. Description
In commercial banking systems like SBI and ICICI, transferring money between accounts requires strict ACID transactional guarantees. If money is deducted from a sender's account but the system crashes before crediting the receiver, the entire transaction must automatically roll back to prevent financial loss. Furthermore, banks regularly migrate legacy ledger data from flat files or in-memory caches into persistent SQL databases.

In this activity, you will implement `JdbcTransactionRepository` to manage transaction audit logging and execute atomic multi-account transfers using manual JDBC transaction control (`conn.setAutoCommit(false)`, `conn.commit()`, and `conn.rollback()`). You will also build `DataMigrator` to copy legacy in-memory accounts and transaction records into relational database tables without losing data.

Following Separation of Concerns, `JdbcTransactionRepository` owns multi-account atomic transfers and ledger logging, keeping `AccountRepository` focused purely on single-entity account CRUD operations.

By the end of this activity, you will be able to: manage database transactions with commit and rollback, execute atomic balance transfers across accounts, and migrate data between different repository implementations.

## 3. Topic Coverage
- **Java concepts**
  - Database Transaction management (`Connection.setAutoCommit(false)`, `commit()`, `rollback()`)
  - SQL atomic multi-statement execution
  - Date and time formatting with `java.time.LocalDateTime`
  - Safe exception propagation with rollback in `catch` blocks
- **Design patterns / architecture**
  - Data Migration Pattern (`DataMigrator`)
  - Repository Pattern with transactional operations
  - Separation of Concerns: Transaction Repository managing atomic multi-account transfers
- **Domain concepts**
  - ACID Transaction guarantees (Atomicity, Consistency, Isolation, Durability)
  - Debit-credit balance consistency
  - Financial transaction audit trail

## 4. Steps to Run the Activity
1. Navigate to the activity directory:
   ```powershell
   cd c:\Users\DELL\Downloads\gdb-Activities\trainee_multi-lang-activities\java-gdb-activities\activity24
   ```
2. Compile all source files into the `bin/` directory:
   ```powershell
   javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
   ```
3. Run the JDBC transaction repository and migration test:
   ```powershell
   java -ea -cp "bin;lib/*" com.gdb.tests.TestJdbcTransactionRepository
   ```
4. Verify that atomic transfers and data migration tests pass completely.

## 5. Target Files, Classes, Methods, Interfaces, Repos, Services

| Type | Name | Package | Status | Notes |
|------|------|---------|--------|-------|
| interface | `TransactionRepository` | `com.gdb.repository` | PROVIDED | Defines transaction logging and `transfer(...)` contract |
| class | `JdbcTransactionRepository` | `com.gdb.repository` | NEW | JDBC implementation managing transactions and atomic transfers |
| class | `DataMigrator` | `com.gdb.db` | NEW | Utility migrating data between repository backends |
| class | `JdbcAccountRepository` | `com.gdb.repository` | PROVIDED | Single-entity account repository from Activity 23 |
| test | `TestJdbcTransactionRepository` | `com.gdb.tests` | NEW | Test suite for atomic transfers and migration |

## 6. Step-by-Step Instructions of Implementation

### STEP 1: Implement save(Transaction transaction)
- **Where:** `src/com/gdb/repository/JdbcTransactionRepository.java` — method `save(Transaction)`
- **What:** Execute `INSERT INTO transactions (id, timestamp, account_number, type, amount, balance_after, status, remarks, from_account, to_account) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`.
- **Why:** Records transaction history into the database ledger.
- **Hint:** Bind `transaction.getTimestamp().toString()`.

### STEP 2: Implement findByAccount(int accountNumber)
- **Where:** `src/com/gdb/repository/JdbcTransactionRepository.java` — method `findByAccount(int)`
- **What:** Execute `SELECT * FROM transactions WHERE account_number = ? OR from_account = ? OR to_account = ? ORDER BY timestamp ASC`.
- **Why:** Fetches statement history for any account involved in debits, credits, or transfers.
- **Hint:** Bind `accountNumber` to all three query parameters.

### STEP 3: Implement findAll() and clear()
- **Where:** `src/com/gdb/repository/JdbcTransactionRepository.java` — methods `findAll()`, `clear()`
- **What:** `findAll()` returns all rows mapped to `Transaction`; `clear()` executes `DELETE FROM transactions`.
- **Why:** Complete audit querying and test environment cleanup.
- **Hint:** Use `mapRow(rs)` in a loop.

### STEP 4: Implement Atomic transfer(int fromAcc, int toAcc, double amount)
- **Where:** `src/com/gdb/repository/JdbcTransactionRepository.java` — method `transfer(int, int, double)`
- **What:** 
  1. Acquire a connection from provider and set `conn.setAutoCommit(false)`.
  2. Verify sender balance has sufficient funds.
  3. Deduct `amount` from `fromAcc` (`UPDATE accounts SET balance = balance - ? WHERE account_number = ?`).
  4. Add `amount` to `toAcc` (`UPDATE accounts SET balance = balance + ? WHERE account_number = ?`).
  5. Insert the transfer transaction record into `transactions` table using the *same* connection.
  6. Call `conn.commit()`.
  7. If any error occurs, call `conn.rollback()` and throw `DataAccessException`.
- **Why:** Guarantees atomic money transfer across accounts.
- **Hint:** Do not acquire a second connection during the transaction.

### STEP 5: Implement DataMigrator
- **Where:** `src/com/gdb/db/DataMigrator.java` — method `migrate(AccountRepository srcAcc, AccountRepository destAcc, TransactionRepository srcTxn, TransactionRepository destTxn)`
- **What:** Loop over all accounts in `srcAcc` and save into `destAcc`. Loop over all transactions in `srcTxn` and save into `destTxn`. Return migration statistics summary.
- **Why:** Enables zero-downtime data migration between storage layers.
- **Hint:** Catch exceptions per item or in batch.

## 7. Acceptance Criteria
- [ ] `save()` records transaction details into `transactions` table accurately.
- [ ] `transfer()` deducts money from sender and credits receiver atomically.
- [ ] In case of insufficient funds or DB failure, `transfer()` rolls back all updates cleanly without modifying balances.
- [ ] `DataMigrator` migrates all in-memory accounts and transaction records to JDBC repositories.
- [ ] Running `TestJdbcTransactionRepository` reports 100% test pass status.

## 8. Expected Output

Running `com.gdb.tests.TestJdbcTransactionRepository`:
```text
============================================================
  ACTIVITY 24 — JDBC TRANSACTION REPOSITORY & MIGRATION
============================================================
[TEST 1] Transaction Save & Query:
  Saved 2 transaction records.
  Found transactions for Account #1001: 2
  -> PASSED

[TEST 2] Atomic Transfer Execution:
  Initial Sender Balance: Rs. 50000.0, Receiver Balance: Rs. 30000.0
  Executing atomic transfer of Rs. 10000.0...
  Updated Sender Balance: Rs. 40000.0
  Updated Receiver Balance: Rs. 40000.0
  Transfer audit transaction recorded in database.
  -> PASSED

[TEST 3] Atomic Rollback on Insufficient Balance:
  Attempting transfer of Rs. 999999.0 (exceeds balance)...
  Caught expected exception: Insufficient balance
  Sender Balance unchanged: Rs. 40000.0
  Receiver Balance unchanged: Rs. 40000.0
  -> PASSED

[TEST 4] Data Migration from Memory to JDBC:
  Migrated 3 accounts and 5 transaction records.
  Verified migrated accounts in SQLite database.
  -> PASSED

============================================================
  ALL ACTIVITY 24 JDBC TRANSACTION TESTS PASSED!
============================================================
```

## 9. How to Run the Commands

### Compile
```powershell
javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

### Run Test Suite
```powershell
java -ea -cp "bin;lib/*" com.gdb.tests.TestJdbcTransactionRepository
```

### Clean Up
```powershell
Remove-Item -Recurse -Force bin, test_txn_gdb.db
```

> **Verification**: Check all checkboxes in [Section 7 (Acceptance Criteria)](#7-acceptance-criteria) once test execution passes.
