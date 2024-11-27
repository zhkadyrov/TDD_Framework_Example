package com.qa;

import com.qa.BaseTest;
import com.qa.pages.SettingsPage;
import com.qa.utils.TestUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class MenuPage extends BaseTest {
    TestUtils testUtils = new TestUtils();
    @AndroidFindBy(accessibility = "test-Menu")
    @iOSXCUITFindBy(accessibility = "test-Menu")
    private WebElement pressSettings;

    public MenuPage() {
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }

    public SettingsPage pressSettings() {
        waitForVisibility(pressSettings);
        click(pressSettings, "press Settings button");
        return new SettingsPage();
    }
}









