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

    // ============================================================
    // 📝 STEP 3: Implement synchronized deposit(...)
    //
    // INSTRUCTIONS:
    //   1. Mark method with 'synchronized' keyword.
    //   2. Validate amount > 0 (throw InvalidAmountException if <= 0).
    //   3. Retrieve account by accountNumber from accountRepo.
    //   4. Execute account.deposit(amount) and update account in repository.
    //   5. Save transaction record to transactionRepo.
    // ============================================================
    public synchronized void deposit(int accountNumber, double amount) throws Exception {
        // TODO: Step 3 - Thread-safe deposit implementation
    }

    // ============================================================
    // 📝 STEP 4: Implement synchronized withdraw(...)
    //
    // INSTRUCTIONS:
    //   1. Mark method with 'synchronized' keyword.
    //   2. Validate amount > 0.
    //   3. Retrieve account and execute account.withdraw(amount, pin).
    //   4. Update account in repository and save transaction record.
    // ============================================================
    public synchronized void withdraw(int accountNumber, double amount, int pin) throws Exception {
        // TODO: Step 4 - Thread-safe withdraw implementation
    }

    // ============================================================
    // 📝 STEP 5: Implement synchronized transfer(...)
    //
    // INSTRUCTIONS:
    //   1. Mark method with 'synchronized' keyword.
    //   2. Perform atomic transfer between fromAcc and toAcc.
    //   3. Record transfer audit transactions.
    // ============================================================
    public synchronized void transfer(int fromAcc, int toAcc, double amount, int pin) throws Exception {
        // TODO: Step 5 - Thread-safe transfer implementation
    }

    // ============================================================
    // 📝 STEP 6: Implement synchronized getBalance(...) and getAccount(...)
    // ============================================================
    public synchronized double getBalance(int accountNumber, int pin) throws Exception {
        // TODO: Step 6 - Thread-safe balance query
        return 0.0;
    }

    public synchronized IAccount getAccount(int accountNumber) {
        // TODO: Step 6 - Thread-safe account query
        return null;
    }

    public synchronized void saveAccount(IAccount account) {
        accountRepo.save(account);
    }
}
