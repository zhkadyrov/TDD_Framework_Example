package com.qa.pages;

import com.qa.BaseTest;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class SettingsPage extends BaseTest {

    @AndroidFindBy(accessibility = "test-LOGOUT")
    @iOSXCUITFindBy(accessibility = "test-LOGOUT")
    private WebElement logoutButton;

    public SettingsPage() {
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    public LoginPage pressLogoutButton() {
        waitForVisibility(logoutButton);
        click(logoutButton);
        return new LoginPage();
    }
}

