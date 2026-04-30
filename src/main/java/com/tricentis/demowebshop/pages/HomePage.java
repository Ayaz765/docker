package com.tricentis.demowebshop.pages;

import com.tricentis.demowebshop.pages.locators.DemoWebShopLocators.Header;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Header search and navigation to login.
 */
public class HomePage extends BasePage {

    @FindBy(css = Header.SEARCH_INPUT_CSS)
    private WebElement searchBox;

    @FindBy(css = Header.SEARCH_SUBMIT_CSS)
    private WebElement searchButton;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage open() {
        driver.get(baseUrl());
        return this;
    }

    public LoginPage goToLogin() {
        wait.untilClickable(Header.LOGIN_LINK).click();
        return new LoginPage(driver);
    }

    public SearchResultsPage searchFor(String keyword) {
        wait.untilVisible(By.cssSelector(Header.SEARCH_INPUT_CSS));
        searchBox.clear();
        searchBox.sendKeys(keyword);
        searchButton.click();
        return new SearchResultsPage(driver);
    }
}
