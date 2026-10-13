# Activity 13.2: Dynamic Account Rules Integration

## Objective
Connect account domain classes and `AccountFactory` to `AccountRulesEngine` so that minimum balances and interest rates are assigned dynamically at runtime based on customer tenure.

---

## Target Files to Complete
- `src/com/gdb/domain/SavingsAccount.java`
- `src/com/gdb/domain/AccountFactory.java`

---

## Plain English Step-by-Step Instructions

### Step 1: Add Tenure Field to `SavingsAccount`
1. Add a private integer field `tenureYears` to `SavingsAccount`.
2. In the constructor, query `AccountRulesEngine.getSavingsMinBalance(tenureYears)` to set `minBalance`.
3. Query `AccountRulesEngine.getSavingsInterestRate(tenureYears)` to set `interestRate`.

### Step 2: Update `AccountFactory`
Update `AccountFactory.createAccount` to accept customer tenure and pass it when constructing accounts.

### Step 3: Verify Dynamic Rule Assignment
Create accounts for customers with different tenure lengths (e.g., 0 years, 2 years, 4 years, 6 years) and verify that their minimum balance requirements and interest rates are dynamically determined by the rules engine.

---

## How to Compile & Run (Multi-OS Guide)

### Windows (PowerShell)
```powershell
# Create bin folder if not exists
if (!(Test-Path bin)) { New-Item -ItemType Directory -Path bin }

# Compile all source files
javac -d bin (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })

# Run the test program
java -cp bin com.gdb.tests.TestAccountRulesEngine
```

### Windows (Command Prompt - CMD)
```cmd
if not exist bin mkdir bin
javac -d bin src\com\gdb\domain\*.java src\com\gdb\tests\*.java src\com\gdb\exceptions\*.java
java -cp bin com.gdb.tests.TestAccountRulesEngine
```

### Linux & macOS (Terminal / Bash / Zsh)
```bash
# Create bin directory
mkdir -p bin

# Compile all Java files
find src -name "*.java" -print0 | xargs -0 javac -d bin

# Run the test program
java -cp bin com.gdb.tests.TestAccountRulesEngine
```

---

## Expected Output
```
=== Activity 13.2: Dynamic Account Rules Test ===
Created Savings Account (Tenure: 4 yrs):
 -> Min Balance: Rs 5000.0 (Dynamically fetched)
 -> Interest Rate: 3.5% (Dynamically fetched)
Dynamic rule integration verified!
```
