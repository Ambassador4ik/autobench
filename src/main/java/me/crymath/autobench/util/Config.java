package me.crymath.autobench.util;

import java.util.Properties;

public final class Config {

    private static final String BOOTSTRAP = "rc1a-43egr9qpn2tr6l9f.mdb.yandexcloud.net:9091,rc1b-ttb54mjl6jvcsgnm.mdb.yandexcloud.net:9091,rc1d-dlpdfkngjqi26j06.mdb.yandexcloud.net:9091";

    private static final String USER = "user";
    private static final String PASSWORD = "password";

    public static final String TRUSTSTORE_LOCATION = "/etc/security/ssl";
    public static final String TRUSTSTORE_PASS = "password";

    public static Properties props() {
        Properties p = new Properties();
        p.put("bootstrap.servers", BOOTSTRAP);

        p.put("security.protocol", "SASL_SSL");
        p.put("sasl.mechanism", "SCRAM-SHA-512");

        p.put("sasl.jaas.config",
                "org.apache.kafka.common.security.scram.ScramLoginModule required "
                        + "username=\"" + USER + "\" "
                        + "password=\"" + PASSWORD + "\";");

        p.put("ssl.truststore.location", TRUSTSTORE_LOCATION);
        p.put("ssl.truststore.password", TRUSTSTORE_PASS);

        return p;
    }

    public static Properties producerProps() {
        Properties p = props();
        p.put("acks", "all");
        p.put("key.serializer",  "org.apache.kafka.common.serialization.ByteArraySerializer");
        p.put("value.serializer","org.apache.kafka.common.serialization.ByteArraySerializer");
        return p;
    }

    public static Properties consumerProps() {
        Properties p = props();
        p.put("key.deserializer",  "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        p.put("value.deserializer","org.apache.kafka.common.serialization.ByteArrayDeserializer");
        return p;
    }

    private Config() { }
}
