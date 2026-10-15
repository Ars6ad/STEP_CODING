# Activity 25: JDBC – Connection Pooling with HikariCP

## 1. Title
# Activity 25: JDBC – Connection Pooling with HikariCP

## 2. Description
In high-volume banking systems like Axis Bank and SBI, opening a physical database connection for every single transaction is extremely expensive and causes high latency. Establishing a TCP connection, performing authentication handshakes, and allocating server memory can take tens of milliseconds per request. To achieve thousands of transactions per second (TPS), production banking platforms utilize connection pools.

In this activity, you will implement database connection pooling in the Global Digital Bank architecture. You will integrate the industry-standard HikariCP connection pool library, create `HikariConnectionProvider`, and compare performance characteristics between unpooled direct connections and pooled connections under load.

By pre-allocating and reusing a fixed set of physical connections, your banking services can process concurrent financial operations with sub-millisecond connection acquisition times.

By the end of this activity, you will be able to: configure HikariCP DataSource properties (pool size, connection timeout, idle timeout), implement connection pooling providers, and measure throughput gains from connection reuse.

## 3. Topic Coverage
- **Java concepts**
  - Database Connection Pooling concepts and lifecycle
  - `javax.sql.DataSource` interface integration
  - HikariCP configuration parameters (`maximumPoolSize`, `connectionTimeout`, `idleTimeout`)
  - Benchmarking connection acquisition latency
  - Resource cleanup and pool shutdown
- **Design patterns / architecture**
  - Object Pool Pattern
  - Provider Pattern for pluggable connection pooling
  - Adapter Pattern wrapping `HikariDataSource` into `ConnectionProvider`
- **Domain concepts**
  - High-throughput transaction processing
  - Database resource limits and concurrency throttling
  - Connection leak prevention

## 4. Steps to Run the Activity
1. Navigate to the activity directory:
   ```powershell
   cd c:\Users\DELL\Downloads\gdb-Activities\trainee_multi-lang-activities\java-gdb-activities\activity25
   ```
2. Compile all source files into the `bin/` directory:
   ```powershell
   javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
   ```
3. Run the connection pooling benchmark and verification test:
   ```powershell
   java -ea -cp "bin;lib/*" com.gdb.tests.TestConnectionPooling
   ```
4. Verify that connection acquisition latency under HikariCP is significantly faster than unpooled connections.

## 5. Target Files, Classes, Methods, Interfaces, Repos, Services

| Type | Name | Package | Status | Notes |
|------|------|---------|--------|-------|
| interface | `ConnectionProvider` | `com.gdb.db` | PROVIDED | Base connection provider interface |
| class | `HikariConnectionProvider` | `com.gdb.db` | NEW | Connection pool provider using HikariCP `HikariDataSource` |
| class | `JdbcConnectionProvider` | `com.gdb.db` | PROVIDED | Unpooled direct connection provider for baseline comparison |
| class | `RepositoryFactory` | `com.gdb.repository` | UPDATE | Configured to switch between unpooled and pooled JDBC providers |
| test | `TestConnectionPooling` | `com.gdb.tests` | NEW | Benchmark and correctness test suite |

## 6. Step-by-Step Instructions of Implementation

### STEP 1: Configure HikariConfig
- **Where:** `src/com/gdb/db/HikariConnectionProvider.java` — constructor
- **What:** Instantiate `HikariConfig`, set `jdbcUrl`, `maximumPoolSize` (default 10), `connectionTimeout` (30000ms), and `idleTimeout` (600000ms).
- **Why:** Configures pool size and timeout thresholds suited for banking workloads.
- **Hint:** `config.setJdbcUrl(jdbcUrl); config.setMaximumPoolSize(poolSize);`.

### STEP 2: Instantiate HikariDataSource
- **Where:** `src/com/gdb/db/HikariConnectionProvider.java` — constructor
- **What:** Initialize `private final HikariDataSource dataSource = new HikariDataSource(config)`.
- **Why:** Creates the active connection pool instance.
- **Hint:** Wrap in `try-catch` to throw `DataAccessException` on initialization failure.

### STEP 3: Implement getConnection() and close()
- **Where:** `src/com/gdb/db/HikariConnectionProvider.java` — methods `getConnection()`, `close()`
- **What:** `getConnection()` returns `dataSource.getConnection()`. `close()` calls `dataSource.close()` to release physical pool resources.
- **Why:** Borrows pooled connections on demand and cleanly shuts down on exit.
- **Hint:** Returning connection from pool is $O(1)$ fast.

### STEP 4: Wire Hikari in RepositoryFactory
- **Where:** `src/com/gdb/repository/RepositoryFactory.java` — method `getConnectionProvider()`
- **What:** Check `db.pool.enabled=true` in `persistence.properties`. If enabled, return cached `HikariConnectionProvider`.
- **Why:** Allows zero-code toggling between direct and pooled connections.
- **Hint:** Read `db.pool.size` property if present.

## 7. Acceptance Criteria
- [ ] `HikariConnectionProvider` initializes a valid `HikariDataSource` with configured pool limits.
- [ ] `getConnection()` successfully delivers working database connections.
- [ ] Closing a borrowed `Connection` returns it to the pool rather than terminating the physical TCP socket.
- [ ] Benchmark confirms pooled connection reuse is measurably faster than unpooled connection creation.
- [ ] Running `TestConnectionPooling` passes all functional and latency assertions.

## 8. Expected Output

Running `com.gdb.tests.TestConnectionPooling`:
```text
============================================================
  ACTIVITY 25 — JDBC CONNECTION POOLING (HIKARICP)
============================================================
[TEST 1] HikariCP Connection Pool Initialization:
  Pool Name: HikariPool-1
  Max Pool Size: 10
  Connection acquired: true
  -> PASSED

[TEST 2] Latency Benchmark (100 sequential connection cycles):
  Unpooled Direct Connections: 245 ms (avg 2.45 ms/op)
  HikariCP Pooled Connections:  18 ms (avg 0.18 ms/op)
  Speedup Factor: ~13.6x faster!
  -> PASSED

[TEST 3] Repository Operations over HikariCP:
  Saved Account #1001 via pooled repository.
  Retrieved Account #1001: Balance Rs. 50000.0
  -> PASSED

============================================================
  ALL ACTIVITY 25 CONNECTION POOLING TESTS PASSED!
============================================================
```

## 9. How to Run the Commands

### Compile
```powershell
javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

### Run Test Suite
```powershell
java -ea -cp "bin;lib/*" com.gdb.tests.TestConnectionPooling
```

### Clean Up
```powershell
Remove-Item -Recurse -Force bin, test_pool_gdb.db
```

> **Verification**: Check all checkboxes in [Section 7 (Acceptance Criteria)](#7-acceptance-criteria) once test execution passes.
