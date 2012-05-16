package com.mindalliance.testscripts;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.mindalliance.configurations.Configurations;
import com.mindalliance.configurations.GlobalVariables;
import com.mindalliance.configurations.LogFunctions;

public class REST004_ISPIssues extends TestCase{
	/**
	 * Invoke testISPPlan() and get plans, versions and planners identities.
	 */
	
	public static void testISPIssues(){
		try {
			LogFunctions.generateLogsDirectory();	
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST004_ISPIssues";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST004_ISPIssues Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST004_ISPIssues Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST004_ISPIssues.csv");
			System.out.println(GlobalVariables.steps +") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api").concat("/version/"+GlobalVariables.testData.get("versionNumber")+"/issues");
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(GlobalVariables.steps +") Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(GlobalVariables.steps +") Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: verify that Issue.
			GlobalVariables.steps++;
			boolean planIdentifierResult;
			// Verify About Id
			planIdentifierResult=Configurations.parseResponse("about","id",GlobalVariables.testData.get("aboutId"));	
			Assert.assertEquals("About Id is",true,planIdentifierResult);
			// Verify About Type
			planIdentifierResult=Configurations.parseResponse("about","type",GlobalVariables.testData.get("aboutType"));	
			Assert.assertEquals("About Type is",true,planIdentifierResult);
			// Verify About name
			planIdentifierResult=Configurations.parseResponse("about","name",GlobalVariables.testData.get("aboutName"));
			Assert.assertEquals("About name is",true,planIdentifierResult);
			// Verify About Description
			planIdentifierResult=Configurations.parseResponse("about","description",GlobalVariables.testData.get("aboutDescription"));
			Assert.assertEquals("About Description is",true,planIdentifierResult);
			// Verify Issue Detected
			planIdentifierResult=Configurations.parseResponse("issue","detected",GlobalVariables.testData.get("issueDetected"));
			Assert.assertEquals("Issue Detected is",true,planIdentifierResult);			
			// Verify Issue Type
//			planIdentifierResult=Configurations.parseResponse("issue","type",GlobalVariables.testData.get("issueType"));
//			Assert.assertEquals("Issue Type is",planIdentifierResultissueType")));			
			// Verify Issue kind
			planIdentifierResult=Configurations.parseResponse("issue","kind",GlobalVariables.testData.get("issueKind"));
			Assert.assertEquals("Issue kind is",true,planIdentifierResult);			
			// Verify Issue waived
			planIdentifierResult=Configurations.parseResponse("issue","waived",GlobalVariables.testData.get("issueWaived"));
			Assert.assertEquals("Issue waived is",true,planIdentifierResult);			
//			// Verify Issue description
//			planIdentifierResult=Configurations.parseResponse("issue","description",GlobalVariables.testData.get("issueDescription"));
//			Assert.assertEquals("Issue description is",planIdentifierResultissueDescription")));			
			// Verify Issue remediation
			planIdentifierResult=Configurations.parseResponse("issue","remediation",GlobalVariables.testData.get("issueRemediation"));
			Assert.assertEquals("Issue remediation is",true,planIdentifierResult);			
			// Verify Issue severity
			planIdentifierResult=Configurations.parseResponse("issue","severity",GlobalVariables.testData.get("issueSeverity"));
			Assert.assertEquals("Issue severity is",true,planIdentifierResult);			
			// Verify Issue reportedBy
			planIdentifierResult=Configurations.parseResponse("issue","reportedBy",GlobalVariables.testData.get("issueReportedBy"));
			Assert.assertEquals("Issue reportedBy is",true,planIdentifierResult);																								
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
						
        	// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST004_ISPIssues Execution Completed");
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
	 * Invoke testISPIssuesPlan() and get plan
	 */
	public static void testISPIssuesPlan(){
		try {
			LogFunctions.generateLogsDirectory();	
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST004_ISPIssuesPlan";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST004_ISPIssuesPlan Execution Started");
			System.out.println("testISPPlanPlanner Method");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST004_ISPIssuesPlan Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST004_ISPIssues.csv");
			System.out.println(GlobalVariables.steps +") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/version/"+GlobalVariables.testData.get("versionNumber")+"/issues";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(GlobalVariables.steps +") Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(GlobalVariables.steps +") Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: verify that PlanSummary
			GlobalVariables.steps++;
			boolean planAgentResult;
			//Verify planIdentifier release
			planAgentResult=Configurations.parseResponse("planIdentifier","release",GlobalVariables.testData.get("release"));
			Assert.assertEquals("planIdentifier release is",true,planAgentResult);			
			//Verify user name 
			planAgentResult=Configurations.parseResponse("user","username",GlobalVariables.testData.get("username"));
			Assert.assertEquals("user name is",true,planAgentResult);			
			// Verify user full Name
			planAgentResult=Configurations.parseResponse("user","fullName",GlobalVariables.testData.get("fullName"));	
			Assert.assertEquals("Full Name is",true,planAgentResult);
			// Verify user email
			planAgentResult=Configurations.parseResponse("user","email",GlobalVariables.testData.get("email"));
			Assert.assertEquals("Email is",true,planAgentResult);
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
			// Verify Agent Kind
			planAgentResult=Configurations.parseResponse("availability","always",GlobalVariables.testData.get("always"));	
			Assert.assertEquals("Availability always is",true,planAgentResult);			
			// Verify  documents type
			planAgentResult=Configurations.parseResponse("documents","type",GlobalVariables.testData.get("documentsType"));
			Assert.assertEquals("Documents type is",true,planAgentResult);			
			//Verify  documents url
			planAgentResult=Configurations.parseResponse("documents","url",GlobalVariables.testData.get("documentsUrl"));
			Assert.assertEquals("Documents url is",true,planAgentResult);			
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST004_ISPIssuesPlan Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST004_ISPIssuesPlan Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
	
	/**
	 * Invoke testISPIssuesPlanMetrics() and get tIssues Plan Metrics
	 */
	public static void testISPIssuesPlanMetrics(){
		try {
			LogFunctions.generateLogsDirectory();	
			
			GlobalVariables.steps = 0;
			GlobalVariables.testCaseId = "REST004_ISPIssuesPlanMetrics";
			
			// Create Log Files
			GlobalVariables.testResultLogFile = LogFunctions.generateLogFile(GlobalVariables.logFile + "_"+ GlobalVariables.testCaseId + ".log");
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST004_ISPIssuesPlanMetrics Execution Started");
			LogFunctions.logDescription(GlobalVariables.steps + ") REST004_ISPIssuesPlanMetrics Execution Started");
			
			// For managing SSL connections
			Configurations.validateTrustManager();
			
			// Reading input data from CSV File
			GlobalVariables.steps++;
			Configurations.getTestData("REST004_ISPIssues.csv");
			System.out.println(GlobalVariables.steps +") Reading Data From CSV File");
			LogFunctions.logDescription(GlobalVariables.steps + ") Reading Data From CSV File");

			// Send Request
			String data =GlobalVariables.testData.get("api")+"/version/"+GlobalVariables.testData.get("versionNumber")+"/issues";
			GlobalVariables.steps++;
			Configurations.sendRequest(data);
			System.out.println(GlobalVariables.steps +") Sending Request");
			LogFunctions.logDescription(GlobalVariables.steps + ") Request Sent");
			
			// Receive Response in XML File (response.xml)
			GlobalVariables.steps++;
			Configurations.getResponse();
			System.out.println(GlobalVariables.steps +") Getting Response");
			LogFunctions.logDescription(GlobalVariables.steps + ") Response Received");
			
			// Assertion: Verify that Plan Metrics
			GlobalVariables.steps++;
			boolean planAgentResult;
			// Verify Count Name
			planAgentResult=Configurations.parseResponse("count","type",GlobalVariables.testData.get("countType"));	
			Assert.assertEquals("Count type is",true,planAgentResult);
			// Verify Count Value
			planAgentResult=Configurations.parseResponse("count","value",GlobalVariables.testData.get("countValue"));	
			Assert.assertEquals("Count Value is",true,planAgentResult);
			System.out.println(GlobalVariables.steps +") Plan Summary Assertion Pass");
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Pass");
			
			// Execution Completed
			GlobalVariables.steps++;
			System.out.println(GlobalVariables.steps +") Test Case : REST004_ISPIssuesPlanMetrics Execution Completed");
			LogFunctions.logDescription(GlobalVariables.steps+ ") Test Case : REST004_ISPIssuesPlanMetrics Execution Completed");
			
		}catch (AssertionError ar) {
			System.out.println(GlobalVariables.steps +")Assertion Failed : ");
			ar.printStackTrace();
			LogFunctions.logDescription(GlobalVariables.steps + ") Assertion Failed");
		} catch (Exception e) {
			LogFunctions.logException(e.getMessage());
		}
	}
}
	