package com.gdb.repository;

import com.gdb.domain.Transaction;
import java.util.List;

/**
 * Repository interface for transaction record storage, querying, and atomic balance transfers.
 */
public interface TransactionRepository {
    void save(Transaction transaction);
    List<Transaction> findByAccount(int accountNumber);
    List<Transaction> findAll();
    void clear();
    void transfer(int fromAccountNumber, int toAccountNumber, double amount);
}
