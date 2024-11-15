package com.qa.pages;

import com.qa.BaseTest;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

//    @iOSXCUITFindBy(accessibility = "exampleLocator") private WebElement exampleElement; <===== iOS Пример
public class LoginPage extends BaseTest {
    @AndroidFindBy(accessibility = "test-Username")
    private WebElement userNameInputField;
    @AndroidFindBy(accessibility = "test-Password")
    private WebElement passwordInputField;
    @AndroidFindBy(accessibility = "test-LOGIN")
    private WebElement loginButton;
    @AndroidFindBy(xpath = "//*[contains(@text, \"Username and password do not match\")]")
    private WebElement errorMessage;

    public LoginPage(AppiumDriver driver) {
        BaseTest.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }


    public LoginPage enterUserName(String name) {
        sendKeys(userNameInputField, name);
        return this;
    }

    public LoginPage enterPassword(String password) {
        sendKeys(passwordInputField, password);
        return this;
    }

    public ProductsPage pressLoginButton() {
        click(loginButton);
        return new ProductsPage(driver);
    }

    public String getErrorMessage() {
        return errorMessage.getText();
    }
}














