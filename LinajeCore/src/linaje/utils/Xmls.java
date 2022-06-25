/*
 * Copyright 2022 Pablo Linaje
 * 
 * This file is part of Linaje Framework.
 *
 * Linaje Framework is free software: you can redistribute it and/or modify it under the terms 
 * of the GNU Lesser General Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 *
 * Linaje Framework is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Linaje Framework.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package linaje.utils;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.*;

import java.io.*;
import java.util.*;

import linaje.statics.Constants;

/**
 * Utils for create, read an write XML documents
 **/
public final class Xmls {

	public static final String ENCODING_PROPERTY = "encoding";
	public static final String ENCODING_DEFAULT = "UTF-8";
		
	public static Hashtable<String, Document> documentsCache = new Hashtable<String, Document>();

	private static String encoding = null;
	
	public static String getValue(Node node) {
		try {
			return node.getTextContent();
		}
		catch (Throwable t) {
			return Constants.VOID;
		}
	}

	public static void setValue(Node node, String value) {
		try {
			node.setTextContent(value);
		}
		catch (Throwable t) {
		}
	}

	public static Node getAttribute(Node node, String attrName) {

		try {
			return node.getAttributes().getNamedItem(attrName);
		}
		catch (Throwable t) {
			return null;
		}
	}

	public static String getAttributeValue(Node node, String attrName) {

		Node attribute = getAttribute(node, attrName);
		return getValue(attribute);
	}

	public static void setAttributeValue(Node node, String attrName, String value) {

		Node attribute = getAttribute(node, attrName);
		setValue(attribute, value);
	}
	
	public static Element getRootNode(Document document) {
		
		return (Element) document.getFirstChild();
	}
	public static NodeList getChildNodes(Node parentNode, String childNodesTagName) {
		
		if (parentNode != null && parentNode.getNodeType() == Node.ELEMENT_NODE) {
			
			Element nodeElementParent = (Element) parentNode;
			return nodeElementParent.getElementsByTagName(childNodesTagName);
		}
		return null;
	}

	public static Node getChildNode(Node parentNode, String childNodeTagName, String attrName, String attrValue) {
		
		try {
			
			NodeList nodosHijos = getChildNodes(parentNode, childNodeTagName);
			if (nodosHijos != null) {
				
				for (int i = 0; i < nodosHijos.getLength(); i++) {
			
					Node nodoHijo = nodosHijos.item(i);
					String value = getAttributeValue(nodoHijo, attrName);
					if (value != null && value.equalsIgnoreCase(attrValue))
						return nodoHijo;
				}
			}
		} catch (Throwable e) {
		}
		return null;
	}
	
	public static Element createChildNode(Node parentNode, String childNodeTagName) {
		return createChildNode(parentNode, childNodeTagName, null);
	}
	public static Element createChildNode(Node parentNode, String childNodeTagName, String childNodeValue) {
	
		Element nodoHijo = parentNode.getOwnerDocument().createElement(childNodeTagName);
		parentNode.appendChild(nodoHijo);
		if (childNodeValue != null)
			setValue(nodoHijo, childNodeValue);
		
		return nodoHijo;
	}
	
	public static Attr createAttribute(Element elementNode, String attrName) {
		return createAttribute(elementNode, attrName, null);
	}
	public static Attr createAttribute(Element elementNode, String attrName, String attrValue) {
		
		Attr attribute = elementNode.getOwnerDocument().createAttribute(attrName);
		if (attrValue != null)
			setAttributeValue(elementNode, attrName, attrValue);
				
		return attribute;
	}
	
	public static Document createXMLFile(File xmlFile, String rootNodeTagName) throws ParserConfigurationException, TransformerException {
		return createXMLFile(xmlFile, rootNodeTagName, getEncoding());
	}
	public static Document createXMLFile(File xmlFile, String rootNodeTagName, String encoding) throws ParserConfigurationException, TransformerException {
	
		Document xmlDocument = createXMLDocument(rootNodeTagName);
		saveXMLFile(xmlFile, xmlDocument, encoding);
		
		String key = getKey(xmlFile);
		getDocumentsCache().put(key, xmlDocument);
		
		return xmlDocument;
	}
	
	public static Document createXMLDocument(String rootNodeTagName) throws ParserConfigurationException {
		
		DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFac.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Element elementocuspide = doc.createElement(rootNodeTagName);
		doc.appendChild(elementocuspide);
		
		return doc;
	}
	
	public static synchronized Hashtable<String, Document> getDocumentsCache() {
	
		if (documentsCache == null)
			documentsCache = new Hashtable<String, Document>();
		
		return documentsCache;
	}
	
	private static String getKey(File xmlFile) {
		return xmlFile.getAbsolutePath()+Constants.AT+xmlFile.lastModified();
	}
	public static synchronized Document readXMLFile(File xmlFile) throws IOException, ParserConfigurationException, SAXException {
	
		String key = getKey(xmlFile);
		
		Document document;
		if (getDocumentsCache().get(key) == null) {
			FileInputStream in = new FileInputStream(xmlFile);
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
			in.close();
			getDocumentsCache().put(key, document);
		}
		else {
			document = (Document) getDocumentsCache().get(key);
		}
		
		return document;
	}
	
	public static void saveXMLFile(File xmlFile, Document xmlDocument) throws TransformerException {
		saveXMLFile(xmlFile, xmlDocument, getEncoding());
	}
	public static void saveXMLFile(File xmlFile, Document xmlDocument, String encoding) throws TransformerException {
	
		Files.createDirParent(xmlFile);
		
		Source source = new DOMSource(xmlDocument);
		Result result = new StreamResult(xmlFile);
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(ENCODING_PROPERTY, encoding);
		transformer.transform(source, result);
	}
	
	public static String getEncoding() {
		if (encoding == null)
			encoding = ENCODING_DEFAULT;
		return encoding;
	}
	public static void setEncoding(String encoding) {
		Xmls.encoding = encoding;
	}
}
