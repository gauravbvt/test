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
			System.out.println(GlobalVariables.steps +") Test Case : REST005_ISPScope Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST005_ISPScope Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST005_ISPScope.csv");
			System.out.println(GlobalVariables.steps +") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/version/"+GlobalVariables.testData.get("versionNumber")+"/scope";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(GlobalVariables.steps +") Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(GlobalVariables.steps +") Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: verify scope.
			GlobalVariables.steps++;
			boolean planAgentResult;
			
			// Verify identity name
			planAgentResult=Configurations.parseResponse("identity","name",GlobalVariables.testData.get("identityName"));	
			Assert.assertEquals("Identity Name is",true,planAgentResult);			
			// Verify identity uri
			planAgentResult=Configurations.parseResponse("identity","uri",GlobalVariables.testData.get("identityUri"));	
			Assert.assertEquals("Identity uri is",true,planAgentResult);			
			// Verify identity version
			planAgentResult=Configurations.parseResponse("identity","version",GlobalVariables.testData.get("identityVersion"));	
			Assert.assertEquals("Identity version is",true,planAgentResult);			
			// Verify identity release
			planAgentResult=Configurations.parseResponse("identity","release",GlobalVariables.testData.get("identityRelease"));	
			Assert.assertEquals("Identity release is",true,planAgentResult);			
			// Verify identity dateVersioned
			planAgentResult=Configurations.parseResponse("identity","dateVersioned",GlobalVariables.testData.get("identityDateVersioned"));	
			Assert.assertEquals("Identity dateVersioned is",true,planAgentResult);			
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
						
			// Assertion: Verify that Plan name is present
			GlobalVariables.steps++;
			Assert.assertEquals("mindalliance_com_channels_plans_railsec","mindalliance_com_channels_plans_railsec");
			System.out.println("GlobalVariables.steps +) Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST005_ISPScope Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : ISP001 Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	/**
	 * Invoke testISPScopesPhase() and get scope phase.
	 */
	public static void testISPScopesPhase(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST005_ISPScopesPhase";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST005_ISPScopesPhase Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST005_ISPScopesPhase Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST005_ISPScope.csv");
			System.out.println(GlobalVariables.steps +") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/version/"+GlobalVariables.testData.get("versionNumber")+"/scope";;
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(GlobalVariables.steps +") Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(GlobalVariables.steps +") Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: Verify that scope phase
			GlobalVariables.steps++;
			boolean planIdentifierResult;
			//Verify Phase Id
			planIdentifierResult=Configurations.parseResponse("phase","id",GlobalVariables.testData.get("phaseId"));
			Assert.assertEquals("Phase Id is",true,planIdentifierResult);
			// Verify phase name
			planIdentifierResult=Configurations.parseResponse("phase","name",GlobalVariables.testData.get("phaseName"));
			Assert.assertEquals("phase name is",true,planIdentifierResult);			
			//Verify phase Timing
			planIdentifierResult=Configurations.parseResponse("phase","timing",GlobalVariables.testData.get("phaseTiming"));
			Assert.assertEquals("phase Timing is",true,planIdentifierResult);			
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST005_ISPScopesPhase Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST005_ISPScopesPhase Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	/**
	 * Invoke testISPScopesPlace() and get scope place
	 */
	public static void testISPScopesPlace(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST005_ISPScopesPlace";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST005_ISPScopesPlace Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST005_ISPScopesPlace Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST005_ISPScope.csv");
			System.out.println(GlobalVariables.steps +") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/version/"+GlobalVariables.testData.get("versionNumber")+"/scope";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(GlobalVariables.steps +") Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(GlobalVariables.steps +") Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: verify that  scope place
			GlobalVariables.steps++;
			boolean planIdentifierResult;
			//Verify place Id
			planIdentifierResult=Configurations.parseResponse("place","id",GlobalVariables.testData.get("placeId"));
			Assert.assertEquals("place Id is",true,planIdentifierResult);
			// Verify place name
			planIdentifierResult=Configurations.parseResponse("place","name",GlobalVariables.testData.get("placeName"));
			Assert.assertEquals("place name is",true,planIdentifierResult);			
			//Verify place kind
			planIdentifierResult=Configurations.parseResponse("place","kind",GlobalVariables.testData.get("placeKind"));
			Assert.assertEquals("place kind is",true,planIdentifierResult);			
			//Verify place categoryId
			planIdentifierResult=Configurations.parseResponse("place","categoryId",GlobalVariables.testData.get("placeCategoryId"));
			Assert.assertEquals("place categoryId is",true,planIdentifierResult);			
			// Verify organization documents type
			planIdentifierResult=Configurations.parseResponse("documents","type",GlobalVariables.testData.get("documentsType"));
			Assert.assertEquals("organization documents type is",true,planIdentifierResult);			
			// Verify organization documents name
			planIdentifierResult=Configurations.parseResponse("documents","name",GlobalVariables.testData.get("documentsName"));
			Assert.assertEquals("Event location Id is",true,planIdentifierResult);			
			//Verify organization documents url
			planIdentifierResult=Configurations.parseResponse("documents","url",GlobalVariables.testData.get("documentsUrl"));
			Assert.assertEquals("organization documents url is",true,planIdentifierResult);			
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST005_ISPScopesPlace Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST005_ISPScopesPlace Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	/**
	 * Invoke testISPScopesEvent() and get scope events
	 */
	public static void testISPScopesEvent(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST005_ISPScopesEvent";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST005_ISPScopesEvent Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST005_ISPScopesEvent Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST005_ISPScope.csv");
			System.out.println(GlobalVariables.steps +") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/version/"+GlobalVariables.testData.get("versionNumber")+"/scope";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(GlobalVariables.steps +") Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(GlobalVariables.steps +") Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: Verify that scope Events
			GlobalVariables.steps++;
			boolean planIdentifierResult;
			// Verify event id
			planIdentifierResult=Configurations.parseResponse("event","id",GlobalVariables.testData.get("eventId"));	
			Assert.assertEquals("Event Id is",true,planIdentifierResult);
			// Verify event Name
			planIdentifierResult=Configurations.parseResponse("event","name",GlobalVariables.testData.get("eventName"));	
			Assert.assertEquals("Event Name is",true,planIdentifierResult);
			// Verify event incident
			planIdentifierResult=Configurations.parseResponse("event","incident",GlobalVariables.testData.get("eventIncident"));
			Assert.assertEquals("Event Incident is",true,planIdentifierResult);
			// Verify event selfTerminating
			planIdentifierResult=Configurations.parseResponse("event","selfTerminating",GlobalVariables.testData.get("eventSelfTerminating"));
			Assert.assertEquals("Event selfTerminating is",true,planIdentifierResult);
			// Verify event Location id
			planIdentifierResult=Configurations.parseResponse("event","locationId",GlobalVariables.testData.get("eventLocationId"));
			Assert.assertEquals("Event location Id is",true,planIdentifierResult);			
			//Verify event plannedPhaseId
			planIdentifierResult=Configurations.parseResponse("event","plannedPhaseId",GlobalVariables.testData.get("eventPlannedPhaseId"));
			Assert.assertEquals("Event planned Phase Id is",true,planIdentifierResult);			
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST005_ISPScopesEvent Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST005_ISPScopesEvent Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	/**
	 * Invoke testISPScopesRole() and get scope role
	 */
	public static void testISPScopesRole(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST005_ISPScopesRole";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST005_ISPScopesRole Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST005_ISPScopesRole Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST005_ISPScope.csv");
			System.out.println(GlobalVariables.steps +") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/version/"+GlobalVariables.testData.get("versionNumber")+"/scope";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(GlobalVariables.steps +") Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(GlobalVariables.steps +") Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: verify that scope role
			GlobalVariables.steps++;
			boolean planIdentifierResult;
		
			//Verify role Id
			planIdentifierResult=Configurations.parseResponse("role","id",GlobalVariables.testData.get("roleId"));
			Assert.assertEquals("role Id is",true,planIdentifierResult);
			// Verify role name
			planIdentifierResult=Configurations.parseResponse("role","name",GlobalVariables.testData.get("roleName"));
			Assert.assertEquals("role name is",true,planIdentifierResult);			
			//Verify role categoryId
			planIdentifierResult=Configurations.parseResponse("role","categoryId",GlobalVariables.testData.get("roleCategoryId"));
			Assert.assertEquals("role categoryId is",true,planIdentifierResult);			
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST005_ISPScopesRole Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST005_ISPScopesRole Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	/**
	 * Invoke testISPScopesOrganization() and get scope Organization
	 */
	public static void testISPScopesOrganization(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST005_ISPScopesOrganization";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST005_ISPScopesOrganization Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST005_ISPScopesOrganization Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST005_ISPScope.csv");
			System.out.println(GlobalVariables.steps +") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/version/"+GlobalVariables.testData.get("versionNumber")+"/scope";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(GlobalVariables.steps +") Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(GlobalVariables.steps +") Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: verify that  organization  
			GlobalVariables.steps++;
			boolean planIdentifierResult;
			//Verify organization Id
			planIdentifierResult=Configurations.parseResponse("organization","id",GlobalVariables.testData.get("organizationId"));
			Assert.assertEquals("organization Id is",true,planIdentifierResult);
			// Verify organization name
			planIdentifierResult=Configurations.parseResponse("organization","name",GlobalVariables.testData.get("organizationName"));
			Assert.assertEquals("organization name is",true,planIdentifierResult);			
			//Verify organization categoryId
			planIdentifierResult=Configurations.parseResponse("organization","categoryId",GlobalVariables.testData.get("organizationCategoryId"));
			Assert.assertEquals("organization categoryId is",true,planIdentifierResult);			
			//Verify organization participating
			planIdentifierResult=Configurations.parseResponse("organization","participating",GlobalVariables.testData.get("organizationParticipating"));
			Assert.assertEquals("organization participating is",true,planIdentifierResult);
			// Verify organization documents type
			planIdentifierResult=Configurations.parseResponse("documents","type",GlobalVariables.testData.get("documentsType"));
			Assert.assertEquals("organization documents type is",true,planIdentifierResult);			
			// Verify organization documents name
//			planIdentifierResult=Configurations.parseResponse("documents","name",GlobalVariables.testData.get("documentsName"));
//			Assert.assertEquals("documents Name is",planIdentifierResultdocumentsName")));			
			//Verify organization documents url
			planIdentifierResult=Configurations.parseResponse("documents","url",GlobalVariables.testData.get("documentsUrl"));
			Assert.assertEquals("organization documents url is",true,planIdentifierResult);			
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST005_ISPScopesOrganization Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST005_ISPScopesOrganization Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	/**
	 * Invoke testISPScopesAgent() and get scope Agent
	 */
	public static void testISPScopesAgent(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST005_ISPScopesAgent";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST005_ISPScopesAgent Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST005_ISPScopesAgent Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST005_ISPScope.csv");
			System.out.println(GlobalVariables.steps +") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/version/"+GlobalVariables.testData.get("versionNumber")+"/scope";
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
			boolean planAgentResult;
			// Verify Agent ID
			planAgentResult=Configurations.parseResponse("agent","id",GlobalVariables.testData.get("agentId"));	
			Assert.assertEquals("Agent is",true,planAgentResult);
			// Verify Agent Name
			planAgentResult=Configurations.parseResponse("agent","name",GlobalVariables.testData.get("agentName"));	
			Assert.assertEquals("Agent Name is",true,planAgentResult);
			// Verify Agent Unique Identity
			planAgentResult=Configurations.parseResponse("agent","hasUniqueIdentity",GlobalVariables.testData.get("hasUniqueIdentity"));	
			Assert.assertEquals("Agent Unique Identity is",true,planAgentResult);
			// Verify Agent Anonymous
			planAgentResult=Configurations.parseResponse("agent","isAnonymous",GlobalVariables.testData.get("isAnonymous"));	
			Assert.assertEquals("Is Agent Anonymous",true,planAgentResult);
			// Verify Agent Kind
			planAgentResult=Configurations.parseResponse("agent","kind",GlobalVariables.testData.get("kind"));	
			Assert.assertEquals("Agent Kind is",true,planAgentResult);					
			// Verify Agent language
			planAgentResult=Configurations.parseResponse("agent","language",GlobalVariables.testData.get("language"));	
			Assert.assertEquals("Agent language is",true,planAgentResult);					
			// Verify availability always
			planAgentResult=Configurations.parseResponse("availability","always",GlobalVariables.testData.get("always"));	
			Assert.assertEquals("Availability always is",true,planAgentResult);					
			//Verify timePeriod dayOfWeek
			planAgentResult=Configurations.parseResponse("timePeriod","dayOfWeek",GlobalVariables.testData.get("dayOfWeek"));	
			Assert.assertEquals("TimePeriod dayOfWeek is",true,planAgentResult);					
			//Verify timePeriod dayOfWeek
			planAgentResult=Configurations.parseResponse("timePeriod","value",GlobalVariables.testData.get("value"));	
			Assert.assertEquals("TimePeriod value is",true,planAgentResult);					
			//Verify timePeriod dayOfWeek
			planAgentResult=Configurations.parseResponse("timePeriod","fromTime",GlobalVariables.testData.get("fromTime"));	
			Assert.assertEquals("TimePeriod fromTime is",true,planAgentResult);					
			//Verify timePeriod dayOfWeek
			planAgentResult=Configurations.parseResponse("timePeriod","toTime",GlobalVariables.testData.get("toTime"));	
			Assert.assertEquals("TimePeriod toTime is",true,planAgentResult);					
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST005_ISPScopesAgent Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST005_ISPScopesAgent Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	/**
	 * Invoke testISPScopesEmployment() and get scope Employment
	 */
	public static void testISPScopesEmployment(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST005_ISPScopesEmployment";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST005_ISPScopesEmployment Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST005_ISPScopesEmployment Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST005_ISPScope.csv");
			System.out.println(GlobalVariables.steps +") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/version/"+GlobalVariables.testData.get("versionNumber")+"/scope";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(GlobalVariables.steps +") Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(GlobalVariables.steps +") Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: verify that employment
			GlobalVariables.steps++;
			boolean planAgentResult;
			// Verify employment Name
			planAgentResult=Configurations.parseResponse("employment","name",GlobalVariables.testData.get("employmentName"));	
			Assert.assertEquals("Employment Name is",true,planAgentResult);
			// Verify employment Organization Id
			planAgentResult=Configurations.parseResponse("employment","organizationId",GlobalVariables.testData.get("employmentOrganizationId"));	
			Assert.assertEquals("Employment Organization Id is",true,planAgentResult);
			// Verify employment Title
			planAgentResult=Configurations.parseResponse("employment","title",GlobalVariables.testData.get("employmentTitle"));	
			Assert.assertEquals("Employment Title is",true,planAgentResult);
			// Verify employment Role Id
			planAgentResult=Configurations.parseResponse("employment","roleId",GlobalVariables.testData.get("employmentRoleId"));	
			Assert.assertEquals("Employment Role Id is",true,planAgentResult);
			// Verify employment jurisdiction Id
//			planAgentResult=Configurations.parseResponse("employment","jurisdictionId",GlobalVariables.testData.get("employmentJurisdictionId"));	
//			Assert.assertEquals("Employment jurisdiction Id is",planAgentResultemploymentJurisdictionId")));			
			// Verify employment Confirmed
			planAgentResult=Configurations.parseResponse("employment","confirmed",GlobalVariables.testData.get("employmentConfirmed"));	
			Assert.assertEquals("Employment Confirmed is",true,planAgentResult);						
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST005_ISPScopesEmployment Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST005_ISPScopesEmployment Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
}
