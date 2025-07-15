package me.crymath.autobench.benchmarks;

import me.crymath.autobench.core.BenchmarkCtx;
import me.crymath.autobench.jmh.BaseJmhBench;
import me.crymath.autobench.util.Config;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ProducerThroughputBench extends BaseJmhBench {
    private KafkaProducer<byte[], byte[]> p;

    protected void doSetup() {
        p = new KafkaProducer<>(Config.producerProps());
    }

    protected void run(BenchmarkCtx ctx){
        long t0 = System.nanoTime();
        long msgs = 1_000_000;
        byte[] payload=new byte[512];

        for(long i = 0; i < msgs; i++) p.send(new ProducerRecord<>("throughput",payload));

        p.flush();
        long durMs=(System.nanoTime()-t0)/1_000_000;
        ctx.recordThroughput(msgs/(durMs/1000.0)); ctx.recordTime(durMs);
    }

    protected void doTeardown() {
        p.close();
    }
}
