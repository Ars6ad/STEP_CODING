package com.gdb.concurrency;

import java.util.Properties;
import java.util.concurrent.*;

/**
 * Factory for creating and managing standard Java ExecutorService thread pools
 * with graceful shutdown lifecycle management.
 */
public class ThreadPoolFactory {

    public static ExecutorService newFixedPool(int size) {
        return Executors.newFixedThreadPool(size);
    }

    public static ExecutorService newCachedPool() {
        return Executors.newCachedThreadPool();
    }

    public static ExecutorService newSingleThreadExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    public static ExecutorService createPoolFromConfig(Properties config) {
        if (config == null) {
            return newFixedPool(10);
        }
        String type = config.getProperty("concurrency.pool.type", "fixed").toLowerCase();
        switch (type) {
            case "cached":
                return newCachedPool();
            case "single":
                return newSingleThreadExecutor();
            case "fixed":
            default:
                int size = Integer.parseInt(config.getProperty("concurrency.pool.size", "10"));
                return newFixedPool(size);
        }
    }

    public static void shutdownGracefully(ExecutorService executor, long timeoutMs) {
        if (executor == null || executor.isShutdown()) {
            return;
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(timeoutMs, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(timeoutMs / 2, TimeUnit.MILLISECONDS)) {
                    System.err.println("ExecutorService did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
