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

    // ============================================================
    // 📝 STEP 5: Implement calculatePercentile(...)
    //
    // INSTRUCTIONS:
    //   Sort latencies and compute the value at percentile (0.0 to 100.0).
    // ============================================================
    public static double calculatePercentile(List<Long> latencies, double percentile) {
        // TODO: Step 5 - Calculate latency percentile
        return 0.0;
    }

    // ============================================================
    // 📝 STEP 6: Implement printComparisonTable(...)
    //
    // INSTRUCTIONS:
    //   Print structured comparison table displaying metrics for both results.
    // ============================================================
    public static void printComparisonTable(BenchmarkResult syncRes, BenchmarkResult asyncRes) {
        // TODO: Step 6 - Format and print comparison table
    }
}
