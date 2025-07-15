package me.crymath.autobench.core;

public interface Recorder {
    void record(double v);
    Stats stats();
}
