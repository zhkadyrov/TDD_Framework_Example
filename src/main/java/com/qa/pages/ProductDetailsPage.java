package com.qa.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class ProductDetailsPage extends MenuPage {

    @AndroidFindBy(xpath = "//android.widget.TextView[@text=\"Sauce Labs Backpack\"]")
    @iOSXCUITFindBy(accessibility = "Sauce Labs Backpack")
    private WebElement slbTitle;

    @AndroidFindBy(xpath = "//*[contains(@text, \"carry.allTheThings() with the sleek\")]")
    @iOSXCUITFindBy(accessibility = "carry.allTheThings() with the sleek, streamlined Sly Pack that melds uncompromising style with unequaled laptop and tablet protection.")
    private WebElement slbText;

    @AndroidFindBy(accessibility = "test-Price")
    @iOSXCUITFindBy(accessibility = "test-Price")
    private WebElement slbPrice;

    @AndroidFindBy(accessibility = "test-ADD TO CART")
    @iOSXCUITFindBy(accessibility = "test-ADD TO CART")
    private WebElement addToCartButton;

    @AndroidFindBy(accessibility = "test-BACK TO PRODUCTS")
    @iOSXCUITFindBy(accessibility = "test-BACK TO PRODUCTS")
    private WebElement backToProductsButton;

    public String getSlbTitle() {
        waitForVisibility(slbTitle);
        String title = getText(slbText);
        System.out.println("title is: " + title);
        return getText(slbTitle);
    }

    public String getSlbText() {
        waitForVisibility(slbText);
        String text = getText(slbText);
        System.out.println("text is: " + text);
        return getText(slbText);
    }

    public String getSlbPrice() {
        waitForVisibility(slbPrice);
        String text = getText(slbPrice);
        System.out.println("text is: " + text);
        return getText(slbPrice);
    }

    public ProductDetailsPage scrollToSlbPrice() {
        scrollToElement();
        waitForVisibility(slbPrice);
        System.out.println("scrolling to SLB price");
        return this;
    }

    public boolean isAddToCartButtonIsDisplayed() {
        waitForVisibility(addToCartButton);
        System.out.println("add to card button is displayed");
        return addToCartButton.isDisplayed();
    }

    public ProductsPage pressBackButton() {
        waitForVisibility(backToProductsButton);
        System.out.println("press back to product button");
        click(backToProductsButton);
        return new ProductsPage();
    }
}


