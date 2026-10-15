# Activity 23: JDBC – Account Repository Implementation

## 1. Title
# Activity 23: JDBC – Account Repository Implementation

## 2. Description
In production banking applications like HDFC and SBI, account data must be securely saved, retrieved, updated, and deleted from persistent relational database tables. Relational storage ensures that customer records and financial balances persist safely across application restarts and power failures.

In this activity, you will implement `JdbcAccountRepository` to fulfill the `AccountRepository` contract using parameterized SQL queries and `PreparedStatement` objects. You will implement complete CRUD functionality (Create, Read, Update, Delete) against the SQLite database initialized in Activity 22.

Following the Separation of Concerns (SoC) principle, `JdbcAccountRepository` focuses purely on single-entity account management. Multi-account transactional logic is delegated to the transaction layer.

By the end of this activity, you will be able to: use `PreparedStatement` to prevent SQL injection, map `ResultSet` rows into domain objects (`IAccount`), implement database CRUD operations, and handle SQL exceptions using custom domain exceptions.

## 3. Topic Coverage
- **Java concepts**
  - Parameterized SQL execution with `PreparedStatement`
  - SQL result extraction using `ResultSet`
  - Safe resource management with `try-with-resources`
  - Safe exception translation from `SQLException` to `DataAccessException`
  - Object-relational mapping (ORM) helper methods
- **Design patterns / architecture**
  - Repository Pattern implementation with JDBC
  - Data Mapper Pattern (`mapRow`)
  - Separation of Concerns: pure single-entity account operations
- **Domain concepts**
  - Account persistence and state lifecycle (Active/Inactive)
  - Account number auto-generation using SQL `MAX(account_number)`
  - Parameter binding and SQL injection defense

## 4. Steps to Run the Activity
1. Navigate to the activity directory:
   ```powershell
   cd c:\Users\DELL\Downloads\gdb-Activities\trainee_multi-lang-activities\java-gdb-activities\activity23
   ```
2. Compile all source files into the `bin/` directory:
   ```powershell
   javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
   ```
3. Run the JDBC account repository test suite:
   ```powershell
   java -ea -cp "bin;lib/*" com.gdb.tests.TestJdbcAccountRepository
   ```
4. Verify that all CRUD operations execute and pass against the SQLite database.

## 5. Target Files, Classes, Methods, Interfaces, Repos, Services

| Type | Name | Package | Status | Notes |
|------|------|---------|--------|-------|
| interface | `AccountRepository` | `com.gdb.repository` | PROVIDED | Pure CRUD contract for accounts (no transfer method) |
| class | `JdbcAccountRepository` | `com.gdb.repository` | NEW | JDBC implementation of `AccountRepository` |
| class | `RepositoryFactory` | `com.gdb.repository` | PROVIDED | Returns `JdbcAccountRepository` in `jdbc` mode |
| test | `TestJdbcAccountRepository` | `com.gdb.tests` | NEW | Verification suite for JDBC account operations |

## 6. Step-by-Step Instructions of Implementation

### STEP 1: Implement save(IAccount account)
- **Where:** `src/com/gdb/repository/JdbcAccountRepository.java` — method `save(IAccount)`
- **What:** Guard against null. Execute `INSERT INTO accounts (account_number, account_holder_name, age, balance, account_type, status, pin, opening_date, overdraft_used, interest_earned) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`.
- **Why:** Persists new account records into the relational database.
- **Hint:** Bind each field using `ps.setInt()`, `ps.setString()`, `ps.setDouble()`.

### STEP 2: Implement findById(int accountNumber)
- **Where:** `src/com/gdb/repository/JdbcAccountRepository.java` — method `findById(int)`
- **What:** Execute `SELECT * FROM accounts WHERE account_number = ?`. If a row exists, return `mapRow(rs)`; otherwise return `null`.
- **Why:** Enables primary key lookups for account information.
- **Hint:** Execute query with `ps.executeQuery()` inside try-with-resources.

### STEP 3: Implement findAll()
- **Where:** `src/com/gdb/repository/JdbcAccountRepository.java` — method `findAll()`
- **What:** Execute `SELECT * FROM accounts`. Iterate through the `ResultSet` and append each mapped account to a `List<IAccount>`.
- **Why:** Fetches all customer accounts from storage.
- **Hint:** Use `while (rs.next()) list.add(mapRow(rs))`.

### STEP 4: Implement update(IAccount account)
- **Where:** `src/com/gdb/repository/JdbcAccountRepository.java` — method `update(IAccount)`
- **What:** Guard against null. Execute `UPDATE accounts SET balance = ?, status = ?, overdraft_used = ?, interest_earned = ? WHERE account_number = ?`.
- **Why:** Updates balance and state changes after transactions.
- **Hint:** Use `ps.executeUpdate()`.

### STEP 5: Implement delete(int accountNumber)
- **Where:** `src/com/gdb/repository/JdbcAccountRepository.java` — method `delete(int)`
- **What:** Execute `DELETE FROM accounts WHERE account_number = ?`.
- **Why:** Removes account records when permanently deleted.
- **Hint:** Bind `accountNumber` to parameter index 1.

### STEP 6: Implement exists(int accountNumber)
- **Where:** `src/com/gdb/repository/JdbcAccountRepository.java` — method `exists(int)`
- **What:** Execute `SELECT 1 FROM accounts WHERE account_number = ?`. Return `rs.next()`.
- **Why:** Fast boolean check without full entity mapping overhead.
- **Hint:** Return `rs.next()`.

### STEP 7: Implement nextAccountNumber()
- **Where:** `src/com/gdb/repository/JdbcAccountRepository.java` — method `nextAccountNumber()`
- **What:** Execute `SELECT COALESCE(MAX(account_number), 1000) + 1 AS next_id FROM accounts`. Return `next_id`.
- **Why:** Automatically generates sequential primary key account numbers.
- **Hint:** Return 1001 if table is empty.

## 7. Acceptance Criteria
- [ ] `save()` inserts a new account row into `accounts` table without errors.
- [ ] `findById()` retrieves the accurate account entity by its account number.
- [ ] `update()` modifies the stored balance in SQLite database correctly.
- [ ] `delete()` removes the specified account record.
- [ ] `exists()` returns `true` for existing accounts and `false` for nonexistent accounts.
- [ ] Running `TestJdbcAccountRepository` outputs all tests passed with zero assertion failures.

## 8. Expected Output

Running `com.gdb.tests.TestJdbcAccountRepository`:
```text
============================================================
  ACTIVITY 23 — JDBC ACCOUNT REPOSITORY CRUD TESTS
============================================================
[TEST 1] Save & FindById:
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
  Found: Account #1001 | Rajesh Sharma (25 yrs, Tenure: 0 yrs) | Savings | Rs. 50000.0 | Active
  -> PASSED

[TEST 2] Update & FindAll:
  Updated balance: Rs. 65000.0 (Expected: 65000.0)
  Total accounts in DB: 2 (Expected: 2)
  -> PASSED

[TEST 3] Delete & Exists:
  Account 1002 successfully deleted.
  -> PASSED

============================================================
  ALL ACTIVITY 23 JDBC ACCOUNT REPOSITORY TESTS PASSED!
============================================================
```

## 9. How to Run the Commands

### Compile
```powershell
javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

### Run Test Suite
```powershell
java -ea -cp "bin;lib/*" com.gdb.tests.TestJdbcAccountRepository
```

### Clean Up
```powershell
Remove-Item -Recurse -Force bin, test_acc_gdb.db
```

> **Verification**: Check all checkboxes in [Section 7 (Acceptance Criteria)](#7-acceptance-criteria) once test execution passes.
