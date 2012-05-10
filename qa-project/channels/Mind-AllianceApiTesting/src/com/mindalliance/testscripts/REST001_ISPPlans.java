package com.mindalliance.testscripts;


import com.mindalliance.configurations.Configurations;
import com.mindalliance.configurations.GlobalVariables;
import com.mindalliance.configurations.LogFunctions;

import junit.framework.Assert;
import junit.framework.TestCase;
/**
 * The ISP001 class contains the test case for rest/isp/plans api
 * @author AFourTech
 */
public class REST001_ISPPlans extends TestCase{
	
	/**
	 * Invoke testISPPlanIdentifier() and get planners identities.
	 */
	public static void testISPPlanIdentifier(){
		try {
			LogFunctions.generateLogsDirectory();	
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST001_ISPPlanIdentifier";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps+" Test Case : REST001_ISPPlanIdentifier Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST001_ISPPlanIdentifier Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST001_ISPPlans.csv");
			System.out.println(GlobalVariables.steps+" Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api");
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(GlobalVariables.steps+" Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(GlobalVariables.steps+" Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
					
			// Assertion: verify that PlanSummary
			GlobalVariables.steps++;
			boolean planIdentifierResult;
			// Verify URI
			planIdentifierResult=Configurations.parseResponse("planIdentifier","uri",GlobalVariables.testData.get("uri"));	
			Assert.assertEquals("Plan URI is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("uri")));
			// Verify Plan Name
			planIdentifierResult=Configurations.parseResponse("planIdentifier","name",GlobalVariables.testData.get("name"));	
			Assert.assertEquals("Plan Name is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("name")));
			// Verify Version
			planIdentifierResult=Configurations.parseResponse("planIdentifier","version",GlobalVariables.testData.get("version"));
			Assert.assertEquals("Version is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("version")));
			// Verify Release
			planIdentifierResult=Configurations.parseResponse("planIdentifier","release",GlobalVariables.testData.get("release"));
			Assert.assertEquals("Plan release is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("release")));
			// Verify Date Versioned
			planIdentifierResult=Configurations.parseResponse("planIdentifier","dateVersioned",GlobalVariables.testData.get("dateVersioned"));
			Assert.assertEquals("Date Versioned is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("dateVersioned")));			
			System.out.println(GlobalVariables.steps+" Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps+" Test Case : REST001_ISPPlanIdentifier Execution Completed");
			System.out.println("");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST001_ISPPlanIdentifier Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps+"Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			//System.out.println("One of the Value missed");
			LogFunctions.logException(e.getMessage());
		}
	}
	
	/**
	 * Invoke testISPPlanner() and get planners.
	 */
	public static void testISPPlanner(){
		try {
			LogFunctions.generateLogsDirectory();	
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST001_ISPPlanner";
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +" Test Case : REST001_ISPPlanner Execution Started");
			System.out.println("testISPPlanPlanner Method");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST001_ISPPlanner Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST001_ISPPlans.csv");
			System.out.println(GlobalVariables.steps +" Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api");
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(GlobalVariables.steps +" Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(GlobalVariables.steps +" Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: verify that Planners
			GlobalVariables.steps++;
			boolean planPlannerResult;
			// Verify URI
			planPlannerResult=Configurations.parseResponse("planner","username",GlobalVariables.testData.get("username"));	
			Assert.assertEquals("Planner UserName",planPlannerResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("username")));
			// Verify Plan Name
			planPlannerResult=Configurations.parseResponse("planner","fullName",GlobalVariables.testData.get("fullName"));	
			Assert.assertEquals("Full Name is",planPlannerResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("fullName")));
			// Verify Version
			planPlannerResult=Configurations.parseResponse("planner","email",GlobalVariables.testData.get("email"));
			Assert.assertEquals("Email is",planPlannerResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("email")));
			System.out.println(GlobalVariables.steps +" Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +" Test Case : REST001_ISPPlanner Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST001_ISPPlanner Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +" Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + " Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
}