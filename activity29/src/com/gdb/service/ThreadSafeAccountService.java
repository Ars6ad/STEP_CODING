package com.gdb.service;

import com.gdb.domain.Account;
import com.gdb.domain.IAccount;
import com.gdb.domain.Transaction;
import com.gdb.domain.TransactionType;
import com.gdb.exceptions.AccountException;
import com.gdb.exceptions.InvalidAmountException;
import com.gdb.exceptions.InvalidPinException;
import com.gdb.repository.AccountRepository;
import com.gdb.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Thread-safe banking service using intrinsic synchronization to guarantee mutual exclusion
 * and prevent race conditions / lost updates across concurrent threads.
 */
public class ThreadSafeAccountService {

    private final AccountRepository accountRepo;
    private final TransactionRepository transactionRepo;

    public ThreadSafeAccountService(AccountRepository accountRepo, TransactionRepository transactionRepo) {
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
    }

    public synchronized void deposit(int accountNumber, double amount) throws Exception {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero");
        }
        IAccount acc = accountRepo.findById(accountNumber);
        if (acc == null) {
            throw new AccountException("Account not found: " + accountNumber);
        }
        acc.deposit(amount);
        accountRepo.update(acc);

        if (transactionRepo != null) {
            Transaction txn = new Transaction(
                    "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    LocalDateTime.now(),
                    accountNumber,
                    TransactionType.DEPOSIT,
                    amount,
                    acc.getBalance(),
                    "SUCCESS",
                    "ThreadSafe Deposit",
                    0,
                    0
            );
            transactionRepo.save(txn);
        }
    }

    public synchronized void withdraw(int accountNumber, double amount, int pin) throws Exception {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero");
        }
        IAccount acc = accountRepo.findById(accountNumber);
        if (acc == null) {
            throw new AccountException("Account not found: " + accountNumber);
        }

        acc.withdraw(amount, pin);
        accountRepo.update(acc);

        if (transactionRepo != null) {
            Transaction txn = new Transaction(
                    "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    LocalDateTime.now(),
                    accountNumber,
                    TransactionType.WITHDRAW,
                    amount,
                    acc.getBalance(),
                    "SUCCESS",
                    "ThreadSafe Withdrawal",
                    0,
                    0
            );
            transactionRepo.save(txn);
        }
    }

    public synchronized void transfer(int fromAcc, int toAcc, double amount, int pin) throws Exception {
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be greater than zero");
        }
        if (fromAcc == toAcc) {
            throw new AccountException("Cannot transfer to the same account");
        }
        IAccount senderAcc = accountRepo.findById(fromAcc);
        IAccount receiverAcc = accountRepo.findById(toAcc);

        if (senderAcc == null || receiverAcc == null) {
            throw new AccountException("One or both accounts do not exist");
        }

        senderAcc.withdraw(amount, pin);
        receiverAcc.deposit(amount);

        accountRepo.update(senderAcc);
        accountRepo.update(receiverAcc);

        if (transactionRepo != null) {
            Transaction txn = new Transaction(
                    "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    LocalDateTime.now(),
                    fromAcc,
                    TransactionType.TRANSFER,
                    amount,
                    senderAcc.getBalance(),
                    "SUCCESS",
                    "Transfer to " + toAcc,
                    fromAcc,
                    toAcc
            );
            transactionRepo.save(txn);
        }
    }

    public synchronized double getBalance(int accountNumber, int pin) throws Exception {
        IAccount acc = accountRepo.findById(accountNumber);
        if (acc == null) {
            throw new AccountException("Account not found: " + accountNumber);
        }
        if (acc.hasPin() && !acc.verifyPin(pin)) {
            throw new InvalidPinException("Invalid PIN for account " + accountNumber);
        }
        return acc.getBalance();
    }

    public synchronized IAccount getAccount(int accountNumber) {
        return accountRepo.findById(accountNumber);
    }

    public synchronized void saveAccount(IAccount account) {
        accountRepo.save(account);
    }
}
