package com.gdb.tests;

import com.gdb.domain.*;
import com.gdb.repository.*;
import com.gdb.service.AccountService;
import com.gdb.logging.*;
import java.util.List;

public class TestRepositoryInMemory {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 21 — REPOSITORY PATTERN (IN-MEMORY)");
        System.out.println("=".repeat(60));

        AccountRepository accountRepo = new InMemoryAccountRepository();
        TransactionRepository txnRepo = new InMemoryTransactionRepository();
        TransactionLogger logger = new TransactionLogger(new MemoryLogDestination());

        AccountService service = new AccountService(accountRepo, txnRepo, logger);

        // ============================================================
        // 📝 STEP 17: Test Account Repository CRUD
        // ============================================================
        // TODO: test account save, findById, findAll, update, exists, nextAccountNumber

        // ============================================================
        // 📝 STEP 18: Test Transaction Repository Operations
        // ============================================================
        // TODO: test transaction save, findByAccount, findAll, clear

        // ============================================================
        // 📝 STEP 19: Test Service Integration With Repositories
        // ============================================================
        // TODO: test openAccount, deposit, withdraw, transfer via AccountService

        System.out.println("Activity 21 Repository tests complete.");
    }
}
