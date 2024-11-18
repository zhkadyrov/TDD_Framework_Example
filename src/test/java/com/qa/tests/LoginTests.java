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

@Listeners(TestListener.class) // Подключение listener'а для отслеживания событий тестов.
public class LoginTests extends BaseTest {
    LoginPage loginPage; // Объект страницы логина.
    ProductsPage productsPage; // Объект страницы с продуктами.
    InputStream inputStream; // Поток для чтения JSON-файла с данными.
    JSONObject loginUsers; // JSON-объект, содержащий данные для тестов логина.

    /**
     * Инициализация данных и страниц перед запуском класса тестов.
     */
    @BeforeClass
    public void beforeClass() throws IOException {
        loginPage = new LoginPage(driver); // Инициализация страницы логина.
        productsPage = new ProductsPage(driver); // Инициализация страницы с продуктами.
        String fileName = "data/loginUsers.json"; // Указание пути к JSON-файлу.

        try {
            // Чтение JSON-файла с данными пользователей.
            inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
            JSONTokener jsonTokener = null;
            if (inputStream != null) { jsonTokener = new JSONTokener(inputStream);}
            if (jsonTokener != null) { loginUsers = new JSONObject(jsonTokener);} // Преобразование в JSON-объект.
        } catch (Exception e) {
            e.printStackTrace(); // Логирование ошибок при чтении файла.
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close(); // Закрытие потока для предотвращения утечек ресурсов.
            }
        }
    }

    /**
     * Очистка ресурсов после выполнения тестового класса.
     */
    @AfterClass
    public void afterClass() {
        // Здесь можно добавить очистку данных или сброс состояния приложения, если потребуется.
    }

    /**
     * Логирование имени тестового метода перед его запуском.
     */
    @BeforeMethod
    public void beforeMethod(Method method) {
        System.out.println("\n" + "************** " + method.getName() + " **************");
    }

    /**
     * Действия после каждого теста (пока ничего не выполняется).
     */
    @AfterMethod
    public void afterMethod() {
        // Можно добавить, например, сброс состояния приложения или выход из аккаунта.
    }

    /**
     * Тест на проверку ошибки при вводе некорректного имени пользователя.
     */
    @Test
    public void invalidLoginTest() {
        loginPage.enterUserName(loginUsers.getJSONObject("invalidUser").getString("userName"))
                .enterPassword(loginUsers.getJSONObject("invalidUser").getString("password"))
                .pressLoginButton(); // Выполнение ввода и нажатие кнопки логина.

        String errorMessage = loginPage.getErrorMessage(); // Получение текста ошибки.

        // Проверка соответствия текста ошибки значению из XML-строк.
        Assert.assertEquals(errorMessage, strings.get("err_invalid_username_or_password"));
    }

    /**
     * Тест на проверку ошибки при вводе некорректного пароля.
     */
    @Test
    public void invalidPasswordTest() {
        loginPage.enterUserName(loginUsers.getJSONObject("invalidPassword").getString("userName"))
                .enterPassword(loginUsers.getJSONObject("invalidPassword").getString("password"))
                .pressLoginButton();

        String errorMessage = loginPage.getErrorMessage();

        Assert.assertEquals(errorMessage, strings.get("err_invalid_username_or_password"));
    }

    /**
     * Тест на проверку успешного входа с корректным логином и паролем.
     */
    @Test
    public void validLoginAndPasswordTest() {
        String title = loginPage.enterUserName(loginUsers.getJSONObject("validLoginAndPassword").getString("userName"))
                .enterPassword(loginUsers.getJSONObject("validLoginAndPassword").getString("password"))
                .pressLoginButton()
                .getTitle(); // Получение заголовка страницы после успешного входа.

        // Проверка соответствия заголовка значениям из XML-строк.
        Assert.assertEquals(title, strings.get("product_title"));
    }
}

















