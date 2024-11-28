package com.qa.ui;

// Класс для работы с глубокими ссылками в мобильных приложениях
public class DeepLink {

//    // Метод для открытия приложения с использованием глубокой ссылки
//    public static void OpenAppWith(String url) {
//        // Получаем текущий драйвер Appium
//        AppiumDriver driver = DriverManager.getDriver();
//
//        // Проверяем, что драйвер не null, и получаем его capabilities
//        Capabilities capabilities = Objects.requireNonNull(driver.getCapabilities());
//
//        // Проверяем, если приложение запущено на Android
//        if ("Android".equals(capabilities.getPlatformName().toString())) {
//
//            // Создаем карту параметров для глубоких ссылок
//            Map<String, String> deepUrl = new HashMap<>();
//            deepUrl.put("url", url); // Добавляем URL для глубокого перехода
//            deepUrl.put("package", "com.swaglabsmobileapp"); // Указываем пакет приложения
//
//            // Выполняем глубокую ссылку с использованием команды Appium
//            driver.executeScript("mobile: deepLink", deepUrl);
//        }
//        // Если приложение запущено на iOS
//        else if ("iOS".equals(capabilities.getPlatformName().toString())) {
//
//            // Локаторы элементов Safari
//            By urlBtn = AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' && name CONTAINS 'URL'");
//            By urlFld = AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeField' && name CONTAINS 'URL'");
//            By openBtn = AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' && name CONTAINS 'Open'");
//
//            // Активируем приложение Safari через Appium
//            ((InteractsWithApps) driver).activateApp("com.apple.mobilesafari");
//
//            // Ожидаем появления элементов с помощью WebDriverWait
//            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//
//            // Ожидаем кнопку URL и кликаем по ней
//            wait.until(ExpectedConditions.visibilityOfElementLocated(urlBtn)).click();
//
//            // Вводим URL в поле для ввода и нажимаем Enter
//            wait.until(ExpectedConditions.visibilityOfElementLocated(urlFld)).sendKeys(url + "\uE007");
//
//            // Ожидаем кнопку "Open" и кликаем по ней
//            wait.until(ExpectedConditions.visibilityOfElementLocated(openBtn)).click();
//        }
//    }
}





























