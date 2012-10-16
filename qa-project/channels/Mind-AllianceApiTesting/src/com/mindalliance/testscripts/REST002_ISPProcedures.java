package com.mindalliance.testscripts;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.mindalliance.configurations.Configurations;
import com.mindalliance.configurations.GlobalVariables;
import com.mindalliance.configurations.LogFunctions;

public class REST002_ISPProcedures extends TestCase{
	
	/**
	 * Invoke testISPProcedures() 
	 */
	public static void testISPProcedures(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST002_ISPProcedures";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST002_ISPProcedures Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST002_ISPProcedures Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST002_ISPProcedures.csv");
			System.out.println(GlobalVariables.steps +") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/procedures";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(GlobalVariables.steps +") Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(GlobalVariables.steps +") Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: verify that Plan Agent
			GlobalVariables.steps++;
			boolean plan;
			// Verify URI
			plan=Configurations.parseResponse("planIdentifier","uri",GlobalVariables.testData.get("uri"));	
			Assert.assertEquals("Plan URI is", true, plan);
			// Verify Plan Name
			plan=Configurations.parseResponse("planIdentifier","name",GlobalVariables.testData.get("name"));	
			Assert.assertEquals("Plan Name is",true,plan);
			// Verify Release
			plan=Configurations.parseResponse("planIdentifier","release",GlobalVariables.testData.get("release"));
			Assert.assertEquals("Plan release is",true,plan);
			// Verify Version
			plan=Configurations.parseResponse("planIdentifier","version",GlobalVariables.testData.get("version"));
			Assert.assertEquals("Plan release is",true,plan);
						
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST002_ISPProcedures Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST002_ISPProceduresAboutAgent Execution Completed");
			System.out.println("");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	
}