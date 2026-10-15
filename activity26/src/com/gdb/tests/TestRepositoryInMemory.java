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

        // Test Account Creation via Service
        IAccount acc1 = service.openAccount("SAVINGS", "Rajesh Sharma", 30, 50000.0, 2);
        IAccount acc2 = service.openAccount("CURRENT", "Priya Patel", 28, 40000.0, 0);
        acc1.setPin(1234);
        acc2.setPin(5678);

        System.out.println("[TEST 1] Account Creation & Auto ID Generation:");
        System.out.println("  acc1 ID = " + acc1.getAccountNumber() + " (Expected 1001)");
        System.out.println("  acc2 ID = " + acc2.getAccountNumber() + " (Expected 1002)");
        assert acc1.getAccountNumber() == 1001 : "acc1 should have ID 1001";
        assert acc2.getAccountNumber() == 1002 : "acc2 should have ID 1002";
        System.out.println("  -> PASSED");

        // Test Deposit
        System.out.println("\n[TEST 2] Deposit via Service:");
        Transaction depTxn = service.deposit(1001, 10000.0);
        System.out.println("  " + depTxn);
        assert acc1.getBalance() == 60000.0 : "Balance after deposit should be 60000.0";
        System.out.println("  -> PASSED");

        // Test Withdraw
        System.out.println("\n[TEST 3] Withdraw via Service:");
        Transaction wthTxn = service.withdraw(1001, 5000.0, 1234);
        System.out.println("  " + wthTxn);
        assert acc1.getBalance() == 55000.0 : "Balance after withdraw should be 55000.0";
        System.out.println("  -> PASSED");

        // Test Transfer
        System.out.println("\n[TEST 4] Transfer via Service:");
        Transaction trfTxn = service.transfer(1001, 1002, 10000.0, 1234);
        System.out.println("  " + trfTxn);
        assert acc1.getBalance() == 45000.0 : "Sender balance should be 45000.0";
        assert acc2.getBalance() == 50000.0 : "Receiver balance should be 50000.0";
        System.out.println("  -> PASSED");

        // Test Repository Queries
        System.out.println("\n[TEST 5] Repository Queries:");
        List<IAccount> allAccs = service.getAllAccounts();
        System.out.println("  Total accounts in repo: " + allAccs.size() + " (Expected 2)");
        assert allAccs.size() == 2 : "Should have 2 accounts";

        List<Transaction> allTxns = service.getTransactionRecords();
        System.out.println("  Total transactions in repo: " + allTxns.size() + " (Expected 3)");
        assert allTxns.size() == 3 : "Should have 3 transactions";

        List<Transaction> acc1Txns = service.getTransactionRecords(1001);
        System.out.println("  Transactions for account 1001: " + acc1Txns.size() + " (Expected 3)");
        assert acc1Txns.size() == 3 : "Account 1001 should have 3 transactions";
        System.out.println("  -> PASSED");

        // Test RepositoryFactory
        System.out.println("\n[TEST 6] RepositoryFactory Mode:");
        String mode = RepositoryFactory.getPersistenceMode();
        System.out.println("  Persistence mode: " + mode);
        AccountRepository factoryAccRepo = RepositoryFactory.getAccountRepository();
        assert factoryAccRepo != null : "Factory should return non-null repo";
        System.out.println("  -> PASSED");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ALL ACTIVITY 21 IN-MEMORY REPOSITORY TESTS PASSED!");
        System.out.println("=".repeat(60));
    }
}
