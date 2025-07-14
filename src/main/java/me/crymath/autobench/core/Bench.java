/* Benchmark, BenchmarkCtx, BenchmarkResult */
package me.crymath.autobench.core;
public interface Bench { void setup() throws Exception; BenchmarkResult execute() throws Exception; void teardown() throws Exception; }
