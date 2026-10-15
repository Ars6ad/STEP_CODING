package com.gdb.concurrency;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * Utility helper methods for non-blocking asynchronous workflows,
 * timeouts, and fallback handling with CompletableFuture.
 */
public class AsyncUtils {

    // ============================================================
    // 📝 STEP 1: Implement supplyAsyncWithFallback(...)
    //
    // INSTRUCTIONS:
    //   1. Execute supplier asynchronously on executor using CompletableFuture.supplyAsync.
    //   2. Attach .exceptionally(ex -> fallbackValue) to gracefully recover on failure.
    // ============================================================
    public static <T> CompletableFuture<T> supplyAsyncWithFallback(Supplier<T> supplier, T fallbackValue, Executor executor) {
        // TODO: Step 1 - Asynchronous supplier with fallback recovery
        return null;
    }

    // ============================================================
    // 📝 STEP 2: Implement withTimeout(...)
    //
    // INSTRUCTIONS:
    //   1. Apply future.completeOnTimeout(fallbackValue, timeout, unit) to enforce bounded execution.
    //   2. Return the guarded CompletableFuture.
    // ============================================================
    public static <T> CompletableFuture<T> withTimeout(CompletableFuture<T> future, long timeout, TimeUnit unit, T fallbackValue) {
        // TODO: Step 2 - Guard future with timeout fallback
        return null;
    }
}
