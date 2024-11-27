package com.qa.listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.qa.BaseTest;
import com.qa.ExtentReport;
import com.qa.utils.TestUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.testng.ITestContext;
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
    TestUtils testUtils = new TestUtils();

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
            testUtils.log().info(sw.toString());
        }

        BaseTest baseTest = new BaseTest();
        File screenShot = baseTest.getDriver().getScreenshotAs(OutputType.FILE);

        //=======================================================================================
        byte[] encoded = null;
        try {
            encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(screenShot));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //=======================================================================================

        Map<String, String> params = new HashMap<>();
        params = result.getTestContext().getCurrentXmlTest().getAllParameters();

        String imagePath = "Reports" + separator
                + "Screenshots" + separator
                + "Platform: " + params.get("platformName") + separator
                + "OS version: " + params.get("platformVersion") + " " + separator
                + "Device: " + params.get("deviceName") + separator
                + "Date: " + baseTest.getDateTime() + separator
                + "Class: " + result.getTestClass().getRealClass().getSimpleName() + separator
                + "Method: " + result.getName()
                + separator + result.getName() + ".png";

        try {
            FileUtils.copyFile(screenShot, new File(imagePath));
//            Reporter.log("This is the sample screenshot");
//            Reporter.log("<a href='" + completeImagePath + "'> <img src='" + completeImagePath
//                    + "'height='100' width='100'/> </a>");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //=======================================================================================
        ExtentReport.getTest().fail("Test Failed",
                MediaEntityBuilder.createScreenCaptureFromPath(imagePath).build());

        ExtentReport.getTest().fail("Test Failed",
                MediaEntityBuilder.createScreenCaptureFromBase64String("base64String").build());

        ExtentReport.getTest().fail(result.getThrowable());
        //=======================================================================================


    }

    @Override
    public void onTestStart(ITestResult result) {
        BaseTest baseTest = new BaseTest();
        ExtentReport.startTest(result.getName(), result.getMethod().getDescription())
                .assignCategory(baseTest.getPlatform() + "_" + baseTest.getDeviceName())
                .assignAuthor("Janbolot");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReport.getTest().log(Status.PASS, "Test Passed");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentReport.getTest().log(Status.SKIP, "Test Skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReport.getReporter().flush();
    }
}










