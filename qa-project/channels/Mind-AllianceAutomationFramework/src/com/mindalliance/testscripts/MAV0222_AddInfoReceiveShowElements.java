package com.mindalliance.testscripts;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class MAV0222_AddInfoReceiveShowElements
{
	public MAV0222_AddInfoReceiveShowElements(){
    	try {    		
    		GlobalVariables.sTestCaseId = "MAV0222_AddInfoReceiveShowElements";
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
				ApplicationFunctionLibrary.addSegment(GlobalVariables.testData.get("Segment For Receive Send Hide and Element"), "New");
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
				// Click 'Add Info Sent' Link. 
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "'Add Info Sent' Link Clicked";
				GlobalVariables.oDriver.findElement(By.linkText("Add info sent")).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Click on Elements of Sent Info
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Sent Info Elements";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathSentElementInformation"))).click();
				// Assertion: Verify that Elements of Information is Present
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.assertion.get("sXpathElementsOfInformation")));
				if(GlobalVariables.oElement.getText().equals("Elements of information")){
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);	
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Click on 'done' button
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Done";
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);		
				
				// Click 'Add Info Receive' Link. 
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "'Add Info Receive' Link Clicked";
				GlobalVariables.oDriver.findElement(By.linkText("Add info received")).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Click on Elements of Receive Info
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Receive Info Elements";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathReceiveElementInformation"))).click();	
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.assertion.get("sXpathElementsOfInformation")));
				if(GlobalVariables.oElement.getText().equals("Elements of information")){
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);	
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Click on 'done' button
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Done";
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Click on 'Remove this segment' under 'Actions' pop up menu
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
				// Get a handle to the open alert, prompt or confirmation
				Alert alert = GlobalVariables.oDriver.switchTo().alert();
				alert.accept();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				
				// Call Logout
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Logout Successful";
				ApplicationFunctionLibrary.logout();
				// Write Results
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
			new MAV0222_AddInfoReceiveShowElements();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}