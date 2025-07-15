package me.crymath.autobench.core;

import java.util.EnumMap;
import java.util.Map;

public class BenchmarkResult implements BenchmarkCtx {
    private final Map<Metric, Recorder> map = new EnumMap<>(Metric.class);

    public void record(Metric m,double v){
        map.computeIfAbsent(m, k -> k == Metric.LATENCY_MS
                        ? new HistogramRecorder()
                        : new ListRecorder()).record(v);
    }

    public Stats statsFor(Metric m){
        Recorder r = map.get(m);
        return r == null ? null : r.stats();
    }

    public String pretty(){
        StringBuilder sb=new StringBuilder();
        map.forEach((m,r)->sb.append(m).append("  ").append(r.stats()).append("\n"));
        return sb.toString();
    }
}
