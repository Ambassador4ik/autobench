/*
 * Benchmark adapted from Apache Kafka ProducerPerformance tool
 * to the autobench framework.
 */
package me.crymath.autobench.benchmarks;

import me.crymath.autobench.core.BenchmarkCtx;
import me.crymath.autobench.jmh.BaseJmhBench;
import me.crymath.autobench.util.Config;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.SplittableRandom;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ProducerPerformanceBench extends BaseJmhBench {

    /* ---------- tunables (override with -Dproperty=value) ---------- */
    private final long   numRecords       = Long.getLong("bench.records",     1_000_000L);
    private final int    recordSize       = Integer.getInteger("bench.size",  512);
    private final double targetThroughput = Double.parseDouble(
            System.getProperty("bench.throughput", "-1")); // msgs/sec, -1 = no throttle
    private final boolean useTransactions = Boolean.getBoolean("bench.tx");
    private final long   txDurationMs     = Long.getLong("bench.tx.duration", 3000L);
    private final String topic            = System.getProperty("bench.topic", "throughput");
    /* ---------------------------------------------------------------- */

    private KafkaProducer<byte[], byte[]> producer;
    private Throttler throttler;

    @Override
    protected void doSetup() {
        Properties props = Config.producerProps();
        producer = new KafkaProducer<>(props);

        if (useTransactions) {
            producer.initTransactions();
        }
        throttler = new Throttler(targetThroughput);
    }

    @Override
    protected void run(BenchmarkCtx ctx) throws Exception {
        byte[] payload = new byte[recordSize];
        SplittableRandom rnd = new SplittableRandom(0);

        CountDownLatch done = new CountDownLatch((int) numRecords);
        long benchStart = System.nanoTime();

        long txStart = 0;
        int  txCount = 0;

        for (long i = 0; i < numRecords; i++) {
            /* begin a new transaction if enabled */
            if (useTransactions && txCount == 0) {
                producer.beginTransaction();
                txStart = System.currentTimeMillis();
            }

            /* generate/overwrite payload */
            for (int j = 0; j < recordSize; j++)
                payload[j] = (byte) (rnd.nextInt(26) + 65);

            long sendStart = System.nanoTime();
            ProducerRecord<byte[], byte[]> rec = new ProducerRecord<>(topic, payload);

            producer.send(rec, (meta, exc) -> {
                if (exc == null) {
                    long latencyMs = (System.nanoTime() - sendStart) / 1_000_000;
                    ctx.recordLatency(latencyMs);
                } else {
                    ctx.error();
                }
                done.countDown();
            });

            txCount++;
            if (useTransactions &&
                    (System.currentTimeMillis() - txStart >= txDurationMs)) {
                producer.commitTransaction();
                txCount = 0;
            }

            throttler.maybeThrottle();
        }

        if (useTransactions && txCount > 0)
            producer.commitTransaction();

        /* wait for all callbacks to complete (max 30 min - safety) */
        done.await(30, TimeUnit.MINUTES);
        producer.flush();

        long durMs = (System.nanoTime() - benchStart) / 1_000_000;
        ctx.recordThroughput(numRecords / (durMs / 1000.0));
        ctx.recordTime(durMs);
    }

    @Override
    protected void doTeardown() {
        producer.close();
    }

    /* ---------- helper to enforce approximate throughput ---------- */
    private static final class Throttler {
        private final double targetMsgsPerSec;
        private long startNs;
        private long sent;

        Throttler(double targetMsgsPerSec) {
            this.targetMsgsPerSec = targetMsgsPerSec;
            this.startNs = System.nanoTime();
        }

        void maybeThrottle() {
            if (targetMsgsPerSec <= 0) return;            // disabled

            sent++;
            double elapsedSec = (System.nanoTime() - startNs) / 1_000_000_000.0;
            double shouldHaveSent = elapsedSec * targetMsgsPerSec;

            /* busy loop avoided – sleep just enough to catch up */
            if (sent > shouldHaveSent) {
                long sleepNs = (long) ((sent - shouldHaveSent) / targetMsgsPerSec * 1_000_000_000L);
                if (sleepNs > 0) {
                    try { TimeUnit.NANOSECONDS.sleep(sleepNs); }
                    catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
                }
            }
        }
    }
}
