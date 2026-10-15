package com.gdb.service;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service demonstrating Lock-Free and Atomic primitives
 * including AtomicLong counters and CAS (Compare-And-Swap) patterns.
 */
public class AtomicAccountService {

    private final AtomicLong globalTxnCounter;
    private final AtomicLong totalDepositVolumeCents;
    private final AtomicReference<String> systemStatus;

    public AtomicAccountService() {
        this.globalTxnCounter = new AtomicLong(0);
        this.totalDepositVolumeCents = new AtomicLong(0);
        this.systemStatus = new AtomicReference<>("ONLINE");
    }

    public String nextTransactionId() {
        return "ATOMIC-TXN-" + globalTxnCounter.incrementAndGet();
    }

    public long recordDepositVolume(double amount) {
        long cents = Math.round(amount * 100);
        return totalDepositVolumeCents.addAndGet(cents);
    }

    public long getGlobalTransactionCount() {
        return globalTxnCounter.get();
    }

    public double getTotalDepositVolume() {
        return totalDepositVolumeCents.get() / 100.0;
    }

    public boolean updateSystemStatus(String expected, String updated) {
        return systemStatus.compareAndSet(expected, updated);
    }

    public String getSystemStatus() {
        return systemStatus.get();
    }
}
