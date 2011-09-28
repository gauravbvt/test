package com.mindalliance.testscripts;

import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

/**
 * Testcase ID: MAC0041_RedoDisintermediateTask
 * 	   Summary: Verify that user is able to undo the disIntermediate task
 * @author AFour
 * 
 */
public class MAC0041_RedoDisintermediateTask {
	public MAC0041_RedoDisintermediateTask() {
		try {
			GlobalVariables.sTestCaseId = "MAC0041_RedoDisintermediateTask";
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
					Thread.sleep(3000);
					
					// Click on 'Add new segment' option under 'Actions' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "New segment added";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewSegment"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
			
					// Enter the details
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "New segment updated";
					GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:name")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:name"));
					for (int i = 0; i <= 8; i++)
						GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
					GlobalVariables.oElement.sendKeys("Segment For Redo Disintermediate");
					// Click on 'done' button
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);

					// Update the Information of the default task
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Task updated";
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDefaultTask"))).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					GlobalVariables.oDriver.findElement(By.name("segment:part:task")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:task"));
					for (int i = 0; i <= 15; i++)
						GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
					GlobalVariables.oElement.sendKeys("Task Sender");
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					GlobalVariables.oElement.sendKeys(Keys.TAB);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					// Click on hide details from action pop-menu bar
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathTaskShowMenu"),GlobalVariables.viewElements.get("hideDetails"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
			
					// Add info sent flow
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Sent Flow added";
					GlobalVariables.oDriver.findElement(By.linkText("Add info sent")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// Create Flow between respective nodes
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Flow created between nodes";
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:name"));
					GlobalVariables.oElement.sendKeys("This is a Flow");
					GlobalVariables.oElement.sendKeys(Keys.TAB);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// From
					GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:other:firstChoice")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:other:firstChoice"));
					GlobalVariables.oElement.sendKeys("Other...");
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					GlobalVariables.oElement.sendKeys(Keys.ENTER);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// Enter Task Name of Sender
					GlobalVariables.oDriver.findElement
					(By.name("segment:sends:flows-div:flows:0:flow:other:secondChoice:secondChoice-input")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement
					(By.name("segment:sends:flows-div:flows:0:flow:other:secondChoice:secondChoice-input"));
					for (int i = 0; i <= 17 ; i++)
						GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
					GlobalVariables.oElement.sendKeys("Task Receiver");
					GlobalVariables.oElement.sendKeys(Keys.ENTER);
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					
					// Create Intermediate Flow
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Intermediate Flow created";		
					// Click on Legend to maximize the information flow details
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathStretchUpShrinkBack"))).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathSendInfoActionMenu"),GlobalVariables.viewElements.get("addIntermediate"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// Click on Legend to minimize the information flow details
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathStretchUpShrinkBack"))).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					
					// Click on 'About plan segment' option under 'Show' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Task selected to disintermediate";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlanSegment"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					// Click on 'task mover' option under 'Shows' pop up menu
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAbtPlanSegShowMenu"),GlobalVariables.viewElements.get("taskMover"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// Select the task to disintermediate
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("data-table"));
					List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("td"));
					for (WebElement td: tds){
						if (td.getText().equals("doing something")){
							td.findElement(By.linkText("doing something")).click();
							break;
						}
					}
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// Click on 'done' button
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDoingSomeThingLink"))).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click on 'Disintermediate' option under 'Actions' pop up menu of task section
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Task disintermediated";
					// Click on Legend to minimize the information flow details
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathStretchUpShrinkBack"))).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathTaskActionsMenu"),GlobalVariables.viewElements.get("disintermediate"));
					// ASSERTION: When clicked on 'Disintermediate' option, the intermediate task should be disintermediated
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath("//span[@class='menubar']/span[3]/span/ul"));
					List <WebElement> list = GlobalVariables.oElement.findElements(By.tagName("li"));
					for (WebElement li: list){
						if (li.getValue().equals("Disintermediate")){
							// Write Results
							LogFunctions.writeLogs(GlobalVariables.sDescription);
							LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
									GlobalVariables.sBlank, GlobalVariables.sBlank);
							break;
						}
						else
					    {
							GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Undo disintermediate' "+" Actual " + li.getValue();
					    	// Write Results
							LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
							LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
									GlobalVariables.sBlank, GlobalVariables.sVerifyError);
							break;
					    }
					}
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(8000);
					
					// Click on 'Undo disintermediate' option under 'Actions' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Redo disintermediate done";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("undoDisintermediate"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// ASSERTION:When clicked on 'Undo disintermediate' option, the disintermediated task should be intermediate again
					// Get the option name
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath("//span[@class='menubar']/span[3]/span/ul"));
					list = GlobalVariables.oElement.findElements(By.tagName("li"));
					for (WebElement li: list){
						if (li.getValue().equals("Undo disintermediate")){
							// Write Results
							LogFunctions.writeLogs(GlobalVariables.sDescription);
							LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
									GlobalVariables.sBlank, GlobalVariables.sBlank);
							break;
						}
						else{
							GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Redo disintermediate' "+" Actual " + li.getValue();
					    	// Write Results
							LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
							LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
									GlobalVariables.sBlank, GlobalVariables.sVerifyError);
							break;
					    }
					}
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click on 'Redo disintermediate' option under 'Actions' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Redo disintermediate done";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("redoDisintermediate"));
					// Assertion: When clicked on 'Redo disintermediate' option, the task should be disntermediate again
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath("//span[@class='menubar']/span[3]/span/ul"));
					list = GlobalVariables.oElement.findElements(By.tagName("li"));
					for (WebElement li: list){
						if (li.getValue().equals("Redo disintermediate")){
							// Write Results
							LogFunctions.writeLogs(GlobalVariables.sDescription);
							LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
									GlobalVariables.sBlank, GlobalVariables.sBlank);
							break;
						}
						else{
							GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Redo disintermediate' "+" Actual " + li.getValue();
					    	// Write Results
							LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
							LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
									GlobalVariables.sBlank, GlobalVariables.sVerifyError);
							break;
					    }
					}
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
					// Get a handle to the open alert, prompt or confirmation
					Alert alert = GlobalVariables.oDriver.switchTo().alert();
					// And acknowledge the alert (equivalent to clicking "OK")
					alert.accept();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);

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
			new MAC0041_RedoDisintermediateTask();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}