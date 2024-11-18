package com.qa.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class parseXML {
    public HashMap<String, String> parseStringXML (InputStream file) throws Exception {
        HashMap<String, String> stringMap = new HashMap<>();

        // Get Document Builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Build Document
        Document document = builder.parse(file);

        // Normalize the XML Structure: It's just too important
        document.getDocumentElement().normalize();

        // Here comes the root node
        Element root = document.getDocumentElement();
//        System.out.println(root.getNodeName());

        // Get all elements
        NodeList nList = document.getElementsByTagName("string");
//        System.out.println("===========================================");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
//            System.out.println(""); // Just a separator
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                // Store each element key value in map
                stringMap.put(element.getAttribute("name"), element.getTextContent());
            }
        }
        return stringMap;
    }
}
