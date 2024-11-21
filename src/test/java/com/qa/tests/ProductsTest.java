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
    private LoginPage loginPage;
    private ProductsPage productsPage;
    private SettingsPage settingsPage;
    private ProductDetailsPage productDetailsPage;
    private JSONObject loginUsers;

    @BeforeClass
    public void beforeClass() throws IOException {
        loginPage = new LoginPage();
        productsPage = new ProductsPage();
        settingsPage = new SettingsPage();
        productDetailsPage = new ProductDetailsPage();
        loginUsers = loadJsonData("data/loginUsers.json"); // Загрузка данных для входа.
        launchApp();
    }

    @AfterClass
    public void afterClass() {
        closeApp();
    }
    @BeforeMethod
    public void beforeMethod(Method method) {
        System.out.println("\n************** " + method.getName() + " **************");
        loginWithValidCredentials();
    }
    @AfterMethod
    public void afterMethod() {
    }

    private JSONObject loadJsonData(String fileName) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new FileNotFoundException("JSON file not found: " + fileName);
            }
            return new JSONObject(new JSONTokener(inputStream));
        }
    }
    private void loginWithValidCredentials() {
        String userName = loginUsers.getJSONObject("validLoginAndPassword").getString("userName");
        String password = loginUsers.getJSONObject("validLoginAndPassword").getString("password");
        productsPage = loginPage.login(userName, password); // Авторизация.
    }
    private void logoutApp() {
        productsPage.pressSettings();
        settingsPage.pressLogoutButton();
    }

//======================================================================================================================

    @Test(priority = 1)
    public void validateProductOnProductPage() { // Проверяет отображение информации о продукте на странице продуктов.
        SoftAssert softAssert = new SoftAssert();

        // Проверка заголовка продукта (SLB Title).
        softAssert.assertEquals(productsPage.getSlbTitle(),
                getStrings().get("product_page_slb_title"),
                "SLB Title mismatch");

        // Проверка цены продукта (SLB Price).
        softAssert.assertEquals(productsPage.getSlbPrice(),
                getStrings().get("product_page_slb_price"),
                "SLB Price mismatch");

        softAssert.assertAll(); // Собираем все ошибки.

        logoutApp();
    }

    @Test(priority = 2) // Проверяет отображение информации о продукте на странице деталей продукта.
    public void validateProductOnProductDetailsPage() {
        SoftAssert softAssert = new SoftAssert();

        // Переход на страницу деталей продукта.
        productDetailsPage = productsPage.pressSlbTitle();

        // Проверка заголовка продукта (SLB Title).
        softAssert.assertEquals(productDetailsPage.getSlbTitle(),
                getStrings().get("product_details_slb_title"),
                "SLB Title mismatch");

        // Проверка описания продукта (SLB Text).
        softAssert.assertEquals(productDetailsPage.getSlbText(),
                getStrings().get("product_details_slb_text"),
                "SLB Text mismatch");

        productDetailsPage.scrollToSlbPrice();

        // Проверка цены продукта (SLB Price)
        softAssert.assertEquals(productDetailsPage.getSlbPrice(),
                getStrings().get("product_details_slb_price"),
                "SLB Price mismatch");

        // Проверка видна ли кнопка Add to cart
        softAssert.assertTrue(productDetailsPage.isAddToCartButtonIsDisplayed(),
                "Add to cart button mismatch");

        // Переход обратно на страницу продуктов.
        productsPage = productDetailsPage.pressBackButton();
        Assert.assertNotNull(productsPage, "Failed to navigate back to Products Page");

        softAssert.assertAll(); // Собираем все ошибки.

        logoutApp();
    }
}


























