package me.crymath.autobench.runner;

import me.crymath.autobench.benchmarks.ProducerPerformanceBench;
import me.crymath.autobench.benchmarks.ProducerThroughputBench;
import me.crymath.autobench.core.Benchmark;
import me.crymath.autobench.core.BenchmarkResult;
import me.crymath.autobench.core.Metric;
import me.crymath.autobench.core.Stats;

import java.util.Arrays;
import java.util.List;

public final class BenchmarkRunner{
    public static void main(String[] args) throws Exception {
        List<Benchmark> benches = Arrays.asList(
                new ProducerPerformanceBench()
        );
        int runs = 5;
        /* runner/BenchmarkRunner.java */
        for (Benchmark b : benches) {
            System.out.println("\n▶ " + b.getClass().getSimpleName());

            BenchmarkResult agg = new BenchmarkResult();   // aggregate across runs

            for (int i = 1; i <= runs; i++) {
                b.setup();
                BenchmarkResult r = b.execute();
                b.teardown();

                // merge *means* into the aggregate
                for (Metric m : Metric.values()) {
                    Stats s = r.statsFor(m);
                    if (s != null && !Double.isNaN(s.mean))
                        agg.record(m, s.mean);
                }

                System.out.printf("  run %d:\n%s", i, r.pretty());
            }

            System.out.println("  ► aggregate (all runs):");
            System.out.print(agg.pretty());
        }

    }

}
