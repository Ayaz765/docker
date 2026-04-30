package com.tricentis.demowebshop.pages;

import com.tricentis.demowebshop.pages.locators.DemoWebShopLocators.SearchResults;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class SearchResultsPage extends BasePage {

    @FindBy(css = SearchResults.PRODUCT_ITEM_CSS)
    private List<WebElement> productItems;

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public int resultCount() {
        wait.untilVisible(SearchResults.PRODUCT_ITEMS);
        return productItems.size();
    }

    /**
     * Opens the first product that is not a "Build your own" configurator (those need extra options before Add to cart).
     */
    public ProductDetailsPage openFirstReadyToShipProduct() {
        wait.untilVisible(SearchResults.PRODUCT_TITLE_LINKS);
        var links = driver.findElements(SearchResults.PRODUCT_TITLE_LINKS);
        for (var link : links) {
            String title = link.getText().toLowerCase();
            if (!title.contains("build your own")) {
                wait.untilClickable(link).click();
                return new ProductDetailsPage(driver);
            }
        }
        wait.untilClickable(SearchResults.PRODUCT_TITLE_LINKS).click();
        return new ProductDetailsPage(driver);
    }
}
