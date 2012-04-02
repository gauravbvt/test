package com.mindalliance.testscripts;

import junit.framework.TestCase;

import com.mindalliance.configurations.Configurations;
import com.mindalliance.configurations.GlobalVariables;
import com.mindalliance.configurations.LogFunctions;

public class REST004_ISPIssues extends TestCase{
	/**
	 * Invoke testISPPlan() and get plans, versions and planners identities.
	 */
	public static void testISPPlan(){
		try {
			LogFunctions.generateLogsDirectory();	
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST004_ISPIssues";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println("1) Test Case : REST002_IsProcedures Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST004_ISPIssues Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST004_ISPIssues.csv");
			System.out.println("2) Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
//			String data =GlobalVariables.testData.get("responseType");
			GlobalVariables.userCredentials=GlobalVariables.testData.get("username")+":"+GlobalVariables.testData.get("password");
			String data= GlobalVariables.userCredentials;
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println("3) Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println("4) Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: Verify that Plan name is present
			GlobalVariables.steps++;
			assertEquals(true,GlobalVariables.responseString.contains("uri"));
			System.out.println("5) Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

			// Execution Completed
			GlobalVariables.steps++;
			System.out.println("6) Test Case : REST004_ISPIssues Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : ISP001 Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println("5)Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
}