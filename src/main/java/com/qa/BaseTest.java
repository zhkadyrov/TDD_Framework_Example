package com.qa;

import com.google.common.collect.ImmutableMap;
import com.qa.utils.TestUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.screenrecording.CanRecordScreen;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Properties;

/**
 * BaseTest is the foundational class for all test classes.
 * It provides common setup, teardown, and utility methods for interacting with the AppiumDriver.
 */
public class BaseTest {

    // Common resources shared across tests
    protected static AppiumDriver driver;
    protected static WebDriverWait wait;
    protected static Properties properties;
    protected static HashMap<String, String> strings = new HashMap<>();
    protected static String platform;
    protected static String dateTime;
    protected TestUtils testUtils;

    // InputStreams for loading configuration files
    private InputStream inputStream;
    private InputStream stringsInputStream;

    /**
     * BeforeTest sets up the AppiumDriver and loads configuration and string resources.
     *
     * @param platformName    The name of the mobile platform (e.g., Android).
     * @param platformVersion The version of the platform.
     * @param deviceName      The name of the device.
     * @throws Exception If any error occurs during setup.
     */
    @BeforeTest
    @Parameters({"emulator", "platformName", "platformVersion", "deviceName"})
    public void beforeTest(String emulator, String platformName, String platformVersion, String deviceName) throws Exception {
        try {
            // Load properties file
            properties = loadProperties("config.properties");

            // Load string resources
            strings = loadStringResources("strings/strings.xml");

            // Initialize AppiumDriver with desired capabilities
            driver = initializeDriver(emulator, platformName, platformVersion, deviceName);

            testUtils = new TestUtils();
            dateTime = testUtils.getDateTime();

            // Configure implicit and explicit waits
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Re-throw the exception to fail the test setup
        }
    }

    /**
     * AfterTest closes the AppiumDriver.
     */
    @AfterTest
    public void afterTest() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeMethod
    public void beforeMethod() {
        ((CanRecordScreen) driver).startRecordingScreen();
    }

    @AfterMethod
    public void afterMethod() {
        ((CanRecordScreen) driver).stopRecordingScreen();
    }

    public void closeApp() {
        switch (platform.toLowerCase()) {
            case "android": ((InteractsWithApps) driver).terminateApp(properties.getProperty("androidAppPackage")); break;
            case "ios": ((InteractsWithApps) driver).terminateApp(properties.getProperty("iosBundleId")); break;
        }
    }

    public void launchApp() {
        switch (platform.toLowerCase()) {
            case "android": ((InteractsWithApps) driver).activateApp(properties.getProperty("androidAppPackage")); break;
            case "ios": ((InteractsWithApps) driver).activateApp(properties.getProperty("iosBundleId")); break;
        }
    }
    // Private Helper Methods

