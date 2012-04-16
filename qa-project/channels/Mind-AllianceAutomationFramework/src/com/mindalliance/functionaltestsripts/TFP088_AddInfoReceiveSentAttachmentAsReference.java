package com.mindalliance.functionaltestsripts;

import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class TFP088_AddInfoReceiveSentAttachmentAsReference 
{
	public TFP088_AddInfoReceiveSentAttachmentAsReference(){
    	try {
    		
    		GlobalVariables.sTestCaseId = "TFP088_AddInfoReceiveSentAttachmentAsReference";
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
				ApplicationFunctionLibrary.addSegment(GlobalVariables.testData.get("Segment For Receive Send Reference"), "New");
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
//				GlobalVariables.oDriver.findElement(By.linkText("Add info sent")).click();
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAddInfoSend"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);

				// Click on New link of Send info panel
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "New Issue Added Of Send Panel";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathSendInfoAddNewIssue"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Enter Attachment as reference
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "New Issue Attachment As Reference Of Send Panel Entered";
				GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:issues:issues-container:issues:0:issue:attachments:container:controls:type")));
				List <WebElement> options = GlobalVariables.oDropDown.getOptions();
				for(WebElement option : options) {
			    	if(GlobalVariables.viewElements.get("reference").equals(option.getText())){
			    		option.setSelected();
			    	}
			    }
				String file="This is File 1";
				GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:issues:issues-container:issues:0:issue:attachments:container:controls:name")).clear();
				GlobalVariables .oElement=GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:issues:issues-container:issues:0:issue:attachments:container:controls:name"));
				GlobalVariables.oElement.sendKeys("This is File 1");
				GlobalVariables .oElement=GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:issues:issues-container:issues:0:issue:attachments:container:controls:upload"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.sTestDataDirectoryPath + "CAP.txt");
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:issues:issues-container:issues:0:issue:attachments:container:controls:submit")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// Assertion : Verify that file is attached successfully
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.linkText(file));
				if(GlobalVariables.oElement.getText().contains(GlobalVariables.testData.get("This is File 1"))) {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);					
				}
				else {
					GlobalVariables.sVerifyError="Verification Failed Actual 'This is File 1' Expected "+GlobalVariables.oElement.getText();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Click 'Add info received' Link. 
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "'Add Info Received' Link Clicked";
//				GlobalVariables.oDriver.findElement(By.linkText("Add info received")).click();
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAddInfoReceive"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Click on New link of Receive info panel
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "New Issue added of Receive Panel";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathReceiveInfoAddNewIssue"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Scroll Down
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.className("flow-details"));
				GlobalVariables.oElement.click();
				for(int i=0;i<30;i++)
					GlobalVariables.oElement.sendKeys(Keys.ARROW_DOWN);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Enter Attachment as reference
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "New Issue Attachment As Reference Of Receive Panel Entered";
				GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:issues:issues-container:issues:0:issue:attachments:container:controls:type")));
				options = GlobalVariables.oDropDown.getOptions();
				for(WebElement option : options) {
			    	if(GlobalVariables.viewElements.get("reference").equals(option.getText())){
			    		option.setSelected();
			    	}
			    }
				GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:issues:issues-container:issues:0:issue:attachments:container:controls:name")).clear();
				GlobalVariables .oElement=GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:issues:issues-container:issues:0:issue:attachments:container:controls:name"));
				GlobalVariables.oElement.sendKeys("This is File 1");
				GlobalVariables .oElement=GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:issues:issues-container:issues:0:issue:attachments:container:controls:upload"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.sTestDataDirectoryPath + "CAP.txt");
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:issues:issues-container:issues:0:issue:attachments:container:controls:submit")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// Assertion : Verify that file is attached successfully
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.linkText(file));
				if(GlobalVariables.oElement.getText().contains(GlobalVariables.testData.get("This is File 1"))) {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);					
				}
				else {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Remove Segment
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
//				// Get a handle to the open alert, prompt or confirmation
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
			new TFP088_AddInfoReceiveSentAttachmentAsReference();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}