package com.tricentis.demowebshop.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Creates WebDriver instances with WebDriverManager-managed drivers.
 */
public final class WebDriverFactory {

    private static final Logger log = LoggerFactory.getLogger(WebDriverFactory.class);

    private WebDriverFactory() {
    }

    public static WebDriver createDriver() {
        String browser = ConfigReader.get("browser", "chrome").toLowerCase();
        boolean headless = Boolean.parseBoolean(ConfigReader.get("browser.headless", "true"));
        WebDriver driver = switch (browser) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions o = new FirefoxOptions();
                if (headless) {
                    o.addArguments("-headless");
                }
                yield new FirefoxDriver(o);
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions o = new EdgeOptions();
                if (headless) {
                    o.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080");
                }
                yield new EdgeDriver(o);
            }
            default -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions o = new ChromeOptions();
                if (headless) {
                    o.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--window-size=1920,1080");
                }
                yield new ChromeDriver(o);
            }
        };

        int implicit = ConfigReader.getInt("implicit.wait.seconds", 0);
        if (implicit > 0) {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicit));
        }
        int pageLoadSec = ConfigReader.getInt("page.load.timeout.seconds", 60);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadSec));
        driver.manage().window().maximize();
        log.info("WebDriver started: browser={}, headless={}, implicitWaitSec={}, pageLoadTimeoutSec={}",
                browser, headless, implicit, pageLoadSec);
        log.debug("Driver class: {}", driver.getClass().getName());
        return driver;
    }
}
