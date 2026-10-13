package com.gdb.domain;

import java.util.HashMap;
import java.util.Map;

public class AccountRulesEngine {
    // TODO: Step 1 - Define the Savings lookup tables: two private static final Map<String, Double> fields
    //   (one for minimum balance, one for interest rate) keyed by tenure bucket, filled in a static { } block:
    //     Bucket      Tenure       Min Balance   Interest Rate
    //     NEW         0 to 1 yr    10000.0       2.70
    //     STANDARD    1 to 3 yrs    7500.0       3.00
    //     PREMIUM     3 to 5 yrs    5000.0       3.50
    //     PRIVILEGE   5+ yrs        2500.0       4.00

    public static String getSavingsBucket(int tenureYears) {
        // TODO: Step 1 - Map tenure to a bucket name:
        //   >= 5 -> "PRIVILEGE", >= 3 -> "PREMIUM", >= 1 -> "STANDARD", otherwise "NEW".
        return null;
    }

    public static double getSavingsMinBalance(int tenureYears) {
        // TODO: Step 2 - Look up getSavingsBucket(tenureYears) in your minimum-balance map
        //   (fall back to 10000.0 if the bucket is missing, e.g. with getOrDefault).
        return 0.0;
    }

    public static double getSavingsInterestRate(int tenureYears) {
        // TODO: Step 3 - Look up getSavingsBucket(tenureYears) in your interest-rate map (fall back to 2.70).
        return 0.0;
    }

    public static double getCurrentOverdraftLimit(double monthlyTurnover) {
        // TODO: Step 4.1 - Return 2.5 times monthlyTurnover, but never less than 25000.0 (hint: Math.max).
        return 0.0;
    }

    public static double getFDInterestRate(int months) {
        // TODO: Step 4.2 - Return the FD rate by duration: 36+ months -> 7.50, 12+ months -> 6.50, otherwise 5.00.
        return 0.0;
    }
}
