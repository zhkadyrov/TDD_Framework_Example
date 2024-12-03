package com.qa;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.google.common.collect.ImmutableMap;
import com.qa.utils.ExtentReport;
import com.qa.utils.TestUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static java.io.File.separator;
public class BaseTest { // Да в общем-то никаких изменений, это ради проверки вебхука
    // Thread-local переменные для параллельного выполнения
    private static final ThreadLocal<Integer> appiumPort = ThreadLocal.withInitial(() -> 4723);
    private static final AtomicInteger portCounter = new AtomicInteger(4723);
    private static final AtomicInteger wdaPortCounter = new AtomicInteger(8100);
    private static final AtomicInteger webKitProxyPortCounter = new AtomicInteger(27753);

    // Common resources shared across tests
    protected static ThreadLocal <AppiumDriver> driver = new ThreadLocal<AppiumDriver>();
    protected static ThreadLocal <WebDriverWait> wait = new ThreadLocal<WebDriverWait>();
    protected static ThreadLocal <Properties> properties = new ThreadLocal<Properties>();
    protected static ThreadLocal <HashMap<String, String>> strings = new ThreadLocal<HashMap<String, String>>();
    protected static ThreadLocal <String> platform = new ThreadLocal<String>();
    protected static ThreadLocal <String> device = new ThreadLocal<String>();
    protected static ThreadLocal <String> dateTime = new ThreadLocal<String>();
    public TestUtils testUtils = new TestUtils();
    private static AppiumDriverLocalService server;
    private static final Logger log = LogManager.getLogger(BaseTest.class.getName());

//======================================================================================================================

    // Setters and Getters
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
    public String getDeviceName() {
        return device.get();
    }
    public void setDeviceName(String device1) {
        device.set(device1);
    }
    public String getDateTime() {
        return dateTime.get();
    }
    public void setDateTime(String dateTime1) {
        dateTime.set(dateTime1);
    }

//======================================================================================================================

    @BeforeSuite
    @Parameters({"parallel"})
    public void beforeSuite(@Optional("false") String parallel, ITestContext context) {
        int port = parallel.equalsIgnoreCase("true") ? portCounter.getAndIncrement() : 4723; // Выбор порта
        appiumPort.set(port);

        ensurePortIsFree(port); // Проверяем и освобождаем порт перед запуском

        ThreadContext.put("ROUTINGKEY", "ServerLogs");
        server = getAppiumService(port); // Запуск Appium-сервера на выбранном порту
        server.start();
        server.clearOutPutStreams();
        testUtils.log().info("Appium server started on port: " + port);

        ExtentTest test = ExtentReport.startTest(context.getSuite().getName(), "");
    }


    @AfterSuite
    public void afterSuite() {
        if (server != null && server.isRunning()) {
            server.stop();
            testUtils.log().info("Appium server stopped on port: " + appiumPort.get());
        }
        ensurePortIsFree(appiumPort.get()); // Освобождаем порт после остановки
    }

// =====================================================================================================================

    @BeforeTest
    @Parameters({"realDevice", "parallel" , "platformName", "platformVersion", "deviceName"})
//    @Optional("onlyAndroid") String systemPort, @Optional("onlyAndroid") String chromeDriverPort
//    @Optional("iOSOnly") String wdaLocalPort, @Optional("iOSOnly") String webKitDebugProxyPort
    public void beforeTest(String realDevice, String parallel, String platformName, String platformVersion, String deviceName) throws Exception {
        int port = parallel.equalsIgnoreCase("true") ? appiumPort.get() : 4723; // Использование порта в зависимости от режима
        setDateTime(testUtils.dateTime());
        setPlatform(platformName);
        setDeviceName(deviceName);

        loadProperties("config.properties");
        loadStrings("strings/strings.xml");

        setDriver(initializeDriver(realDevice, platformName, platformVersion, deviceName, port));
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        setWait(new WebDriverWait(getDriver(), Duration.ofSeconds(TestUtils.WAIT)));
    }

    @AfterTest
    public void afterTest() {
        if (driver != null) {
            getDriver().quit();
        }

        // Остановка симуляторов и эмуляторов
        if (getPlatform().equalsIgnoreCase("ios")){
            stopIOSSimulator();
        } else if (getPlatform().equalsIgnoreCase("android")) {
            stopAndroidEmulator();
        }
    }

    public boolean checkIfAppiumServerIsRunnning() throws Exception {
        int port = appiumPort.get();
        try (ServerSocket socket = new ServerSocket(port)) {
            return false; // Порт свободен
        } catch (IOException e) {
            return true; // Порт занят
        }

    }

    public AppiumDriverLocalService getAppiumServerDefault() { // Для Windows работает по умолчанию
        return AppiumDriverLocalService.buildDefaultService();
    }

