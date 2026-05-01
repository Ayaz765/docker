package com.tricentis.demowebshop.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public final class WebDriverFactory {

    private WebDriverFactory() {}

    public static WebDriver createDriver() {
        String browser = ConfigReader.get("browser", "chrome").toLowerCase();
        boolean headless = Boolean.parseBoolean(ConfigReader.get("browser.headless", "true"));
        boolean remote = Boolean.parseBoolean(ConfigReader.get("remote.execution", "false"));

        WebDriver driver;

        try {
            if (remote) {
                String gridUrl = ConfigReader.get("grid.url", "http://localhost:4444/wd/hub");

                switch (browser) {
                    case "firefox" -> {
                        FirefoxOptions options = new FirefoxOptions();
                        if (headless) options.addArguments("-headless");
                        driver = new RemoteWebDriver(new URL(gridUrl), options);
                    }
                    case "edge" -> {
                        EdgeOptions options = new EdgeOptions();
                        if (headless) {
                            options.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080");
                        }
                        driver = new RemoteWebDriver(new URL(gridUrl), options);
                    }
                    default -> {
                        ChromeOptions options = new ChromeOptions();
                        if (headless) {
                            options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--window-size=1920,1080");
                        }
                        driver = new RemoteWebDriver(new URL(gridUrl), options);
                    }
                }

            } else {
                // 🔥 LOCAL EXECUTION
                switch (browser) {
                    case "firefox" -> {
                        WebDriverManager.firefoxdriver().setup();
                        FirefoxOptions options = new FirefoxOptions();
                        if (headless) options.addArguments("-headless");
                        driver = new FirefoxDriver(options);
                    }
                    case "edge" -> {
                        WebDriverManager.edgedriver().setup();
                        EdgeOptions options = new EdgeOptions();
                        if (headless) {
                            options.addArguments("--headless=new", "--disable-gpu");
                        }
                        driver = new EdgeDriver(options);
                    }
                    default -> {
                        WebDriverManager.chromedriver().setup();
                        ChromeOptions options = new ChromeOptions();
                        if (headless) {
                            options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox");
                        }
                        driver = new ChromeDriver(options);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to create driver", e);
        }

        driver.manage().window().maximize();
        return driver;
    }
}