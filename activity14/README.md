# Activity 14: External Properties Rules Engine

## Objective
Move all banking business rules from hardcoded Java code into external `.properties` configuration files, creating a configuration-driven banking engine.

---

## Target Files to Complete
- `src/main/resources/config/rules/savings.properties`
- `src/main/resources/config/rules/current.properties`
- `src/main/resources/config/rules/fixeddeposit.properties`
- `src/main/resources/config/rules/salary.properties`
- `src/com/gdb/domain/AccountRulesPropertiesLoader.java`
- `src/com/gdb/domain/AccountRulesEngine.java`

---

## Plain English Step-by-Step Instructions

### Step 1: Define Rules in External Properties Files
In `savings.properties`, define key-value pairs for minimum balance and interest rates across tenure buckets (new, standard, premium, privilege).

### Step 2: Implement Properties Loader
In `AccountRulesPropertiesLoader.java`:
1. Use Java's `java.util.Properties` class to load properties files from the file path or classpath stream.
2. Implement helper methods:
   - `getProperty(String key, String defaultValue)` to retrieve text values.
   - `getDouble(String key, double defaultValue)` to retrieve decimal numbers (parsing text to double).

### Step 3: Refactor `AccountRulesEngine` to Use Properties Loader
Update `AccountRulesEngine` so that when querying rules (e.g., `getSavingsMinBalance`, `getSavingsInterestRate`), it reads directly from `AccountRulesPropertiesLoader` instead of using in-memory maps.

### Step 4: Verify External Configuration
Run the test driver to confirm that all rules are correctly loaded from external properties files and applied to account operations.

---

## How to Compile & Run (Multi-OS Guide)

### Windows (PowerShell)
```powershell
# Create bin folder if not exists
if (!(Test-Path bin)) { New-Item -ItemType Directory -Path bin }

# Compile all source files
javac -d bin (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })

# Run the test program
java -cp bin com.gdb.tests.TestAccountRulesEngineProperties
```

### Windows (Command Prompt - CMD)
```cmd
if not exist bin mkdir bin
javac -d bin src\com\gdb\domain\*.java src\com\gdb\tests\*.java src\com\gdb\exceptions\*.java
java -cp bin com.gdb.tests.TestAccountRulesEngineProperties
```

### Linux & macOS (Terminal / Bash / Zsh)
```bash
# Create bin directory
mkdir -p bin

# Compile all Java files
find src -name "*.java" -print0 | xargs -0 javac -d bin

# Run the test program
java -cp bin com.gdb.tests.TestAccountRulesEngineProperties
```

---

## Expected Output
```
=== Activity 14: Properties-Driven Rules Engine Test ===
[Config] Loaded rules from src/main/resources/config/rules/savings.properties
Tenure 0 yrs -> Min Balance: Rs 10000.0 | Interest: 2.70%
Tenure 2 yrs -> Min Balance: Rs 7500.0  | Interest: 3.00%
Tenure 4 yrs -> Min Balance: Rs 5000.0  | Interest: 3.50%
Tenure 6 yrs -> Min Balance: Rs 2500.0  | Interest: 4.00%
All external properties loaded and verified successfully!
```
