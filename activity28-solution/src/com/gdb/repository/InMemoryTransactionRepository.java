package com.gdb.repository;

import com.gdb.domain.Transaction;
import com.gdb.domain.TransactionType;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTransactionRepository implements TransactionRepository {

    private final List<Transaction> transactions = new ArrayList<>();

    public InMemoryTransactionRepository() {
    }

    @Override
    public synchronized void save(Transaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
        }
    }

    @Override
    public synchronized List<Transaction> findByAccount(int accountNumber) {
        return transactions.stream()
                .filter(t -> t.getAccountNumber() == accountNumber || 
                             t.getFromAccount() == accountNumber || 
                             t.getToAccount() == accountNumber)
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<Transaction> findAll() {
        return new ArrayList<>(transactions);
    }

    @Override
    public synchronized void clear() {
        transactions.clear();
    }

    @Override
    public synchronized void transfer(int fromAccountNumber, int toAccountNumber, double amount) {
        Transaction txn = new Transaction(
                Transaction.generateId(),
                LocalDateTime.now(),
                fromAccountNumber,
                TransactionType.TRANSFER,
                amount,
                0.0,
                "SUCCESS",
                "Transfer to Account #" + toAccountNumber,
                fromAccountNumber,
                toAccountNumber
        );
        save(txn);
    }
}
