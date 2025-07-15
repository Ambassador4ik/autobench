package me.crymath.autobench.core;

import java.util.ArrayList;
import java.util.List;

class ListRecorder implements Recorder {
    private final List<Double> xs = new ArrayList<>();

    public void record(double v) {
        xs.add(v);
    }

    public Stats stats(){
        return new Stats(xs);
    }
}
