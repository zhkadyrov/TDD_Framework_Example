package com.qa.utils;

import com.qa.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static java.io.File.separator;

/**
 * Этот класс предназначен для вспомогательных операций, которые могут быть полезны в тестах.
 * Его основная задача — это парсинг XML-файла и преобразование содержимого в HashMap,
 * где ключами являются атрибуты name, а значениями — содержимое соответствующих тегов <string>.
 */
public class TestUtils {
    public static final long WAIT = 10;

    public HashMap<String, String> parseStringXML(InputStream file) throws Exception {
        HashMap<String, String> stringMap = new HashMap<>(); // Результирующая мапа.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();
        NodeList nList = document.getElementsByTagName("string");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp); // Получение текущего узла
            if (node.getNodeType() == Node.ELEMENT_NODE) { // Проверка типа узла
                Element element = (Element) node; // Преобразование в Element
                stringMap.put(element.getAttribute("name"), element.getTextContent());
            }
        }
        return stringMap;
    }

    public String dateTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss-dd.MM.yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public Logger log() {
        ThreadContext.put("ROUTINGKEY", "default");
        return LogManager.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public void log(String text) {
        BaseTest baseTest = new BaseTest();
        String message = Thread.currentThread().getId() + ": " + baseTest.getPlatform() + ": " + baseTest.getDeviceName()
                + ": " + Thread.currentThread().getStackTrace()[2].getClassName() + ": " + text;

        System.out.println(message);

        String stringFile = "Logs" + separator + baseTest.getPlatform() + "_" + baseTest.getDeviceName()
                + separator + baseTest.getDateTime();

        File logFile = new File(stringFile);

        if (!logFile.exists()) {
            logFile.mkdirs();
        }

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(logFile + separator + "log.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(message);
        printWriter.close();
    }
}




























