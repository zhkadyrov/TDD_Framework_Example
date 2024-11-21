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
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.screenrecording.CanRecordScreen;
import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.io.File.separator;

/**
 * BaseTest is the foundational class for all test classes.
 * It provides common setup, teardown, and utility methods for interacting with the AppiumDriver.
 */
public class BaseTest {
    // Common resources shared across tests
    protected static ThreadLocal <AppiumDriver> driver = new ThreadLocal<AppiumDriver>();
    protected static ThreadLocal <WebDriverWait> wait = new ThreadLocal<WebDriverWait>();
    protected static ThreadLocal <Properties> properties = new ThreadLocal<Properties>();
    protected static ThreadLocal <HashMap<String, String>> strings = new ThreadLocal<HashMap<String, String>>();
    protected static ThreadLocal <String> platform = new ThreadLocal<String>();
    protected static ThreadLocal <String> dateTime = new ThreadLocal<String>();
    TestUtils testUtils;

    public BaseTest() {
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }
    public AppiumDriver getDriver() {
        return driver.get();
    }
    public void setDriver(AppiumDriver driver1) {
        driver.set(driver1);
    }
    public WebDriverWait getWait() {
        return wait.get();
    }
    public void setWait(WebDriverWait wait1) {
        wait.set(wait1);
    }
    public Properties getProperty() {
        return properties.get();
    }
    public void setProperties(Properties properties1) {
        properties.set(properties1);
    }
    public HashMap<String, String> getStrings() {
        return strings.get();
    }
    public void setStrings(HashMap<String, String> strings1) {
        strings.set(strings1);
    }
    public String getPlatform() {
        return platform.get();
    }
    public void setPlatform(String platform1) {
        platform.set(platform1);
    }
    public String getDateTime() {
        return dateTime.get();
    }
    public void setDateTime(String dateTime1) {
        dateTime.set(dateTime1);
    }

    @BeforeTest
    @Parameters({"emulator", "platformName", "platformVersion", "deviceName"})
    public void beforeTest(String emulator, String platformName, String platformVersion, String deviceName) throws Exception {
        testUtils = new TestUtils();
        setDateTime(testUtils.dateTime());
        setPlatform(platformName);
        InputStream inputStream = null;
        InputStream stringsInputStream = null;
        Properties props;

        try {
            props = new Properties();
            inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
            props.load(inputStream);
            setProperties(props);

            stringsInputStream = getClass().getClassLoader().getResourceAsStream("strings/strings.xml");

            HashMap<String, String> parsedStrings = testUtils.parseStringXML(stringsInputStream);
            setStrings(parsedStrings);

            setDriver(initializeDriver(emulator, platformName, platformVersion, deviceName));

            getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            setWait(new WebDriverWait(getDriver(), Duration.ofSeconds(TestUtils.WAIT)));

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (inputStream != null) { inputStream.close(); }
            if (stringsInputStream != null) { stringsInputStream.close(); }
        }
    }

    @AfterTest
    public void afterTest() {
        if (driver != null) {
            getDriver().quit();
        }
    }

    @BeforeMethod
    public void beforeMethod() {
        ((CanRecordScreen) driver).startRecordingScreen();
    }

    @AfterMethod
    public synchronized void afterMethod(ITestResult result) {
        String video = ((CanRecordScreen) driver).stopRecordingScreen();
        if (result.getStatus() == 2) {
            // Получаем параметры текущего теста из XML-конфигурации
            Map<String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();

            // Формируем путь для сохранения видео, включая параметры платформы, устройства и теста
            String videoDir = "Reports" + separator
                    + "Videos" + separator
                    + "Platform: " + params.get("platformName") + separator
                    + "OS version: " + params.get("platformVersion") + separator
                    + "Device: " + params.get("deviceName") + separator
                    + "Date: " + getDateTime() + separator
                    + "Class: " + result.getTestClass().getRealClass().getSimpleName() + separator
                    + "Method: " + result.getName();

            // Создаём директорию для хранения видео, если она ещё не существует
            File videoFolder = new File(videoDir);

            synchronized (videoFolder) {
                if (!videoFolder.exists()) { videoFolder.mkdirs(); }  // Создаёт все недостающие каталоги
            }

            // Формируем полный путь для файла видео, включая имя метода и расширение
            String videoFilePath = videoDir + separator + result.getName() + ".mp4";

            // Сохраняем видеозапись в файл
            try (FileOutputStream stream = new FileOutputStream(videoFilePath)) {
                // Декодируем строку Base64 в массив байтов и записываем их в файл
                stream.write(Base64.decodeBase64(video));
                // Уведомляем в консоли, что видео успешно сохранено
                System.out.println("Video saved at: " + videoFilePath);
            } catch (Exception e) {
                // Обрабатываем возможные ошибки при записи файла
                e.printStackTrace();
            }
        }
    }

