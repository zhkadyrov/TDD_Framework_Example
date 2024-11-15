package com.qa.tests;

import com.qa.BaseTest;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductsPage;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.*;

import java.lang.reflect.Method;

public class LoginTests extends BaseTest {
    LoginPage loginPage;
    ProductsPage productsPage;

    @BeforeClass
    public void beforeClass() {
        loginPage = new LoginPage(driver);
        productsPage = new ProductsPage(driver);
    }

    @AfterClass
    public void afterClass() {

    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        System.out.println("\n" + "************** " + method.getName() + " **************");
    }

    @AfterMethod
    public void afterMethod() {

    }

    @Test
    public void testWithInvalidLoginAndInvalidPassword() {
        loginPage.enterUserName("invalid_username")
                .enterPassword("invalid_password")
                .pressLoginButton();
        String errorMessage =  loginPage.getErrorMessage();

        Assert.assertEquals(errorMessage, "Username and password do not match any user in this service.");

    }

    @Test
    public void testWithValidLoginAndInvalidPassword() {
        loginPage.enterUserName("standard_user")
                .enterPassword("invalid_password")
                .pressLoginButton();

        String errorMessage =  loginPage.getErrorMessage();

        Assert.assertEquals(errorMessage, "Username and password do not match any user in this service.");
    }

    @Test
    public void testWithInvaligLoginAndValidPassword() {
        loginPage.enterUserName("invalid_login")
                .enterPassword("secret_sauce")
                .pressLoginButton();

        String errorMessage =  loginPage.getErrorMessage();

        Assert.assertEquals(errorMessage, "Username and password do not match any user in this service.");
    }

    @Test
    public void testWithValidLoginAndValidPassword() {
        String title = loginPage.enterUserName("standard_user")
                .enterPassword("secret_sauce")
                .pressLoginButton()
                .getTitle();

        Assert.assertEquals(title, "PRODUCTS");
    }
}


















