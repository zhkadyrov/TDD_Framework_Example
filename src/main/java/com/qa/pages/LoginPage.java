package com.qa.pages;

import com.qa.BaseTest;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

//    @iOSXCUITFindBy(accessibility = "exampleLocator") private WebElement exampleElement; <===== iOS Пример
public class LoginPage extends BaseTest {
    @AndroidFindBy(accessibility = "test-Username")
    @iOSXCUITFindBy(id = "test-Username")
    private WebElement userNameInputField;
    @AndroidFindBy(accessibility = "test-Password")
    @iOSXCUITFindBy(id = "test-Password")
    private WebElement passwordInputField;
    @AndroidFindBy(accessibility = "test-LOGIN")
    @iOSXCUITFindBy(id = "test-LOGIN")
    private WebElement loginButton;
    @AndroidFindBy(xpath = "//*[contains(@text, \"Username and password do not match\")]")
    @iOSXCUITFindBy(accessibility = "test-Error message")
    private WebElement errorMessage;

    public LoginPage() {
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }


    public LoginPage enterUserName(String name) {
        clearField(userNameInputField);
        sendKeys(userNameInputField, name);
        System.out.println("login is: " + name);
        return this;
    }

    public LoginPage enterPassword(String password) {
        clearField(passwordInputField);
        sendKeys(passwordInputField, password);
        System.out.println("password is: " + password);
        return this;
    }

    public ProductsPage pressLoginButton() {
        click(loginButton);
        System.out.println("press login button");
        return new ProductsPage();
    }

    public String getErrorMessage() {
        String text = getText(errorMessage);
        System.out.println("error text is: " + text);
        return text;
    }

    public ProductsPage login(String username, String password) {
        enterUserName(username);
        enterPassword(password);
        pressLoginButton();
        return new ProductsPage();
    }
}














