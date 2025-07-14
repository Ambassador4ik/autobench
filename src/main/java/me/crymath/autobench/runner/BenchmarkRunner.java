package me.crymath.autobench.runner;

import me.crymath.autobench.benchmarks.ProducerThroughputBench;
import me.crymath.autobench.core.Bench;
import me.crymath.autobench.core.BenchmarkResult;

import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

public final class BenchmarkRunner{
    public static void main(String[] args) throws Exception {
        List<Bench> benches = Arrays.asList(
                new ProducerThroughputBench()
        );
        int runs = 1;
        for (Bench b : benches) {
            System.out.println("\n▶ " + b.getClass().getSimpleName());
            for (int i = 1; i <= runs; i++) {
                b.setup();
                BenchmarkResult r = b.execute();
                b.teardown();
                System.out.printf("  run %d:\n%s", i, r.pretty());
            }
        }
    }

}
