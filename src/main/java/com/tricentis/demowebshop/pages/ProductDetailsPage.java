package com.tricentis.demowebshop.pages;

import com.tricentis.demowebshop.pages.locators.DemoWebShopLocators.Product;
import com.tricentis.demowebshop.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductDetailsPage extends BasePage {

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Configurable nopCommerce products need attributes (RAM, HDD, etc.) before add-to-cart shows the notification.
     */
    private void selectDefaultAttributesIfPresent() {
        List<WebElement> selects = driver.findElements(By.cssSelector(Product.ATTR_SELECT_CSS));
        for (WebElement sel : selects) {
            if (!sel.isDisplayed() || !sel.isEnabled()) {
                continue;
            }
            Select dd = new Select(sel);
            if (dd.getOptions().size() <= 1) {
                continue;
            }
            String firstText = dd.getOptions().get(0).getText().toLowerCase();
            if (firstText.contains("please") || firstText.contains("select")) {
                dd.selectByIndex(1);
            } else {
                dd.selectByIndex(0);
            }
        }

        Set<String> radioGroupsSeen = new HashSet<>();
        List<WebElement> radios = driver.findElements(By.cssSelector(Product.ATTR_RADIO_CSS));
        for (WebElement radio : radios) {
            String name = radio.getAttribute("name");
            if (name != null && radioGroupsSeen.add(name) && radio.isDisplayed()) {
                wait.untilClickable(radio).click();
            }
        }
    }

    public ProductDetailsPage addToCart() {
        wait.untilClickable(Product.ADD_TO_CART);
        selectDefaultAttributesIfPresent();
        WebElement btn = driver.findElement(Product.ADD_TO_CART);
        btn.click();
        waitForAddToCartNotification();
        return this;
    }

    private void waitForAddToCartNotification() {
        int sec = ConfigReader.getInt("explicit.wait.seconds", 20);
        new WebDriverWait(driver, Duration.ofSeconds(sec)).until(d -> {
            try {
                WebElement el = d.findElement(Product.BAR_NOTIFICATION);
                if (!el.isDisplayed()) {
                    return false;
                }
                String cls = el.getAttribute("class");
                String t = el.getText().toLowerCase();
                return (cls != null && cls.contains("success"))
                        || t.contains("has been added")
                        || t.contains("added to your");
            } catch (NoSuchElementException e) {
                return false;
            }
        });
    }

    public boolean isAddToCartSuccessDisplayed() {
        WebElement bar = driver.findElement(Product.BAR_NOTIFICATION);
        String cls = bar.getAttribute("class");
        String t = bar.getText().toLowerCase();
        return bar.isDisplayed()
                && ((cls != null && cls.contains("success"))
                || t.contains("has been added")
                || t.contains("added to your"));
    }

    public String getProductTitle() {
        WebElement title = wait.untilVisible(Product.PRODUCT_TITLE);
        return title.getText().trim();
    }

    public ShoppingCartPage goToCart() {
        wait.untilClickable(Product.CART_NAV).click();
        return new ShoppingCartPage(driver);
    }
}
