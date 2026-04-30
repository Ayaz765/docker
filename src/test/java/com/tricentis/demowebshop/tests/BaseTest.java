package com.tricentis.demowebshop.tests;

import com.tricentis.demowebshop.utils.ConfigReader;
import com.tricentis.demowebshop.utils.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

    /**
     * Holds the current thread's WebDriver so TestNG listeners (e.g. ExtentReports) can take
     * failure screenshots before {@link #tearDown()} runs.
     */
    private static final ThreadLocal<WebDriver> CURRENT_DRIVER = new ThreadLocal<>();

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected WebDriver driver;

    /**
     * @return the WebDriver for the running test method on this thread, or {@code null}
     */
    public static WebDriver currentDriver() {
        return CURRENT_DRIVER.get();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = WebDriverFactory.createDriver();
        CURRENT_DRIVER.set(driver);
        log.info("Starting test with base URL {}", ConfigReader.get("base.url"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
                log.info("WebDriver closed");
            }
        } finally {
            CURRENT_DRIVER.remove();
        }
    }
}
