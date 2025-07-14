/* src/.../jmh/BaseJmhBenchmark.java */
package me.crymath.autobench.jmh;
import me.crymath.autobench.core.Bench;
import me.crymath.autobench.core.BenchCtx;
import me.crymath.autobench.core.BenchmarkResult;
import org.openjdk.jmh.annotations.*;

@State(Scope.Thread)
public abstract class BaseJmhBench implements Bench {
    protected BenchCtx ctx;
    protected void doSetup()throws Exception{}
    protected void doTeardown()throws Exception{}
    protected abstract void run(BenchCtx ctx)throws Exception;

    @Override public void setup() throws Exception { doSetup(); }
    @Override public BenchmarkResult execute() throws Exception{
        BenchmarkResult r=new BenchmarkResult(); ctx=r; run(ctx); return r;
    }
    @Override public void teardown() throws Exception { doTeardown(); }

    @Benchmark @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(java.util.concurrent.TimeUnit.MILLISECONDS)
    public void jmhHarness() throws Exception { execute(); }
}
