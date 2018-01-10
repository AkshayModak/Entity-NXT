package one;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReadXMLFile {

    public static Map<String, Object> getXMLData(String fileName) {

        Map<String, Object> nodeResult = new HashMap<String, Object>();
        try {

            File file = new File(fileName);
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            if (doc.hasChildNodes()) {
                nodeResult = printNote(doc.getChildNodes());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return nodeResult;

    }

    private static Map<String, Object> printNote(NodeList nodeList) {

        Map<String, Object> nodeData = new HashMap<String, Object>();
        String nodeName = null;
        String nodeValue = null;

        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                if (tempNode.hasAttributes()) {
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node node = nodeMap.item(i);
                        nodeName = node.getNodeName();
                        nodeValue = node.getNodeValue();
                        nodeData.put(nodeName, nodeValue);
                    }
                }
                if (tempNode.hasChildNodes()) {
                    return printNote(tempNode.getChildNodes());
                }
            }
        }
        return nodeData;
    }

}