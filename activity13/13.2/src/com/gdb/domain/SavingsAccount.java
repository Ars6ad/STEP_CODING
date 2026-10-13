package com.gdb.domain;

import com.gdb.exceptions.*;

public class SavingsAccount extends AbstractAccount {
    // TODO: Step 1.1 - Declare a private int field tenureYears.
    private double minBalance;
    private double interestRate;

    public SavingsAccount(String accountNumber, String name, int age, double balance, String status, String pin, int tenureYears) {
        super(accountNumber, name, age, balance, "SAVINGS", status, pin);
        // TODO: Step 1.2 - Store tenureYears in its field, then ask the rules engine for this tenure's rules:
        //   minBalance   <- AccountRulesEngine.getSavingsMinBalance(tenureYears)
        //   interestRate <- AccountRulesEngine.getSavingsInterestRate(tenureYears)
    }

    public SavingsAccount(String accountNumber, String name, int age, double balance, String status, String pin, double minBalance, double interestRate) {
        super(accountNumber, name, age, balance, "SAVINGS", status, pin);
        this.minBalance = minBalance;
        this.interestRate = interestRate;
    }

    @Override
    public void processDebit(double amount) throws AccountException {
        if ((this.balance - amount) < this.minBalance) {
            throw new MinimumBalanceViolationException("Cannot breach minimum balance of Rs " + minBalance);
        }
        this.balance -= amount;
    }

    public void applyInterest() {
        double interest = this.balance * (interestRate / 100.0);
        this.balance += interest;
    }

    public int getTenureYears() {
        // TODO: Step 1.1 - Return the tenureYears field.
        return 0;
    }

    public double getMinBalance() { return minBalance; }
    public double getInterestRate() { return interestRate; }
}
