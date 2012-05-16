package com.mindalliance.testscripts;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.mindalliance.configurations.Configurations;
import com.mindalliance.configurations.GlobalVariables;
import com.mindalliance.configurations.LogFunctions;

public class REST002_ISPProcedures extends TestCase{
	/**
	 * Invoke testISPProceduresAboutAgent() and get plans, versions and planners identities.
	 */
	
	public static void testISPProceduresAboutAgent(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST002_ISPProceduresAboutAgent";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST002_ISPProceduresAboutAgent Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST002_ISPProceduresAboutAgent Execution Started");
			
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
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST002_ISPProceduresAboutAgent Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST002_ISPProceduresAboutAgent Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
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
			boolean planAgentResult;
			// Verify Agent ID
			planAgentResult=Configurations.parseResponse("procedure","agentId",GlobalVariables.testData.get("procedureAgentId"));	
			Assert.assertEquals("Agent ID is",true,planAgentResult);
			// Verify Agent Event ID
			planAgentResult=Configurations.parseResponse("situation","eventId",GlobalVariables.testData.get("situationEventId"));	
			Assert.assertEquals("Event ID is",true,planAgentResult);
			// Verify Agent Phase ID
			planAgentResult=Configurations.parseResponse("situation","phaseId",GlobalVariables.testData.get("situationPhaseId"));	
			Assert.assertEquals("Phase ID is",true,planAgentResult);
			// Verify Agent Anonymous
			planAgentResult=Configurations.parseResponse("task","name",GlobalVariables.testData.get("taskName"));	
			Assert.assertEquals("Task is",true,planAgentResult);
			// Verify Agent Kind
			planAgentResult=Configurations.parseResponse("goal","kind",GlobalVariables.testData.get("goalKind"));	
			Assert.assertEquals("goal Kind is",true,planAgentResult);			
			// Verify goal description
			planAgentResult=Configurations.parseResponse("goal","description",GlobalVariables.testData.get("goalDescription"));	
			Assert.assertEquals("Goal Description is",true,planAgentResult);			
			// Verify goal Category
//			planAgentResult=Configurations.parseResponse("goal","category",GlobalVariables.testData.get("goalCategory"));	
//			Assert.assertEquals("Goal Category is",planAgentResultgoalCategory")));			
			// Verify goal level
			planAgentResult=Configurations.parseResponse("goal","level",GlobalVariables.testData.get("goalLevel"));	
			Assert.assertEquals("Goal Level is",true,planAgentResult);			
			// Verify goal level
			planAgentResult=Configurations.parseResponse("goal","organizationId",GlobalVariables.testData.get("goalorganizationId"));	
			Assert.assertEquals("Goal organizationId is",true,planAgentResult);			
			// Verify failureImpact
			planAgentResult=Configurations.parseResponse("task","failureImpact",GlobalVariables.testData.get("taskFailureImpact"));	
			Assert.assertEquals("FailureImpact is",true,planAgentResult);			
			// Verify information Name
			planAgentResult=Configurations.parseResponse("information","name",GlobalVariables.testData.get("informationName"));	
			Assert.assertEquals("Information Name is",true,planAgentResult);			
			// Verify outNotification Intent
			planAgentResult=Configurations.parseResponse("outNotification","intent",GlobalVariables.testData.get("outNotificationIntent"));	
			Assert.assertEquals("OutNotification Inten is",true,planAgentResult);			
			// Verify outNotification Task Failed
			planAgentResult=Configurations.parseResponse("outNotification","taskFailed",GlobalVariables.testData.get("outNotificationTaskFailed"));	
			Assert.assertEquals("OutNotification Task Failed is",true,planAgentResult);			
			// Verify outNotification Receipt Confirmation Requested
			planAgentResult=Configurations.parseResponse("outNotification","receiptConfirmationRequested",GlobalVariables.testData.get("outNotificationReceiptConfirmationRequested"));	
			Assert.assertEquals("OutNotification Receipt Confirmation Requested is",true,planAgentResult);			
			// Verify outNotification ContactAll
			planAgentResult=Configurations.parseResponse("outNotification","contactAll",GlobalVariables.testData.get("outNotificationContactAll"));	
			Assert.assertEquals("OutNotification ContactAll is",true,planAgentResult);			
			// Verify outNotification failureImpact
			planAgentResult=Configurations.parseResponse("outNotification","failureImpact",GlobalVariables.testData.get("outNotificationFailureImpact"));	
			Assert.assertEquals("OutNotification failureImpact is",true,planAgentResult);			
			// Verify maxDelay unit
			planAgentResult=Configurations.parseResponse("maxDelay","amount",GlobalVariables.testData.get("maxDelayAmount"));	
			Assert.assertEquals("MaxDelay amount is",true,planAgentResult);			
			// Verify maxDelay Amount
			planAgentResult=Configurations.parseResponse("maxDelay","unit",GlobalVariables.testData.get("maxDelayUnit"));	
			Assert.assertEquals("MaxDelay unit is",true,planAgentResult);			
			// Verify maxDelay seconds
			planAgentResult=Configurations.parseResponse("maxDelay","seconds",GlobalVariables.testData.get("maxDelaySeconds"));	
			Assert.assertEquals("MaxDelay seconds is",true,planAgentResult);			
			// Verify identity name
			planAgentResult=Configurations.parseResponse("identity","name",GlobalVariables.testData.get("identityName"));	
			Assert.assertEquals("Identity Name is",true,planAgentResult);			
			// Verify identity organizationId
			planAgentResult=Configurations.parseResponse("identity","organizationId",GlobalVariables.testData.get("identityOrganizationId"));	
			Assert.assertEquals("Identity organizationId is",true,planAgentResult);			
			// Verify identity organizationId
			planAgentResult=Configurations.parseResponse("identity","title",GlobalVariables.testData.get("identityTitle"));	
			Assert.assertEquals("Identity title is",true,planAgentResult);			
			// Verify identity roleId
			planAgentResult=Configurations.parseResponse("identity","roleId",GlobalVariables.testData.get("identityRoleId"));	
			Assert.assertEquals("Identity roleId is",true,planAgentResult);			
			// Verify identity supervisorId
			planAgentResult=Configurations.parseResponse("identity","supervisorId",GlobalVariables.testData.get("identitySupervisorId"));	
			Assert.assertEquals("Identity SupervisorId is",true,planAgentResult);			
			// Verify identity confirmed
			planAgentResult=Configurations.parseResponse("identity","confirmed",GlobalVariables.testData.get("identityConfirmed"));	
			Assert.assertEquals("Identity confirmed is",true,planAgentResult);			
			// Verify workChannel MediumId
			planAgentResult=Configurations.parseResponse("workChannel","mediumId",GlobalVariables.testData.get("workChannelMediumId"));	
			Assert.assertEquals("WorkChannel MediumId is",true,planAgentResult);			
			// Verify workChannel medium
			planAgentResult=Configurations.parseResponse("workChannel","medium",GlobalVariables.testData.get("workChannelMedium"));	
			Assert.assertEquals("WorkChannel Medium is",true,planAgentResult);			
			// Verify workChannel address
			planAgentResult=Configurations.parseResponse("workChannel","address",GlobalVariables.testData.get("workChannelAddress"));	
			Assert.assertEquals("WorkChannel address is",true,planAgentResult);			
			// Verify organizationChannel MediumId
			planAgentResult=Configurations.parseResponse("organizationChannel","mediumId",GlobalVariables.testData.get("organizationChannelMediumId"));	
			Assert.assertEquals("Organization Channel MediumId is",true,planAgentResult);			
			// Verify organizationChannel medium
			planAgentResult=Configurations.parseResponse("organizationChannel","medium",GlobalVariables.testData.get("organizationChannelMedium"));	
			Assert.assertEquals("Organization Channel Medium is",true,planAgentResult);			
			// Verify organizationChannel address
			planAgentResult=Configurations.parseResponse("organizationChannel","address",GlobalVariables.testData.get("organizationChannelAddress"));	
			Assert.assertEquals("Organization Channel address is",true,planAgentResult);			
			// Verify consumingTask name
			planAgentResult=Configurations.parseResponse("consumingTask","name",GlobalVariables.testData.get("consumingTaskName"));	
			Assert.assertEquals("ConsumingTask Name is",true,planAgentResult);			
			// Verify consumingTask FailureImpact
			planAgentResult=Configurations.parseResponse("consumingTask","failureImpact",GlobalVariables.testData.get("consumingTaskFailureImpact"));	
			Assert.assertEquals("ConsumingTask Failure Impact is",true,planAgentResult);			
			System.out.println("GlobalVariables.steps +) Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST002_ISPProcedures Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST002_ISPProceduresAboutAgent Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	/**
	 * Invoke testISPProcedureEvents() and get Procedure Events 
	 */
	public static void testISPProcedureEvents(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST002_ISPProcedureEvents";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST002_ISPProcedureEvents Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST002_ISPProceduresEvents Execution Started");
			
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
			
			// Assertion: Verify that Procedure Events
			GlobalVariables.steps++;
			boolean planAgentResult;
			// Verify Event ID
			planAgentResult=Configurations.parseResponse("event","id",GlobalVariables.testData.get("environmentEventID"));	
			Assert.assertEquals("Event ID is",true,planAgentResult);
			// Verify Event Name
			planAgentResult=Configurations.parseResponse("event","name",GlobalVariables.testData.get("envirnomentName"));	
			Assert.assertEquals("Event Name is",true,planAgentResult);
			// Verify Incident
			planAgentResult=Configurations.parseResponse("event","incident",GlobalVariables.testData.get("environmentIncident"));	
			Assert.assertEquals("Incident is",true,planAgentResult);
			// Verify Agent Self Terminating
			planAgentResult=Configurations.parseResponse("event","selfTerminating",GlobalVariables.testData.get("environmentSelfTerminating"));	
			Assert.assertEquals("Is Event Self Terminating",true,planAgentResult);
			// Verify Location ID
			planAgentResult=Configurations.parseResponse("event","locationId",GlobalVariables.testData.get("environmentLocationId"));	
			Assert.assertEquals("Location ID is",true,planAgentResult);			
			// Verify Planned Phase ID
			planAgentResult=Configurations.parseResponse("event","plannedPhaseId",GlobalVariables.testData.get("environmentPlannedPhaseId"));	
			Assert.assertEquals("Planned Phase ID is",true,planAgentResult);						
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST002_ISPProceduresEvents Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST002_ISPProceduresAboutAgent Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	/**
	 * Invoke testISPProcedureEmployment() and get Procedure Employment
	 */
	public static void testISPProcedureEmployment(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST002_ISPProcedureEmployment";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST002_ISPProcedureEmployment Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST002_ISPProcedureEmployment Execution Started");
			
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
			System.out.println("GlobalVariables.steps +) Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(GlobalVariables.steps +") Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: Verify that Procedure Employment 
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
			// Verify employment Supervisor Id
			planAgentResult=Configurations.parseResponse("employment","supervisorId",GlobalVariables.testData.get("employmentSupervisorId"));	
			Assert.assertEquals("Employment Supervisor Id is",true,planAgentResult);			
			// Verify employment Confirmed
			planAgentResult=Configurations.parseResponse("employment","confirmed",GlobalVariables.testData.get("employmentConfirmed"));	
			Assert.assertEquals("Employment Confirmed is",true,planAgentResult);						
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST002_ISPProcedureEmployment Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST002_ISPProcedureEmployment Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	/**
	 * Invoke testISPProcedurePlan() and get plan
	 */
	public static void testISPProcedurePlan(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST002_ISPProcedurePlan";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST002_ISPProcedurePlan Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST002_ISPProcedurePlan Execution Started");
			
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
			
			// Assertion: verify that  procedure Plan
			GlobalVariables.steps++;
			boolean planIdentifierResult;
			// Verify URI
			planIdentifierResult=Configurations.parseResponse("plan","uri",GlobalVariables.testData.get("planUri"));	
			Assert.assertEquals("Plan Uri is",true,planIdentifierResult);
			// Verify Plan Name
			planIdentifierResult=Configurations.parseResponse("plan","name",GlobalVariables.testData.get("planName"));	
			Assert.assertEquals("Plan Name is",true,planIdentifierResult);
			// Verify Version
			planIdentifierResult=Configurations.parseResponse("plan","version",GlobalVariables.testData.get("planVersion"));
			Assert.assertEquals("Version is",true,planIdentifierResult);
			// Verify Release
			planIdentifierResult=Configurations.parseResponse("plan","release",GlobalVariables.testData.get("planRelease"));
			Assert.assertEquals("Plan release is",true,planIdentifierResult);
			// Verify Date Versioned
			planIdentifierResult=Configurations.parseResponse("plan","dateVersioned",GlobalVariables.testData.get("planDateVersioned"));
			Assert.assertEquals("Date Versioned is",true,planIdentifierResult);			
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST002_ISPProcedurePlan Execution Completed");
			System.out.println("");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST002_ISPProcedurePlan Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	/**
	 * Invoke testISPProcedureEnvironment() and get Environment
	 */
	public static void testISPProcedureEnvironment(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST002_ISPProcedureEnvironment";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST002_ISPProcedureEnvironment Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST002_ISPProcedureEnvironment Execution Started");
			
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
			
			// Assertion: verify that  procedure Event.
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
			// Verify Date Location id
			planIdentifierResult=Configurations.parseResponse("event","locationId",GlobalVariables.testData.get("eventLocationId"));
			Assert.assertEquals("Event location Id is",true,planIdentifierResult);			
			//Verify plannedPhaseId
			planIdentifierResult=Configurations.parseResponse("event","plannedPhaseId",GlobalVariables.testData.get("eventPlannedPhaseId"));
			Assert.assertEquals("Event planned Phase Id is",true,planIdentifierResult);			
			// Assertion: verify that  Environment phase
			//Verify Phase Id
			planIdentifierResult=Configurations.parseResponse("phase","id",GlobalVariables.testData.get("phaseId"));
			Assert.assertEquals("Phase Id is",true,planIdentifierResult);
			// Verify phase name
			planIdentifierResult=Configurations.parseResponse("phase","name",GlobalVariables.testData.get("phaseName"));
			Assert.assertEquals("phase name is",true,planIdentifierResult);			
			//Verify phase Timing
			planIdentifierResult=Configurations.parseResponse("phase","timing",GlobalVariables.testData.get("phaseTiming"));
			Assert.assertEquals("phase Timing is",true,planIdentifierResult);			
			// Assertion: verify that  Environment organization 
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
//			Assert.assertEquals("Event location Id is",planIdentifierResultdocumentsName")));			
			//Verify organization documents url
			planIdentifierResult=Configurations.parseResponse("documents","url",GlobalVariables.testData.get("documentsUrl"));
			Assert.assertEquals("organization documents url is",true,planIdentifierResult);			
			// Assertion: verify that  environment role
			//Verify role Id
			planIdentifierResult=Configurations.parseResponse("role","id",GlobalVariables.testData.get("roleId"));
			Assert.assertEquals("role Id is",true,planIdentifierResult);
			// Verify role name
			planIdentifierResult=Configurations.parseResponse("role","name",GlobalVariables.testData.get("roleName"));
			Assert.assertEquals("role name is",true,planIdentifierResult);			
			//Verify role categoryId
			planIdentifierResult=Configurations.parseResponse("role","categoryId",GlobalVariables.testData.get("roleCategoryId"));
			Assert.assertEquals("role categoryId is",true,planIdentifierResult);			
			// Assertion: verify that  environment place
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
			// Assertion: verify that  environment transmissionMedium
			//Verify transmissionMedium Name
			planIdentifierResult=Configurations.parseResponse("transmissionMedium","name",GlobalVariables.testData.get("transmissionMediumName"));
			Assert.assertEquals("transmissionMedium Name is",true,planIdentifierResult);
			// Verify transmissionMedium Id
			planIdentifierResult=Configurations.parseResponse("transmissionMedium","id",GlobalVariables.testData.get("transmissionMediumId"));
			Assert.assertEquals("transmissionMedium Id is",true,planIdentifierResult);			
			//Verify transmissionMedium Mode
			planIdentifierResult=Configurations.parseResponse("transmissionMedium","mode",GlobalVariables.testData.get("transmissionMediumMode"));
			Assert.assertEquals("transmissionMedium Mode is",true,planIdentifierResult);			
			//Verify transmissionMedium Synchronous
			planIdentifierResult=Configurations.parseResponse("transmissionMedium","synchronous",GlobalVariables.testData.get("transmissionMediumSynchronous"));
			Assert.assertEquals("transmissionMedium Synchronous is",true,planIdentifierResult);			
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST002_ISPProcedureEnvironment Execution Completed");
			System.out.println("");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST002_ISPProcedureEnvironment Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
}