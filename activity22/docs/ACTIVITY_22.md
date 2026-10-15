# Activity 22: JDBC – Connection Management and Schema Initialization

## 1. Title
# Activity 22: JDBC – Connection Management and Schema Initialization

## 2. Description
Enterprise banking systems such as ICICI Bank and Axis Bank rely heavily on relational database management systems (RDBMS) like Oracle, PostgreSQL, and SQLite for mission-critical transactional persistence. Direct file or memory storage is inadequate when data integrity, ACID compliance, and crash-recovery guarantees are mandatory.

In this activity, you will establish the foundational database infrastructure for Global Digital Bank using Java Database Connectivity (JDBC). You will define a `ConnectionProvider` abstraction to isolate how JDBC connections are acquired, implement `JdbcConnectionProvider` to connect to SQLite databases, and build `SchemaInitializer` to execute SQL DDL scripts that create the `accounts` and `transactions` tables.

Establishing clean database connectivity and automated schema initialization ensures that subsequent activities can implement JDBC repositories and transactions reliably across various relational backends.

By the end of this activity, you will be able to: load JDBC drivers dynamically, manage database connections using standard JDBC interfaces, execute automated schema initialization scripts, and verify database tables.

## 3. Topic Coverage
- **Java concepts**
  - Java Database Connectivity (JDBC) API (`DriverManager`, `Connection`, `Statement`)
  - Resource management with `try-with-resources`
  - SQL execution via `Statement.execute()`
  - JDBC URL formatting (`jdbc:sqlite:<filepath>`)
  - Exception handling with `SQLException` and custom `DataAccessException`
- **Design patterns / architecture**
  - Provider Pattern (`ConnectionProvider`) for connection abstraction
  - Schema Migration / Initialization pattern
  - Separation of Concerns between infrastructure and repositories
- **Domain concepts**
  - Relational schema design for accounts and transactions
  - Table primary keys, foreign key relationships, and column constraints
  - DDL schema creation scripts (`schema.sql`)

## 4. Steps to Run the Activity
1. Navigate to the activity directory:
   ```powershell
   cd c:\Users\DELL\Downloads\gdb-Activities\trainee_multi-lang-activities\java-gdb-activities\activity22
   ```
2. Compile all source files into the `bin/` directory:
   ```powershell
   javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
   ```
3. Run the JDBC connection and schema verification test:
   ```powershell
   java -ea -cp "bin;lib/*" com.gdb.tests.TestJdbcConnection
   ```
4. Verify that table schemas and connectivity tests pass with green status.

## 5. Target Files, Classes, Methods, Interfaces, Repos, Services

| Type | Name | Package | Status | Notes |
|------|------|---------|--------|-------|
| interface | `ConnectionProvider` | `com.gdb.db` | PROVIDED | Defines contract for acquiring JDBC `Connection` objects |
| class | `JdbcConnectionProvider` | `com.gdb.db` | UPDATE | Implementation connecting via SQLite JDBC `DriverManager` |
| class | `SchemaInitializer` | `com.gdb.db` | UPDATE | Executes DDL statements from `schema.sql` |
| class | `RepositoryFactory` | `com.gdb.repository` | UPDATE | Updated to wire `ConnectionProvider` from properties |
| test | `TestJdbcConnection` | `com.gdb.tests` | UPDATE | Verifies connection establishment and schema integrity |

## 6. Step-by-Step Instructions of Implementation

### STEP 1: Implement JdbcConnectionProvider
- **Where:** `src/com/gdb/db/JdbcConnectionProvider.java` — constructor and `getConnection()`
- **What:** In constructor, store `jdbcUrl` and load driver class `org.sqlite.JDBC`. In `getConnection()`, call `DriverManager.getConnection(jdbcUrl)`.
- **Why:** Establishes direct connection to the SQLite database instance.
- **Hint:** Use `Class.forName("org.sqlite.JDBC")` inside constructor.

### STEP 2: Implement SchemaInitializer
- **Where:** `src/com/gdb/db/SchemaInitializer.java` — method `initialize(ConnectionProvider provider)`
- **What:** Obtain a connection from `provider`, create a `Statement`, and execute DDL queries creating `accounts` and `transactions` tables if they do not exist.
- **Why:** Automatically prepares database tables on startup before repository operations execute.
- **Hint:** Use `CREATE TABLE IF NOT EXISTS accounts (...)`.

### STEP 3: Wire ConnectionProvider in RepositoryFactory
- **Where:** `src/com/gdb/repository/RepositoryFactory.java` — method `getConnectionProvider()`
- **What:** If `persistence.mode=jdbc`, instantiate and cache `JdbcConnectionProvider` using `db.url` from properties.
- **Why:** Centralizes database connectivity configuration across all repositories.
- **Hint:** Return existing instance if already initialized.

## 7. Acceptance Criteria
- [ ] `JdbcConnectionProvider` successfully loads the SQLite driver and returns a valid `Connection`.
- [ ] `SchemaInitializer` executes DDL statements without throwing syntax or execution errors.
- [ ] Table `accounts` contains all required columns: `account_number`, `account_holder_name`, `age`, `balance`, `account_type`, `status`, `pin`, `opening_date`.
- [ ] Table `transactions` contains columns: `id`, `timestamp`, `account_number`, `type`, `amount`, `balance_after`, `status`, `remarks`, `from_account`, `to_account`.
- [ ] Running `TestJdbcConnection` verifies table creation and reports all tests passed.

## 8. Expected Output

Running `com.gdb.tests.TestJdbcConnection`:
```text
============================================================
  ACTIVITY 22 — JDBC CONNECTION & SCHEMA INITIALIZATION
============================================================
[TEST 1] Database Connection Establishment:
  Connected to: jdbc:sqlite:test_gdb.db
  Driver Name: SQLite JDBC
  -> PASSED

[TEST 2] Schema DDL Execution:
  Creating tables: accounts, transactions...
  Tables initialized successfully.
  -> PASSED

[TEST 3] Schema Verification:
  Table 'accounts' exists: true
  Table 'transactions' exists: true
  -> PASSED

============================================================
  ALL ACTIVITY 22 JDBC CONNECTION TESTS PASSED!
============================================================
```

## 9. How to Run the Commands

### Compile
```powershell
javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

### Run Test Suite
```powershell
java -ea -cp "bin;lib/*" com.gdb.tests.TestJdbcConnection
```

### Clean Up
```powershell
Remove-Item -Recurse -Force bin, test_gdb.db
```

> **Verification**: Check all checkboxes in [Section 7 (Acceptance Criteria)](#7-acceptance-criteria) once test execution passes.
