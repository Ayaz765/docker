package com.tricentis.demowebshop.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures PNG screenshots for failed tests or debugging.
 * Files are written under {@code target/screenshots/} with a timestamp and test name in the filename.
 */
public final class ScreenshotHelper {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotHelper.class);
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private ScreenshotHelper() {
    }

    /**
     * Saves a full-page screenshot if the driver supports {@link TakesScreenshot}.
     *
     * @param driver   active WebDriver (may be null)
     * @param testName logical test name (used in the file name; sanitized)
     * @return absolute path to the PNG file, or {@code null} if capture was skipped or failed
     */
    public static String capturePng(WebDriver driver, String testName) {
        if (driver == null) {
            log.debug("Screenshot skipped: WebDriver is null");
            return null;
        }
        if (!(driver instanceof TakesScreenshot ts)) {
            log.debug("Screenshot skipped: driver does not implement TakesScreenshot");
            return null;
        }
        String safe = testName == null ? "test" : testName.replaceAll("[^a-zA-Z0-9._-]+", "_");
        String stamp = LocalDateTime.now().format(TS);
        Path dir = Path.of("target", "screenshots");
        try {
            Files.createDirectories(dir);
            Path file = dir.resolve(stamp + "_" + safe + ".png");
            byte[] png = ts.getScreenshotAs(OutputType.BYTES);
            Files.write(file, png);
            String absolute = file.toAbsolutePath().normalize().toString();
            log.info("Screenshot saved: {}", absolute);
            return absolute;
        } catch (Exception e) {
            log.warn("Could not save screenshot: {}", e.getMessage());
            return null;
        }
    }
}
