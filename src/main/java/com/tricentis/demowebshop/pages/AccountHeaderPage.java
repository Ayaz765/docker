package com.tricentis.demowebshop.pages;

import com.tricentis.demowebshop.pages.locators.DemoWebShopLocators.Header;
import org.openqa.selenium.WebDriver;

/**
 * Log out from the header when session is authenticated.
 */
public class AccountHeaderPage extends BasePage {

    public AccountHeaderPage(WebDriver driver) {
        super(driver);
    }

    public HomePage logout() {
        wait.untilClickable(Header.LOGOUT_LINK).click();
        return new HomePage(driver);
    }
}
