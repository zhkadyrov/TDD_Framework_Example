package com.qa;

import com.qa.utils.TestUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Properties;

public class BaseTest {
    protected static AppiumDriver driver;
    protected static WebDriverWait wait;
    protected static Properties properties;
    protected InputStream inputStream;
    protected static HashMap<String, String> strings = new HashMap<>();
    protected InputStream stringsInputStream;
    protected TestUtils testUtils;

    @BeforeTest
    @Parameters({"platformName", "platformVersion", "deviceName"})
    public void beforeTest(String platformName, String platformVersion, String deviceName) throws Exception {
        try {
            properties = new Properties();
            String propertiesFileName = "config.properties";
            inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName);

            properties.load(inputStream);

            String xmlFileName = "strings/strings.xml";
            stringsInputStream = getClass().getClassLoader().getResourceAsStream(xmlFileName);
            testUtils = new TestUtils();
            strings = testUtils.parseStringXML(stringsInputStream);

            URL url = new URL(properties.getProperty("appiumURL"));
            URL appPath = getClass().getClassLoader().getResource(properties.getProperty("androidAppLocation"));

            UiAutomator2Options androidOptions = new UiAutomator2Options();
            androidOptions.setCapability("appium:automationName", properties.getProperty("androidAutomationName"));
            androidOptions.setCapability("appium:platformName", platformName);
            androidOptions.setCapability("appium:platformVersion", platformVersion);
            androidOptions.setCapability("appium:deviceName", deviceName);
            androidOptions.setCapability("appium:app", appPath);
            androidOptions.setCapability("appium:appPackage", properties.getProperty("androidAppPackage"));
            androidOptions.setCapability("appium:appActivity", properties.getProperty("androidAppActivity"));

            driver = new AndroidDriver(url,androidOptions);
            String sessionId = driver.getSessionId().toString();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (stringsInputStream != null) {
                stringsInputStream.close();
            }
        }
    }

    @AfterTest
    public void afterTest() {
        driver.quit();
    }

    public void waitForVisibility(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void click(WebElement element) {
        waitForVisibility(element);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    public void sendKeys(WebElement element, String text) {
        waitForVisibility(element);
        element.sendKeys(text);
    }

    public String getAttribute(WebElement element, String key) {
        waitForVisibility(element);
        return element.getAttribute(key);
    }

    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView;", element);
    }
}


























