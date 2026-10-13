# Activity 11: Interface & Factory Pattern

## Objective
Decouple banking operations using the `IAccount` interface and implement the Factory Design Pattern in `AccountFactory` for centralized object creation.

---

## Target Files to Complete
- `src/com/gdb/domain/IAccount.java`
- `src/com/gdb/domain/AccountFactory.java`

---

## Plain English Step-by-Step Instructions

### Step 1: Define `IAccount` Interface
Create the `IAccount` interface defining the contract that all bank accounts must follow:
- Getters for account number, customer name, balance, account type, and status.
- `deposit(double amount)` declaring `InvalidAmountException`.
- `withdraw(double amount, String pin)` declaring `AccountException`.
- `displayAccountInfo()` returning void.

### Step 2: Implement `IAccount` in `AbstractAccount`
Update `AbstractAccount` so that it formally implements `IAccount`.

### Step 3: Implement `AccountFactory`
Create a factory class named `AccountFactory` with a static creation method `createAccount`:
1. Accept the desired account type (e.g., "SAVINGS", "CURRENT", "FIXED_DEPOSIT", "SALARY") along with basic account parameters.
2. Use a `switch` statement on the account type:
   - If "SAVINGS", return a new `SavingsAccount`.
   - If "CURRENT", return a new `CurrentAccount`.
   - If "FIXED_DEPOSIT" or "FD", return a new `FixedDepositAccount`.
   - If "SALARY", return a new `SalaryAccount`.
   - If an unknown type is provided, throw an `IllegalArgumentException`.

---

## How to Compile & Run (Multi-OS Guide)

> [!NOTE]
> Before running the tests, open `src/com/gdb/tests/TestInterfaceFactory.java` and uncomment the test calls inside the `main` method once you have finished `IAccount`, `AbstractAccount` and `AccountFactory`.

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
=== Activity 11: Interface & Factory Pattern Test ===
Factory created: SAVINGS account for Rajesh Sharma
Factory created: CURRENT account for Priya Patel
Factory created: FIXED_DEPOSIT account for Amit Kumar
Factory created: SALARY account for Sneha Verma
All accounts successfully created through AccountFactory!
```
