/* HistogramRecorder — p95/p99 latency */
package me.crymath.autobench.core;

import org.HdrHistogram.Histogram;

import java.util.ArrayList;
import java.util.List;
class HistogramRecorder implements Recorder {
    private final Histogram hist = new Histogram(1, 60000, 3);
    private final List<Double> raw = new ArrayList<>();
    public void record(double v){ hist.recordValue((long)v); raw.add(v); }
    public Stats stats(){
        Stats s = new Stats(raw);
        s.setP95(hist.getValueAtPercentile(95));
        s.setP99(hist.getValueAtPercentile(99));
        return s;
    }
}
