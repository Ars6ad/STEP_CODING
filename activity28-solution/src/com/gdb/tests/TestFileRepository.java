package com.gdb.tests;

import com.gdb.domain.*;
import com.gdb.repository.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class TestFileRepository {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 26 — FILE REPOSITORY (CSV + ATOMIC WRITES + BACKUP)");
        System.out.println("=".repeat(60));

        String accPath = "test_data/accounts.csv";
        String txnPath = "test_data/transactions.csv";

        // Clean test files
        File dir = new File("test_data");
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) f.delete();
            }
        }

        FileAccountRepository accRepo = new FileAccountRepository(accPath, true, 5);
        FileTransactionRepository txnRepo = new FileTransactionRepository(txnPath, true, 5);

        // TEST 1: Account Save & FindById
        System.out.println("[TEST 1] Account Save & Read Back from CSV:");
        IAccount acc1 = AccountFactory.createAccount("SAVINGS", 1001, "Rajesh Sharma", 30, 50000.0);
        acc1.setPin(1234);
        accRepo.save(acc1);

        IAccount retrieved = accRepo.findById(1001);
        System.out.println("  Saved: " + acc1.getAccountInfo());
        System.out.println("  Found: " + (retrieved != null ? retrieved.getAccountInfo() : "null"));
        assert retrieved != null : "Retrieved account should not be null";
        assert retrieved.getAccountNumber() == 1001 : "Account number mismatch";
        assert retrieved.getBalance() == 50000.0 : "Balance mismatch";
        assert "Rajesh Sharma".equals(retrieved.getAccountHolderName()) : "Name mismatch";
        System.out.println("  -> PASSED");

        // TEST 2: Update & Multiple Accounts
        System.out.println("\n[TEST 2] Multiple Accounts & Atomic Update:");
        IAccount acc2 = AccountFactory.createAccount("CURRENT", 1002, "Priya Patel", 28, 40000.0);
        accRepo.save(acc2);

        retrieved.deposit(10000.0);
        accRepo.update(retrieved);

        IAccount updated = accRepo.findById(1001);
        System.out.println("  Updated balance: Rs. " + updated.getBalance() + " (Expected: 60000.0)");
        assert updated.getBalance() == 60000.0 : "Balance should be updated in CSV";

        List<IAccount> all = accRepo.findAll();
        System.out.println("  Total accounts in CSV: " + all.size() + " (Expected: 2)");
        assert all.size() == 2 : "Should have 2 accounts";
        System.out.println("  -> PASSED");

        // TEST 3: Transaction Persistence in CSV
        System.out.println("\n[TEST 3] Transaction Save & Query in CSV:");
        Transaction t1 = new Transaction(
            "TXN-201", LocalDateTime.now(), 1001,
            TransactionType.DEPOSIT, 10000.0, 60000.0, "SUCCESS",
            "Cash Deposit", 1001, 1001
        );
        Transaction t2 = new Transaction(
            "TXN-202", LocalDateTime.now(), 1001,
            TransactionType.TRANSFER, 5000.0, 55000.0, "SUCCESS",
            "Transfer to #1002", 1001, 1002
        );
        txnRepo.save(t1);
        txnRepo.save(t2);

        List<Transaction> acc1Txns = txnRepo.findByAccount(1001);
        System.out.println("  Found transactions for #1001: " + acc1Txns.size() + " (Expected: 2)");
        assert acc1Txns.size() == 2 : "Account 1001 should have 2 transactions";

        List<Transaction> acc2Txns = txnRepo.findByAccount(1002);
        System.out.println("  Found transactions for #1002: " + acc2Txns.size() + " (Expected: 1)");
        assert acc2Txns.size() == 1 : "Account 1002 should have 1 transaction";
        System.out.println("  -> PASSED");

        // TEST 4: Atomic Transfer in File Repository
        System.out.println("\n[TEST 4] Atomic Transfer via FileAccountRepository:");
        txnRepo.transfer(1001, 1002, 5000.0);
        IAccount srcAfter = accRepo.findById(1001);
        IAccount dstAfter = accRepo.findById(1002);
        System.out.println("  Sender balance: Rs. " + srcAfter.getBalance() + " (Expected: 55000.0)");
        System.out.println("  Receiver balance: Rs. " + dstAfter.getBalance() + " (Expected: 45000.0)");
        assert srcAfter.getBalance() == 55000.0 : "Sender balance mismatch";
        assert dstAfter.getBalance() == 45000.0 : "Receiver balance mismatch";
        System.out.println("  -> PASSED");

        // TEST 5: Backup Creation Verification
        System.out.println("\n[TEST 5] Verify Timestamped Backups Created:");
        File[] backups = dir.listFiles((d, name) -> name.contains(".bak."));
        int backupCount = backups != null ? backups.length : 0;
        System.out.println("  Backup files generated in test_data: " + backupCount);
        assert backupCount > 0 : "BackupManager should have created backup files";
        System.out.println("  -> PASSED");

        // Clean up test data
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) f.delete();
            }
            dir.delete();
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ALL ACTIVITY 26 FILE REPOSITORY TESTS PASSED!");
        System.out.println("=".repeat(60));
    }
}
