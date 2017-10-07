package com.pj.magic.util;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class QueriesUtil {

	private final static Map<String, String> queries = new HashMap<>();

	public static String getSql(String key) {
		if (queries.isEmpty()) {
			try {
				initializeQueriesFromXml();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return queries.get(key);
	}

	private static void initializeQueriesFromXml() throws Exception {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.parse(QueriesUtil.class.getClassLoader().getResourceAsStream("sql/queries.xml"));
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("/root/entry");		
		NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String key = node.getAttributes().getNamedItem("name").getNodeValue();
			queries.put(key, node.getTextContent().trim());
		}
	}

}