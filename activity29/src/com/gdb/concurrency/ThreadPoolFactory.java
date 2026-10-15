package com.gdb.concurrency;

import java.util.Properties;
import java.util.concurrent.*;

/**
 * Factory for creating and managing standard Java ExecutorService thread pools
 * with graceful shutdown lifecycle management.
 */
public class ThreadPoolFactory {

    // ============================================================
    // 📝 STEP 1: Implement Factory Methods
    //
    // INSTRUCTIONS:
    //   1. newFixedPool(int size): return Executors.newFixedThreadPool(size).
    //   2. newCachedPool(): return Executors.newCachedThreadPool().
    //   3. newSingleThreadExecutor(): return Executors.newSingleThreadExecutor().
    // ============================================================
    public static ExecutorService newFixedPool(int size) {
        // TODO: Step 1 - Return fixed thread pool
        return null;
    }

    public static ExecutorService newCachedPool() {
        // TODO: Step 1 - Return cached thread pool
        return null;
    }

    public static ExecutorService newSingleThreadExecutor() {
        // TODO: Step 1 - Return single thread executor
        return null;
    }

    // ============================================================
    // 📝 STEP 2: Implement createPoolFromConfig(Properties config)
    //
    // INSTRUCTIONS:
    //   Read "concurrency.pool.type" (fixed, cached, single).
    //   If "fixed", read "concurrency.pool.size" (default 10).
    // ============================================================
    public static ExecutorService createPoolFromConfig(Properties config) {
        // TODO: Step 2 - Create pool from config properties
        return newFixedPool(10);
    }

    // ============================================================
    // 📝 STEP 3: Implement shutdownGracefully(ExecutorService executor, long timeoutMs)
    //
    // INSTRUCTIONS:
    //   1. Call executor.shutdown() to reject new tasks.
    //   2. Wait for termination using awaitTermination(timeoutMs, TimeUnit.MILLISECONDS).
    //   3. If not terminated within timeout, invoke executor.shutdownNow().
    //   4. If InterruptedException is caught, call shutdownNow() and restore interrupted status.
    // ============================================================
    public static void shutdownGracefully(ExecutorService executor, long timeoutMs) {
        // TODO: Step 3 - Implement graceful two-phase shutdown
    }
}