    private AppiumDriverLocalService getAppiumService(int port) { // Для Mac лучше использовать это
        HashMap<String, String> environment = new HashMap<>();
        environment.put("PATH", "/Users/janbolot/Library/Android/sdk/platform-tools:" +
                "/Users/janbolot/Library/Android/sdk/emulator:" +
                "/Users/janbolot/Library/Android/sdk/cmdline-tools/latest/bin:" +
                "/opt/homebrew/opt/node@18/bin:" +
                "/Users/janbolot/.nvm/versions/node/v22.9.0/bin:" +
                "/opt/homebrew/bin:" + // Добавляем путь к ffmpeg для MacOs
                "/usr/bin:/bin:/usr/sbin:/sbin");
        environment.put("ANDROID_HOME", "/Users/janbolot/Library/Android/sdk");

        ensurePortIsFree(port); // Проверяем и освобождаем порт перед запуском сервера
        return AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
                .usingDriverExecutable(new File("/opt/homebrew/opt/node@18/bin/node"))
                .withAppiumJS(new File("/opt/homebrew/lib/node_modules/appium/build/lib/main.js"))
                .usingPort(port) // Уникальный или дефолтный порт
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withEnvironment(environment)
                .withLogFile(new File("ServerLogs/server-" + port + ".log")));
    }

    private void releasePort(int port) {
        try {
            String command = String.format("lsof -i tcp:%d | grep LISTEN | awk '{print $2}'", port);
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", command});

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String pid = reader.readLine();
                if (pid != null && !pid.isEmpty()) {
                    String killCommand = "kill -9 " + pid;
                    Runtime.getRuntime().exec(killCommand);
                    log.info("Port " + port + " successfully released.");
                } else {
                    log.info("No process found on port " + port + ". Port is already free.");
                }
            }
        } catch (Exception e) {
            log.error("Failed to release port " + port, e);
        }
    }

