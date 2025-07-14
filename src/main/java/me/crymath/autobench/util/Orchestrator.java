package me.crymath.autobench.util;

/**
 * Stub methods that *pretend* to start/stop brokers or controllers.
 *
 * Replace the printlns with SSH calls, Kubernetes API invocations,
 * docker-compose commands, or whatever your cloud provider uses.
 * For compile-time purposes we just sleep a little to simulate work.
 */
public final class Orchestrator {

    public static void killController() {
        System.out.println("[Orchestrator] (mock) killing active controller…");
        sleep(1_000);
    }

    public static void startBroker() {
        System.out.println("[Orchestrator] (mock) starting broker…");
        sleep(2_000);
    }

    /* ---------- utils ---------- */
    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    private Orchestrator() {}
}