    /**
     * Loads properties from the specified file.
     *
     * @param fileName The name of the properties file.
     * @return The loaded Properties object.
     * @throws Exception If the file cannot be loaded.
     */
    private Properties loadProperties(String fileName) throws Exception {
        Properties props = new Properties();
        inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new Exception("Properties file not found: " + fileName);
        }
        props.load(inputStream);
        inputStream.close();
        return props;
    }

    /**
     * Loads string resources from the specified XML file.
     *
     * @param fileName The name of the XML file.
     * @return A HashMap containing the parsed strings.
     * @throws Exception If the file cannot be loaded or parsed.
     */
    private HashMap<String, String> loadStringResources(String fileName) throws Exception {
        stringsInputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (stringsInputStream == null) {
            throw new Exception("Strings file not found: " + fileName);
        }
        HashMap<String, String> parsedStrings = new TestUtils().parseStringXML(stringsInputStream);
        stringsInputStream.close();
        return parsedStrings;
    }

    /**
     * Initializes the AppiumDriver with the specified parameters.
     *
     * @param platformName    The name of the mobile platform.
     * @param platformVersion The version of the platform.
     * @param deviceName      The name of the device.
     * @return The initialized AppiumDriver.
     * @throws Exception If an error occurs during driver initialization.
     */
    private AppiumDriver initializeDriver(String emulator, String platformName, String platformVersion, String deviceName) throws Exception {
        URL appiumServerUrl = new URL(properties.getProperty("appiumURL"));
        URL appLocationPath = getClass().getClassLoader().getResource(
                platformName.equalsIgnoreCase("android")
                        ? properties.getProperty("androidAppLocation")
                        : properties.getProperty("iosAppLocation")
        );
        platform = platformName;

        switch (platformName.toLowerCase()) {
            case "android":
                UiAutomator2Options androidOptions = new UiAutomator2Options();
                setCommonCapabilities(androidOptions, platformName, platformVersion, deviceName);
                if (emulator.equals("true")) {
                    androidOptions.setCapability("appium:avd", deviceName); // Pixel_5_API_34
                    androidOptions.setCapability("appium:avdLaunchTimeout", 120_000);
                    androidOptions.setCapability("appium:newCommandTimeout", 120);
                }

//                androidOptions.setCapability("appium:app", appLocationPath);
                androidOptions.setCapability("appium:appPackage", properties.getProperty("androidAppPackage"));
                androidOptions.setCapability("appium:appActivity", properties.getProperty("androidAppActivity"));
                return new AndroidDriver(appiumServerUrl, androidOptions);

            case "ios":
                XCUITestOptions iosOptions = new XCUITestOptions();
                setCommonCapabilities(iosOptions, platformName, platformVersion, deviceName);
//                iosOptions.setCapability("appium:app", appLocationPath);
                iosOptions.setCapability("appium:bundleId", properties.getProperty("iosBundleId"));

                if (emulator.equals("true")) {
                    iosOptions.setCapability("appium:simulatorStartupTimeout", 120_000);
                    iosOptions.setCapability("appium:newCommandTimeout", 120);
                }

                return new IOSDriver(appiumServerUrl, iosOptions);

            default:
                throw new IllegalArgumentException("Unknown platform: " + platformName);
        }
    }

    /**
     * Устанавливает общие капабилити для драйверов.
     */
    private void setCommonCapabilities(MutableCapabilities options, String platformName, String platformVersion, String deviceName) {
        if (platformName.equalsIgnoreCase("android")) {
            options.setCapability("appium:automationName", properties.getProperty("androidAutomationName"));
        } else if (platformName.equalsIgnoreCase("ios")) {
            options.setCapability("appium:automationName", properties.getProperty("iosAutomationName"));
        }

        options.setCapability("appium:automationName", properties.getProperty(platformName.toLowerCase() + "AutomationName"));
        options.setCapability("appium:platformName", platformName);
        options.setCapability("appium:platformVersion", platformVersion);
        options.setCapability("appium:deviceName", deviceName);
    }

    public AppiumDriver getDriver() {
        return driver;
    }

    public String getDateTime() {
        return dateTime;
    }

    // Utility Methods

    /**
     * Waits for a WebElement to be visible.
     *
     * @param element The WebElement to wait for.
     */
    public void waitForVisibility(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Clicks on a WebElement after ensuring it is clickable.
     *
     * @param element The WebElement to click on.
     */
    public void click(WebElement element) {
        waitForVisibility(element);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    /**
     * Sends text input to a WebElement.
     *
     * @param element The WebElement to send text to.
     * @param text    The text to input.
     */
    public void sendKeys(WebElement element, String text) {
        waitForVisibility(element);
        element.sendKeys(text);
    }

    /**
     * Retrieves the specified attribute of a WebElement.
     *
     * @param element The WebElement to query.
     * @param key     The attribute name.
     * @return The attribute value.
     */
    public String getAttribute(WebElement element, String key) {
        waitForVisibility(element);
        return element.getAttribute(key);
    }

    /**
     * Scrolls to a WebElement.
     */
    public WebElement scrollToElement() {
        if (platform.equalsIgnoreCase("android")) {
            return driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView("
                            + "new UiSelector().description(\"test-Price\")"
                            + ");"));
        } else if (platform.equalsIgnoreCase("ios")) {
            RemoteWebElement activity = (RemoteWebElement) driver.findElement(
                    AppiumBy.accessibilityId("test-Price"));
            driver.executeScript("mobile: scroll", ImmutableMap.of(
                    "element", activity.getId(),
                    "toVisible", true
//                    "predicateString", "label == '$29.99'" // проверка видимости, экспериментально
            ));
            return driver.findElement(AppiumBy.accessibilityId("test-Price"));
        }
        return null;
    }


    public String getText(WebElement element) {
        return switch (platform.toLowerCase()) {
            case "android" -> getAttribute(element, "text");
            case "ios" -> getAttribute(element, "label");
            default -> null;
        };
    }

    public void clearField(WebElement element) {
        waitForVisibility(element);
        element.clear();
    }
}












































