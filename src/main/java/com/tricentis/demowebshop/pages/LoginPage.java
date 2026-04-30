package com.tricentis.demowebshop.pages;

import com.tricentis.demowebshop.pages.locators.DemoWebShopLocators.Auth;
import com.tricentis.demowebshop.pages.locators.DemoWebShopLocators.Header;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    @FindBy(id = Auth.EMAIL_ID)
    private WebElement emailInput;

    @FindBy(id = Auth.PASSWORD_ID)
    private WebElement passwordInput;

    @FindBy(css = Auth.LOGIN_BUTTON_CSS)
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public HomePage loginAs(String email, String password) {
        wait.untilVisible(Auth.EMAIL);
        emailInput.clear();
        emailInput.sendKeys(email);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        loginButton.click();
        return new HomePage(driver);
    }

    public boolean isLogoutVisible() {
        var els = driver.findElements(Header.LOGOUT_LINK);
        return !els.isEmpty() && els.get(0).isDisplayed();
    }
}
