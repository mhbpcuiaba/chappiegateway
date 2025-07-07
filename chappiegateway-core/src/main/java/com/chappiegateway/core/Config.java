package com.chappiegateway.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class Config {

    private static final Logger log = LoggerFactory.getLogger(Config.class);

    public final String host;
    public final int port;

    private Config(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static Config load() {
        Yaml yaml = new Yaml();

        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("application.yaml")) {
            if (in == null) {
                throw new IllegalStateException("❌ application.yaml not found on classpath!");
            }

            Map<String, Object> obj = yaml.load(in);

            if (obj == null || !obj.containsKey("server")) {
                throw new IllegalStateException("❌ Missing 'server' section in application.yaml!");
            }

            Map<String, Object> server = (Map<String, Object>) obj.get("server");

            String host = (String) server.getOrDefault("host", "0.0.0.0");
            int port = (Integer) server.getOrDefault("port", 8080);

            log.info("✅ Loaded config: host={}, port={}", host, port);
            return new Config(host, port);

        } catch (Exception e) {
            log.error("Failed to load configuration", e);
            throw new RuntimeException("Could not load configuration", e);
        }
    }
}
