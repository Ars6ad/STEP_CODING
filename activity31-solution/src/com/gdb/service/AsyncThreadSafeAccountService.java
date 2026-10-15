package com.gdb.service;

import com.gdb.concurrency.AccountLockRegistry;
import com.gdb.concurrency.ThreadPoolFactory;
import com.gdb.domain.IAccount;
import com.gdb.domain.Transaction;
import com.gdb.domain.TransactionType;
import com.gdb.exceptions.AccountException;
import com.gdb.exceptions.InvalidAmountException;
import com.gdb.exceptions.InvalidPinException;
import com.gdb.repository.AccountRepository;
import com.gdb.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Production-grade banking service combining fine-grained per-account locks,
 * lock-free atomic counters, non-blocking CompletableFuture API, and bounded execution timeouts.
 */
public class AsyncThreadSafeAccountService {

    private final AccountRepository accountRepo;
    private final TransactionRepository transactionRepo;
    private final AccountLockRegistry lockRegistry;
    private final ExecutorService executor;
    private final long timeoutMs;
    private final AtomicLong totalOperations;

    public AsyncThreadSafeAccountService(AccountRepository accountRepo, TransactionRepository transactionRepo, Properties config) {
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
        this.lockRegistry = new AccountLockRegistry();
        int poolSize = config != null ? Integer.parseInt(config.getProperty("service.thread.pool.size", "10")) : 10;
        this.timeoutMs = config != null ? Long.parseLong(config.getProperty("service.timeout.ms", "5000")) : 5000;
        this.executor = ThreadPoolFactory.newFixedPool(poolSize);
        this.totalOperations = new AtomicLong(0);
    }

    public CompletableFuture<Transaction> depositAsync(int accountNumber, double amount) {
        CompletableFuture<Transaction> future = CompletableFuture.supplyAsync(() -> {
            if (amount <= 0) {
                throw new CompletionException(new InvalidAmountException("Deposit amount must be greater than zero"));
            }
            ReentrantLock lock = lockRegistry.lockFor(accountNumber);
            lock.lock();
            try {
                IAccount acc = accountRepo.findById(accountNumber);
                if (acc == null) {
                    throw new CompletionException(new AccountException("Account not found: " + accountNumber));
                }
                acc.deposit(amount);
                accountRepo.update(acc);
                totalOperations.incrementAndGet();

                Transaction txn = new Transaction(
                        "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                        LocalDateTime.now(),
                        accountNumber,
                        TransactionType.DEPOSIT,
                        amount,
                        acc.getBalance(),
                        "SUCCESS",
                        "Async ThreadSafe Deposit",
                        0,
                        0
                );
                if (transactionRepo != null) {
                    transactionRepo.save(txn);
                }
                return txn;
            } catch (Exception e) {
                if (e instanceof CompletionException) throw (CompletionException) e;
                throw new CompletionException(e);
            } finally {
                lock.unlock();
            }
        }, executor);

        return future.completeOnTimeout(null, timeoutMs, TimeUnit.MILLISECONDS);
    }

    public CompletableFuture<Transaction> withdrawAsync(int accountNumber, double amount, int pin) {
        CompletableFuture<Transaction> future = CompletableFuture.supplyAsync(() -> {
            if (amount <= 0) {
                throw new CompletionException(new InvalidAmountException("Withdrawal amount must be greater than zero"));
            }
            ReentrantLock lock = lockRegistry.lockFor(accountNumber);
            lock.lock();
            try {
                IAccount acc = accountRepo.findById(accountNumber);
                if (acc == null) {
                    throw new CompletionException(new AccountException("Account not found: " + accountNumber));
                }
                acc.withdraw(amount, pin);
                accountRepo.update(acc);
                totalOperations.incrementAndGet();

                Transaction txn = new Transaction(
                        "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                        LocalDateTime.now(),
                        accountNumber,
                        TransactionType.WITHDRAW,
                        amount,
                        acc.getBalance(),
                        "SUCCESS",
                        "Async ThreadSafe Withdrawal",
                        0,
                        0
                );
                if (transactionRepo != null) {
                    transactionRepo.save(txn);
                }
                return txn;
            } catch (Exception e) {
                if (e instanceof CompletionException) throw (CompletionException) e;
                throw new CompletionException(e);
            } finally {
                lock.unlock();
            }
        }, executor);

        return future.completeOnTimeout(null, timeoutMs, TimeUnit.MILLISECONDS);
    }

    public CompletableFuture<Transaction> transferAsync(int fromAcc, int toAcc, double amount, int pin) {
        CompletableFuture<Transaction> future = CompletableFuture.supplyAsync(() -> {
            if (amount <= 0) {
                throw new CompletionException(new InvalidAmountException("Transfer amount must be greater than zero"));
            }
            if (fromAcc == toAcc) {
                throw new CompletionException(new AccountException("Cannot transfer to same account"));
            }

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
                        throw new CompletionException(new AccountException("One or both accounts do not exist"));
                    }

                    sender.withdraw(amount, pin);
                    receiver.deposit(amount);

                    accountRepo.update(sender);
                    accountRepo.update(receiver);
                    totalOperations.incrementAndGet();

                    Transaction txn = new Transaction(
                            "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                            LocalDateTime.now(),
                            fromAcc,
                            TransactionType.TRANSFER,
                            amount,
                            sender.getBalance(),
                            "SUCCESS",
                            "Async Transfer to " + toAcc,
                            fromAcc,
                            toAcc
                    );
                    if (transactionRepo != null) {
                        transactionRepo.save(txn);
                    }
                    return txn;
                } finally {
                    secondLock.unlock();
                }
            } catch (Exception e) {
                if (e instanceof CompletionException) throw (CompletionException) e;
                throw new CompletionException(e);
            } finally {
                firstLock.unlock();
            }
        }, executor);

        return future.completeOnTimeout(null, timeoutMs, TimeUnit.MILLISECONDS);
    }

    public CompletableFuture<Double> getBalanceAsync(int accountNumber, int pin) {
        return CompletableFuture.supplyAsync(() -> {
            ReentrantReadWriteLock rwLock = lockRegistry.rwLockFor(accountNumber);
            rwLock.readLock().lock();
            try {
                IAccount acc = accountRepo.findById(accountNumber);
                if (acc == null) {
                    throw new CompletionException(new AccountException("Account not found: " + accountNumber));
                }
                if (acc.hasPin() && !acc.verifyPin(pin)) {
                    throw new CompletionException(new InvalidPinException("Invalid PIN for account " + accountNumber));
                }
                return acc.getBalance();
            } finally {
                rwLock.readLock().unlock();
            }
        }, executor);
    }

    public long getTotalOperations() {
        return totalOperations.get();
    }

    public void shutdown() {
        ThreadPoolFactory.shutdownGracefully(executor, 3000);
    }

    public void saveAccount(IAccount account) {
        accountRepo.save(account);
    }

    public IAccount getAccount(int accountNumber) {
        return accountRepo.findById(accountNumber);
    }
}
