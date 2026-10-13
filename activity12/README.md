# Activity 12: Factory-Driven Banking System

## Objective
Build a factory-driven test suite where accounts are instantiated solely through `AccountFactory` and manipulated exclusively through the `IAccount` interface.

---

## Target File to Complete
- `src/com/gdb/tests/TestInterfaceFactory.java`

---

## Plain English Step-by-Step Instructions

### Step 1: Instantiate Accounts via Factory
Use `AccountFactory.createAccount` to create instances of `SavingsAccount`, `CurrentAccount`, and `FixedDepositAccount`, assigning each to an `IAccount` reference.

### Step 2: Test Interface-Driven Transactions
Perform deposits and withdrawals through the `IAccount` interface variables without casting to specific concrete classes.

### Step 3: Verify Subclass Business Rules
1. Verify that savings accounts enforce minimum balance rules through the interface.
2. Verify that current accounts allow overdrafts up to the limit through the interface.
3. Verify that fixed deposit accounts reject premature withdrawals through the interface.
4. Verify that requesting an unknown account type from the factory throws an `IllegalArgumentException`.

---

## How to Compile & Run (Multi-OS Guide)

### Windows (PowerShell)
```powershell
# Create bin folder if not exists
if (!(Test-Path bin)) { New-Item -ItemType Directory -Path bin }

# Compile all source files
javac -d bin (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })

# Run the test program
java -cp bin com.gdb.tests.TestInterfaceFactory
```

### Windows (Command Prompt - CMD)
```cmd
if not exist bin mkdir bin
javac -d bin src\com\gdb\domain\*.java src\com\gdb\tests\*.java src\com\gdb\exceptions\*.java
java -cp bin com.gdb.tests.TestInterfaceFactory
```

### Linux & macOS (Terminal / Bash / Zsh)
```bash
# Create bin directory
mkdir -p bin

# Compile all Java files
find src -name "*.java" -print0 | xargs -0 javac -d bin

# Run the test program
java -cp bin com.gdb.tests.TestInterfaceFactory
```

---

## Expected Output
```
=== Activity 12: Factory-Driven System Suite ===
[Test 1] Savings Account Creation & Deposit: [PASS]
[Test 2] Current Account Overdraft Withdrawal: [PASS]
[Test 3] Fixed Deposit Premature Withdrawal Block: [PASS]
[Test 4] Invalid Type Rejection: [PASS]
Factory-driven architecture successfully verified!
```
