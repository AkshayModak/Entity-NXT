package architecture;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import one.DefaultObjects;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadRedirector {

	private List<Map<String, Object>> redirectorList = new ArrayList<Map<String, Object>>();

	public List<Map<String, Object>> getRedirector(String fileName) {

		try {

			File file = new File(fileName);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();

			if (doc.hasChildNodes()) {
				printNote(doc.getElementsByTagName("request"));
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return redirectorList;

	}

	private void printNote(NodeList nodeList) {

		Map<String, Object> nodeMap = new HashMap<String, Object>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			String requestName;
			String filePath;
			String method;
			String show;

			Node childNodes = nodeList.item(i);
			Map<String, Object> requestMap = new HashMap<String, Object>();

			if (childNodes.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) childNodes;
				requestName = element.getElementsByTagName("request-name").item(0).getTextContent();
				show = element.getElementsByTagName("show").item(0).getTextContent();

				if ((element.getElementsByTagName("file-path").item(0) != null) && (element.getElementsByTagName("file-path").item(0) != null)) {
					filePath = element.getElementsByTagName("file-path").item(0).getTextContent();
					method = element.getElementsByTagName("method").item(0).getTextContent();
				
					requestMap.put("file-path", filePath);
					requestMap.put("method", method);
				} else {
					filePath = null;
					method = null;
				
					requestMap.put("file-path", filePath);
					requestMap.put("method", method);
				}

				requestMap.put("request-name", requestName);
				requestMap.put("show", show);
				
				redirectorList.add(requestMap);
			}
		}
	}
	
	public void setRedirectorList(Map<String, Object> requestMap) {
		redirectorList.add(requestMap);
	}
	
	public List<Map<String, Object>> getRedirectorList() {
		return redirectorList;
	}
}



