package me.crymath.autobench.util;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

/**
 * Tiny helper for one-shot JMX reads.  Works against *local* JVM
 * (the benchmark process) or a remote broker if you set
 * jmxHost/jmxPort System-properties or env-vars.
 *
 *   ./gradlew run -DjmxHost=broker1 -DjmxPort=9999
 *
 * If remote connection fails we silently fall back to the current VM
 * so the suite still runs.
 */
public final class Jmx {

    private static final MBeanServerConnection CONN = connect();

    /* ---------- Public convenience API ---------- */

    public static long readCount(String objectName) throws Exception {
        return (Long) CONN.getAttribute(new ObjectName(objectName), "Count");
    }

    public static double cpuLoad() {
        try {
            return (Double) CONN.getAttribute(
                    new ObjectName("java.lang:type=OperatingSystem"),
                    "ProcessCpuLoad");
        } catch (Exception e) { return Double.NaN; }
    }

    public static long heapUsed() {
        try {
            CompositeData cd = (CompositeData) CONN.getAttribute(
                    new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
            return (Long) cd.get("used");
        } catch (Exception e) { return -1; }
    }

    /**
     * Block until the given counter increases or timeoutMillis elapses.
     */
    public static void waitForCountIncrease(String mbean, long before, long timeoutMillis)
            throws Exception {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMillis) {
            if (readCount(mbean) > before) return;
            TimeUnit.MILLISECONDS.sleep(200);
        }
        throw new IllegalStateException("Timeout waiting for " + mbean + " to increase");
    }

    /* ---------- Internal connection logic ---------- */

    private static MBeanServerConnection connect() {
        String host = System.getProperty("jmxHost", System.getenv("JMX_HOST"));
        String port = System.getProperty("jmxPort", System.getenv("JMX_PORT"));
        if (host != null && port != null) {
            try {
                String url = String.format(
                        "service:jmx:rmi:///jndi/rmi://%s:%s/jmxrmi", host, port);
                JMXConnector jmxc = JMXConnectorFactory.connect(
                        new JMXServiceURL(url), null);
                return jmxc.getMBeanServerConnection();
            } catch (Exception ignored) { /* fallthrough */ }
        }
        // fallback -> connect to our own JVM
        return ManagementFactory.getPlatformMBeanServer();
    }

    private Jmx() {}
}
