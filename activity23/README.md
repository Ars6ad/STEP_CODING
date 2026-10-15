# Activity 23: JdbcAccountRepository (CRUD + Atomic Transfer)

## Overview
This activity delivers complete relational persistence for accounts using **JDBC PreparedStatements** and **transactional ACID boundaries**. Students build `JdbcAccountRepository` implementing full CRUD routines, ResultSet row mapping, and atomic multi-step balance transfers with rollback safety.

---

## Files Included
| File | Description |
|---|---|
| `src/com/gdb/exceptions/DataAccessException.java` | Unchecked runtime exception wrapping SQL errors |
| `src/com/gdb/repository/AccountRepository.java` | Updated interface declaring account CRUD and atomic transfer |
| `src/com/gdb/repository/JdbcAccountRepository.java` | Relational repository implementation backed by SQLite JDBC |
| `src/com/gdb/repository/RepositoryFactory.java` | Updated factory supplying `JdbcAccountRepository` when in JDBC mode |
| `src/com/gdb/tests/TestJdbcAccountRepository.java` | Comprehensive test driver validating CRUD, atomic transfers, and rollback |
| `docs/ACTIVITY_23.md` | Student lab guide with instructions, architecture diagrams, and exercises |

---

## How to Compile & Run

### Windows (PowerShell)
```powershell
New-Item bin -ItemType Directory -Force | Out-Null
Copy-Item src\mainesources\* bin -Recurse -Force
javac -encoding UTF-8 -cp "lib/*;." -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
java -cp "lib/*;bin" com.gdb.tests.TestJdbcAccountRepository
```

### Linux & macOS (Terminal / Bash)
```bash
mkdir -p bin && cp -r src/main/resources/* bin/
javac -encoding UTF-8 -cp "lib/*:." -d bin $(find src -name "*.java")
java -cp "lib/*:bin" com.gdb.tests.TestJdbcAccountRepository
```
