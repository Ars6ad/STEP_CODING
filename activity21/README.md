# Activity 21: Repository Pattern (Interfaces + InMemory + Factory)

## Overview
This activity introduces the **Repository Pattern**, decoupling domain models and the service layer from data persistence mechanisms. In this activity, students define repository interfaces for accounts and transactions, build in-memory implementations, configure a `RepositoryFactory` driven by `persistence.properties`, and refactor `AccountService` to use repositories.

---

## Files Included
| File | Description |
|---|---|
| `src/com/gdb/repository/AccountRepository.java` | Contract defining account persistence operations (`save`, `findById`, `findAll`, `update`, `delete`, `exists`, `nextAccountNumber`) |
| `src/com/gdb/repository/TransactionRepository.java` | Contract defining transaction persistence operations (`save`, `findByAccount`, `findAll`, `clear`) |
| `src/com/gdb/repository/InMemoryAccountRepository.java` | In-memory `Map<Integer, IAccount>` implementation with auto-increment ID generation |
| `src/com/gdb/repository/InMemoryTransactionRepository.java` | In-memory `List<Transaction>` implementation supporting filtering by account ID |
| `src/com/gdb/repository/RepositoryFactory.java` | Factory reading `persistence.properties` (`memory`, with future placeholders for `jdbc` and `file`) |
| `src/main/resources/config/persistence.properties` | Configuration declaring `persistence.mode=memory` |
| `src/com/gdb/service/AccountService.java` | Refactored service layer operating exclusively through repository interfaces |
| `src/com/gdb/tests/TestRepositoryInMemory.java` | Test driver validating repository CRUD, transactions, auto ID generation, and service integration |
| `docs/ACTIVITY_21.md` | Full activity lab guide with instructions, architecture diagrams, and exercises |

---

## How to Compile & Run

### Windows (PowerShell)
```powershell
New-Item bin -ItemType Directory -Force | Out-Null
Copy-Item src\mainesources\config bin -Recurse -Force
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
java -cp bin com.gdb.tests.TestRepositoryInMemory
```

### Linux & macOS (Terminal / Bash)
```bash
mkdir -p bin && cp -r src/main/resources/config bin/
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -cp bin com.gdb.tests.TestRepositoryInMemory
```
