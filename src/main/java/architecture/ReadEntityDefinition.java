package architecture;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import architecture.utils.DebugWrapper;
import architecture.ReadXMLFile;
import architecture.utils.Utility;

public class ReadEntityDefinition {

    private List<List> NodeDataList = new ArrayList<List>();
    Connection conn;
    private Map<String, Object> tablesMap = new HashMap<String, Object>();
    public final String className = ReadEntityDefinition.class.getName();

    private String nextrr_home = System.getProperty("user.dir") + "/";

    private String getJdbcTypeName(int jdbcType) {
        Map map = new HashMap();

        // Get all field in java.sql.Types
        Field[] fields = java.sql.Types.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                String name = fields[i].getName();
                Integer value = (Integer) fields[i].get(null);
                map.put(value, name);
            } catch (IllegalAccessException e) {
            }
        }
        return (String) map.get(jdbcType);
    }

    public Map<String, Object> getEntityDefinition() {

        Map<String, Object> nodeResult = new HashMap<String, Object>();
        try {
            String fileName = nextrr_home + "Entity.xml";
            File file = new File(fileName);
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            Element tableElements = doc.getDocumentElement();
            NodeList tableNodeList = tableElements.getElementsByTagName("table");

            if (doc.hasChildNodes()) {
                for (int i = 0; i < tableNodeList.getLength(); i++) {
                    Node columnNodes = tableNodeList.item(i);
                    if (columnNodes.hasAttributes()) {
                        NamedNodeMap nodeMap = columnNodes.getAttributes();
                        Node node = nodeMap.item(0);
                        String tableName = node.getNodeValue();
                        getTableNodes(columnNodes.getChildNodes(), tableName);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return tablesMap;
    }

    private void getTableNodes(NodeList nodeList, String tableName) {

        List<Map<String, Object>> tableList = new ArrayList<Map<String, Object>>();
        String nodeName = null;
        String nodeValue = null;

        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            if (tempNode.hasAttributes()) {
                NamedNodeMap nodeMap = tempNode.getAttributes();
                Map<String, Object> nodeData = new HashMap<String, Object>();
                nodeData.put("tableName", tableName);
                for (int i = 0; i < nodeMap.getLength(); i++) {
                    Node node = nodeMap.item(i);
                    nodeName = node.getNodeName();
                    nodeValue = node.getNodeValue();
                    nodeData.put(nodeName, nodeValue);
                }
                tableList.add(nodeData);
            }
        }
        tablesMap.put(tableName.toLowerCase(), tableList);
    }
}