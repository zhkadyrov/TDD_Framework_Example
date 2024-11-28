package com.qa.tests;

import com.qa.BaseTest;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductsPage;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public class LoginTests extends BaseTest {
    LoginPage loginPage; // Объект страницы логина.
    ProductsPage productsPage; // Объект страницы с продуктами.
    JSONObject loginUsers; // JSON-объект, содержащий данные для тестов логина.

    @BeforeClass
    public void beforeClass() throws IOException {
        loginPage = new LoginPage();
        productsPage = new ProductsPage();
        loginUsers = loadJsonData("data/loginUsers.json"); // Загрузка JSON-данных.
    }

    @AfterClass
    public void afterClass() {
        closeApp();
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        System.out.println("\n" + "************** " + method.getName() + " **************");
    }

    @AfterMethod
    public void afterMethod() {

    }

    private JSONObject loadJsonData(String fileName) throws IOException { // Метод для загрузки JSON-данных из указанного файла.
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IOException("Файл JSON не найден: " + fileName);
            }
            return new JSONObject(new JSONTokener(inputStream));
        }
    }

    private ProductsPage performLogin(String userKey) { // Универсальный метод для выполнения авторизации.
        String userName = loginUsers.getJSONObject(userKey).getString("userName");
        String password = loginUsers.getJSONObject(userKey).getString("password");

        return loginPage.enterUserName(userName)
                .enterPassword(password)
                .pressLoginButton();
    }

//======================================================================================================================

    @Test(priority = 1)
    public void invalidLoginTest() {
        performLogin("invalidUser");
        Assert.assertEquals(loginPage.getErrorMessage(), getStrings().get("err_invalid_username_or_password"));
    }

    @Test(priority = 2)
    public void invalidPasswordTest() {
        performLogin("invalidPassword");
        Assert.assertEquals(loginPage.getErrorMessage(), getStrings().get("err_invalid_username_or_password"));
    }

    @Test(priority = 3)
    public void validLoginAndPasswordTest() {
        String title = performLogin("validLoginAndPassword").getTitle(); // Вход с валидными данными.
        Assert.assertEquals(title, getStrings().get("product_title"));
    }
}


















