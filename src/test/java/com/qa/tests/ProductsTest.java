package com.qa.tests;

import com.qa.BaseTest;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductDetailsPage;
import com.qa.pages.ProductsPage;
import com.qa.pages.SettingsPage;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * Тестовый класс для проверки функционала страницы продуктов.
 * Включает проверку отображения информации о продукте на странице продуктов и странице деталей продукта.
 */
public class ProductsTest extends BaseTest {
    private LoginPage loginPage; // Страница входа в систему.
    private ProductsPage productsPage; // Страница продуктов.
    private SettingsPage settingsPage; // Страница настроек.
    private ProductDetailsPage productDetailsPage; // Страница деталей продукта.
    private JSONObject loginUsers; // JSON-данные для авторизации.

    /**
     * Инициализация страниц и загрузка данных перед запуском тестового класса.
     */
    @BeforeClass
    public void beforeClass() throws IOException {
        loginPage = new LoginPage();
        productsPage = new ProductsPage();
        settingsPage = new SettingsPage();
        productDetailsPage = new ProductDetailsPage();

        loginUsers = loadJsonData("data/loginUsers.json"); // Загрузка данных для входа.
        launchApp(); // Запуск приложения.
    }

    /**
     * Завершение тестового класса: закрытие приложения.
     */
    @AfterClass
    public void afterClass() {
        closeApp();
    }

    /**
     * Логирование текущего теста и выполнение входа в систему перед каждым тестом.
     */
    @BeforeMethod
    public void beforeMethod(Method method) {
        System.out.println("\n************** " + method.getName() + " **************");
        loginWithValidCredentials(); // Вход в систему перед каждым тестом.
    }

    /**
     * Выполнение выхода из системы после каждого теста.
     */
    @AfterMethod
    public void afterMethod() {
        if (productsPage != null) {
            settingsPage = productsPage.pressSettings(); // Переход на страницу настроек.
            loginPage = settingsPage.pressLogoutButton(); // Выход из системы.
        } else {
            System.err.println("No productsPage instance found. Skipping logout.");
        }
    }

    /**
     * Загрузка JSON-данных из файла.
     *
     * @param fileName имя файла JSON.
     * @return объект JSON с данными.
     * @throws IOException если файл не найден или произошла ошибка чтения.
     */
    private JSONObject loadJsonData(String fileName) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new FileNotFoundException("JSON file not found: " + fileName);
            }
            return new JSONObject(new JSONTokener(inputStream));
        }
    }

    /**
     * Выполняет вход в систему с корректными учетными данными.
     */
    private void loginWithValidCredentials() {
        String userName = loginUsers.getJSONObject("validLoginAndPassword").getString("userName");
        String password = loginUsers.getJSONObject("validLoginAndPassword").getString("password");
        productsPage = loginPage.login(userName, password); // Авторизация.
    }

    /**
     * Проверяет отображение информации о продукте на странице продуктов.
     */
    @Test(priority = 1)
    public void validateProductOnProductPage() {
        SoftAssert softAssert = new SoftAssert();

        // Проверка заголовка продукта (SLB Title).
        softAssert.assertEquals(productsPage.getSlbTitle(),
                strings.get("product_page_slb_title"),
                "SLB Title mismatch");

        // Проверка цены продукта (SLB Price).
        softAssert.assertEquals(productsPage.getSlbPrice(),
                strings.get("product_page_slb_price"),
                "SLB Price mismatch");

        softAssert.assertAll(); // Собираем все ошибки.
    }

    /**
     * Проверяет отображение информации о продукте на странице деталей продукта.
     */
    @Test(priority = 2)
    public void validateProductOnProductDetailsPage() {
        SoftAssert softAssert = new SoftAssert();

        // Переход на страницу деталей продукта.
        productDetailsPage = productsPage.pressSlbTitle();

        // Проверка заголовка продукта (SLB Title).
        softAssert.assertEquals(productDetailsPage.getSlbTitle(),
                strings.get("product_details_slb_title"),
                "SLB Title mismatch");

        // Проверка описания продукта (SLB Text).
        softAssert.assertEquals(productDetailsPage.getSlbText(),
                strings.get("product_details_slb_text"),
                "SLB Text mismatch");

        productDetailsPage.scrollToSlbPrice();

        // Проверка цены продукта (SLB Price)
        softAssert.assertEquals(productDetailsPage.getSlbPrice(),
                strings.get("product_details_slb_price"),
                "SLB Price mismatch");

        // Проверка видна ли кнопка Add to cart
        softAssert.assertTrue(productDetailsPage.isAddToCartButtonIsDisplayed(),
                "Add to cart button mismatch");

        // Переход обратно на страницу продуктов.
        productsPage = productDetailsPage.pressBackButton();
        Assert.assertNotNull(productsPage, "Failed to navigate back to Products Page");

        softAssert.assertAll(); // Собираем все ошибки.
    }
}


























