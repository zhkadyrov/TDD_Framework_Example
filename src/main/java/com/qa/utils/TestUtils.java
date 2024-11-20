package com.qa.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Этот класс предназначен для вспомогательных операций, которые могут быть полезны в тестах.
 * Его основная задача — это парсинг XML-файла и преобразование содержимого в HashMap,
 * где ключами являются атрибуты name, а значениями — содержимое соответствующих тегов <string>.
 */
public class TestUtils {
    // Константа для ожидания (может использоваться в тестах)
    public static final long WAIT = 10;

    /**
     * Метод для парсинга XML-файла и преобразования его содержимого в HashMap.
     * @param file InputStream, содержащий XML-файл.
     * @return HashMap, где ключ — атрибут name, значение — текст элемента <string>.
     * @throws Exception В случае ошибок при чтении или обработке XML.
     */
    public HashMap<String, String> parseStringXML(InputStream file) throws Exception {
        HashMap<String, String> stringMap = new HashMap<>(); // Результирующая мапа.

        // Создание фабрики и парсера для XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Построение DOM-документа из входного файла
        Document document = builder.parse(file);

        // Нормализация структуры XML
        document.getDocumentElement().normalize();

        // Извлечение корневого элемента (например, <resources>)
        Element root = document.getDocumentElement();

        // Извлечение всех элементов <string>
        NodeList nList = document.getElementsByTagName("string");

        // Обход всех элементов <string>
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp); // Получение текущего узла
            if (node.getNodeType() == Node.ELEMENT_NODE) { // Проверка типа узла
                Element element = (Element) node; // Преобразование в Element
                // Сохранение атрибута "name" и текста в мапу
                stringMap.put(element.getAttribute("name"), element.getTextContent());
            }
        }
        return stringMap; // Возврат мапы.
    }

    public String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss-dd.MM.yyyy");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        return dateFormat.format(date);
    }
}





