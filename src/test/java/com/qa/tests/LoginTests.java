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

public class LoginTests extends BaseTest {

    private LoginPage loginPage; // Объект страницы логина.
    private ProductsPage productsPage; // Объект страницы с продуктами.
    private JSONObject loginUsers; // JSON-объект, содержащий данные для тестов логина.

    @BeforeClass
    public void beforeClass() throws IOException {
        loginPage = new LoginPage();
        productsPage = new ProductsPage();
        loginUsers = loadJsonData("data/loginUsers.json"); // Загрузка JSON-данных.
    }

    @AfterClass
    public void afterClass() {
        closeApp(); // Закрытие приложения после завершения тестов.
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        System.out.println("\n" + "************** " + method.getName() + " **************");
    }

    @AfterMethod
    public void afterMethod() {

    }

    /**
     * Метод для загрузки JSON-данных из указанного файла.
     */
    private JSONObject loadJsonData(String fileName) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IOException("Файл JSON не найден: " + fileName);
            }
            return new JSONObject(new JSONTokener(inputStream));
        }
    }

    /**
     * Универсальный метод для выполнения логина.
     *
     * @param userKey Ключ пользователя из JSON-данных (e.g., "invalidUser", "validLoginAndPassword").
     * @return Продуктовая страница или текущая страница, если логин не удался.
     */
    private ProductsPage performLogin(String userKey) {
        String userName = loginUsers.getJSONObject(userKey).getString("userName");
        String password = loginUsers.getJSONObject(userKey).getString("password");

        return loginPage.enterUserName(userName)
                .enterPassword(password)
                .pressLoginButton();
    }

    /**
     * Проверяет сообщение об ошибке на странице логина.
     *
     * @param expectedErrorMessage Ожидаемое сообщение об ошибке.
     */
    private void assertErrorMessage(String expectedErrorMessage) {
        String actualErrorMessage = loginPage.getErrorMessage();
        Assert.assertEquals(actualErrorMessage, expectedErrorMessage, "Сообщение об ошибке не соответствует ожидаемому!");
    }

//======================================================================================================================

    @Test(priority = 1)
    public void invalidLoginTest() {
        performLogin("invalidUser");
        assertErrorMessage(getStrings().get("err_invalid_username_or_password"));
    }

    @Test(priority = 2)
    public void invalidPasswordTest() {
        performLogin("invalidPassword");
        assertErrorMessage(getStrings().get("err_invalid_username_or_password"));
    }

    @Test(priority = 3)
    public void validLoginAndPasswordTest() {
        String title = performLogin("validLoginAndPassword").getTitle(); // Вход с валидными данными.
        Assert.assertEquals(title, getStrings().get("product_title"), "Заголовок страницы не соответствует ожидаемому!");
    }
}


















