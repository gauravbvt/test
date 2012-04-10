package com.mindalliance.testscripts;


import java.text.SimpleDateFormat;
import java.util.Date;

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
	 * Invoke testISPPlan() and get plans, versions and planners identities.
	 */
	public static void testISPPlan(){
		try {
			LogFunctions.generateLogsDirectory();	
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST001_ISPPlans";
			
			Date currentDate = new java.util.Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String formattedDate = formatter.format(currentDate)+" "+"IST";
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println("1) Test Case : ISP001 Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") ISP001 Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST001_ISPPlans.csv");
			System.out.println("2) Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			GlobalVariables.userCredentials=GlobalVariables.testData.get("username")+":"+GlobalVariables.testData.get("password");
			String data =GlobalVariables.userCredentials;
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println("3) Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println("4) Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
				
			// Assertion: verify that Plan is present
			GlobalVariables.steps++;
			Assert.assertEquals("Plan URI is",GlobalVariables.responseString.contains(GlobalVariables.testData.get("uri")),true);			
			Assert.assertEquals("Plan Name is",true,GlobalVariables.responseString.contains(GlobalVariables.testData.get("name")));
			Assert.assertEquals("Date is",true,GlobalVariables.responseString.contains(formattedDate));
			Assert.assertEquals("Date Versioned is",true,GlobalVariables.responseString.contains(GlobalVariables.testData.get("dateVersioned")));
			Assert.assertEquals("Version is",true,GlobalVariables.responseString.contains(GlobalVariables.testData.get("version")));
			Assert.assertEquals("Plan release is",true,GlobalVariables.responseString.contains(GlobalVariables.testData.get("release")));
			
			System.out.println("5) Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

//			// Expected Result
//			GlobalVariables.steps++;
//			System.out.println("6) Expected Result");
//			Configurations.expectedResult();
//			LogFunctions.logDescription(GlobalVariables.steps + ") Expected Result");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println("6) Test Case : REST001_ISPPlans Execution Completed");
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