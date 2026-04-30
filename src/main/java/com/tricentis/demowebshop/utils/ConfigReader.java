package com.tricentis.demowebshop.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads {@code config.properties} from the classpath (typically {@code src/test/resources}).
 */
public final class ConfigReader {

    private static final Logger log = LoggerFactory.getLogger(ConfigReader.class);
    private static final String CONFIG = "config.properties";
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG)) {
            if (in == null) {
                throw new IllegalStateException("Missing classpath resource: " + CONFIG);
            }
            PROPS.load(in);
            log.info("Loaded configuration from {}", CONFIG);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        String v = PROPS.getProperty(key);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("Missing or empty config key: " + key);
        }
        return v.trim();
    }

    public static String get(String key, String defaultValue) {
        String v = PROPS.getProperty(key);
        return (v == null || v.isBlank()) ? defaultValue : v.trim();
    }

    public static int getInt(String key, int defaultValue) {
        String v = PROPS.getProperty(key);
        if (v == null || v.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(v.trim());
    }
}
