package com.gdb.concurrency;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * Utility helper methods for non-blocking asynchronous workflows,
 * timeouts, and fallback handling with CompletableFuture.
 */
public class AsyncUtils {

    public static <T> CompletableFuture<T> supplyAsyncWithFallback(Supplier<T> supplier, T fallbackValue, Executor executor) {
        if (executor != null) {
            return CompletableFuture.supplyAsync(supplier, executor)
                    .exceptionally(ex -> fallbackValue);
        } else {
            return CompletableFuture.supplyAsync(supplier)
                    .exceptionally(ex -> fallbackValue);
        }
    }

    public static <T> CompletableFuture<T> withTimeout(CompletableFuture<T> future, long timeout, TimeUnit unit, T fallbackValue) {
        if (future == null) {
            return CompletableFuture.completedFuture(fallbackValue);
        }
        return future.completeOnTimeout(fallbackValue, timeout, unit);
    }
}
