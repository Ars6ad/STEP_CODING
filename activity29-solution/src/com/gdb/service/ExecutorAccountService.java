package com.gdb.service;

import com.gdb.concurrency.ThreadPoolFactory;
import com.gdb.domain.IAccount;
import com.gdb.domain.Transaction;
import com.gdb.domain.TransactionType;
import com.gdb.repository.AccountRepository;
import com.gdb.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Banking service executing operations asynchronously on a managed ExecutorService thread pool
 * and returning Future<T> tokens for non-blocking task submission and deferred result retrieval.
 */
public class ExecutorAccountService {

    private final LockedAccountService lockedService;
    private final ExecutorService executor;

    public ExecutorAccountService(AccountRepository accountRepo, TransactionRepository transactionRepo, int poolSize) {
        this.lockedService = new LockedAccountService(accountRepo, transactionRepo);
        this.executor = ThreadPoolFactory.newFixedPool(poolSize);
    }

    public ExecutorAccountService(LockedAccountService lockedService, ExecutorService executor) {
        this.lockedService = lockedService;
        this.executor = executor;
    }

    public Future<Transaction> depositAsync(int accountNumber, double amount) {
        return executor.submit(() -> {
            lockedService.deposit(accountNumber, amount);
            IAccount acc = lockedService.getAccount(accountNumber);
            return new Transaction(
                    "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    LocalDateTime.now(),
                    accountNumber,
                    TransactionType.DEPOSIT,
                    amount,
                    acc != null ? acc.getBalance() : 0.0,
                    "SUCCESS",
                    "Async Deposit",
                    0,
                    0
            );
        });
    }

    public Future<Transaction> withdrawAsync(int accountNumber, double amount, int pin) {
        return executor.submit(() -> {
            lockedService.withdraw(accountNumber, amount, pin);
            IAccount acc = lockedService.getAccount(accountNumber);
            return new Transaction(
                    "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    LocalDateTime.now(),
                    accountNumber,
                    TransactionType.WITHDRAW,
                    amount,
                    acc != null ? acc.getBalance() : 0.0,
                    "SUCCESS",
                    "Async Withdrawal",
                    0,
                    0
            );
        });
    }

    public Future<Transaction> transferAsync(int fromAcc, int toAcc, double amount, int pin) {
        return executor.submit(() -> {
            lockedService.transfer(fromAcc, toAcc, amount, pin);
            IAccount sender = lockedService.getAccount(fromAcc);
            return new Transaction(
                    "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    LocalDateTime.now(),
                    fromAcc,
                    TransactionType.TRANSFER,
                    amount,
                    sender != null ? sender.getBalance() : 0.0,
                    "SUCCESS",
                    "Async Transfer to " + toAcc,
                    fromAcc,
                    toAcc
            );
        });
    }

    public Future<Double> getBalanceAsync(int accountNumber, int pin) {
        return executor.submit(() -> lockedService.getBalance(accountNumber, pin));
    }

    public <T> List<Future<T>> invokeAllTasks(List<Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return executor.invokeAll(tasks, timeout, unit);
    }

    public void shutdown() {
        ThreadPoolFactory.shutdownGracefully(executor, 3000);
    }

    public boolean isShutdown() {
        return executor.isShutdown();
    }

    public void saveAccount(IAccount account) {
        lockedService.saveAccount(account);
    }

    public IAccount getAccount(int accountNumber) {
        return lockedService.getAccount(accountNumber);
    }
}
