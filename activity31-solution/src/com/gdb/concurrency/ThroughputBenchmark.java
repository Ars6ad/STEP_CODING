package com.gdb.concurrency;

import java.util.*;

/**
 * NFR performance benchmark measuring throughput (ops/sec),
 * average latency, and percentile latencies (P95, P99).
 */
public class ThroughputBenchmark {

    public static class BenchmarkResult {
        public final String name;
        public final int totalOperations;
        public final long durationMs;
        public final double opsPerSecond;
        public final double avgLatencyMs;
        public final double p95LatencyMs;
        public final double p99LatencyMs;
        public final double errorRatePercent;

        public BenchmarkResult(String name, int totalOperations, long durationMs,
                               double avgLatencyMs, double p95LatencyMs, double p99LatencyMs, double errorRatePercent) {
            this.name = name;
            this.totalOperations = totalOperations;
            this.durationMs = durationMs;
            this.opsPerSecond = durationMs > 0 ? (totalOperations * 1000.0) / durationMs : 0.0;
            this.avgLatencyMs = avgLatencyMs;
            this.p95LatencyMs = p95LatencyMs;
            this.p99LatencyMs = p99LatencyMs;
            this.errorRatePercent = errorRatePercent;
        }
    }

    public static double calculatePercentile(List<Long> latencies, double percentile) {
        if (latencies == null || latencies.isEmpty()) {
            return 0.0;
        }
        List<Long> sorted = new ArrayList<>(latencies);
        Collections.sort(sorted);
        int index = (int) Math.ceil((percentile / 100.0) * sorted.size()) - 1;
        index = Math.max(0, Math.min(index, sorted.size() - 1));
        return sorted.get(index) / 1_000_000.0; // Convert nanoseconds to milliseconds
    }

    public static void printComparisonTable(BenchmarkResult syncRes, BenchmarkResult asyncRes) {
        System.out.println("┌───────────────────────────┬──────────────────────┬──────────────────────┐");
        System.out.println("│ Metric                    │ Synchronous (Sync)   │ Asynchronous (Async) │");
        System.out.println("├───────────────────────────┼──────────────────────┼──────────────────────┤");
        System.out.printf("│ Total Operations          │ %-20d │ %-20d │%n", syncRes.totalOperations, asyncRes.totalOperations);
        System.out.printf("│ Total Duration (ms)       │ %-20d │ %-20d │%n", syncRes.durationMs, asyncRes.durationMs);
        System.out.printf("│ Throughput (ops/sec)      │ %-20.2f │ %-20.2f │%n", syncRes.opsPerSecond, asyncRes.opsPerSecond);
        System.out.printf("│ Average Latency (ms)      │ %-20.3f │ %-20.3f │%n", syncRes.avgLatencyMs, asyncRes.avgLatencyMs);
        System.out.printf("│ P95 Latency (ms)          │ %-20.3f │ %-20.3f │%n", syncRes.p95LatencyMs, asyncRes.p95LatencyMs);
        System.out.printf("│ P99 Latency (ms)          │ %-20.3f │ %-20.3f │%n", syncRes.p99LatencyMs, asyncRes.p99LatencyMs);
        System.out.printf("│ Error Rate (%%)            │ %-20.2f │ %-20.2f │%n", syncRes.errorRatePercent, asyncRes.errorRatePercent);
        System.out.println("└───────────────────────────┴──────────────────────┴──────────────────────┘");
    }
}
