package com.mindalliance.testscripts;

import junit.framework.TestCase;

import com.mindalliance.configurations.Configurations;
import com.mindalliance.configurations.GlobalVariables;
import com.mindalliance.configurations.LogFunctions;

public class REST007_ISPVersion extends TestCase{
	/**
	 * Invoke testISPPlanIdentifier() and get planners identities.
	 */
	public static void testISPPlanRelease(){
		try {
			LogFunctions.generateLogsDirectory();	
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST007_ISPVersion";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps+") Test Case : REST007_ISPVersion Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST007_ISPVersion Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST006_ISPRelease.csv");
			System.out.println(GlobalVariables.steps+") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api");
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(GlobalVariables.steps+") Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(GlobalVariables.steps+") Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
					
//			// Assertion: verify that PlanSummary
//			GlobalVariables.steps++;
//			boolean planIdentifierResult;
//			// Verify URI
//			planIdentifierResult=Configurations.parseResponse("planIdentifier","uri",GlobalVariables.testData.get("uri"));	
//			Assert.assertEquals("Plan URI is", true, planIdentifierResult);
//			// Verify Plan Name
//			planIdentifierResult=Configurations.parseResponse("planIdentifier","name",GlobalVariables.testData.get("name"));	
//			Assert.assertEquals("Plan Name is",true,planIdentifierResult);
//			// Verify Release
//			planIdentifierResult=Configurations.parseResponse("planIdentifier","release",GlobalVariables.testData.get("release"));
//			Assert.assertEquals("Plan release is",true,planIdentifierResult);
//			// Verify Version
//			planIdentifierResult=Configurations.parseResponse("planIdentifier","version",GlobalVariables.testData.get("version"));
//			Assert.assertEquals("Plan release is",true,planIdentifierResult);
//			
//			System.out.println(GlobalVariables.steps+") Plan Summary Assertion Pass");
//			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps+") Test Case : REST007_ISPVersion Execution Completed");
			System.out.println("");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST007_ISPVersion Execution Completed");
			System.out.println("");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps+") Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			//System.out.println("One of the Value missed");
			LogFunctions.logException(e.getMessage());
		}
	}
	
}
