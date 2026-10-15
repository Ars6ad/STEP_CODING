# Activity 26: File Repository – CSV, Atomic Writes, and Backups

## 1. Title
# Activity 26: File Repository – CSV, Atomic Writes, and Backups

## 2. Description
In edge banking terminals, micro-ATM devices, and disaster-recovery offline nodes, a full relational database server may not be available. In these constrained environments, financial records are stored directly on the local filesystem as structured CSV files. However, writing directly to disk carries significant risk: a sudden power failure or JVM crash in the middle of a write operation can corrupt the entire ledger file.

In this activity, you will build a crash-resilient file persistence layer for Global Digital Bank. You will implement `FileAccountRepository` and `FileTransactionRepository` to read and write records in standard CSV format, `CsvMapper` to serialize domain objects to CSV lines, `AtomicFileWriter` to prevent file corruption via atomic rename semantics (`.tmp` file writes followed by atomic swap), and `BackupManager` to maintain timestamped rotating backups before mutating files.

By incorporating atomic file operations and rotating backups, your file-based repository ensures zero data corruption even under sudden process termination.

By the end of this activity, you will be able to: serialize and parse CSV records, implement atomic file writing using `java.nio.file`, generate timestamped backup files, and implement repository interfaces over flat files.

## 3. Topic Coverage
- **Java concepts**
  - Java NIO File API (`Files`, `Path`, `Paths`, `StandardCopyOption`)
  - Crash-resilient Atomic write pattern (`.tmp` write and `ATOMIC_MOVE` rename)
  - String parsing and formatting for CSV records
  - Timestamp formatting with `java.time.LocalDateTime` and `DateTimeFormatter`
  - Defensive copying and memory cache synchronization
- **Design patterns / architecture**
  - Repository Pattern with file-based persistence
  - Data Mapper Pattern (`CsvMapper`)
  - Safety Decorator / Backup Strategy (`BackupManager`)
- **Domain concepts**
  - CSV schema serialization for accounts and transactions
  - Disaster recovery and rotating file backups
  - Power-loss resilience in offline banking nodes

## 4. Steps to Run the Activity
1. Navigate to the activity directory:
   ```powershell
   cd c:\Users\DELL\Downloads\gdb-Activities\trainee_multi-lang-activities\java-gdb-activities\activity26
   ```
2. Compile all source files into the `bin/` directory:
   ```powershell
   javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
   ```
3. Run the file repository verification suite:
   ```powershell
   java -ea -cp "bin;lib/*" com.gdb.tests.TestFileRepository
   ```
4. Verify that CSV parsing, atomic updates, and backup creation tests pass.

## 5. Target Files, Classes, Methods, Interfaces, Repos, Services

| Type | Name | Package | Status | Notes |
|------|------|---------|--------|-------|
| class | `AtomicFileWriter` | `com.gdb.file` | NEW | Performs atomic writes using temp files and atomic rename |
| class | `CsvMapper` | `com.gdb.file` | NEW | Serializes and deserializes accounts and transactions to CSV |
| class | `BackupManager` | `com.gdb.file` | NEW | Creates timestamped backups and purges old backups |
| class | `FileAccountRepository` | `com.gdb.repository` | NEW | File-based implementation of `AccountRepository` |
| class | `FileTransactionRepository` | `com.gdb.repository` | NEW | File-based implementation of `TransactionRepository` |
| test | `TestFileRepository` | `com.gdb.tests` | NEW | Comprehensive test suite for file storage and resilience |

## 6. Step-by-Step Instructions of Implementation

### STEP 1: Implement AtomicFileWriter
- **Where:** `src/com/gdb/file/AtomicFileWriter.java` — method `write(String path, List<String> lines)`
- **What:** Create parent directories if missing. Write lines to `path + ".tmp"`, flush and close writer, then move temp file to target path using `StandardCopyOption.ATOMIC_MOVE` (with fallback to `StandardCopyOption.REPLACE_EXISTING`).
- **Why:** Prevents half-written or corrupted files if process crashes during write.
- **Hint:** Use `Files.newBufferedWriter(...)` and `Files.move(...)`.

### STEP 2: Implement CsvMapper
- **Where:** `src/com/gdb/file/CsvMapper.java` — methods `toCsv(...)` and `fromCsv(...)`
- **What:** Convert `IAccount` and `Transaction` instances to comma-separated values and parse CSV rows back into domain objects.
- **Why:** Standardizes text file formatting and schema evolution.
- **Hint:** Handle comma escaping and string trimming.

