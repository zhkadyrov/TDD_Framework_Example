package com.qa.utils;

import com.qa.BaseTest;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class TempClass {

//    // Thread-local переменные для параллельного выполнения
//    private static final ThreadLocal<Integer> appiumPort = ThreadLocal.withInitial(() -> 4723);
//    private static final AtomicInteger portCounter = new AtomicInteger(4723);
//    private static final AtomicInteger wdaPortCounter = new AtomicInteger(8100);
//    private static final AtomicInteger webKitProxyPortCounter = new AtomicInteger(27753);
//
//    // Common resources shared across tests
//    protected static ThreadLocal <AppiumDriver> driver = new ThreadLocal<AppiumDriver>();
//    protected static ThreadLocal <WebDriverWait> wait = new ThreadLocal<WebDriverWait>();
//    protected static ThreadLocal <Properties> properties = new ThreadLocal<Properties>();
//    protected static ThreadLocal <HashMap<String, String>> strings = new ThreadLocal<HashMap<String, String>>();
//    protected static ThreadLocal <String> platform = new ThreadLocal<String>();
//    protected static ThreadLocal <String> device = new ThreadLocal<String>();
//    protected static ThreadLocal <String> dateTime = new ThreadLocal<String>();
//    public TestUtils testUtils = new TestUtils();
//    private static AppiumDriverLocalService server;
//    private static final Logger log = LogManager.getLogger(BaseTest.class.getName());
//
////======================================================================================================================
//
//    // Setters and Getters
//    public AppiumDriver getDriver() {
//        return driver.get();
//    }
//    public void setDriver(AppiumDriver driver1) {
//        driver.set(driver1);
//    }
//    public WebDriverWait getWait() {
//        return wait.get();
//    }
//    public void setWait(WebDriverWait wait1) {
//        wait.set(wait1);
//    }
//    public Properties getProperty() {
//        return properties.get();
//    }
//    public void setProperties(Properties properties1) {
//        properties.set(properties1);
//    }
//    public HashMap<String, String> getStrings() {
//        return strings.get();
//    }
//    public void setStrings(HashMap<String, String> strings1) {
//        strings.set(strings1);
//    }
//    public String getPlatform() {
//        return platform.get();
//    }
//    public void setPlatform(String platform1) {
//        platform.set(platform1);
//    }
//    public String getDeviceName() {
//        return device.get();
//    }
//    public void setDeviceName(String device1) {
//        device.set(device1);
//    }
//    public String getDateTime() {
//        return dateTime.get();
//    }
//    public void setDateTime(String dateTime1) {
//        dateTime.set(dateTime1);
//    }
//
//    //======================================================================================================================
//    @BeforeSuite
//    public void beforeSuite() {
//        int port = appiumPort.get();
//        releasePort(port); // Освобождаем порт перед запуском сервера
//
//        ThreadContext.put("ROUTINGKEY", "ServerLogs");
//        AppiumDriverLocalService server = getAppiumService();
//        server.start();
//        server.clearOutPutStreams();
//        testUtils.log().info("Appium server started on port: " + port);
//    }
//
//    @AfterSuite
//    public void afterSuite() {
//        AppiumDriverLocalService server = getAppiumService();
//        if (server.isRunning()) {
//            server.stop();
//            testUtils.log().info("Appium server stopped on port: " + appiumPort.get());
//        }
//        releasePort(appiumPort.get()); // Освобождаем порт после остановки
//    }
//
//// =====================================================================================================================
//
//    @BeforeTest
//    @Parameters({"realDevice", "parallel" , "platformName", "platformVersion", "deviceName"})
////    @Optional("onlyAndroid") String systemPort, @Optional("onlyAndroid") String chromeDriverPort
////    @Optional("iOSOnly") String wdaLocalPort, @Optional("iOSOnly") String webKitDebugProxyPort
//    public void beforeTest(String realDevice, String parallel, String platformName, String platformVersion, String deviceName) throws Exception {
//        setDateTime(testUtils.dateTime());
//        setPlatform(platformName);
//        setDeviceName(deviceName);
//
//        Properties props = new Properties();
//        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties")) {
//            props.load(inputStream);
//            setProperties(props);
//        }
//
//        try (InputStream stringsInputStream = getClass().getClassLoader().getResourceAsStream("strings/strings.xml")) {
//            HashMap<String, String> parsedStrings = testUtils.parseStringXML(stringsInputStream);
//            setStrings(parsedStrings);
//        }
//
//        setDriver(initializeDriver(realDevice, parallel, platformName, platformVersion, deviceName));
//        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
//        setWait(new WebDriverWait(getDriver(), Duration.ofSeconds(TestUtils.WAIT)));
//    }
//
//    @AfterTest
//    public void afterTest() {
//        if (driver != null) {
//            getDriver().quit();
//        }
//
//        // Остановка симуляторов и эмуляторов
//        if (getPlatform().equalsIgnoreCase("ios")){
//            stopIOSSimulator();
//        } else if (getPlatform().equalsIgnoreCase("android")) {
//            stopAndroidEmulator();
//        }
//    }
//
//    public boolean checkIfAppiumServerIsRunnning() throws Exception {
//        int port = appiumPort.get();
//        try (ServerSocket socket = new ServerSocket(port)) {
//            return false; // Порт свободен
//        } catch (IOException e) {
//            return true; // Порт занят
//        }
//
//    }
//
//    public AppiumDriverLocalService getAppiumServerDefault() {
//        return AppiumDriverLocalService.buildDefaultService();
//    }
//
//    public AppiumDriverLocalService getAppiumService() {
//        // Генерация уникального порта для текущего потока
//        int currentPort = portCounter.getAndIncrement();
//        appiumPort.set(currentPort);
//
//        return AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
//                .usingDriverExecutable(new File("/opt/homebrew/opt/node@18/bin/node"))
//                .withAppiumJS(new File("/opt/homebrew/lib/node_modules/appium/build/lib/main.js"))
//                .usingPort(currentPort) // Использование уникального порта
//                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
//                .withLogFile(new File("ServerLogs/server-" + currentPort + ".log")) // Логи для каждого потока
//        );
//    }
//
//    private void releasePort(int port) {
//        try {
//            // Выполняем команду для поиска процесса, связанного с портом
//            String command = String.format("lsof -i tcp:%d | grep LISTEN | awk '{print $2}'", port);
//            Process process = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", command});
//
//            // Считываем PID процесса
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                String pid = reader.readLine();
//                if (pid != null && !pid.isEmpty()) {
//                    // Убиваем процесс
//                    String killCommand = "kill -9 " + pid;
//                    Runtime.getRuntime().exec(killCommand);
//                    log.info("Port " + port + " successfully released.");
//                } else {
//                    log.info("No process found on port " + port + ". Port is already free.");
//                }
//            }
//        } catch (Exception e) {
//            log.error("Failed to release port " + port, e);
//        }
//    }
//
//// =====================================================================================================================
//
//    // Driver initialization
//    private synchronized AppiumDriver initializeDriver(String realDevice, String parallel, String platformName,
//                                                       String platformVersion, String deviceName) throws Exception {
//        int currentAppiumPort = appiumPort.get();
//        int currentWdaPort = wdaPortCounter.getAndIncrement();
//        int currentWebKitPort = webKitProxyPortCounter.getAndIncrement();
//
//        URL appiumServerUrl = getAppiumServerUrl(currentAppiumPort);
//        URL appLocationPath = getAppLocationPath(platformName);
//
//        switch (platformName.toLowerCase()) {
//            case "android":
//                UiAutomator2Options androidOptions = createAndroidOptions(
//                        realDevice, platformName, platformVersion, deviceName, appLocationPath, currentAppiumPort);
//
//                return new AndroidDriver(appiumServerUrl, androidOptions);
//
//            case "ios":
//                XCUITestOptions iosOptions = createIOSOptions(
//                        realDevice, platformName, platformVersion, deviceName, appLocationPath, currentWdaPort, currentWebKitPort);
//
//                return new IOSDriver(appiumServerUrl, iosOptions);
//
//            default:
//                throw new IllegalArgumentException("Unknown platform: " + platformName);
//        }
//    }
//
//    //======================================================================================================================
//
//    private URL getAppiumServerUrl(int port) throws MalformedURLException {
//        return new URL("http://0.0.0.0:" + port + "/");
//    }
//
//    private URL getAppLocationPath(String platformName) {
//        String appLocationKey = platformName.equalsIgnoreCase("android")
//                ? "androidAppLocation"
//                : "iosAppLocation";
//        URL appLocationPath = getClass().getClassLoader().getResource(getProperty().getProperty(appLocationKey));
//
//        if (appLocationPath == null) {
//            throw new IllegalArgumentException("App location not found for platform: " + platformName);
//        }
//
//        return appLocationPath;
//    }
//
//    private UiAutomator2Options createAndroidOptions(String realDevice, String platformName, String platformVersion,
//                                                     String deviceName, URL appLocationPath, int appiumPort) {
//        UiAutomator2Options options = new UiAutomator2Options();
//        setCommonCapabilities(options, platformName, platformVersion, deviceName);
//
//        if (realDevice.equalsIgnoreCase("true")) {
//            options.setCapability("appium:systemPort", appiumPort + 100);
//            options.setCapability("appium:chromeDriverPort", appiumPort + 200);
//        }
//
////        options.setCapability("appium:app", appLocationPath.getPath());
//        options.setCapability("appium:avd", deviceName);
//        options.setCapability("appium:avdLaunchTimeout", 120_000);
//        options.setCapability("appium:newCommandTimeout", 120);
//        options.setCapability("appium:appPackage", getProperty().getProperty("androidAppPackage"));
//        options.setCapability("appium:appActivity", getProperty().getProperty("androidAppActivity"));
//
//        return options;
//    }
//
//    private XCUITestOptions createIOSOptions(String realDevice, String platformName, String platformVersion,
//                                             String deviceName, URL appLocationPath, int wdaPort, int webKitPort) {
//        XCUITestOptions options = new XCUITestOptions();
//        setCommonCapabilities(options, platformName, platformVersion, deviceName);
//
//        if (realDevice.equalsIgnoreCase("true")) {
//            options.setCapability("wdaLocalPort", wdaPort);
//            options.setCapability("webKitDebugProxyPort", webKitPort);
//        }
//
////        options.setCapability("appium:app", appLocationPath.getPath());
//        options.setCapability("appium:bundleId", getProperty().getProperty("iosBundleId"));
//        options.setCapability("appium:simulatorStartupTimeout", 120_000);
//        options.setCapability("appium:newCommandTimeout", 120);
//
//        return options;
//    }
//    private void setCommonCapabilities(MutableCapabilities options, String platformName, String platformVersion, String deviceName) {
//        if (platformName.equalsIgnoreCase("android")) {
//            options.setCapability("appium:automationName", getProperty().getProperty("androidAutomationName"));
//        } else if (platformName.equalsIgnoreCase("ios")) {
//            options.setCapability("appium:automationName", getProperty().getProperty("iosAutomationName"));
//        }
//
//        options.setCapability("appium:automationName", getProperty().getProperty(platformName.toLowerCase() + "AutomationName"));
//        options.setCapability("appium:platformName", platformName);
//        options.setCapability("appium:platformVersion", platformVersion);
//        options.setCapability("appium:deviceName", deviceName);
//    }
//
////======================================================================================================================
//
//    // Device management
//    public static void stopIOSSimulator() {
//        try {
//            String command = "xcrun simctl shutdown all";
//            Process process = new ProcessBuilder("/bin/bash", "-c", command).start();
//            int exitCode = process.waitFor();
//            if (exitCode == 0) {
//                log.info("All iOS Simulators have been stopped.");
//            } else {
//                log.error("Failed to stop iOS Simulators. Exit code: " + exitCode);
//            }
//        } catch (Exception e) {
//            log.error("Error while stopping iOS Simulators: ", e);
//        }
//    }
//    public static void stopAndroidEmulator() {
//        try {
//            String command = "adb emu kill";
//            Process process = new ProcessBuilder("/bin/bash", "-c", command).start();
//            int exitCode = process.waitFor();
//            if (exitCode == 0) {
//                log.info("All Android Emulators have been stopped.");
//            } else {
//                log.error("Failed to stop Android Emulators. Exit code: " + exitCode);
//            }
//        } catch (Exception e) {
//            log.error("Error while stopping Android Emulators: ", e);
//        }
//    }
//    public void closeApp() {
//        switch (getPlatform().toLowerCase()) {
//            case "android": ((InteractsWithApps) getDriver()).terminateApp(getProperty().getProperty("androidAppPackage")); break;
//            case "ios": ((InteractsWithApps) getDriver()).terminateApp(getProperty().getProperty("iosBundleId")); break;
//        }
//    }
//    public void launchApp() {
//        switch (getPlatform().toLowerCase()) {
//            case "android": ((InteractsWithApps) getDriver()).activateApp(getProperty().getProperty("androidAppPackage")); break;
//            case "ios": ((InteractsWithApps) getDriver()).activateApp(getProperty().getProperty("iosBundleId")); break;
//        }
//    }

}
