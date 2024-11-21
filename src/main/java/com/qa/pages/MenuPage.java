package com.qa.pages;

import com.qa.BaseTest;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class MenuPage extends BaseTest {

    @AndroidFindBy(accessibility = "test-Menu")
    @iOSXCUITFindBy(accessibility = "test-Menu")
    private WebElement pressSettings;

    public MenuPage() {
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }

    public SettingsPage pressSettings() {
        waitForVisibility(pressSettings);
        System.out.println("press Settings button");
        click(pressSettings);
        return new SettingsPage();
    }
}









