package com.qa.tests;

import com.qa.BaseTest;
import com.qa.listeners.TestListener;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductsPage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

@Listeners(TestListener.class)
public class LoginTests extends BaseTest {
    LoginPage loginPage;
    ProductsPage productsPage;
    InputStream inputStream;
    JSONObject loginUsers;

    @BeforeClass
    public void beforeClass() throws IOException {
        loginPage = new LoginPage(driver);
        productsPage = new ProductsPage(driver);
        String fileName = "data/loginUsers.json";

        try {
            inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
            JSONTokener jsonTokener = new JSONTokener(inputStream);
            loginUsers = new JSONObject(jsonTokener);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) { inputStream.close();}
        }
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
    public void invalidLoginTest() {
        loginPage.enterUserName(loginUsers.getJSONObject("invalidUser").getString("userName"))
                .enterPassword(loginUsers.getJSONObject("invalidUser").getString("password"))
                .pressLoginButton();

        String errorMessage =  loginPage.getErrorMessage();

        Assert.assertEquals(errorMessage, "Username and password do not match any user in this service.");
    }

    @Test
    public void invalidPasswordTest() {
        loginPage.enterUserName(loginUsers.getJSONObject("invalidPassword").getString("userName"))
                .enterPassword(loginUsers.getJSONObject("invalidPassword").getString("password"))
                .pressLoginButton();

        String errorMessage =  loginPage.getErrorMessage();

        Assert.assertEquals(errorMessage, "Username and password do not match any user in this service.");
    }

    @Test
    public void validLoginAndPasswordTest() {
        String title = loginPage.enterUserName(loginUsers.getJSONObject("validLoginAndPassword").getString("userName"))
                .enterPassword(loginUsers.getJSONObject("validLoginAndPassword").getString("password"))
                .pressLoginButton()
                .getTitle();

        Assert.assertEquals(title, "PRODUCTS");
    }
}


















