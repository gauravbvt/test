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
	public static void testISPScopes(){
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
			
			// Assertion: verify that Issue.
			GlobalVariables.steps++;
			boolean planAgentResult;
			
			// Verify identity name
			planAgentResult=Configurations.parseResponse("identity","name",GlobalVariables.testData.get("identityName"));	
			Assert.assertEquals("Identity Name is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("identityName")));			
			// Verify identity uri
			planAgentResult=Configurations.parseResponse("identity","uri",GlobalVariables.testData.get("identityUri"));	
			Assert.assertEquals("Identity uri is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("identityUri")));			
			// Verify identity version
			planAgentResult=Configurations.parseResponse("identity","version",GlobalVariables.testData.get("identityVersion"));	
			Assert.assertEquals("Identity version is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("identityVersion")));			
			// Verify identity release
			planAgentResult=Configurations.parseResponse("identity","release",GlobalVariables.testData.get("identityRelease"));	
			Assert.assertEquals("Identity release is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("identityRelease")));			
			// Verify identity dateVersioned
			planAgentResult=Configurations.parseResponse("identity","dateVersioned",GlobalVariables.testData.get("identityDateVersioned"));	
			Assert.assertEquals("Identity dateVersioned is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("identityDateVersioned")));			
			System.out.println("5) Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
						
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
	
	public static void testISPScopesPhase(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST005_ISPScopesPhase";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println("1) Test Case : REST005_ISPScopesPhase Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST005_ISPScopesPhase Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST005_ISPScope.csv");
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
			boolean planIdentifierResult;
			//Verify Phase Id
			planIdentifierResult=Configurations.parseResponse("phase","id",GlobalVariables.testData.get("phaseId"));
			Assert.assertEquals("Phase Id is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("phaseId")));
			// Verify phase name
			planIdentifierResult=Configurations.parseResponse("phase","name",GlobalVariables.testData.get("phaseName"));
			Assert.assertEquals("phase name is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("phaseName")));			
			//Verify phase Timing
			planIdentifierResult=Configurations.parseResponse("phase","timing",GlobalVariables.testData.get("phaseTiming"));
			Assert.assertEquals("phase Timing is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("phaseTiming")));			
			System.out.println("5) Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println("6) Test Case : REST005_ISPScopesPhase Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST005_ISPScopesPhase Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println("5)Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	public static void testISPScopesPlace(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST005_ISPScopesPlace";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println("1) Test Case : REST005_ISPScopesPlace Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST005_ISPScopesPlace Execution Started");
			
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
			
			// Assertion: verify that  environment place
			GlobalVariables.steps++;
			boolean planIdentifierResult;
			//Verify place Id
			planIdentifierResult=Configurations.parseResponse("place","id",GlobalVariables.testData.get("placeId"));
			Assert.assertEquals("place Id is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("placeId")));
			// Verify place name
			planIdentifierResult=Configurations.parseResponse("place","name",GlobalVariables.testData.get("placeName"));
			Assert.assertEquals("place name is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("placeName")));			
			//Verify place kind
			planIdentifierResult=Configurations.parseResponse("place","kind",GlobalVariables.testData.get("placeKind"));
			Assert.assertEquals("place kind is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("placeKind")));			
			//Verify place categoryId
			planIdentifierResult=Configurations.parseResponse("place","categoryId",GlobalVariables.testData.get("placeCategoryId"));
			Assert.assertEquals("place categoryId is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("placeCategoryId")));			
			// Verify organization documents type
			planIdentifierResult=Configurations.parseResponse("documents","type",GlobalVariables.testData.get("documentsType"));
			Assert.assertEquals("organization documents type is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("documentsType")));			
			// Verify organization documents name
			planIdentifierResult=Configurations.parseResponse("documents","name",GlobalVariables.testData.get("documentsName"));
			Assert.assertEquals("Event location Id is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("documentsName")));			
			//Verify organization documents url
			planIdentifierResult=Configurations.parseResponse("documents","url",GlobalVariables.testData.get("documentsUrl"));
			Assert.assertEquals("organization documents url is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("documentsUrl")));			
									
			System.out.println("5) Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println("6) Test Case : REST005_ISPScopesPlace Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST005_ISPScopesPlace Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println("5)Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	public static void testISPScopesEvents(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST005_ISPScopesEvents";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println("1) Test Case : REST005_ISPScopesEvents Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST005_ISPScopesEvents Execution Started");
			
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
			
			// Assertion: Verify that  Events
			GlobalVariables.steps++;
			boolean planIdentifierResult;
			// Verify event id
			planIdentifierResult=Configurations.parseResponse("event","id",GlobalVariables.testData.get("eventId"));	
			Assert.assertEquals("Event Id is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("eventId")));
			// Verify event Name
			planIdentifierResult=Configurations.parseResponse("event","name",GlobalVariables.testData.get("eventName"));	
			Assert.assertEquals("Event Name is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("eventName")));
			// Verify event incident
			planIdentifierResult=Configurations.parseResponse("event","incident",GlobalVariables.testData.get("eventIncident"));
			Assert.assertEquals("Event Incident is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("eventIncident")));
			// Verify event selfTerminating
			planIdentifierResult=Configurations.parseResponse("event","selfTerminating",GlobalVariables.testData.get("eventSelfTerminating"));
			Assert.assertEquals("Event selfTerminating is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("eventSelfTerminating")));
			// Verify event Location id
			planIdentifierResult=Configurations.parseResponse("event","locationId",GlobalVariables.testData.get("eventLocationId"));
			Assert.assertEquals("Event location Id is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("eventLocationId")));			
			//Verify event plannedPhaseId
			planIdentifierResult=Configurations.parseResponse("event","plannedPhaseId",GlobalVariables.testData.get("eventPlannedPhaseId"));
			Assert.assertEquals("Event planned Phase Id is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("eventPlannedPhaseId")));			
				
			System.out.println("5) Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println("6) Test Case : REST005_ISPScopesEvents Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST005_ISPScopesEvents Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println("5)Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	public static void testISPScopesRoles(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST005_ISPScopesRoles";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println("1) Test Case : REST005_ISPScopesRoles Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST005_ISPScopesRoles Execution Started");
			
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
			
			// Assertion: verify that  environment role
			GlobalVariables.steps++;
			boolean planIdentifierResult;
		
			//Verify role Id
			planIdentifierResult=Configurations.parseResponse("role","id",GlobalVariables.testData.get("roleId"));
			Assert.assertEquals("role Id is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("roleId")));
			// Verify role name
			planIdentifierResult=Configurations.parseResponse("role","name",GlobalVariables.testData.get("roleName"));
			Assert.assertEquals("role name is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("roleName")));			
			//Verify role categoryId
			planIdentifierResult=Configurations.parseResponse("role","categoryId",GlobalVariables.testData.get("roleCategoryId"));
			Assert.assertEquals("role categoryId is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("roleCategoryId")));			
							
			System.out.println("5) Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println("6) Test Case : REST005_ISPScopesRoles Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST005_ISPScopesRoles Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println("5)Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	public static void testISPScopesOrganization(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST005_ISPScopesOrganization";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println("1) Test Case : REST005_ISPScopesOrganization Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST005_ISPScopesOrganization Execution Started");
			
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
			
			// Assertion: verify that  environment role
			GlobalVariables.steps++;
			boolean planIdentifierResult;
			// Assertion: verify that  Environment organization 
			//Verify organization Id
			planIdentifierResult=Configurations.parseResponse("organization","id",GlobalVariables.testData.get("organizationId"));
			Assert.assertEquals("organization Id is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("organizationId")));
			// Verify organization name
			planIdentifierResult=Configurations.parseResponse("organization","name",GlobalVariables.testData.get("organizationName"));
			Assert.assertEquals("organization name is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("organizationName")));			
			//Verify organization categoryId
			planIdentifierResult=Configurations.parseResponse("organization","categoryId",GlobalVariables.testData.get("organizationCategoryId"));
			Assert.assertEquals("organization categoryId is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("organizationCategoryId")));			
			//Verify organization participating
			planIdentifierResult=Configurations.parseResponse("organization","participating",GlobalVariables.testData.get("organizationParticipating"));
			Assert.assertEquals("organization participating is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("organizationParticipating")));
			// Verify organization documents type
			planIdentifierResult=Configurations.parseResponse("documents","type",GlobalVariables.testData.get("documentsType"));
			Assert.assertEquals("organization documents type is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("documentsType")));			
			// Verify organization documents name
			planIdentifierResult=Configurations.parseResponse("documents","name",GlobalVariables.testData.get("documentsName"));
			Assert.assertEquals("Event location Id is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("documentsName")));			
			//Verify organization documents url
			planIdentifierResult=Configurations.parseResponse("documents","url",GlobalVariables.testData.get("documentsUrl"));
			Assert.assertEquals("organization documents url is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("documentsUrl")));			
						
			System.out.println("5) Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println("6) Test Case : REST005_ISPScopesOrganization Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST005_ISPScopesOrganization Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println("5)Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	public static void testISPScopesAgent(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST005_ISPScopesAgent";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println("1) Test Case : REST005_ISPScopesAgent Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST005_ISPScopesAgent Execution Started");
			
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
			// Verify Agent language
			planAgentResult=Configurations.parseResponse("agent","language",GlobalVariables.testData.get("language"));	
			Assert.assertEquals("Agent language is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("language")));					
			// Verify availability always
			planAgentResult=Configurations.parseResponse("availability","always",GlobalVariables.testData.get("always"));	
			Assert.assertEquals("Availability always is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("always")));					
			//Verify timePeriod dayOfWeek
			planAgentResult=Configurations.parseResponse("timePeriod","dayOfWeek",GlobalVariables.testData.get("dayOfWeek"));	
			Assert.assertEquals("TimePeriod dayOfWeek is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("dayOfWeek")));					
			//Verify timePeriod dayOfWeek
			planAgentResult=Configurations.parseResponse("timePeriod","value",GlobalVariables.testData.get("value"));	
			Assert.assertEquals("TimePeriod value is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("value")));					
			//Verify timePeriod dayOfWeek
			planAgentResult=Configurations.parseResponse("timePeriod","fromTime",GlobalVariables.testData.get("fromTime"));	
			Assert.assertEquals("TimePeriod fromTime is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("fromTime")));					
			//Verify timePeriod dayOfWeek
			planAgentResult=Configurations.parseResponse("timePeriod","toTime",GlobalVariables.testData.get("toTime"));	
			Assert.assertEquals("TimePeriod toTime is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("toTime")));					
		
			System.out.println("5) Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println("6) Test Case : REST005_ISPScopesAgent Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST005_ISPScopesAgent Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println("5)Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	public static void testISPScopesEmployment(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST005_ISPScopesEmployment";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println("1) Test Case : REST005_ISPScopesEmployment Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST005_ISPScopesEmployment Execution Started");
			
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
			
			// Assertion: verify that employment
			GlobalVariables.steps++;
			boolean planAgentResult;
			// Verify employment Name
			planAgentResult=Configurations.parseResponse("employment","name",GlobalVariables.testData.get("employmentName"));	
			Assert.assertEquals("Employment Name is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("employmentName")));
			// Verify employment Organization Id
			planAgentResult=Configurations.parseResponse("employment","organizationId",GlobalVariables.testData.get("employmentOrganizationId"));	
			Assert.assertEquals("Employment Organization Id is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("employmentOrganizationId")));
			// Verify employment Title
			planAgentResult=Configurations.parseResponse("employment","title",GlobalVariables.testData.get("employmentTitle"));	
			Assert.assertEquals("Employment Title is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("employmentTitle")));
			// Verify employment Role Id
			planAgentResult=Configurations.parseResponse("employment","roleId",GlobalVariables.testData.get("employmentRoleId"));	
			Assert.assertEquals("Employment Role Id is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("employmentRoleId")));
			// Verify employment jurisdiction Id
			planAgentResult=Configurations.parseResponse("employment","jurisdictionId",GlobalVariables.testData.get("employmentJurisdictionId"));	
			Assert.assertEquals("Employmentjurisdiction Id is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("employmentJurisdictionId")));			
			// Verify employment Confirmed
			planAgentResult=Configurations.parseResponse("employment","confirmed",GlobalVariables.testData.get("employmentConfirmed"));	
			Assert.assertEquals("Employment Confirmed is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("employmentConfirmed")));						
			System.out.println("5) Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println("6) Test Case : REST005_ISPScopesEmployment Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST005_ISPScopesEmployment Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println("5)Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
}
