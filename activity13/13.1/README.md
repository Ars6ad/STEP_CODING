# Activity 13.1: Account Rules Engine (In-Memory Lookup)

## Objective
Create a centralized `AccountRulesEngine` class using in-memory lookup tables (`Map`) to determine minimum balance, interest rates, and limits based on customer relationship tenure.

---

## Target File to Complete
- `src/com/gdb/domain/AccountRulesEngine.java`

---

## Plain English Step-by-Step Instructions

### Step 1: Define Tenure Buckets and Lookup Tables
In `AccountRulesEngine.java`, set up lookup tables for Savings account rules across 4 tenure tiers:
- **New (0 to 1 year)**: Minimum Balance = 10000.0, Interest Rate = 2.70%
- **Standard (1 to 3 years)**: Minimum Balance = 7500.0, Interest Rate = 3.00%
- **Premium (3 to 5 years)**: Minimum Balance = 5000.0, Interest Rate = 3.50%
- **Privilege (5+ years)**: Minimum Balance = 2500.0, Interest Rate = 4.00%

### Step 2: Implement `getSavingsMinBalance(int tenureYears)`
Write a static method that takes customer tenure in years:
1. If tenure is 5 years or more, return 2500.0.
2. If tenure is 3 to 4 years, return 5000.0.
3. If tenure is 1 to 2 years, return 7500.0.
4. Otherwise (new customer), return 10000.0.

### Step 3: Implement `getSavingsInterestRate(int tenureYears)`
Write a static method that takes customer tenure in years:
1. If tenure is 5 years or more, return 4.00%.
2. If tenure is 3 to 4 years, return 3.50%.
3. If tenure is 1 to 2 years, return 3.00%.
4. Otherwise (new customer), return 2.70%.

### Step 4: Implement Current Account & FD Rules
1. `getCurrentOverdraftLimit(double monthlyTurnover)`: Returns 2.5 times the monthly turnover (minimum 25000.0).
2. `getFDInterestRate(int months)`: Returns interest rate based on deposit duration (e.g., 6.5% for 12+ months).

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
=== Activity 13.1: Hardcoded Rules Engine Test ===
Tenure 0 yrs -> Min Balance: Rs 10000.0 | Interest: 2.7%
Tenure 2 yrs -> Min Balance: Rs 7500.0  | Interest: 3.0%
Tenure 4 yrs -> Min Balance: Rs 5000.0  | Interest: 3.5%
Tenure 6 yrs -> Min Balance: Rs 2500.0  | Interest: 4.0%
Rules Engine lookup completed successfully!
```
