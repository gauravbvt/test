package com.mindalliance.testscripts;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.mindalliance.configurations.Configurations;
import com.mindalliance.configurations.GlobalVariables;
import com.mindalliance.configurations.LogFunctions;

public class REST003_ISPAgents extends TestCase{
	/**
	 * Invoke testISPPlan() and get plans, versions and planners identities.
	 */
	static int step=0;
	public static void testISPAgents(){
		try {
			LogFunctions.generateLogsDirectory();	
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST003_ISPAgents";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(++step +") Test Case : REST003_ISPAgents Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST003_ISPAgents Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST003_ISPAgents.csv");
			System.out.println(++step +") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/procedures/agent/2587";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(++step +") Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(++step +") Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
								
			// Assertion: verify that Plan Agent
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
			System.out.println(++step +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(++step +") Test Case : REST003_ISPAgents Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : ISP001 Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(++step +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	/**
	 * Invoke testISPPlan() and get plans, versions and planners identities.
	 */
	public static void testISPPlan(){
		try {
			LogFunctions.generateLogsDirectory();	
			step=0;
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST003_ISPPlan";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(++step +") Test Case : REST003_ISPPlan Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST003_ISPPlan Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST003_ISPAgents.csv");
			System.out.println(++step +") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/procedures/agent/2587";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(++step +") Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(++step +") Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: verify that  procedure Plan
			GlobalVariables.steps++;
			boolean planIdentifierResult;
			// Verify URI
			planIdentifierResult=Configurations.parseResponse("plan","uri",GlobalVariables.testData.get("planUri"));	
			Assert.assertEquals("Plan Uri is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("planUri")));
			// Verify Plan Name
			planIdentifierResult=Configurations.parseResponse("plan","name",GlobalVariables.testData.get("planName"));	
			Assert.assertEquals("Plan Name is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("planName")));
			// Verify Version
			planIdentifierResult=Configurations.parseResponse("plan","version",GlobalVariables.testData.get("planVersion"));
			Assert.assertEquals("Version is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("planVersion")));
			// Verify Release
			planIdentifierResult=Configurations.parseResponse("plan","release",GlobalVariables.testData.get("planRelease"));
			Assert.assertEquals("Plan release is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("planRelease")));
			// Verify Date Versioned
			planIdentifierResult=Configurations.parseResponse("plan","dateVersioned",GlobalVariables.testData.get("planDateVersioned"));
			Assert.assertEquals("Date Versioned is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("planDateVersioned")));			
			System.out.println(++step +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(++step +") Test Case : REST003_ISPPlan Execution Completed");
			System.out.println("");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST003_ISPPlan Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println("5)Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	/**
	 * Invoke testISPProcedures() and get agent Procedures.
	 */
	public static void testISPProcedures(){
		try {
			LogFunctions.generateLogsDirectory();	
			step=0;
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST003_ISPProcedures";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(++step +") Test Case : REST003_ISPProcedures Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST003_ISPProcedures Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST003_ISPAgents.csv");
			System.out.println(++step +") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/procedures/agent/2587";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println("++step +) Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println("4) Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: verify that Plan procedure
			GlobalVariables.steps++;
			boolean planAgentResult;
			// Verify Agent ID
			planAgentResult=Configurations.parseResponse("procedure","agentId",GlobalVariables.testData.get("procedureAgentId"));	
			Assert.assertEquals("Agent ID is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("procedureAgentId")));
			// Verify Agent Event ID
			planAgentResult=Configurations.parseResponse("situation","eventId",GlobalVariables.testData.get("situationEventId"));	
			Assert.assertEquals("Event ID is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("situationEventId")));
			// Verify Agent Phase ID
			planAgentResult=Configurations.parseResponse("situation","phaseId",GlobalVariables.testData.get("situationPhaseId"));	
			Assert.assertEquals("Phase ID is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("situationPhaseId")));
			// Verify Agent Anonymous
			planAgentResult=Configurations.parseResponse("task","name",GlobalVariables.testData.get("taskName"));	
			Assert.assertEquals("Task is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("taskName")));
			// Verify Agent Kind
			planAgentResult=Configurations.parseResponse("goal","kind",GlobalVariables.testData.get("goalKind"));	
			Assert.assertEquals("goal Kind is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("goalKind")));			
			// Verify goal description
			planAgentResult=Configurations.parseResponse("goal","description",GlobalVariables.testData.get("goalDescription"));	
			Assert.assertEquals("Goal Description is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("goalDescription")));			
			// Verify goal Category
//			planAgentResult=Configurations.parseResponse("goal","category",GlobalVariables.testData.get("goalCategory"));	
//			Assert.assertEquals("Goal Category is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("goalCategory")));			
			// Verify goal level
			planAgentResult=Configurations.parseResponse("goal","level",GlobalVariables.testData.get("goalLevel"));	
			Assert.assertEquals("Goal Level is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("goalLevel")));			
			// Verify goal level
			planAgentResult=Configurations.parseResponse("goal","organizationId",GlobalVariables.testData.get("goalorganizationId"));	
			Assert.assertEquals("Goal organizationId is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("goalorganizationId")));			
			// Verify failureImpact
			planAgentResult=Configurations.parseResponse("task","failureImpact",GlobalVariables.testData.get("taskFailureImpact"));	
			Assert.assertEquals("FailureImpact is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("taskFailureImpact")));			
			// Verify information Name
			planAgentResult=Configurations.parseResponse("information","name",GlobalVariables.testData.get("informationName"));	
			Assert.assertEquals("Information Name is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("informationName")));			
			// Verify outNotification Intent
			planAgentResult=Configurations.parseResponse("outNotification","intent",GlobalVariables.testData.get("outNotificationIntent"));	
			Assert.assertEquals("OutNotification Inten is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("outNotificationIntent")));			
			// Verify outNotification Task Failed
			planAgentResult=Configurations.parseResponse("outNotification","taskFailed",GlobalVariables.testData.get("outNotificationTaskFailed"));	
			Assert.assertEquals("OutNotification Task Failed is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("outNotificationTaskFailed")));			
			// Verify outNotification Receipt Confirmation Requested
			planAgentResult=Configurations.parseResponse("outNotification","receiptConfirmationRequested",GlobalVariables.testData.get("outNotificationReceiptConfirmationRequested"));	
			Assert.assertEquals("OutNotification Receipt Confirmation Requested is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("outNotificationReceiptConfirmationRequested")));			
			// Verify outNotification ContactAll
			planAgentResult=Configurations.parseResponse("outNotification","contactAll",GlobalVariables.testData.get("outNotificationContactAll"));	
			Assert.assertEquals("OutNotification ContactAll is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("outNotificationContactAll")));			
			// Verify outNotification failureImpact
			planAgentResult=Configurations.parseResponse("outNotification","failureImpact",GlobalVariables.testData.get("outNotificationFailureImpact"));	
			Assert.assertEquals("OutNotification failureImpact is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("outNotificationFailureImpact")));			
			// Verify maxDelay unit
			planAgentResult=Configurations.parseResponse("maxDelay","amount",GlobalVariables.testData.get("maxDelayAmount"));	
			Assert.assertEquals("MaxDelay amount is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("maxDelayAmount")));			
			// Verify maxDelay Amount
			planAgentResult=Configurations.parseResponse("maxDelay","unit",GlobalVariables.testData.get("maxDelayUnit"));	
			Assert.assertEquals("MaxDelay unit is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("maxDelayUnit")));			
			// Verify maxDelay seconds
			planAgentResult=Configurations.parseResponse("maxDelay","seconds",GlobalVariables.testData.get("maxDelaySeconds"));	
			Assert.assertEquals("MaxDelay seconds is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("maxDelaySeconds")));			
			// Verify identity name
			planAgentResult=Configurations.parseResponse("identity","name",GlobalVariables.testData.get("identityName"));	
			Assert.assertEquals("Identity Name is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("identityName")));			
			// Verify identity organizationId
			planAgentResult=Configurations.parseResponse("identity","organizationId",GlobalVariables.testData.get("identityOrganizationId"));	
			Assert.assertEquals("Identity organizationId is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("identityOrganizationId")));			
			// Verify identity organizationId
			planAgentResult=Configurations.parseResponse("identity","title",GlobalVariables.testData.get("identityTitle"));	
			Assert.assertEquals("Identity title is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("identityTitle")));			
			// Verify identity roleId
			planAgentResult=Configurations.parseResponse("identity","roleId",GlobalVariables.testData.get("identityRoleId"));	
			Assert.assertEquals("Identity roleId is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("identityRoleId")));			
			// Verify identity supervisorId
			planAgentResult=Configurations.parseResponse("identity","supervisorId",GlobalVariables.testData.get("identitySupervisorId"));	
			Assert.assertEquals("Identity SupervisorId is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("identitySupervisorId")));			
			// Verify identity confirmed
			planAgentResult=Configurations.parseResponse("identity","confirmed",GlobalVariables.testData.get("identityConfirmed"));	
			Assert.assertEquals("Identity confirmed is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("identityConfirmed")));			
			// Verify workChannel MediumId
			planAgentResult=Configurations.parseResponse("workChannel","mediumId",GlobalVariables.testData.get("workChannelMediumId"));	
			Assert.assertEquals("WorkChannel MediumId is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("workChannelMediumId")));			
			// Verify workChannel medium
			planAgentResult=Configurations.parseResponse("workChannel","medium",GlobalVariables.testData.get("workChannelMedium"));	
			Assert.assertEquals("WorkChannel Medium is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("workChannelMedium")));			
			// Verify workChannel address
			planAgentResult=Configurations.parseResponse("workChannel","address",GlobalVariables.testData.get("workChannelAddress"));	
			Assert.assertEquals("WorkChannel address is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("workChannelAddress")));			
			// Verify organizationChannel MediumId
			planAgentResult=Configurations.parseResponse("organizationChannel","mediumId",GlobalVariables.testData.get("organizationChannelMediumId"));	
			Assert.assertEquals("Organization Channel MediumId is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("organizationChannelMediumId")));			
			// Verify organizationChannel medium
			planAgentResult=Configurations.parseResponse("organizationChannel","medium",GlobalVariables.testData.get("organizationChannelMedium"));	
			Assert.assertEquals("Organization Channel Medium is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("organizationChannelMedium")));			
			// Verify organizationChannel address
			planAgentResult=Configurations.parseResponse("organizationChannel","address",GlobalVariables.testData.get("organizationChannelAddress"));	
			Assert.assertEquals("Organization Channel address is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("organizationChannelAddress")));			
			// Verify consumingTask name
			planAgentResult=Configurations.parseResponse("consumingTask","name",GlobalVariables.testData.get("consumingTaskName"));	
			Assert.assertEquals("ConsumingTask Name is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("consumingTaskName")));			
			// Verify consumingTask FailureImpact
			planAgentResult=Configurations.parseResponse("consumingTask","failureImpact",GlobalVariables.testData.get("consumingTaskFailureImpact"));	
			Assert.assertEquals("ConsumingTask Failure Impact is",planAgentResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("consumingTaskFailureImpact")));			
			System.out.println(++step +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(++step +") Test Case : REST003_ISPProcedures Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST003_ISPProcedures Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(++step +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	/**
	 * Invoke testISPEnvironment() and get agent Environment
	 */
	public static void testISPEnvironment(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST003_ISPEnvironment";
			step=0;
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(++step +") Test Case : REST003_ISPEnvironment Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST003_ISPEnvironment Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST003_ISPAgents.csv");
			System.out.println(++step +") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");
            // int agentId=
			// Send Request
			String data =GlobalVariables.testData.get("api")+"/procedures/agent/2587";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(++step +") Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(++step +") Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: verify that  Environment
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
			// Verify Date Location id
			planIdentifierResult=Configurations.parseResponse("event","locationId",GlobalVariables.testData.get("eventLocationId"));
			Assert.assertEquals("Event location Id is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("eventLocationId")));			
			//Verify plannedPhaseId
			planIdentifierResult=Configurations.parseResponse("event","plannedPhaseId",GlobalVariables.testData.get("eventPlannedPhaseId"));
			Assert.assertEquals("Event planned Phase Id is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("eventPlannedPhaseId")));			
			// Assertion: verify that  Environment phase
			//Verify Phase Id
			planIdentifierResult=Configurations.parseResponse("phase","id",GlobalVariables.testData.get("phaseId"));
			Assert.assertEquals("Phase Id is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("phaseId")));
			// Verify phase name
			planIdentifierResult=Configurations.parseResponse("phase","name",GlobalVariables.testData.get("phaseName"));
			Assert.assertEquals("phase name is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("phaseName")));			
			//Verify phase Timing
			planIdentifierResult=Configurations.parseResponse("phase","timing",GlobalVariables.testData.get("phaseTiming"));
			Assert.assertEquals("phase Timing is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("phaseTiming")));			
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
//			planIdentifierResult=Configurations.parseResponse("documents","name",GlobalVariables.testData.get("documentsName"));
//			Assert.assertEquals("Event location Id is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("documentsName")));			
			//Verify organization documents url
			planIdentifierResult=Configurations.parseResponse("documents","url",GlobalVariables.testData.get("documentsUrl"));
			Assert.assertEquals("organization documents url is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("documentsUrl")));			
			// Assertion: verify that  environment role
			//Verify role Id
			planIdentifierResult=Configurations.parseResponse("role","id",GlobalVariables.testData.get("roleId"));
			Assert.assertEquals("role Id is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("roleId")));
			// Verify role name
			planIdentifierResult=Configurations.parseResponse("role","name",GlobalVariables.testData.get("roleName"));
			Assert.assertEquals("role name is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("roleName")));			
			//Verify role categoryId
			planIdentifierResult=Configurations.parseResponse("role","categoryId",GlobalVariables.testData.get("roleCategoryId"));
			Assert.assertEquals("role categoryId is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("roleCategoryId")));			
			// Assertion: verify that  environment place
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
			// Assertion: verify that  environment transmissionMedium
			//Verify transmissionMedium Name
			planIdentifierResult=Configurations.parseResponse("transmissionMedium","name",GlobalVariables.testData.get("transmissionMediumName"));
			Assert.assertEquals("transmissionMedium Name is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("transmissionMediumName")));
			// Verify transmissionMedium Id
			planIdentifierResult=Configurations.parseResponse("transmissionMedium","id",GlobalVariables.testData.get("transmissionMediumId"));
			Assert.assertEquals("transmissionMedium Id is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("transmissionMediumId")));			
			//Verify transmissionMedium Mode
			planIdentifierResult=Configurations.parseResponse("transmissionMedium","mode",GlobalVariables.testData.get("transmissionMediumMode"));
			Assert.assertEquals("transmissionMedium Mode is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("transmissionMediumMode")));			
			//Verify transmissionMedium Synchronous
			planIdentifierResult=Configurations.parseResponse("transmissionMedium","synchronous",GlobalVariables.testData.get("transmissionMediumSynchronous"));
			Assert.assertEquals("transmissionMedium Synchronous is",planIdentifierResult,GlobalVariables.responseString.contains(GlobalVariables.testData.get("transmissionMediumSynchronous")));			
			System.out.println(++step +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");

			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(++step +") Test Case : REST003_ISPEnvironment Execution Completed");
			System.out.println("");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST003_ISPEnvironment Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(++step +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
}