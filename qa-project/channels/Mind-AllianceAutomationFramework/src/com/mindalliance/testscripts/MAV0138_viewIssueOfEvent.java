package com.mindalliance.testscripts;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class MAV0138_viewIssueOfEvent 
{
	public MAV0138_viewIssueOfEvent()
	{
		try{
			
			GlobalVariables.sTestCaseId = "MAV0138_viewIssueOfEvent";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			if (GlobalVariables.bIsSuccess) {
				
				// Click on 'Information Sharing Model' link
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Navigated to Information Sharing Model";
				GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("informationSharingModel"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);  
				
				// Clicks on 'About plan' link under show pop up menu option
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "About plan section opened";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlan"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);

				// Clicks on 'All events' link under show pop up menu option on About plan window 
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="All events section opened";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAboutPlanShowMenu"),GlobalVariables.viewElements.get("allEvents"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				// Create an event
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Event created";
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("plan:mo:aspect:incidents:eventsDiv:event:1:name-container:name-input"));
				for (int i = 0; i <= 50; i++)
					GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Event"));
				GlobalVariables.oElement.sendKeys(Keys.TAB);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				GlobalVariables.oDriver.findElement(By.name("plan:mo:aspect:incidents:eventsDiv:event:1:confirmed")).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
								
				// Click on event
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Event";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathEvent"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Click on issue under show pop up menu
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Event issue";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathPhaseShowMenu"),GlobalVariables.viewElements.get("Issues"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Assertion: Verify that "Event details" page loaded 
			    GlobalVariables.iStepNo++;
			    GlobalVariables.sDescription="'Event issue' gets loaded";
			    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.assertion.get("sXpathPhaseAssertionDetails")));
			    if (GlobalVariables.oElement.getText().equalsIgnoreCase(GlobalVariables.viewElements.get("issues"))) {
			    	// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
	            }
			    else{
			    	GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'issues' Actual "+GlobalVariables.oElement.getText();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				}
			    // WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				
			    // Call logout()
			    GlobalVariables.iStepNo++ ;
			    GlobalVariables.sDescription = "Logout is successful";
			    ApplicationFunctionLibrary.logout();
			    // Write Results
			    LogFunctions.writeLogs(GlobalVariables.sDescription);
			    LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
			    		GlobalVariables.sBlank, GlobalVariables.sBlank);
			    LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
			    System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
			
			}
			else
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
		} 
		catch (Exception e) {
			if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.id("stackTrace"));
				LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
				ApplicationFunctionLibrary.logout();
			}
			else {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sBlank);
				ApplicationFunctionLibrary.logout();	
			}
			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
		}
	}
	public static void main(String args[]) {
		try {
			GenericFunctionLibrary.initializeTestData();
			GenericFunctionLibrary.loadObjectRepository();
			new MAV0138_viewIssueOfEvent();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
