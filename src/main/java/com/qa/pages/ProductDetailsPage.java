package com.qa.pages;

import com.qa.MenuPage;
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
        return getText(slbText, "title is: ");
    }

    public String getSlbText() {
        waitForVisibility(slbText);
        return getText(slbText, "text is: ");
    }

    public String getSlbPrice() {
        waitForVisibility(slbPrice);
        return getText(slbPrice, "price is: ");
    }

    public ProductDetailsPage scrollToSlbPrice() {
        scrollToElement("scrolling to SLB price");
        waitForVisibility(slbPrice);
        return this;
    }

    public boolean isAddToCartButtonIsDisplayed() {
        waitForVisibility(addToCartButton);
        return addToCartButton.isDisplayed();
    }

    public ProductsPage pressBackButton() {
        waitForVisibility(backToProductsButton);
        click(backToProductsButton, "press back to product button");
        return new ProductsPage();
    }
}


