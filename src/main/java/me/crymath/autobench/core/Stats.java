package me.crymath.autobench.core;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.List;
public final class Stats {
    public final double mean, stdDev, ci95;

    private double p95 = Double.NaN, p99 = Double.NaN;

    Stats(List<Double> xs) {
        DescriptiveStatistics ds = new DescriptiveStatistics();
        xs.forEach(ds::addValue);

        mean   = ds.getMean();
        stdDev = ds.getStandardDeviation();

        if (xs.size() >= 2) {
            double t = new TDistribution(xs.size() - 1)
                    .inverseCumulativeProbability(0.975);
            ci95 = t * stdDev / Math.sqrt(xs.size());
        } else {
            ci95 = Double.NaN;           // or 0, or leave null
        }
    }
    void setP95(double v){ p95=v; } void setP99(double v){ p99=v; }
    @Override public String toString(){
        return String.format("mean=%.3f±%.3f  p95=%.1f  p99=%.1f",mean,ci95,p95,p99);
    }
}
