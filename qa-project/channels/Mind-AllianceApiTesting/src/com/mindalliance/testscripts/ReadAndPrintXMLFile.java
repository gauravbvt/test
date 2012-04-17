package com.mindalliance.testscripts;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ReadAndPrintXMLFile {
	public static void main (String argv []){
	    try {

	            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	            Document doc = docBuilder.parse (new File("Testdata//response.xml"));

	            // normalize text representation
	            doc.getDocumentElement ().normalize ();
	            System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

	            NodeList node = doc.getElementsByTagName("planIdentifier");
	            int totalPersons = node.getLength();
	            System.out.println("Total no of node : " + totalPersons);

	            for(int s=0; s<node.getLength() ; s++){

	                Node firstPersonNode = node.item(s);
	                if(firstPersonNode.getNodeType() == Node.ELEMENT_NODE){

	                    Element firstPersonElement = (Element)firstPersonNode;
	                    NodeList firstNameList = firstPersonElement.getElementsByTagName("uri");
	                    Element firstNameElement = (Element)firstNameList.item(0);

	                    NodeList textFNList = firstNameElement.getChildNodes();
	                    System.out.println("Attribute 1 : " + ((Node)textFNList.item(0)).getNodeValue().trim());

	                    NodeList lastNameList = firstPersonElement.getElementsByTagName("name");
	                    Element lastNameElement = (Element)lastNameList.item(0);

	                    NodeList textLNList = lastNameElement.getChildNodes();
	                    System.out.println("Attribute 2 : " + ((Node)textLNList.item(0)).getNodeValue().trim());
	                    
	                    NodeList version = firstPersonElement.getElementsByTagName("version");
	                    Element versionElement = (Element)version.item(0);

	                    NodeList versionlist = versionElement.getChildNodes();
	                    System.out.println("Attribute 3 : " + ((Node)versionlist.item(0)).getNodeValue().trim());

	                    NodeList ageList = firstPersonElement.getElementsByTagName("dateVersioned");
	                    Element ageElement = (Element)ageList.item(0);

	                    NodeList textAgeList = ageElement.getChildNodes();
	                    System.out.println("Attribute 4 : " + ((Node)textAgeList.item(0)).getNodeValue().trim());
	                    System.out.println("");

	                }

	            }

	    }catch (SAXParseException err) {
	    	System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
	    	System.out.println(" " + err.getMessage ());

	    }catch (SAXException e) {
	    	Exception x = e.getException ();
	    	((x == null) ? e : x).printStackTrace ();

	    }catch (Throwable t) {
	    	t.printStackTrace ();
	    }

	}
}
