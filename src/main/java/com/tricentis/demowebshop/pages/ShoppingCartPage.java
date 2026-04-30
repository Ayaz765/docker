package com.tricentis.demowebshop.pages;

import com.tricentis.demowebshop.pages.locators.DemoWebShopLocators.Cart;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

public class ShoppingCartPage extends BasePage {

    @FindBy(css = Cart.ROWS_CSS)
    private List<WebElement> cartRows;

    @FindBy(id = Cart.TERMS_ID)
    private WebElement termsCheckbox;

    @FindBy(id = Cart.CHECKOUT_ID)
    private WebElement checkoutButton;

    public ShoppingCartPage(WebDriver driver) {
        super(driver);
    }

    public List<String> getProductNamesInCart() {
        wait.untilVisible(Cart.TABLE);
        wait.untilVisible(Cart.LINE_PRODUCT_NAME);
        return driver.findElements(Cart.LINE_PRODUCT_NAME).stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());
    }

    public CheckoutPage proceedToCheckout() {
        wait.untilClickable(Cart.TERMS);
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }
        wait.untilClickable(Cart.CHECKOUT);
        checkoutButton.click();
        return new CheckoutPage(driver);
    }
}
