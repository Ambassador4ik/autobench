package me.crymath.autobench.jmh;
import me.crymath.autobench.core.Benchmark;
import me.crymath.autobench.core.BenchmarkCtx;
import me.crymath.autobench.core.BenchmarkResult;
import org.openjdk.jmh.annotations.*;

@State(Scope.Thread)
public abstract class BaseJmhBench implements Benchmark {
    protected BenchmarkCtx ctx;

    protected void doSetup() throws Exception{}
    protected void doTeardown() throws Exception{}
    protected abstract void run (BenchmarkCtx ctx) throws Exception;

    @Override
    public void setup() throws Exception {
        doSetup();
    }

    @Override
    public BenchmarkResult execute() throws Exception {
        BenchmarkResult r = new BenchmarkResult();
        ctx = r;
        run(ctx);
        return r;
    }

    @Override
    public void teardown() throws Exception {
        doTeardown();
    }

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(java.util.concurrent.TimeUnit.MILLISECONDS)
    public void jmhHarness() throws Exception {
        setup();
        try {
            execute();
        } finally {
            teardown();
        }
    }
}
