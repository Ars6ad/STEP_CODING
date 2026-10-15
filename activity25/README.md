# Activity 25: Connection Pooling (NFR - Non-Functional Requirements)

## Overview
This activity replaces direct `DriverManager` connection calls with **HikariCP Connection Pooling**, enhancing application scalability, connection reusability, and latency characteristics under high concurrency.

---

## Files Included
| File | Description |
|---|---|
| `lib/HikariCP-5.0.1.jar` | High-performance JDBC connection pool library |
| `src/com/gdb/db/HikariConnectionProvider.java` | Connection pool implementation backed by `HikariDataSource` |
| `src/main/resources/config/persistence.properties` | Configured with `persistence.pool.enabled=true`, pool size, and timeouts |
| `src/com/gdb/repository/RepositoryFactory.java` | Factory dynamically selecting `HikariConnectionProvider` when pool is enabled |
| `src/com/gdb/tests/TestConnectionPooling.java` | Test suite verifying pool lifecycle, concurrent usage, and performance benchmarks |
| `docs/ACTIVITY_25.md` | Student lab guide with architecture diagrams, instructions, and benchmarks |

---

## How to Compile & Run

### Windows (PowerShell)
```powershell
New-Item bin -ItemType Directory -Force | Out-Null
Copy-Item src\mainesources\* bin -Recurse -Force
javac -encoding UTF-8 -cp "lib/*;." -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
java -cp "lib/*;bin" com.gdb.tests.TestConnectionPooling
```

### Linux & macOS (Terminal / Bash)
```bash
mkdir -p bin && cp -r src/main/resources/* bin/
javac -encoding UTF-8 -cp "lib/*:." -d bin $(find src -name "*.java")
java -cp "lib/*:bin" com.gdb.tests.TestConnectionPooling
```
