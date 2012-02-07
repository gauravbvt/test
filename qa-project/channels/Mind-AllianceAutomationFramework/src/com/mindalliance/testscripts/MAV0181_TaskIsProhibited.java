package com.mindalliance.testscripts;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class MAV0181_TaskIsProhibited 
{
	public MAV0181_TaskIsProhibited(){
    	try {
    		
    		GlobalVariables.sTestCaseId = "MAV0181_TaskIsProhibited";
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
				Thread.sleep(2000);
				
				// Click 'Add new Segment' option under 'Actions' pop up menu and enter the details
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "New segment added";
				ApplicationFunctionLibrary.addSegment(GlobalVariables.testData.get("Segment For Add Task As Operational"), "New");
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Stretch Up Task Details
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathStretchUpShrinkBack"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Click on default task
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Task";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDoingSomeThingLink"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				// Click on Show Advance form link
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Navigated to Advance form";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowAdvanceSimpleFormOfTask"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				// Check Prohibit Option to Prohibit the task
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Task Prohibited";
				GlobalVariables.oDriver.findElement(By.name("segment:part:classificationContainer:prohibited")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Assertion : Verify that Task gets Prohibited
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathOperationalNonOperational")));
				if(GlobalVariables.oElement.getText().equals(GlobalVariables.viewElements.get("prohibited"))) {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);					
				}
				else {
					GlobalVariables.sVerifyError="Verification Failed Expected "+GlobalVariables.viewElements.get("prohibited")+"Actual is"+GlobalVariables.oElement.getText();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sVerifyError, GlobalVariables.sBlank);
				}

				// Uncheck Prohibit Option to Permit the task
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Task Permitted";
				GlobalVariables.oDriver.findElement(By.name("segment:part:classificationContainer:prohibited")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Assertion : Verify that Task gets Prohibited
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathOperationalNonOperational")));
				if(GlobalVariables.oElement.getText().equals(".")) {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);					
				}
				else {
					GlobalVariables.sVerifyError="Verification Failed Expected ."+"Actual is"+GlobalVariables.oElement.getText();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sVerifyError, GlobalVariables.sBlank);
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Remove Segment
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Get a handle to the open alert, prompt or confirmation
//				Alert alert = GlobalVariables.oDriver.switchTo().alert();
//				// And acknowledge the alert (equivalent to clicking "OK")
//				alert.accept();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);

				// 	Call Logout
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Logout Successful";
				ApplicationFunctionLibrary.logout();
				// 	Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
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
			new MAV0181_TaskIsProhibited();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}