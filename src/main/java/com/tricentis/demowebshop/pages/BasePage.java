package com.tricentis.demowebshop.pages;

import com.tricentis.demowebshop.utils.ConfigReader;
import com.tricentis.demowebshop.utils.WaitHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WaitHelper wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitHelper(driver);
        PageFactory.initElements(driver, this);
    }

    protected static String baseUrl() {
        return ConfigReader.get("base.url");
    }
}
