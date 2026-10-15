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

    // ============================================================
    // 📝 STEP 5: Implement deposit(...) with per-account lock
    //
    // INSTRUCTIONS:
    //   1. Acquire per-account lock using lockRegistry.lockFor(accountNumber).
    //   2. Use try-finally to guarantee lock release.
    //   3. Validate amount, retrieve account, deposit, update repository, and log transaction.
    // ============================================================
    public void deposit(int accountNumber, double amount) throws Exception {
        // TODO: Step 5 - Fine-grained locked deposit
    }

    // ============================================================
    // 📝 STEP 6: Implement withdraw(...) with per-account lock
    //
    // INSTRUCTIONS:
    //   1. Acquire per-account lock using lockRegistry.lockFor(accountNumber).
    //   2. Use try-finally to guarantee lock release.
    //   3. Execute withdraw(amount, pin), update repository, and log transaction.
    // ============================================================
    public void withdraw(int accountNumber, double amount, int pin) throws Exception {
        // TODO: Step 6 - Fine-grained locked withdrawal
    }

    // ============================================================
    // 📝 STEP 7: Implement transfer(...) with Deadlock-Free Lock Ordering
    //
    // INSTRUCTIONS:
    //   1. Determine lock order to prevent deadlocks:
    //      - int firstAcc = Math.min(fromAcc, toAcc);
    //      - int secondAcc = Math.max(fromAcc, toAcc);
    //   2. Acquire lock for firstAcc, then acquire lock for secondAcc.
    //   3. Use nested try-finally blocks to release secondLock, then firstLock.
    //   4. Perform withdrawal from fromAcc, deposit to toAcc, update repo, and log transaction.
    // ============================================================
    public void transfer(int fromAcc, int toAcc, double amount, int pin) throws Exception {
        // TODO: Step 7 - Deadlock-free ordered multi-account transfer
    }

    // ============================================================
    // 📝 STEP 8: Implement getBalance(...) using ReadLock
    //
    // INSTRUCTIONS:
    //   1. Acquire read lock: lockRegistry.rwLockFor(accountNumber).readLock().lock().
    //   2. In try-finally, retrieve account, verify PIN, and return balance.
    // ============================================================
    public double getBalance(int accountNumber, int pin) throws Exception {
        // TODO: Step 8 - ReadLock balance query
        return 0.0;
    }

    public IAccount getAccount(int accountNumber) {
        return accountRepo.findById(accountNumber);
    }

    public void saveAccount(IAccount account) {
        accountRepo.save(account);
    }
}
