package com.gdb.service;

import com.gdb.concurrency.AccountLockRegistry;
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
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * High-concurrency banking service utilizing fine-grained per-account locks
 * and strict lock ordering to eliminate contention across distinct accounts and avoid deadlocks.
 */
public class LockedAccountService {

    private final AccountRepository accountRepo;
    private final TransactionRepository transactionRepo;
    private final AccountLockRegistry lockRegistry;

    public LockedAccountService(AccountRepository accountRepo, TransactionRepository transactionRepo) {
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
        this.lockRegistry = new AccountLockRegistry();
    }

    public void deposit(int accountNumber, double amount) throws Exception {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero");
        }
        ReentrantLock lock = lockRegistry.lockFor(accountNumber);
        lock.lock();
        try {
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
                        "Locked Deposit",
                        0,
                        0
                );
                transactionRepo.save(txn);
            }
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(int accountNumber, double amount, int pin) throws Exception {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero");
        }
        ReentrantLock lock = lockRegistry.lockFor(accountNumber);
        lock.lock();
        try {
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
                        "Locked Withdrawal",
                        0,
                        0
                );
                transactionRepo.save(txn);
            }
        } finally {
            lock.unlock();
        }
    }

    public void transfer(int fromAcc, int toAcc, double amount, int pin) throws Exception {
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be greater than zero");
        }
        if (fromAcc == toAcc) {
            throw new AccountException("Cannot transfer to the same account");
        }

        // Deadlock-free lock ordering: always lock lowest account number first
        int firstAcc = Math.min(fromAcc, toAcc);
        int secondAcc = Math.max(fromAcc, toAcc);

        ReentrantLock firstLock = lockRegistry.lockFor(firstAcc);
        ReentrantLock secondLock = lockRegistry.lockFor(secondAcc);

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                IAccount sender = accountRepo.findById(fromAcc);
                IAccount receiver = accountRepo.findById(toAcc);

                if (sender == null || receiver == null) {
                    throw new AccountException("One or both accounts do not exist");
                }

                sender.withdraw(amount, pin);
                receiver.deposit(amount);

                accountRepo.update(sender);
                accountRepo.update(receiver);

                if (transactionRepo != null) {
                    Transaction txn = new Transaction(
                            "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                            LocalDateTime.now(),
                            fromAcc,
                            TransactionType.TRANSFER,
                            amount,
                            sender.getBalance(),
                            "SUCCESS",
                            "Locked Transfer to " + toAcc,
                            fromAcc,
                            toAcc
                    );
                    transactionRepo.save(txn);
                }
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }

    public double getBalance(int accountNumber, int pin) throws Exception {
        ReentrantReadWriteLock rwLock = lockRegistry.rwLockFor(accountNumber);
        rwLock.readLock().lock();
        try {
            IAccount acc = accountRepo.findById(accountNumber);
            if (acc == null) {
                throw new AccountException("Account not found: " + accountNumber);
            }
            if (acc.hasPin() && !acc.verifyPin(pin)) {
                throw new InvalidPinException("Invalid PIN for account " + accountNumber);
            }
            return acc.getBalance();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public IAccount getAccount(int accountNumber) {
        return accountRepo.findById(accountNumber);
    }

    public void saveAccount(IAccount account) {
        accountRepo.save(account);
    }
}