    public void closeApp() {
        switch (getPlatform().toLowerCase()) {
            case "android": ((InteractsWithApps) getDriver()).terminateApp(getProperty().getProperty("androidAppPackage")); break;
            case "ios": ((InteractsWithApps) getDriver()).terminateApp(getProperty().getProperty("iosBundleId")); break;
        }
    }
    public void launchApp() {
        switch (getPlatform().toLowerCase()) {
            case "android": ((InteractsWithApps) getDriver()).activateApp(getProperty().getProperty("androidAppPackage")); break;
            case "ios": ((InteractsWithApps) getDriver()).activateApp(getProperty().getProperty("iosBundleId")); break;
        }
    }

    // Private Helper Methods
    private AppiumDriver initializeDriver(String emulator, String platformName, String platformVersion, String deviceName) throws Exception {
        URL appiumServerUrl = new URL(getProperty().getProperty("appiumURL"));
        URL appLocationPath = getClass().getClassLoader().getResource(
                platformName.equalsIgnoreCase("android")
                        ? getProperty().getProperty("androidAppLocation")
                        : getProperty().getProperty("iosAppLocation")
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
                androidOptions.setCapability("appium:appPackage", getProperty().getProperty("androidAppPackage"));
                androidOptions.setCapability("appium:appActivity", getProperty().getProperty("androidAppActivity"));
                return new AndroidDriver(appiumServerUrl, androidOptions);

            case "ios":
                XCUITestOptions iosOptions = new XCUITestOptions();
                setCommonCapabilities(iosOptions, platformName, platformVersion, deviceName);
//                iosOptions.setCapability("appium:app", appLocationPath);
                iosOptions.setCapability("appium:bundleId", getProperty().getProperty("iosBundleId"));

                if (emulator.equals("true")) {
                    iosOptions.setCapability("appium:simulatorStartupTimeout", 120_000);
                    iosOptions.setCapability("appium:newCommandTimeout", 120);
                }

                return new IOSDriver(appiumServerUrl, iosOptions);

            default:
                throw new IllegalArgumentException("Unknown platform: " + platformName);
        }
    }
    private void setCommonCapabilities(MutableCapabilities options, String platformName, String platformVersion, String deviceName) {
        if (platformName.equalsIgnoreCase("android")) {
            options.setCapability("appium:automationName", getProperty().getProperty("androidAutomationName"));
        } else if (platformName.equalsIgnoreCase("ios")) {
            options.setCapability("appium:automationName", getProperty().getProperty("iosAutomationName"));
        }

        options.setCapability("appium:automationName", getProperty().getProperty(platformName.toLowerCase() + "AutomationName"));
        options.setCapability("appium:platformName", platformName);
        options.setCapability("appium:platformVersion", platformVersion);
        options.setCapability("appium:deviceName", deviceName);
    }

    // Utility Methods
    public void waitForVisibility(WebElement element) {
        getWait().until(ExpectedConditions.visibilityOf(element));
    }
    public void click(WebElement element) {
        waitForVisibility(element);
        getWait().until(ExpectedConditions.elementToBeClickable(element));
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
    public WebElement scrollToElement() {
        if (getPlatform().equalsIgnoreCase("android")) {
            return getDriver().findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView("
                            + "new UiSelector().description(\"test-Price\")"
                            + ");"));
        } else if (getPlatform().equalsIgnoreCase("ios")) {
            RemoteWebElement activity = (RemoteWebElement) getDriver().findElement(
                    AppiumBy.accessibilityId("test-Price"));
            getDriver().executeScript("mobile: scroll", ImmutableMap.of(
                    "element", activity.getId(),
                    "toVisible", true
//                    "predicateString", "label == '$29.99'" // проверка видимости, экспериментально
            ));
            return getDriver().findElement(AppiumBy.accessibilityId("test-Price"));
        }
        return null;
    }
    public String getText(WebElement element) {
        return switch (getPlatform().toLowerCase()) {
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












































