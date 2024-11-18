package com.qa.pages;

import com.qa.BaseTest;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class ProductsPage extends BaseTest {
    @AndroidFindBy(xpath = "//android.widget.TextView[@text=\"PRODUCTS\"]")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@name=\"PRODUCTS\"]")
    private WebElement productsPageTitle;

    public ProductsPage(AppiumDriver driver) {
        BaseTest.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    public String getTitle() {
        return getText(productsPageTitle);
    }
}


// //XCUIElementTypeOther[@name=\"test-Toggle\"]
