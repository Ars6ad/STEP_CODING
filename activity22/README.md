# Activity 22: JDBC Foundation (Connection, Schema, Provider)

## Overview
This activity establishes the relational database foundation for GDB by integrating **SQLite JDBC**. Students abstract database connection acquisition using `ConnectionProvider` and `JdbcConnectionProvider`, define relational tables in `schema.sql`, create an idempotent `SchemaInitializer`, and update `RepositoryFactory` to support JDBC mode.

---

## Files Included
| File | Description |
|---|---|
| `lib/sqlite-jdbc-3.44.1.0.jar` (+ SLF4J jars) | SQLite JDBC Driver and logging runtime libraries |
| `src/com/gdb/db/ConnectionProvider.java` | Interface abstracting connection acquisition (`getConnection()`, `shutdown()`, `getProviderName()`) |
| `src/com/gdb/db/JdbcConnectionProvider.java` | Concrete connection provider using `DriverManager.getConnection(url)` |
| `src/main/resources/schema.sql` | DDL script with idempotent definitions for `accounts` and `transactions` tables |
| `src/com/gdb/db/SchemaInitializer.java` | Helper loading and executing `schema.sql` statements idempotently |
| `src/main/resources/config/persistence.properties` | Configured with `persistence.mode=jdbc`, `persistence.db.url=jdbc:sqlite:gdb.db`, and `persistence.db.driver=org.sqlite.JDBC` |
| `src/com/gdb/repository/RepositoryFactory.java` | Updated JDBC factory branch initializing `JdbcConnectionProvider` and invoking `SchemaInitializer` |
| `src/com/gdb/tests/TestJdbcConnection.java` | Automated test suite verifying connection lifecycle, `SELECT 1`, schema deployment, and table existence |
| `docs/ACTIVITY_22.md` | Comprehensive lab documentation with student instructions and architecture diagrams |

---

## How to Compile & Run

### Windows (PowerShell)
```powershell
New-Item bin -ItemType Directory -Force | Out-Null
Copy-Item src\mainesources\* bin -Recurse -Force
javac -encoding UTF-8 -cp "lib/*;." -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
java -cp "lib/*;bin" com.gdb.tests.TestJdbcConnection
```

### Linux & macOS (Terminal / Bash)
```bash
mkdir -p bin && cp -r src/main/resources/* bin/
javac -encoding UTF-8 -cp "lib/*:." -d bin $(find src -name "*.java")
java -cp "lib/*:bin" com.gdb.tests.TestJdbcConnection
```
