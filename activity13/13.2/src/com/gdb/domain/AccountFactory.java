package com.gdb.domain;

public class AccountFactory {
    public static IAccount createAccount(String type, String accNum, String name, int age, double balance, String status, String pin) {
        if (type == null) return null;
        switch (type.toUpperCase()) {
            case "SAVINGS":
                return new SavingsAccount(accNum, name, age, balance, status, pin, 1000.0, 4.0);
            case "CURRENT":
                return new CurrentAccount(accNum, name, age, balance, status, pin, 25000.0);
            case "FIXED_DEPOSIT":
            case "FD":
                return new FixedDepositAccount(accNum, name, age, balance, status, pin, 12, 6.5);
            case "SALARY":
                return new SalaryAccount(accNum, name, age, balance, status, pin, "TechCorp");
            default:
                throw new IllegalArgumentException("Unknown account type: " + type);
        }
    }

    // TODO: Step 2 - Tenure-aware factory method:
    //   1. Use the same switch as createAccount(...) above, but build "SAVINGS" accounts with the
    //      SavingsAccount(accNum, name, age, balance, status, pin, tenureYears) constructor so the
    //      rules engine decides the minimum balance and interest rate.
    //   2. Then make the 7-argument createAccount(...) above delegate here with tenureYears = 0,
    //      so both methods share one switch.
    public static IAccount createAccount(String type, String accNum, String name, int age, double balance, String status, String pin, int tenureYears) {
        throw new UnsupportedOperationException("TODO: implement tenure-aware AccountFactory.createAccount (Activity 13.2, Step 2)");
    }
}
