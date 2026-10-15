# Activity 24: JdbcTransactionRepository & Data Migration

## Overview
This activity introduces relational persistence for financial transactions using `JdbcTransactionRepository` and provides a `DataMigrator` utility to transition transaction logs from memory/file storage into SQLite.

---

## Files Included
| File | Description |
|---|---|
| `src/com/gdb/repository/JdbcTransactionRepository.java` | Relational transaction repository backed by SQLite JDBC |
| `src/com/gdb/db/DataMigrator.java` | Data migration utility transferring legacy logs into `TransactionRepository` |
| `src/com/gdb/repository/RepositoryFactory.java` | Factory supplying `JdbcTransactionRepository` when in JDBC mode |
| `src/com/gdb/tests/TestJdbcTransactionRepository.java` | Test suite validating transaction queries and migration pipeline |
| `docs/ACTIVITY_24.md` | Comprehensive lab documentation with student instructions and diagrams |

---

## How to Compile & Run

### Windows (PowerShell)
```powershell
New-Item bin -ItemType Directory -Force | Out-Null
Copy-Item src\mainesources\* bin -Recurse -Force
javac -encoding UTF-8 -cp "lib/*;." -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
java -cp "lib/*;bin" com.gdb.tests.TestJdbcTransactionRepository
```

### Linux & macOS (Terminal / Bash)
```bash
mkdir -p bin && cp -r src/main/resources/* bin/
javac -encoding UTF-8 -cp "lib/*:." -d bin $(find src -name "*.java")
java -cp "lib/*:bin" com.gdb.tests.TestJdbcTransactionRepository
```
