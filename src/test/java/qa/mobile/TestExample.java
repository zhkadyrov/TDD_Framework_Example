package qa.mobile;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static java.io.File.separator;

public class TestExample {
    AppiumDriver driver;

    @BeforeClass
    public void beforeClass() throws MalformedURLException {
        URL url = new URL("http://0.0.0.0:4723/");

        String androidApp = System.getProperty("user.dir") + separator + "src" + separator + "test" + separator
                + "resources" + separator + "app/SauceLabsMobileSampleApp.apk";

        UiAutomator2Options androidOptions = new UiAutomator2Options();
        androidOptions.setCapability("appium:automationName", "UiAutomator2");
        androidOptions.setCapability("appium:platformName", "Android");
        androidOptions.setCapability("appium:deviceName", "Pixel_5");
        androidOptions.setCapability("appium:platformVersion", "14");
        androidOptions.setCapability("appium:udid", "emulator-5554"); // Сюда можно указать id реального устройства, команда adb devices
        androidOptions.setCapability("unlockType", "pattern"); // Тип блокировки
        androidOptions.setCapability("unlockKey", "1235789"); // Графический ключ в виде цифр
//        androidOptions.setCapability("appium:app", androidApp);
        androidOptions.setCapability("appium:appPackage", "com.swaglabsmobileapp");
        androidOptions.setCapability("appium:appActivity", "com.swaglabsmobileapp.SplashActivity");

        driver = new AndroidDriver(url,androidOptions);
        String sessionId = driver.getSessionId().toString();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterClass
    public void afterClass() {
        driver.quit();
    }

    @Test
    public void testWithInvalidLoginAndInvalidPassword() {
        WebElement loginInputField = driver.findElement(AppiumBy.accessibilityId("test-Username"));
        WebElement passwordInputField = driver.findElement(AppiumBy.accessibilityId("test-Password"));
        WebElement loginButton = driver.findElement(AppiumBy.accessibilityId("test-LOGIN"));

        loginInputField.sendKeys("invalid_login");
        passwordInputField.sendKeys("invalid_password");
        loginButton.click();

        WebElement errorMessage = driver.findElement(AppiumBy.xpath(
                "//*[contains(@text, \"Username and password do not match\")]"));

        Assert.assertEquals(errorMessage.getText(), "Username and password do not match any user in this service.");
    }

    @Test
    public void testWithValidLoginAndInvalidPassword() {
        WebElement loginInputField = driver.findElement(AppiumBy.accessibilityId("test-Username"));
        WebElement passwordInputField = driver.findElement(AppiumBy.accessibilityId("test-Password"));
        WebElement loginButton = driver.findElement(AppiumBy.accessibilityId("test-LOGIN"));

        loginInputField.sendKeys("standard_user");
        passwordInputField.sendKeys("invalid_password");
        loginButton.click();

        WebElement errorMessage = driver.findElement(AppiumBy.xpath(
                "//*[contains(@text, \"Username and password do not match\")]"));

        Assert.assertEquals(errorMessage.getText(), "Username and password do not match any user in this service.");
    }

    @Test
    public void testWithInvaligLoginAndValidPassword() {
        WebElement loginInputField = driver.findElement(AppiumBy.accessibilityId("test-Username"));
        WebElement passwordInputField = driver.findElement(AppiumBy.accessibilityId("test-Password"));
        WebElement loginButton = driver.findElement(AppiumBy.accessibilityId("test-LOGIN"));

        loginInputField.sendKeys("invalid_login");
        passwordInputField.sendKeys("secret_sauce");
        loginButton.click();

        WebElement errorMessage = driver.findElement(AppiumBy.xpath(
                "//*[contains(@text, \"Username and password do not match\")]"));

        Assert.assertEquals(errorMessage.getText(), "Username and password do not match any user in this service.");
    }

    @Test
    public void testWithValidLoginAndValidPassword() {
        WebElement loginInputField = driver.findElement(AppiumBy.accessibilityId("test-Username"));
        WebElement passwordInputField = driver.findElement(AppiumBy.accessibilityId("test-Password"));
        WebElement loginButton = driver.findElement(AppiumBy.accessibilityId("test-LOGIN"));

        loginInputField.sendKeys("standard_user");
        passwordInputField.sendKeys("secret_sauce");
        loginButton.click();

        WebElement errorMessage = driver.findElement(AppiumBy.xpath(
                "//android.widget.TextView[@text=\"PRODUCTS\"]"));

        Assert.assertEquals(errorMessage.getText(), "PRODUCTS");
    }
}

// standard_user
// secret_sauce


















