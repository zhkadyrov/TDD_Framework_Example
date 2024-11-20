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

    /**
     * Инициализация данных и страниц перед запуском класса тестов.
     */
    @BeforeClass
    public void beforeClass() throws IOException {
        loginPage = new LoginPage();
        productsPage = new ProductsPage();
        loginUsers = loadJsonData("data/loginUsers.json"); // Загрузка JSON-данных.
    }

    /**
     * Очистка ресурсов после выполнения тестового класса.
     */
    @AfterClass
    public void afterClass() {
        closeApp(); // Закрытие приложения после завершения тестов.
    }

    /**
     * Логирование имени тестового метода перед его запуском.
     */
    @BeforeMethod
    public void beforeMethod(Method method) {
        System.out.println("\n" + "************** " + method.getName() + " **************");
    }

    /**
     * Тест на проверку ошибки при вводе некорректного имени пользователя.
     */
    @Test(priority = 1)
    public void invalidLoginTest() {
        performLogin("invalidUser");
        assertErrorMessage(strings.get("err_invalid_username_or_password"));
    }

    /**
     * Тест на проверку ошибки при вводе некорректного пароля.
     */
    @Test(priority = 2)
    public void invalidPasswordTest() {
        performLogin("invalidPassword");
        assertErrorMessage(strings.get("err_invalid_username_or_password"));
    }

    /**
     * Тест на проверку успешного входа с корректным логином и паролем.
     */
    @Test(priority = 3)
    public void validLoginAndPasswordTest() {
        String title = performLogin("validLoginAndPassword").getTitle(); // Вход с валидными данными.
        Assert.assertEquals(title, strings.get("product_title"), "Заголовок страницы не соответствует ожидаемому!");
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
}


















