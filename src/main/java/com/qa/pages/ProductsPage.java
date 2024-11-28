package com.qa.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class ProductsPage extends MenuPage {
    @AndroidFindBy(xpath = "//android.widget.TextView[@text=\"PRODUCTS\"]")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@name=\"PRODUCTS\"]")
    private WebElement productsPageTitle;

    @AndroidFindBy(xpath = "//android.widget.TextView[@content-desc=\"test-Item title\" and @text=\"Sauce Labs Backpack\"]")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@name=\"test-Item title\" and @label=\"Sauce Labs Backpack\"]")
    private WebElement slbTitle;

    @AndroidFindBy(xpath = "//android.widget.TextView[@content-desc=\"test-Price\" and @text=\"$29.99\"]")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@name=\"test-Price\" and @label=\"$29.99\"]")
    private WebElement slbPrice;

    public ProductsPage() {
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }

    public String getTitle() {
        waitForVisibility(productsPageTitle);
        return getText(productsPageTitle, "product page title is: ");
    }

    public String getSlbTitle() {
        waitForVisibility(slbTitle);
        return getText(slbTitle, "title is: ");
    }

    public String getSlbPrice() {
        waitForVisibility(slbPrice);
        return getText(slbPrice, "price is: ");
    }

    public ProductDetailsPage pressSlbTitle() {
        waitForVisibility(slbTitle);
        click(slbTitle, "press SLB title");
        return new ProductDetailsPage();
    }
}


