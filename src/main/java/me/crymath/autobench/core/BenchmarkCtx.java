package me.crymath.autobench.core;
public interface BenchmarkCtx {
    void record(Metric m, double v);

    default void recordTime(long ms) {
        record(Metric.DURATION_MS,ms);
    }

    default void recordThroughput(double tps) {
        record(Metric.THROUGHPUT_MSGS_SEC,tps);
    }

    default void recordLatency(double ms) {
        record(Metric.LATENCY_MS, ms);
    }

    default void recordCpu(double pct) {
        record(Metric.CPU_UTIL_PCT,pct);
    }

    default void recordHeap(double mb) {
        record(Metric.HEAP_USED_MB,mb);
    }

    default void error(){
        record(Metric.ERROR_RATE,1);
    }
}
