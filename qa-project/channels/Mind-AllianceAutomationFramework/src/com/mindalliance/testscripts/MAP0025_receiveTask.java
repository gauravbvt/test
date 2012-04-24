package com.mindalliance.testscripts;

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
/**
 * Test Case ID: MAP0025_receiveTask
 * Summary: Verify that user is able to receive task information/event message to other user or agent
 * @author AfourTech
 *
 */
public class MAP0025_receiveTask
{
	public MAP0025_receiveTask() {
		try {
			GlobalVariables.sTestCaseId = "MAP0025_receiveTask";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			if (GlobalVariables.bIsSuccess) {
					
				// Select  the plan from 'Switch to Plan' drop down, located on the top right corner
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Switch to plan";
				GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("switch-plan:plan-sel")));
				List <WebElement> options = GlobalVariables.oDropDown.getOptions();
			    for(WebElement option : options) {
			    	if(GlobalVariables.testData.get("Automation Test Plan v.1 (dev)").equals(option.getText())){
			    			option.setSelected();
			    			break;
			    	}
			    }
			    // Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);	
				
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
				Thread.sleep(5000);
			    
				// Click on 'Add new segment' option under 'Actions' pop up menu
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Segment Added";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewSegment"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);	

				// Details of Segment
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Details of Segment enetered";					
				GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name")).click();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name"));
				for (int i = 0; i <= 8; i++)
					GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Segment for Receive info"));
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);	
				
				// Select the Segment from 'Select Plan Segment' drop down, located on the top right corner
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Select Plan Segment";
				GlobalVariables.oDropDown  =new Select(GlobalVariables.oDriver.findElement(By.name("select-segment:sg-sel")));
				options = GlobalVariables.oDropDown.getOptions();
			    for(WebElement option : options) {
			    	if(GlobalVariables.testData.get("Segment for Receive info").equals(option.getText())){
			    			option.setSelected();
			    			break;
			    	}
			    }
			    // Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				
				// create task one
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Task 1 Created";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewTask"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(8000);
				// Write Task
				GlobalVariables.oDriver.findElement(By.name("segment:part:task")).click();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:task"));
				for (int i = 0; i <= 50; i++)
					GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Receiver"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(8000);
				GlobalVariables.oElement.sendKeys(Keys.TAB);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(8000);
				// Click on hide details from action pop-menu bar
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathTaskShowMenu"),GlobalVariables.viewElements.get("hideDetails"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(8000);
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(8000);
				
				//  Click on 'add info received' option under 'Receives'  section
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Receives Informations";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAddInfoReceive"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(8000);
				GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:name")).click();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:name"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Receives informations"));
				GlobalVariables.oElement.sendKeys(Keys.ENTER);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(8000);
				GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:other:firstChoice")));
				options = GlobalVariables.oDropDown.getOptions();
			    for(WebElement option : options) {
			    	if(GlobalVariables.viewElements.get("other").equals(option.getText())){
			    			option.setSelected();
			    			break;
			    	}
			    }
			    // WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(8000);
				GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:other:secondChoice:secondChoice-input")).click();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:other:secondChoice:secondChoice-input"));
				for (int i = 0; i <= 50; i++)
					GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Sender"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(8000);
				GlobalVariables.oElement.sendKeys(Keys.TAB);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(8000);
				// ASSERTION: When details entered, the flow should be connected between two nodes
				GlobalVariables.bIsSuccess = Boolean.FALSE;
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("flow-map"));
				List<WebElement> areas = GlobalVariables.oElement.findElements(By.tagName("area"));
				for(WebElement area:areas){
				if(area.getAttribute("id").equals("edge2"))
					 GlobalVariables.bIsSuccess = Boolean.TRUE;
					 break;
			    }
				if (GlobalVariables.bIsSuccess == Boolean.FALSE) {
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else
			    {
			    	// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
			    }					
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				
				// Click on 'Remove this segment' under 'Actions' pop up menu
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
				// Get a handle to the open alert, prompt or confirmation
				Alert alert = GlobalVariables.oDriver.switchTo().alert();
				alert.accept();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				
				// Call logout()
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Logout is successful";
				ApplicationFunctionLibrary.logout();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				
				LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
				System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
			}
			else{
				LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
				System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
				
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				System.out.println("Unable to Receive Task" + ReportFunctions.getScreenShot("Receive Task failed"));
				GlobalVariables.oDriver.quit();
			}
		} 
		catch (Exception e) {
			if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.id("stackTrace"));
				LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
				System.out.println("Unable to Receive Task"+ReportFunctions.getScreenShot("Receive Task failed"));
				ApplicationFunctionLibrary.logout();
			}
			else {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sBlank);
				System.out.println("Unable to Receive Task"+ReportFunctions.getScreenShot("Receive Task failed"));
				ApplicationFunctionLibrary.logout();	
			}
			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
		}
	}
    public static void main(String args[]) {
		try {
			GenericFunctionLibrary.initializeTestData();
			GenericFunctionLibrary.loadObjectRepository();
			new MAP0025_receiveTask();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
			System.out.println("Unable to Receive Task"+ReportFunctions.getScreenShot("Receive Task failed"));
		}
	}
}