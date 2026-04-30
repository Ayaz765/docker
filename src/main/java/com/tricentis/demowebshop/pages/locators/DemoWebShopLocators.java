package com.tricentis.demowebshop.pages.locators;

import org.openqa.selenium.By;

/**
 * Single source of truth for element selectors. When the demo site markup changes,
 * update locators here rather than hunting through page classes.
 */
public final class DemoWebShopLocators {

    private DemoWebShopLocators() {
    }

    /** Header, search, and account navigation. */
    public static final class Header {
        public static final By LOGOUT_LINK = By.cssSelector("a.ico-logout");
        public static final By LOGIN_LINK = By.xpath("//a[contains(@href,'/login')]");
        public static final String LOGIN_LINK_EXACT_XPATH = "//a[contains(@href,'/login') and normalize-space()='Log in']";
        public static final String SEARCH_INPUT_CSS = "input#small-searchterms";
        public static final String SEARCH_SUBMIT_CSS = "input.search-box-button[type='submit']";
    }

    /** Login / register form fields. */
    public static final class Auth {
        public static final String EMAIL_ID = "Email";
        public static final String PASSWORD_ID = "Password";
        public static final By EMAIL = By.id(EMAIL_ID);
        public static final By PASSWORD = By.id(PASSWORD_ID);
        public static final String LOGIN_BUTTON_CSS = "input.button-1.login-button";
    }

    /** Search results grid. */
    public static final class SearchResults {
        public static final String PRODUCT_ITEM_CSS = "div.product-item";
        public static final By PRODUCT_ITEMS = By.cssSelector(PRODUCT_ITEM_CSS);
        public static final String PRODUCT_TITLE_LINK_CSS = "div.product-item h2 a";
        public static final By PRODUCT_TITLE_LINKS = By.cssSelector(PRODUCT_TITLE_LINK_CSS);
    }

    /** Product details PDP. */
    public static final class Product {
        public static final By ADD_TO_CART = By.xpath("//input[contains(@id,'add-to-cart-button')]");
        public static final By BAR_NOTIFICATION = By.id("bar-notification");
        public static final By PRODUCT_TITLE = By.cssSelector("div.product-name h1, h1[itemprop='name']");
        public static final By CART_NAV = By.cssSelector("#topcartlink a[href*='cart'], a.ico-cart, a[href='/cart']");
        public static final String ATTR_SELECT_CSS = "select[id^='product_attribute'], select[name^='product_attribute']";
        public static final String ATTR_RADIO_CSS = "input[type='radio'][name^='product_attribute_']";
    }

    /** Shopping cart table and checkout entry. */
    public static final class Cart {
        public static final By TABLE = By.cssSelector("table.cart");
        public static final String ROWS_CSS = "table.cart tbody tr";
        public static final String TERMS_ID = "termsofservice";
        public static final String CHECKOUT_ID = "checkout";
        public static final By TERMS = By.id(TERMS_ID);
        public static final By CHECKOUT = By.id(CHECKOUT_ID);
        public static final By LINE_PRODUCT_NAME = By.cssSelector("td.product a.product-name");
    }
}
