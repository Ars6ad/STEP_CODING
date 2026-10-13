package com.gdb.domain;

public class AccountRulesEngine {
    private static AccountRulesPropertiesLoader savingsLoader =
        new AccountRulesPropertiesLoader("src/main/resources/config/rules/savings.properties");

    // Bucket names are lowercase so they match the keys in savings.properties (e.g. min.balance.new).
    public static String getSavingsBucket(int tenureYears) {
        if (tenureYears >= 5) return "privilege";
        if (tenureYears >= 3) return "premium";
        if (tenureYears >= 1) return "standard";
        return "new";
    }

    public static double getSavingsMinBalance(int tenureYears) {
        // TODO: Step 3 - Replace the Activity 13 in-memory map lookup: read the key
        //   "min.balance." + getSavingsBucket(tenureYears) from savingsLoader with getDouble(...),
        //   falling back to 10000.0 when the key is missing.
        return 0.0;
    }

    public static double getSavingsInterestRate(int tenureYears) {
        // TODO: Step 3 - Read the key "interest.rate." + getSavingsBucket(tenureYears) from savingsLoader
        //   with getDouble(...), falling back to 2.70.
        return 0.0;
    }

    public static double getCurrentOverdraftLimit(double monthlyTurnover) {
        return Math.max(25000.0, monthlyTurnover * 2.5);
    }

    public static double getFDInterestRate(int months) {
        if (months >= 36) return 7.50;
        if (months >= 12) return 6.50;
        return 5.00;
    }
}
