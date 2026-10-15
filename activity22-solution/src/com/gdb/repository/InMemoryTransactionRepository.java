package com.gdb.repository;

import com.gdb.domain.Transaction;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of TransactionRepository backed by a List.
 */
public class InMemoryTransactionRepository implements TransactionRepository {

    private final List<Transaction> transactions = new ArrayList<>();

    @Override
    public void save(Transaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
        }
    }

    @Override
    public List<Transaction> findByAccount(int accountNumber) {
        return transactions.stream()
                .filter(t -> t.getAccountNumber() == accountNumber || 
                             t.getFromAccount() == accountNumber || 
                             t.getToAccount() == accountNumber)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions);
    }

    @Override
    public void clear() {
        transactions.clear();
    }
}
