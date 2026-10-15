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

    // ============================================================
    // 📝 STEP 4: Implement depositAsync(...) returning Future<Transaction>
    //
    // INSTRUCTIONS:
    //   1. Submit a Callable<Transaction> to executor.
    //   2. In Callable: invoke lockedService.deposit(accountNumber, amount),
    //      then return the generated Transaction object.
    // ============================================================
    public Future<Transaction> depositAsync(int accountNumber, double amount) {
        // TODO: Step 4 - Submit deposit task to executor
        return null;
    }

    // ============================================================
    // 📝 STEP 5: Implement withdrawAsync(...) returning Future<Transaction>
    //
    // INSTRUCTIONS:
    //   Submit a Callable<Transaction> executing lockedService.withdraw(accountNumber, amount, pin).
    // ============================================================
    public Future<Transaction> withdrawAsync(int accountNumber, double amount, int pin) {
        // TODO: Step 5 - Submit withdraw task to executor
        return null;
    }

    // ============================================================
    // 📝 STEP 6: Implement transferAsync(...) returning Future<Transaction>
    //
    // INSTRUCTIONS:
    //   Submit a Callable<Transaction> executing lockedService.transfer(fromAcc, toAcc, amount, pin).
    // ============================================================
    public Future<Transaction> transferAsync(int fromAcc, int toAcc, double amount, int pin) {
        // TODO: Step 6 - Submit transfer task to executor
        return null;
    }

    // ============================================================
    // 📝 STEP 7: Implement getBalanceAsync(...) returning Future<Double>
    //
    // INSTRUCTIONS:
    //   Submit a Callable<Double> executing lockedService.getBalance(accountNumber, pin).
    // ============================================================
    public Future<Double> getBalanceAsync(int accountNumber, int pin) {
        // TODO: Step 7 - Submit balance query task to executor
        return null;
    }

    // ============================================================
    // 📝 STEP 8: Implement invokeAllTasks(...) and shutdown()
    //
    // INSTRUCTIONS:
    //   1. invokeAllTasks: call executor.invokeAll(tasks, timeout, unit).
    //   2. shutdown: call ThreadPoolFactory.shutdownGracefully(executor, 3000).
    // ============================================================
    public <T> List<Future<T>> invokeAllTasks(List<Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        // TODO: Step 8 - Invoke all tasks with timeout
        return null;
    }

    public void shutdown() {
        // TODO: Step 8 - Shutdown executor
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
