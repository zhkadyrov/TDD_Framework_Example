package com.qa.listeners;

import com.qa.BaseTest;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static java.io.File.separator;

public class TestListener implements ITestListener {

    /**
     * Метод, который срабатывает при падении теста.
     * @param result объект `ITestResult`, содержащий информацию о тесте и его состоянии.
     */
    public void onTestFailure(ITestResult result) {
        // Проверяем, что тест завершился с исключением (т.е. причина падения существует).
        if (result.getThrowable() != null) {
            // Создаем StringWriter для записи стека исключения в строку.
            StringWriter sw = new StringWriter();
            // Используем PrintWriter для перенаправления вывода исключения в StringWriter.
            PrintWriter pw = new PrintWriter(sw);
            // Записываем стек исключения в StringWriter.
            result.getThrowable().printStackTrace(pw);
            // Выводим полный стек ошибки в консоль.
            System.out.println(sw.toString());
        }

        BaseTest baseTest = new BaseTest();
        File screenShot = baseTest.getDriver().getScreenshotAs(OutputType.FILE);

        Map<String, String> params = new HashMap<>();
        params = result.getTestContext().getCurrentXmlTest().getAllParameters();

        String imagePath = "Screenshots" + separator
                + "Platform: " + params.get("platformName") + separator
                + "Platform version: " + params.get("platformVersion") + " " + separator
                + params.get("deviceName") + separator
                + "Date: " + baseTest.getDateTime() + separator
                + "ClassName: " + result.getTestClass().getRealClass().getSimpleName() + separator
                + "MethodName: " + result.getName() + ".png";

//        String completeImagePath = System.getProperty("user.dir") + separator + imagePath;

        try {
            FileUtils.copyFile(screenShot, new File(imagePath));
//            Reporter.log("This is the sample screenshot");
//            Reporter.log("<a href='" + completeImagePath + "'> <img src='" + completeImagePath
//                    + "'height='100' width='100'/> </a>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}










