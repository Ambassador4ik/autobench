group = "me.crymath"
version = "1.0-SNAPSHOT"

plugins {
    application
    java
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories { mavenCentral() }

/* ---------- Dependencies ---------- */
dependencies {
    // Kafka client + perf-test helpers
    implementation("org.apache.kafka:kafka-clients:4.0.0")     // :contentReference[oaicite:0]{index=0}
    implementation("org.apache.kafka:kafka-tools:4.0.0")       // :contentReference[oaicite:1]{index=1}

    // Statistics & histograms
    implementation("org.apache.commons:commons-math3:3.6.1")   // :contentReference[oaicite:2]{index=2}
    implementation("org.hdrhistogram:HdrHistogram:2.1.12")     // :contentReference[oaicite:3]{index=3}

    // JMH – micro-benchmark harness
    implementation("org.openjdk.jmh:jmh-core:1.37")            // :contentReference[oaicite:4]{index=4}
    annotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.37")

    // Simple console logging (optional)
    implementation("org.slf4j:slf4j-simple:2.0.13")

    // JUnit 5 skeleton (in case you add tests)
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

application {
    mainClass.set("me.crymath.autobench.runner.BenchmarkRunner")
}
