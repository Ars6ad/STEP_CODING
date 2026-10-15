# Activity 26: File Repository (CSV + Atomic Writes + Backup)

## Overview
This activity completes the persistence story by building file-based repositories (`FileAccountRepository`, `FileTransactionRepository`) backed by CSV formatting, crash-safe atomic write operations (`AtomicFileWriter`), and automated timestamped backups (`BackupManager`).

---

## Files Included
| File | Description |
|---|---|
| `src/com/gdb/file/CsvMapper.java` | Serializes and deserializes `Account` and `Transaction` models to/from CSV rows |
| `src/com/gdb/file/AtomicFileWriter.java` | Writes to temporary files and atomically renames to target path for crash resilience |
| `src/com/gdb/file/BackupManager.java` | Creates timestamped backup copies (`.bak.<timestamp>`) and prunes old backups |
| `src/com/gdb/repository/FileAccountRepository.java` | File-backed repository persisting account records to CSV |
| `src/com/gdb/repository/FileTransactionRepository.java` | File-backed repository persisting transaction logs to CSV |
| `src/com/gdb/repository/RepositoryFactory.java` | Factory updated to support `persistence.mode=file` |
| `src/com/gdb/tests/TestFileRepository.java` | Comprehensive test driver validating CSV persistence, atomic writes, and backups |
| `docs/ACTIVITY_26.md` | Complete student lab guide with diagrams, instructions, and compilation commands |

---

## How to Compile & Run

### Windows (PowerShell)
```powershell
New-Item bin -ItemType Directory -Force | Out-Null
Copy-Item src\mainesources\* bin -Recurse -Force
javac -encoding UTF-8 -cp "lib/*;." -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
java -cp "lib/*;bin" com.gdb.tests.TestFileRepository
```

### Linux & macOS (Terminal / Bash)
```bash
mkdir -p bin && cp -r src/main/resources/* bin/
javac -encoding UTF-8 -cp "lib/*:." -d bin $(find src -name "*.java")
java -cp "lib/*:bin" com.gdb.tests.TestFileRepository
```
