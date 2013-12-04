package com.mindalliance.automationbvt;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.Test;

public class MABVT0004_VerifyXMLFiles {
	
	@Test
	public void test() throws Exception {
		try {
			
			File ObjectRepository=new File("ObjectRepository");
			// Create a new factory to create parsers
			DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
			// Use the factory to create a parser (builder) and use it to parse the document.
		    DocumentBuilder builder = dBF.newDocumentBuilder();
           
		    ObjectRepository=new File(ObjectRepository.getCanonicalPath().toString());
		
			File[] listOfFiles = ObjectRepository.listFiles();

			for (File file : listOfFiles) {
				if (file.isFile() && file.toString().contains(".xml")) {
					builder.parse(file);
					System.out.println(file + " is well-formed!");
				}
			}
			
		   File TestData=new File("TestData");
			
		   TestData=new File(TestData.getCanonicalPath().toString());
		
		   File[] listOfFiles1 = TestData.listFiles();

			for (File file : listOfFiles1) {
				if (file.isFile() && file.toString().contains(".xml")) {
					builder.parse(file);
					System.out.println(file + " is well-formed!");
				}
			}	
			
		}
		catch (Exception e) {
			System.out.println("xml file is not well formed");
			System.exit(1);
		}
	}
		
	
	
	
	


}