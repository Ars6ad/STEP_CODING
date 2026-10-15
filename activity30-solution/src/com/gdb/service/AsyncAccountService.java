package com.gdb.service;

import com.gdb.concurrency.ThreadPoolFactory;
import com.gdb.domain.AccountFactory;
import com.gdb.domain.IAccount;
import com.gdb.domain.Transaction;
import com.gdb.domain.TransactionType;
import com.gdb.repository.AccountRepository;
import com.gdb.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Modern non-blocking banking service utilizing CompletableFuture pipelines,
 * asynchronous composition (thenApply, thenCompose), fan-out (allOf), and resilient error recovery.
 */
public class AsyncAccountService {

    private final LockedAccountService lockedService;
    private final ExecutorService executor;

    public AsyncAccountService(AccountRepository accountRepo, TransactionRepository transactionRepo, int poolSize) {
        this.lockedService = new LockedAccountService(accountRepo, transactionRepo);
        this.executor = ThreadPoolFactory.newFixedPool(poolSize);
    }

    public CompletableFuture<IAccount> openAccountAsync(String type, int accNo, String name, int age, double initialBal) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                IAccount acc = AccountFactory.createAccount(type, accNo, name, age, initialBal);
                lockedService.saveAccount(acc);
                return acc;
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    public CompletableFuture<Transaction> depositAsync(int accountNumber, double amount) {
        return CompletableFuture.supplyAsync(() -> {
            try {
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
                        "Async CF Deposit",
                        0,
                        0
                );
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    public CompletableFuture<Transaction> withdrawAsync(int accountNumber, double amount, int pin) {
        return CompletableFuture.supplyAsync(() -> {
            try {
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
                        "Async CF Withdrawal",
                        0,
                        0
                );
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    public CompletableFuture<Transaction> transferAsync(int fromAcc, int toAcc, double amount, int pin) {
        return CompletableFuture.supplyAsync(() -> {
            try {
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
                        "Async CF Transfer to " + toAcc,
                        fromAcc,
                        toAcc
                );
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    public CompletableFuture<Double> getBalanceAsync(int accountNumber, int pin) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return lockedService.getBalance(accountNumber, pin);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    public CompletableFuture<Double> executePipelineAsync(int accNo, double depositAmt, double withdrawAmt, int pin) {
        return depositAsync(accNo, depositAmt)
                .thenCompose(t1 -> withdrawAsync(accNo, withdrawAmt, pin))
                .thenCompose(t2 -> getBalanceAsync(accNo, pin));
    }

    public CompletableFuture<List<Transaction>> depositAllAsync(List<Integer> accountNumbers, double amount) {
        List<CompletableFuture<Transaction>> futures = accountNumbers.stream()
                .map(accNo -> depositAsync(accNo, amount))
                .collect(Collectors.toList());

        CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allDone.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
        );
    }

    public void shutdown() {
        ThreadPoolFactory.shutdownGracefully(executor, 3000);
    }

    public void saveAccount(IAccount account) {
        lockedService.saveAccount(account);
    }

    public IAccount getAccount(int accountNumber) {
        return lockedService.getAccount(accountNumber);
    }
}
