package com.tricentis.demowebshop.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.tricentis.demowebshop.tests.BaseTest;
import com.tricentis.demowebshop.utils.ScreenshotHelper;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Generates {@code target/extent-report.html} after the suite finishes.
 * Use {@link #step(String)} from tests to record steps in the report.
 * On failure, attaches a PNG from {@link com.tricentis.demowebshop.utils.ScreenshotHelper} when a driver is available.
 */
public class ExtentReporterListener implements ITestListener {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();

    private static synchronized ExtentReports extent() {
        if (extent == null) {
            Path targetDir = Path.of("target");
            try {
                Files.createDirectories(targetDir);
            } catch (Exception ignored) {
                // fall through; Spark reporter may still create parent dirs
            }
            Path out = targetDir.resolve("extent-report.html");
            ExtentSparkReporter spark = new ExtentSparkReporter(out.toAbsolutePath().toString());
            spark.config().setDocumentTitle("Demo Web Shop E2E");
            spark.config().setReportName("Purchase flow");
            spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
            extent.setSystemInfo("Java", System.getProperty("java.version"));
            extent.setSystemInfo("User", System.getProperty("user.name"));
        }
        return extent;
    }

    /**
     * Records a step in the current Extent test (no-op if no test is running).
     */
    public static void step(String detail) {
        ExtentTest t = TEST.get();
        if (t != null) {
            t.info(detail);
        }
    }

    @Override
    public void onStart(ITestContext context) {
        extent();
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest t = extent().createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
        t.assignCategory("E2E", "DemoWebShop");
        TEST.set(t);
        step("Test started: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest t = TEST.get();
        if (t != null) {
            t.pass("Test finished successfully");
        }
        TEST.remove();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest t = TEST.get();
        if (t != null) {
            Throwable ex = result.getThrowable();
            // Failure screenshot (optional): captured while WebDriver is still open (runs before @AfterMethod)
            String shot = ScreenshotHelper.capturePng(BaseTest.currentDriver(), result.getName());
            if (shot != null) {
                if (ex != null) {
                    t.fail(ex, MediaEntityBuilder.createScreenCaptureFromPath(shot).build());
                } else {
                    t.fail("Test failed (no throwable attached)",
                            MediaEntityBuilder.createScreenCaptureFromPath(shot).build());
                }
            } else if (ex != null) {
                t.fail(ex);
            } else {
                t.fail("Test failed (no throwable attached)");
            }
        }
        TEST.remove();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest t = TEST.get();
        if (t != null) {
            t.skip("Skipped: " + (result.getThrowable() != null ? result.getThrowable().getMessage() : ""));
        }
        TEST.remove();
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
            extent = null;
        }
    }
}
