package com.qa.pages;

import com.qa.BaseTest;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.awt.*;

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
        String pageTitle = getText(productsPageTitle);
        testUtils.log("product page title is: " + pageTitle);
        return getText(productsPageTitle);
    }

    public String getSlbTitle() {
        waitForVisibility(slbTitle);
        String title = getText(slbTitle);
        testUtils.log("title is: " + title);
        return getText(slbTitle);
    }

    public String getSlbPrice() {
        waitForVisibility(slbPrice);
        String price = getText(slbPrice);
        testUtils.log("price is: " + price);
        return getText(slbPrice);
    }

    public ProductDetailsPage pressSlbTitle() {
        waitForVisibility(slbTitle);
        testUtils.log("press SLB title");
        click(slbTitle);
        return new ProductDetailsPage();
    }
}


