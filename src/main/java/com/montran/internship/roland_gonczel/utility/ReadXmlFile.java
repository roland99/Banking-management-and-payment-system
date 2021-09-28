package com.montran.internship.roland_gonczel.utility;

import com.montran.internship.roland_gonczel.entity.Payment;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import org.dom4j.io.OutputFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReadXmlFile {

    public static List<String> getCurrencies(){
        List<String> list = new ArrayList<>();
        List<Currency> currencyList = new ArrayList<Currency>();
        File xmlFile = new File("src/main/resources/static/currency.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try{
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("Currency");


            for(int i=0; i< nodeList.getLength(); i++){
                currencyList.add(getCurrency(nodeList.item(i)));
            }

            for(Currency c: currencyList){
                list.add(c.getName());
            }


        }catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }

        return list;
    }

    private static Currency getCurrency(Node node) {

        File xmlFile = new File("src/main/resources/static/currency.xml");
        // XMLReaderDOM domReader = new XMLReaderDOM();
        Currency currency = new Currency();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            currency.setName(getTagValue("name",element));

        }
        return currency;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }


    public static String readXmlFileAsString() {

        String xmlFile = "src/main/resources/static/p008-template.xml";

        try {
            String xml = Files.lines(Paths.get(xmlFile)).collect(Collectors.joining("\n"));
            return xml;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String();
    }

    public static String xmlFormatter(String xml){
        int indent = 2;
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            throw new RuntimeException(e); // simple exception handling, please review it
        }
    }

    public static String fillXml(Payment payment){
        String xml = ReadXmlFile.readXmlFileAsString();
        xml = xml.replace("$SENDER$", "INTBROB0");
        xml = xml.replace("$REF$", payment.getReference());
        xml = xml.replace("$DATETIME$", java.time.LocalDateTime.now().toString());

        xml = xml.replace("$DATE$", java.time.LocalDate.now().toString());

        xml = xml.replace("$FROM_ACCOUNT$", payment.getDebitAccount());
        xml = xml.replace("$RECEIVER$", payment.getBank());
        xml = xml.replace("$CREDITOR NAME$", payment.getFullName());
        xml = xml.replace("$TO ACCOUNT$", payment.getCreditAccount());
        xml = xml.replace("$DETAILS$", payment.getMessage());
        return xml;
    }


}
