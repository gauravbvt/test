package com.mindalliance.functionaltestsripts;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class TFP078_AddInfoReceiveSendAttachmentAsURL 
{
	public TFP078_AddInfoReceiveSendAttachmentAsURL(){
    	try {
    		
    		GlobalVariables.sTestCaseId = "TFP078_AddInfoReceiveSendAttachmentAsURL";
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
				ApplicationFunctionLibrary.addSegment(GlobalVariables.testData.get("Segment For Receive Send Attachment As URL"), "New");
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

				// Sends : Attachment as a URL
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Attachment as a URL";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathSendsAttachmentURL"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				GlobalVariables .oElement=GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:attachments:container:controls:url"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("URL"));
				GlobalVariables.oElement.sendKeys(Keys.TAB);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Assertion : Verify that Attachment attached as a URL
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAddInfoSendAttachment")));
				if(GlobalVariables.oElement.getText().contains("")) {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);					
				}
				else {
					GlobalVariables.sVerifyError ="Verification Failed "+"Expected '"+GlobalVariables.testData.get("URL")+"' "+" Actual "+GlobalVariables.oElement.getText();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription+" "+GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sVerifyError, GlobalVariables.sBlank);
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Click 'Add info received' Link. 
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "'Add Info Received' Link Clicked";
				GlobalVariables.oDriver.findElement(By.linkText("Add info received")).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);

				// Receives : Attachment as a URL
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Attachment as a URL";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathReceivesAttachmentURL"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				GlobalVariables .oElement=GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:attachments:container:controls:url"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("URL"));
				GlobalVariables.oElement.sendKeys(Keys.TAB);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Assertion : Verify that Attachment attached as a URL
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAddInfoReceiveAttachment")));
				if(GlobalVariables.oElement.getText().contains("")) {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);					
				}
				else {
					GlobalVariables.sVerifyError ="Verification Failed "+"Expected '"+GlobalVariables.testData.get("URL")+"' "+" Actual "+GlobalVariables.oElement.getText();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription+" "+GlobalVariables.sFailed);
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
				Alert alert = GlobalVariables.oDriver.switchTo().alert();
				// And acknowledge the alert (equivalent to clicking "OK")
				alert.accept();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
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
			new TFP078_AddInfoReceiveSendAttachmentAsURL();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}