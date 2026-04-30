package com.tricentis.demowebshop.pages;

import com.tricentis.demowebshop.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Multi-step nopCommerce checkout: billing, shipping, shipping method, payment, confirm.
 */
public class CheckoutPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(CheckoutPage.class);

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public CheckoutPage completeBillingShippingAndPayment() {
        log.info("Checkout: starting billing address step");
        handleBillingAddress();
        clickContinueInSection("billing-buttons-container");
        log.debug("Checkout: billing continue clicked");

        log.info("Checkout: shipping address step");
        handleShippingAddress();
        clickContinueInSection("shipping-buttons-container");
        log.debug("Checkout: shipping continue clicked");

        log.info("Checkout: selecting shipping method");
        selectFirstShippingMethod();
        clickContinueInSection("shipping-method-buttons-container");

        log.info("Checkout: selecting payment method (prefer COD)");
        selectPaymentMethodPreferCod();
        clickContinueInSection("payment-method-buttons-container");

        log.debug("Checkout: payment info step (if shown)");
        handlePaymentInformationIfPresent();
        clickContinueInSection("payment-info-buttons-container");

        log.info("Checkout: confirming order");
        confirmOrder();
        log.info("Checkout: confirm order submitted");
        return this;
    }

    private void handleBillingAddress() {
        By existing = By.id("billing-address-select");
        if (isDisplayed(existing)) {
            WebElement selectEl = driver.findElement(existing);
            Select select = new Select(selectEl);
            if (select.getOptions().size() > 1) {
                select.selectByIndex(1);
            } else {
                select.selectByIndex(0);
            }
        }
        By newFirstName = By.id("BillingNewAddress_FirstName");
        if (isDisplayed(newFirstName)) {
            typeIfPresent(By.id("BillingNewAddress_FirstName"), ConfigReader.get("billing.firstname"));
            typeIfPresent(By.id("BillingNewAddress_LastName"), ConfigReader.get("billing.lastname"));
            typeIfPresent(By.id("BillingNewAddress_Email"), ConfigReader.get("billing.email"));
            selectByVisibleTextIfPresent(By.id("BillingNewAddress_CountryId"), ConfigReader.get("billing.country.id"));
            selectByVisibleTextIfPresent(By.id("BillingNewAddress_StateProvinceId"), ConfigReader.get("billing.state.id"));
            typeIfPresent(By.id("BillingNewAddress_City"), ConfigReader.get("billing.city"));
            typeIfPresent(By.id("BillingNewAddress_Address1"), ConfigReader.get("billing.address1"));
            typeIfPresent(By.id("BillingNewAddress_ZipPostalCode"), ConfigReader.get("billing.zip"));
            typeIfPresent(By.id("BillingNewAddress_PhoneNumber"), ConfigReader.get("billing.phone"));
        }
    }

    private void handleShippingAddress() {
        By shipSame = By.id("ShipToSameAddress");
        if (isDisplayed(shipSame) && !driver.findElement(shipSame).isSelected()) {
            driver.findElement(shipSame).click();
        }
        By shippingNew = By.id("ShippingNewAddress_FirstName");
        if (isDisplayed(shippingNew)) {
            typeIfPresent(By.id("ShippingNewAddress_FirstName"), ConfigReader.get("billing.firstname"));
            typeIfPresent(By.id("ShippingNewAddress_LastName"), ConfigReader.get("billing.lastname"));
            selectByVisibleTextIfPresent(By.id("ShippingNewAddress_CountryId"), ConfigReader.get("billing.country.id"));
            selectByVisibleTextIfPresent(By.id("ShippingNewAddress_StateProvinceId"), ConfigReader.get("billing.state.id"));
            typeIfPresent(By.id("ShippingNewAddress_City"), ConfigReader.get("billing.city"));
            typeIfPresent(By.id("ShippingNewAddress_Address1"), ConfigReader.get("billing.address1"));
            typeIfPresent(By.id("ShippingNewAddress_ZipPostalCode"), ConfigReader.get("billing.zip"));
            typeIfPresent(By.id("ShippingNewAddress_PhoneNumber"), ConfigReader.get("billing.phone"));
        }
    }

    private void selectFirstShippingMethod() {
        List<WebElement> options = driver.findElements(By.cssSelector("input[name='shippingoption']"));
        if (!options.isEmpty()) {
            wait.untilClickable(options.get(0)).click();
        }
    }

    private void selectPaymentMethodPreferCod() {
        By cod = By.cssSelector("input[name='paymentmethod'][value*='CashOnDelivery']");
        if (!driver.findElements(cod).isEmpty()) {
            wait.untilClickable(cod).click();
            return;
        }
        By fallback = By.xpath("//label[contains(.,'Cash On Delivery')]/preceding-sibling::input[@type='radio']");
        if (!driver.findElements(fallback).isEmpty()) {
            wait.untilClickable(fallback).click();
            return;
        }
        List<WebElement> any = driver.findElements(By.cssSelector("input[name='paymentmethod'][type='radio']"));
        if (!any.isEmpty()) {
            wait.untilClickable(any.get(0)).click();
        }
    }

    private void handlePaymentInformationIfPresent() {
        By cardHolder = By.id("CardholderName");
        if (isDisplayed(cardHolder)) {
            // Dummy data if credit card appears (rare when COD selected)
            typeIfPresent(cardHolder, "Test User");
            typeIfPresent(By.id("CardNumber"), "4111111111111111");
            typeIfPresent(By.id("CardCode"), "123");
            selectByVisibleTextIfPresent(By.id("ExpireMonth"), "12");
            selectByVisibleTextIfPresent(By.id("ExpireYear"), String.valueOf(java.time.Year.now().getValue() + 1));
        }
    }

    private void confirmOrder() {
        List<By> tries = List.of(
                By.cssSelector("#confirm-order-buttons-container input.confirm-order-next-step-button"),
                By.cssSelector("input.confirm-order-next-step-button[value='Confirm']"),
                By.xpath("//div[@id='confirm-order-buttons-container']//input[@type='button' and @value='Confirm']")
        );
        for (By confirm : tries) {
            if (!driver.findElements(confirm).isEmpty()) {
                wait.untilClickable(confirm).click();
                return;
            }
        }
        wait.untilClickable(By.xpath("//input[@type='button' and @value='Confirm']")).click();
    }

    public String getOrderSuccessMessage() {
        new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds", 20)))
                .until(d -> {
                    String url = d.getCurrentUrl();
                    if (url.contains("checkout/completed")) {
                        return true;
                    }
                    return !d.findElements(By.cssSelector(
                            "div.page.order-completed-page, .order-completed-page, div.checkout-page.order-completed-page"))
                            .isEmpty();
                });
        By[] lines = new By[]{
                By.cssSelector("div.order-completed-page div.title"),
                By.cssSelector("div.page.order-completed-page div.title"),
                By.cssSelector("div.order-completed-page div.page-title"),
                By.cssSelector("div.page.order-completed-page h1"),
                By.cssSelector(".order-completed-page .title"),
                By.cssSelector(".order-completed .title"),
        };
        for (By line : lines) {
            List<WebElement> els = driver.findElements(line);
            if (!els.isEmpty() && els.get(0).isDisplayed()) {
                return els.get(0).getText().trim();
            }
        }
        return driver.findElement(By.tagName("body")).getText();
    }

    private void clickContinueInSection(String containerId) {
        String base = "//div[@id='" + containerId + "']";
        By[] buttons = new By[]{
                By.xpath(base + "//input[@type='button' and @value='Continue']"),
                By.xpath(base + "//input[@type='submit' and @value='Continue']"),
                By.xpath(base + "//button[contains(.,'Continue')]"),
        };
        for (By button : buttons) {
            if (!driver.findElements(button).isEmpty()) {
                wait.untilClickable(button).click();
                return;
            }
        }
    }

    private boolean isDisplayed(By locator) {
        List<WebElement> found = driver.findElements(locator);
        return !found.isEmpty() && found.get(0).isDisplayed();
    }

    private void typeIfPresent(By locator, String text) {
        if (isDisplayed(locator)) {
            WebElement el = driver.findElement(locator);
            el.clear();
            el.sendKeys(text);
        }
    }

    private void selectByVisibleTextIfPresent(By locator, String visibleText) {
        if (isDisplayed(locator)) {
            new Select(driver.findElement(locator)).selectByVisibleText(visibleText);
        }
    }
}
