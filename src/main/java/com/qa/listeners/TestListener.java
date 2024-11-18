package com.qa.listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.PrintWriter;
import java.io.StringWriter;

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
    }
}