### STEP 3: Implement BackupManager
- **Where:** `src/com/gdb/file/BackupManager.java` — method `backup(String sourceFile, int maxBackups)`
- **What:** If `sourceFile` exists, copy it to `sourceFile + ".bak." + timestamp`. If total backups exceed `maxBackups`, delete the oldest backup files.
- **Why:** Maintains roll-back history before any mutating write.
- **Hint:** Use `DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")`.

### STEP 4: Implement FileAccountRepository
- **Where:** `src/com/gdb/repository/FileAccountRepository.java` — CRUD methods
- **What:** Implement `save()`, `findById()`, `findAll()`, `update()`, `delete()`, `exists()`, and `nextAccountNumber()` by reading/writing `data/accounts.csv` through `AtomicFileWriter` and `CsvMapper`.
- **Why:** Delivers persistent account management over local CSV files.
- **Hint:** Synchronize read/write methods to protect against concurrent file access.

### STEP 5: Implement FileTransactionRepository
- **Where:** `src/com/gdb/repository/FileTransactionRepository.java` — methods `save()`, `findByAccount()`, `findAll()`, `transfer()`
- **What:** Implement transaction storage into `data/transactions.csv`. In `transfer()`, update balances in `accounts.csv` and append transfer record to `transactions.csv` atomically.
- **Why:** Ensures transaction ledger logging and atomic balance updates in file storage.
- **Hint:** Write updated account lines using `AtomicFileWriter`.

## 7. Acceptance Criteria
- [ ] `AtomicFileWriter` creates valid files without partial write corruption.
- [ ] `CsvMapper` accurately serializes and reconstructs `IAccount` and `Transaction` domain entities.
- [ ] `BackupManager` creates timestamped backup files and limits retention to `maxBackups`.
- [ ] `FileAccountRepository` performs CRUD operations and updates `accounts.csv` on disk.
- [ ] `FileTransactionRepository` records transactions and updates sender/receiver balances during transfers.
- [ ] Running `TestFileRepository` completes with all assertions passing.

## 8. Expected Output

Running `com.gdb.tests.TestFileRepository`:
```text
============================================================
  ACTIVITY 26 — FILE REPOSITORY (CSV + ATOMIC WRITES + BACKUP)
============================================================
[TEST 1] Account Save & Read Back from CSV:
? Loading account rules from properties files...
--------------------------------------------------
? All rules loaded successfully!
   Loaded at: 2026-09-19T20:15:00.123456700
   Account types: [SALARY, SAVINGS, FIXEDDEPOSIT, CURRENT]
  Saved: Account #1001 | Rajesh Sharma (30 yrs, Tenure: 0 yrs) | Savings | Rs. 50000.0 | Active
  Found: Account #1001 | Rajesh Sharma (25 yrs, Tenure: 0 yrs) | Savings | Rs. 50000.0 | Active
  -> PASSED

[TEST 2] Multiple Accounts & Atomic Update:
  Updated balance: Rs. 60000.0 (Expected: 60000.0)
  Total accounts in CSV: 2 (Expected: 2)
  -> PASSED

[TEST 3] Transaction Save & Query in CSV:
  Found transactions for #1001: 2 (Expected: 2)
  Found transactions for #1002: 1 (Expected: 1)
  -> PASSED

[TEST 4] Atomic Transfer via FileAccountRepository:
  Sender balance: Rs. 55000.0 (Expected: 55000.0)
  Receiver balance: Rs. 45000.0 (Expected: 45000.0)
  -> PASSED

[TEST 5] Verify Timestamped Backups Created:
  Found 2 backup files for accounts.csv.
  -> PASSED

============================================================
  ALL ACTIVITY 26 FILE REPOSITORY TESTS PASSED!
============================================================
```

## 9. How to Run the Commands

### Compile
```powershell
javac -d bin -cp "bin;lib/*" (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

### Run Test Suite
```powershell
java -ea -cp "bin;lib/*" com.gdb.tests.TestFileRepository
```

### Clean Up
```powershell
Remove-Item -Recurse -Force bin, data_test
```

> **Verification**: Check all checkboxes in [Section 7 (Acceptance Criteria)](#7-acceptance-criteria) once test execution passes.
