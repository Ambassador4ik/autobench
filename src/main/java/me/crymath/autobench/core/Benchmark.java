package me.crymath.autobench.core;

public interface Benchmark {
    void setup() throws Exception;

    BenchmarkResult execute() throws Exception;

    void teardown() throws Exception;
}
