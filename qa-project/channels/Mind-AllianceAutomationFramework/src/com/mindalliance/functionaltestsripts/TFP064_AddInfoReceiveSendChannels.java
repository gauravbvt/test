package com.mindalliance.functionaltestsripts;

import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class TFP064_AddInfoReceiveSendChannels 
{
	public TFP064_AddInfoReceiveSendChannels(){
    	try {
    		
    		GlobalVariables.sTestCaseId = "TFP064_AddInfoReceiveSendChannels";
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
				ApplicationFunctionLibrary.addSegment(GlobalVariables.testData.get("Segment For Receive Send Channels"), "New");
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

				// Click on Show Advance form link
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Navigated to Advance form";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowAdvanceSimpleFormSendInfo"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);

				// Sends : Channels <option> List 
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Channels <option> List";
				// Select option from List
				GlobalVariables.oDropDown=new Select(GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:channel-row:channels:editable-container:channels:0:medium")));
				List <WebElement> options = GlobalVariables.oDropDown.getOptions();
				if(options.get(1).getText().equals(GlobalVariables.viewElements.get("cell")) &&
				   options.get(2).getText().equals(GlobalVariables.viewElements.get("conferenceCall")) &&
				   options.get(3).getText().equals(GlobalVariables.viewElements.get("courier")) &&
				   options.get(4).getText().equals(GlobalVariables.viewElements.get("email")) &&
				   options.get(5).getText().equals(GlobalVariables.viewElements.get("faceToFace")) &&
				   options.get(6).getText().equals(GlobalVariables.viewElements.get("fax")) &&
				   options.get(7).getText().equals(GlobalVariables.viewElements.get("im")) &&
				   options.get(8).getText().equals(GlobalVariables.viewElements.get("landline")) &&
				   options.get(9).getText().equals(GlobalVariables.viewElements.get("mail")) &&
				   options.get(10).getText().equals(GlobalVariables.viewElements.get("meeting")) &&
				   options.get(11).getText().equals(GlobalVariables.viewElements.get("notificationSystem")) &&
				   options.get(12).getText().equals(GlobalVariables.viewElements.get("onlineChat")) &&
				   options.get(13).getText().equals(GlobalVariables.viewElements.get("pager")) &&
				   options.get(14).getText().equals(GlobalVariables.viewElements.get("paSystems")) &&
				   options.get(15).getText().equals(GlobalVariables.viewElements.get("phone")) &&
				   options.get(16).getText().equals(GlobalVariables.viewElements.get("radio")) &&
				   options.get(17).getText().equals(GlobalVariables.viewElements.get("television")) &&
				   options.get(18).getText().equals(GlobalVariables.viewElements.get("twoWayRadio")) &&
				   options.get(19).getText().equals(GlobalVariables.viewElements.get("newMedium"))) {
						// 	Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription+" "+GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
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

				// Click on Show Advance form link
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Navigated to Advance form";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowAdvanceSimpleFormSendInfo"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);

				// Receives : Channels <option> List 
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Channels <option> List";
				// Select option from List
				GlobalVariables.oDropDown=new Select(GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:channel-row:channels:editable-container:channels:0:medium")));
				options = GlobalVariables.oDropDown.getOptions();
				if(options.get(1).getText().equals(GlobalVariables.viewElements.get("cell")) &&
				   options.get(2).getText().equals(GlobalVariables.viewElements.get("conferenceCall")) &&
				   options.get(3).getText().equals(GlobalVariables.viewElements.get("courier")) &&
				   options.get(4).getText().equals(GlobalVariables.viewElements.get("email")) &&
				   options.get(5).getText().equals(GlobalVariables.viewElements.get("faceToFace")) &&
				   options.get(6).getText().equals(GlobalVariables.viewElements.get("fax")) &&
				   options.get(7).getText().equals(GlobalVariables.viewElements.get("im")) &&
				   options.get(8).getText().equals(GlobalVariables.viewElements.get("landline")) &&
				   options.get(9).getText().equals(GlobalVariables.viewElements.get("mail")) &&
				   options.get(10).getText().equals(GlobalVariables.viewElements.get("meeting")) &&
				   options.get(11).getText().equals(GlobalVariables.viewElements.get("notificationSystem")) &&
				   options.get(12).getText().equals(GlobalVariables.viewElements.get("onlineChat")) &&
				   options.get(13).getText().equals(GlobalVariables.viewElements.get("pager")) &&
				   options.get(14).getText().equals(GlobalVariables.viewElements.get("paSystems")) &&
				   options.get(15).getText().equals(GlobalVariables.viewElements.get("phone")) &&
				   options.get(16).getText().equals(GlobalVariables.viewElements.get("radio")) &&
				   options.get(17).getText().equals(GlobalVariables.viewElements.get("television")) &&
				   options.get(18).getText().equals(GlobalVariables.viewElements.get("twoWayRadio")) &&
				   options.get(19).getText().equals(GlobalVariables.viewElements.get("newMedium"))) {
						// 	Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription+" "+GlobalVariables.sFailed);
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
    		System.out.println(e.getMessage());
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
			new TFP064_AddInfoReceiveSendChannels();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			oException.printStackTrace();
		}
	}
}