// =====================================================================================================================

    // Driver initialization
    private synchronized AppiumDriver initializeDriver(String realDevice, String platformName, String platformVersion,
                                                       String deviceName, int appiumPort) throws Exception {
        int currentWdaPort = wdaPortCounter.getAndIncrement();
        int currentWebKitPort = webKitProxyPortCounter.getAndIncrement();

        URL appiumServerUrl = getAppiumServerUrl(appiumPort);
        URL appLocationPath = getAppLocationPath(platformName);

        switch (platformName.toLowerCase()) {
            case "android":
                UiAutomator2Options androidOptions = createAndroidOptions(
                        realDevice, platformName, platformVersion, deviceName, appLocationPath, appiumPort);
                return new AndroidDriver(appiumServerUrl, androidOptions);

            case "ios":
                XCUITestOptions iosOptions = createIOSOptions(
                        realDevice, platformName, platformVersion, deviceName, appLocationPath, currentWdaPort, currentWebKitPort);
                return new IOSDriver(appiumServerUrl, iosOptions);

            default:
                throw new IllegalArgumentException("Unknown platform: " + platformName);
        }
    }

    //======================================================================================================================

    private void ensurePortIsFree(int port) {
        if (isPortInUse(port)) {
            releasePort(port);
        }
    }

    private boolean isPortInUse(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            return false; // Порт свободен
        } catch (IOException e) {
            return true; // Порт занят
        }
    }

    private URL getAppiumServerUrl(int port) throws MalformedURLException {
        return new URL("http://0.0.0.0:" + port + "/");
    }

    private URL getAppLocationPath(String platformName) {
        String appLocationKey = platformName.equalsIgnoreCase("android")
                ? "androidAppLocation"
                : "iosAppLocation";
        URL appLocationPath = getClass().getClassLoader().getResource(getProperty().getProperty(appLocationKey));

        if (appLocationPath == null) {
            throw new IllegalArgumentException("App location not found for platform: " + platformName);
        }

        return appLocationPath;
    }

    private UiAutomator2Options createAndroidOptions(String realDevice, String platformName, String platformVersion,
                                                     String deviceName, URL appLocationPath, int appiumPort) {
        UiAutomator2Options options = new UiAutomator2Options();
        setCommonCapabilities(options, platformName, platformVersion, deviceName);

        if (realDevice.equalsIgnoreCase("true")) {
            options.setCapability("appium:systemPort", appiumPort + 100);
            options.setCapability("appium:chromeDriverPort", appiumPort + 200);
        }

//        options.setCapability("appium:app", appLocationPath.getPath());
        options.setCapability("appium:avd", deviceName);
        options.setCapability("appium:avdLaunchTimeout", 120_000);
        options.setCapability("appium:newCommandTimeout", 120);

        options.setCapability("appium:appPackage", getProperty().getProperty("androidAppPackage"));
        options.setCapability("appium:appActivity", getProperty().getProperty("androidAppActivity"));

        return options;
    }

    private XCUITestOptions createIOSOptions(String realDevice, String platformName, String platformVersion,
                                             String deviceName, URL appLocationPath, int wdaPort, int webKitPort) {
        XCUITestOptions options = new XCUITestOptions();
        setCommonCapabilities(options, platformName, platformVersion, deviceName);

        if (realDevice.equalsIgnoreCase("true")) {
            options.setCapability("wdaLocalPort", wdaPort);
            options.setCapability("webKitDebugProxyPort", webKitPort);
        }

//        options.setCapability("appium:app", appLocationPath.getPath());
        options.setCapability("appium:bundleId", getProperty().getProperty("iosBundleId"));
        options.setCapability("appium:simulatorStartupTimeout", 120_000);
        options.setCapability("appium:newCommandTimeout", 120);

        return options;
    }
    private void setCommonCapabilities(MutableCapabilities options, String platformName, String platformVersion, String deviceName) {
        if (platformName.equalsIgnoreCase("android")) {
            options.setCapability("appium:automationName", getProperty().getProperty("androidAutomationName"));
        } else if (platformName.equalsIgnoreCase("ios")) {
            options.setCapability("appium:automationName", getProperty().getProperty("iosAutomationName"));
        }

        options.setCapability("appWaitDuration", 60000); // увеличьте до 60 секунд
        options.setCapability("adbExecTimeout", 60000); // увеличьте таймаут выполнения ADB
        options.setCapability("appium:automationName", getProperty().getProperty(platformName.toLowerCase() + "AutomationName"));
        options.setCapability("appium:platformName", platformName);
        options.setCapability("appium:platformVersion", platformVersion);
        options.setCapability("appium:deviceName", deviceName);
    }

    private void loadProperties(String fileName) throws IOException {
        Properties props = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Property file '" + fileName + "' not found in the classpath");
            }
            props.load(inputStream);
            setProperties(props); // Устанавливаем свойства в thread-local переменную
            log.info("Loaded properties from: " + fileName);
        }
    }

    private void loadStrings(String filePath) throws IOException {
        try (InputStream stringsInputStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (stringsInputStream == null) {
                throw new FileNotFoundException("Strings file '" + filePath + "' not found in the classpath");
            }
            HashMap<String, String> parsedStrings = null;
            try {
                parsedStrings = testUtils.parseStringXML(stringsInputStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            setStrings(parsedStrings); // Устанавливаем строки в thread-local переменную
            log.info("Loaded strings from: " + filePath);
        }
    }

//======================================================================================================================

    // Device management
    public static void stopIOSSimulator() {
        try{
            String command = "xcrun simctl shutdown all";
            Process process = new ProcessBuilder("/bin/bash", "-c", command).start();
            log.info("All iOS Simulators have been stopped.");
        } catch (Exception e) {
            log.error("Error while stopping iOS Simulators: ", e);
        }
    }
    public static void stopAndroidEmulator() {
        try{
            String command = "adb emu kill";
            Process process = new ProcessBuilder("/bin/bash", "-c", command).start();
            log.info("All Android Emulators have been stopped.");
        } catch (Exception e) {
            log.error("Error while stopping Android Emulators: ", e);
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

// =====================================================================================================================

    @BeforeMethod
    public void beforeMethod() {
        ((CanRecordScreen) getDriver()).startRecordingScreen();
    }

    @AfterMethod
    public synchronized void afterMethod(ITestResult result) {
        String video = ((CanRecordScreen) getDriver()).stopRecordingScreen();
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

//======================================================================================================================

    // Utility Methods
    public void waitForVisibility(WebElement element) {
        getWait().until(ExpectedConditions.visibilityOf(element));
    }

    public void click(WebElement element, String message) {
        waitForVisibility(element);
        getWait().until(ExpectedConditions.elementToBeClickable(element));
        testUtils.log().info(message);
        ExtentReport.getTest().log(Status.INFO, message);
        element.click();
    }

    public void sendKeys(WebElement element, String text, String message) {
        waitForVisibility(element);
        testUtils.log().info(message);
        ExtentReport.getTest().log(Status.INFO, message);
        element.sendKeys(text);
    }

    public String getAttribute(WebElement element, String key) {
        waitForVisibility(element);
        return element.getAttribute(key);
    }

    public WebElement scrollToElement(String message) {
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
        testUtils.log().info(message);
        ExtentReport.getTest().log(Status.INFO, message);
        return null;
    }

    public String getText(WebElement element, String message) {
        String text = null;

        switch (getPlatform().toLowerCase()) {
            case "android": text = getAttribute(element, "text"); break;
            case "ios": text = getAttribute(element, "label"); break;
        }
        ExtentReport.getTest().log(Status.INFO, message);
        testUtils.log().info(message + text);
        return text;
    }

    public void clearField(WebElement element, String message) {
        waitForVisibility(element);
        testUtils.log().info(message);
        ExtentReport.getTest().log(Status.INFO, message);
        element.clear();
    }
}












































