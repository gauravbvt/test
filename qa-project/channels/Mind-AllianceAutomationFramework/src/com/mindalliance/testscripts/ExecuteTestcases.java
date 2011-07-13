package com.mindalliance.testscripts;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.ReportFunctions;

public class ExecuteTestcases {
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws XMLStreamException 
	 */
	public static void main(String[] args) throws InterruptedException, IOException, XMLStreamException {
    	// Call initializeAutomationScripts()
		GenericFunctionLibrary.initializeTestData();
	
		//Call tearAutomationScripts()
		GenericFunctionLibrary.tearDownTestData();
		//Call generateAutomationReportInOds()
		ReportFunctions.generateAutomationReport();
	}

}

