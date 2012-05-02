package com.mindalliance.testscripts;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.mindalliance.configurations.Configurations;
import com.mindalliance.configurations.GlobalVariables;
import com.mindalliance.configurations.LogFunctions;

public class REST002_ISPProcedures extends TestCase{
	
	public static void testISPProceduresAboutAgent(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST002_ISPProceduresAboutAgent";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println("1) Test Case : REST002_ISPProceduresAboutAgent Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST002_ISPProceduresAboutAgent Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST002_ISPProcedures.csv");
			System.out.println("2) Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/procedures";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println("3) Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println("4) Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: verify that PlanAgent
			GlobalVariables.steps++;
			boolean planAgentResult;
			// Verify Agent ID
			planAgentResult=Configurations.parseResponse("agent","id",GlobalVariables.testData.get("agentId"));	
			Assert.assertEquals("Agent is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("agentId")));
			// Verify Agent Name
			planAgentResult=Configurations.parseResponse("agent","name",GlobalVariables.testData.get("agentName"));	
			Assert.assertEquals("Agent Name is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("agentName")));
			// Verify Agent Unique Identity
			planAgentResult=Configurations.parseResponse("agent","hasUniqueIdentity",GlobalVariables.testData.get("hasUniqueIdentity"));	
			Assert.assertEquals("Agent Unique Identity is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("hasUniqueIdentity")));
			// Verify Agent Anonymous
			planAgentResult=Configurations.parseResponse("agent","isAnonymous",GlobalVariables.testData.get("isAnonymous"));	
			Assert.assertEquals("Is Agent Anonymous",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("isAnonymous")));
			// Verify Agent Kind
			planAgentResult=Configurations.parseResponse("agent","kind",GlobalVariables.testData.get("kind"));	
			Assert.assertEquals("Agent Kind is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("kind")));			
			System.out.println("5) Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

			// Execution Completed
			GlobalVariables.steps++;
			System.out.println("6) Test Case : REST002_ISPProceduresAboutAgent Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST002_ISPProceduresAboutAgent Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println("6)Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	public static void testISPProcedures(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST002_ISPProcedures";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println("1) Test Case : REST002_ISPProcedures Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST002_ISPProcedures Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST002_ISPProcedures.csv");
			System.out.println("2) Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/procedures";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println("3) Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println("4) Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: verify that PlanAgent
			GlobalVariables.steps++;
			boolean planAgentResult;
			// Verify Agent ID
			planAgentResult=Configurations.parseResponse("procedure","agentId",GlobalVariables.testData.get("agentId"));	
			Assert.assertEquals("Agent ID is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("agentId")));
			// Verify Agent Event ID
			planAgentResult=Configurations.parseResponse("situation","eventId",GlobalVariables.testData.get("eventId"));	
			Assert.assertEquals("Event ID is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("eventId")));
			// Verify Agent Phase ID
			planAgentResult=Configurations.parseResponse("situation","phaseId",GlobalVariables.testData.get("phaseId"));	
			Assert.assertEquals("Phase ID is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("phaseId")));
			// Verify Agent Anonymous
			planAgentResult=Configurations.parseResponse("task","name",GlobalVariables.testData.get("taskName"));	
			Assert.assertEquals("Task is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("taskName")));
			// Verify Agent Kind
			planAgentResult=Configurations.parseResponse("goal","kind",GlobalVariables.testData.get("taskKind"));	
			Assert.assertEquals("Task Kind is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("taskKind")));			
			System.out.println("5) Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

			// Execution Completed
			GlobalVariables.steps++;
			System.out.println("6) Test Case : REST002_ISPProcedures Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST002_ISPProceduresAboutAgent Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println("6)Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	public static void testISPProcedureEvents(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST002_ISPProceduresEvents";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println("1) Test Case : REST002_ISPProceduresEvents Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST002_ISPProceduresEvents Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST002_ISPProcedures.csv");
			System.out.println("2) Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/procedures";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println("3) Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println("4) Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: Verify that Procedure Events
			GlobalVariables.steps++;
			boolean planAgentResult;
			// Verify Event ID
			planAgentResult=Configurations.parseResponse("event","id",GlobalVariables.testData.get("environmentEventID"));	
			Assert.assertEquals("Event ID is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("environmentEventID")));
			// Verify Event Name
			planAgentResult=Configurations.parseResponse("event","name",GlobalVariables.testData.get("envirnomentName"));	
			Assert.assertEquals("Event Name is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("envirnomentName")));
			// Verify Incident
			planAgentResult=Configurations.parseResponse("event","incident",GlobalVariables.testData.get("environmentIncident"));	
			Assert.assertEquals("Incident is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("environmentIncident")));
			// Verify Agent Self Terminating
			planAgentResult=Configurations.parseResponse("event","selfTerminating",GlobalVariables.testData.get("environmentSelfTerminating"));	
			Assert.assertEquals("Is Event Self Terminating",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("environmentSelfTerminating")));
			// Verify Location ID
			planAgentResult=Configurations.parseResponse("event","locationId",GlobalVariables.testData.get("environmentLocationId"));	
			Assert.assertEquals("Location ID is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("environmentLocationId")));			
			// Verify Planned Phase ID
			planAgentResult=Configurations.parseResponse("event","plannedPhaseId",GlobalVariables.testData.get("environmentPlannedPhaseId"));	
			Assert.assertEquals("Planned Phase ID is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("environmentPlannedPhaseId")));						
			System.out.println("5) Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println("6) Test Case : REST002_ISPProceduresEvents Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST002_ISPProceduresAboutAgent Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println("5)Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
}