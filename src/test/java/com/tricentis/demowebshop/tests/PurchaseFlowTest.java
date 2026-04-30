package com.tricentis.demowebshop.tests;

import com.tricentis.demowebshop.listeners.ExtentReporterListener;
import com.tricentis.demowebshop.pages.locators.DemoWebShopLocators.Header;
import com.tricentis.demowebshop.pages.AccountHeaderPage;
import com.tricentis.demowebshop.pages.CheckoutPage;
import com.tricentis.demowebshop.pages.HomePage;
import com.tricentis.demowebshop.pages.LoginPage;
import com.tricentis.demowebshop.pages.ProductDetailsPage;
import com.tricentis.demowebshop.pages.SearchResultsPage;
import com.tricentis.demowebshop.pages.ShoppingCartPage;
import com.tricentis.demowebshop.utils.ConfigReader;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * End-to-end purchase scenario on https://demowebshop.tricentis.com
 */
public class PurchaseFlowTest extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(PurchaseFlowTest.class);

    @DataProvider(name = "searchKeywords")
    public Object[][] searchKeywords() {
        return new Object[][]{
                {ConfigReader.get("search.keyword")}
        };
    }

    @Test(dataProvider = "searchKeywords", description = "Login, search, cart, checkout, confirm, logout")
    public void shouldCompletePurchaseEndToEnd(String searchKeyword) {
        log.info("=== E2E purchase flow: search keyword = [{}] ===", searchKeyword);
        ExtentReporterListener.step("Parameters: search keyword = " + searchKeyword);

        log.debug("Opening home page");
        HomePage home = new HomePage(driver).open();
        ExtentReporterListener.step("Opened home page: " + ConfigReader.get("base.url"));

        log.debug("Navigating to login");
        LoginPage login = home.goToLogin();
        ExtentReporterListener.step("Navigated to Log in page");

        log.info("Logging in as user [{}]", maskEmail(ConfigReader.get("user.email")));
        login.loginAs(ConfigReader.get("user.email"), ConfigReader.get("user.password"));
        ExtentReporterListener.step("Submitted login form");

        waitForLogoutLink();
        log.info("Login assertion: checking Log out link is visible");
        assertLogoutLinkVisible("login");
        ExtentReporterListener.step("Assertion passed: user is logged in (Log out visible)");

        log.debug("Searching for: {}", searchKeyword);
        SearchResultsPage results = new HomePage(driver).searchFor(searchKeyword);
        int count = results.resultCount();
        log.info("Search returned {} product row(s)", count);
        ExtentReporterListener.step("Search completed; result count = " + count);
        assertSearchHasResults(count, searchKeyword);

        ProductDetailsPage product = results.openFirstReadyToShipProduct();
        String selectedName = product.getProductTitle();
        log.info("Selected product title: {}", selectedName);
        ExtentReporterListener.step("Opened product details: " + selectedName);

        log.debug("Adding product to cart (with attributes if configurable)");
        product.addToCart();
        log.info("Add-to-cart notification check");
        assertAddToCartBannerVisible(selectedName, product.isAddToCartSuccessDisplayed());
        ExtentReporterListener.step("Assertion passed: add-to-cart success notification displayed");

        log.debug("Opening shopping cart");
        ShoppingCartPage cart = product.goToCart();
        List<String> names = cart.getProductNamesInCart();
        log.info("Cart line item name(s): {}", names);
        ExtentReporterListener.step("Cart page: line items = " + names);
        assertCartListsProduct(names, selectedName);
        ExtentReporterListener.step("Assertion passed: cart contains selected product");

        log.info("Starting checkout wizard");
        CheckoutPage checkout = cart.proceedToCheckout();
        ExtentReporterListener.step("Proceeded to checkout (terms accepted)");

        checkout.completeBillingShippingAndPayment();
        log.info("Checkout steps completed (billing, shipping, payment, confirm)");
        ExtentReporterListener.step("Completed billing, shipping, shipping method, payment, confirm order");

        String msg = checkout.getOrderSuccessMessage();
        log.info("Order confirmation text: {}", msg);
        ExtentReporterListener.step("Order confirmation message: " + msg);
        assertOrderConfirmationMessage(msg);
        ExtentReporterListener.step("Assertion passed: order success message visible");

        log.debug("Logging out");
        new AccountHeaderPage(driver).logout();
        assertLoggedOut();
        log.info("=== E2E flow finished successfully ===");
        ExtentReporterListener.step("Logged out; session ended");
    }

    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        int at = email.indexOf('@');
        String local = email.substring(0, at);
        String tail = local.length() > 2 ? local.substring(0, 2) + "***" : "***";
        return tail + email.substring(at);
    }

    private void waitForLogoutLink() {
        log.debug("Waiting for Log out link after login");
        new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds", 20)))
                .until(ExpectedConditions.visibilityOfElementLocated(Header.LOGOUT_LINK));
    }

    private boolean isLogoutPresent() {
        var els = driver.findElements(Header.LOGOUT_LINK);
        return !els.isEmpty() && els.get(0).isDisplayed();
    }

    private String currentContext() {
        return "url=" + driver.getCurrentUrl();
    }

    private void assertLogoutLinkVisible(String phase) {
        Assert.assertTrue(
                isLogoutPresent(),
                "After " + phase + ", the Log out link should be visible (logged-in state). " + currentContext());
    }

    private void assertSearchHasResults(int count, String keyword) {
        Assert.assertTrue(
                count > 0,
                "Search for '" + keyword + "' should return at least one product row; got count=" + count + ". "
                        + currentContext());
    }

    private void assertAddToCartBannerVisible(String productTitle, boolean successBannerShown) {
        Assert.assertTrue(
                successBannerShown,
                "After add-to-cart for '" + productTitle + "', the success bar-notification should appear. "
                        + currentContext());
    }

    private void assertCartListsProduct(List<String> lineNames, String selectedTitle) {
        Assert.assertFalse(
                lineNames.isEmpty(),
                "Cart should list at least one line; cart was empty. " + currentContext());
        boolean match = lineNames.stream().anyMatch(n -> n.toLowerCase().contains(selectedTitle.toLowerCase())
                || selectedTitle.toLowerCase().contains(n.toLowerCase()));
        Assert.assertTrue(
                match,
                "Cart line should reference the selected product. Expected title fragment '" + selectedTitle
                        + "' to match one of: "
                        + lineNames.stream().map(n -> "'" + n + "'").collect(Collectors.joining(", "))
                        + ". " + currentContext());
    }

    private void assertOrderConfirmationMessage(String msg) {
        String lower = msg.toLowerCase();
        Assert.assertTrue(
                lower.contains("successfully processed") || lower.contains("order"),
                "Order confirmation should mention processing or order. Actual message: "
                        + (msg == null || msg.isBlank() ? "(empty)" : msg.strip())
                        + ". " + currentContext());
    }

    private void assertLoggedOut() {
        Assert.assertFalse(
                isLogoutPresent(),
                "After logout, the Log out link should not be visible. " + currentContext());
    }
}
