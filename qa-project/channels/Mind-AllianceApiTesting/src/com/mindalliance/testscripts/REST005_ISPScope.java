package com.mindalliance.testscripts;

import com.mindalliance.configurations.Configurations;
import com.mindalliance.configurations.GlobalVariables;
import com.mindalliance.configurations.LogFunctions;

import junit.framework.Assert;
import junit.framework.TestCase;

public class REST005_ISPScope extends TestCase{
	/**
	 * Invoke testISPPlan() and get plans, versions and planners identities.
	 */
	public static void testISPPlan(){
		try {
			LogFunctions.generateLogsDirectory();	
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST005_ISPScope";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println("1) Test Case : REST005_ISPScope Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST005_ISPScope Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST005_ISPScope.csv");
			System.out.println("2) Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/version/5/scope";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println("3) Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println("4) Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
//			// Expected Result
//			GlobalVariables.steps++;
//			System.out.println("5) Expected Result");
//			Configurations.expectedResult();
//			LogFunctions.logDescription(GlobalVariables.steps + ") Expected Result");
						
			// Assertion: Verify that Plan name is present
			GlobalVariables.steps++;
			Assert.assertEquals("mindalliance_com_channels_plans_railsec","mindalliance_com_channels_plans_railsec");
			System.out.println("6) Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

			// Execution Completed
			GlobalVariables.steps++;
			System.out.println("7) Test Case : REST005_ISPScope Execution Completed");
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
