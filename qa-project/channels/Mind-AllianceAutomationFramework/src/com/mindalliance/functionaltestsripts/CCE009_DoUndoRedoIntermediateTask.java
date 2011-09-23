package com.mindalliance.functionaltestsripts;

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

public class CCE009_DoUndoRedoIntermediateTask 
{
	public CCE009_DoUndoRedoIntermediateTask() {
		try {
			
			GlobalVariables.sTestCaseId = "CCE009_DoUndoRedoIntermediateTask";
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
					Thread.sleep(2000);
			
					// Enter the details
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "New segment updated";
					GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:name")).clear();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:name"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Segment For Do Undo Redo Intermediate Task"));
					// Click on 'done' button
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);

					// Update the Information of the default task
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Task updated";
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDefaultTask"))).click();
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDefaultTask"))).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					GlobalVariables.oDriver.findElement(By.name("segment:part:task")).clear();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:task"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Task Sender"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
			
					// Add info sends flow
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Send Flow added";
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAddInfoSend"))).click();
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
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("This is a Flow"));
					GlobalVariables.oElement.sendKeys(Keys.TAB);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// To
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
					GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:other:secondChoice:secondChoice-input")).clear();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:other:secondChoice:secondChoice-input"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Task Receiver"));
					GlobalVariables.oElement.sendKeys(Keys.TAB);
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
					// Click on legend for maximize the graph
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathStretchUpShrinkBack"))).click();
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathSendFlowMoreMenu"),GlobalVariables.viewElements.get("addIntermediate"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// Click on Legend to minimize the information flow details
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathStretchUpShrinkBack"))).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// Click on 'About plan segment' option under 'Show' pop up menu
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlanSegment"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// Click on 'task mover' option under 'Shows' pop up menu
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAbtPlanSegShowMenu"),GlobalVariables.viewElements.get("taskMover"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// ASSERTION: When clicked on 'Add intermediate' option, the task should be created between the selected task and its respective other task
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath("/html/body/form/div[5]/div/div[2]/div[2]/span/table/tbody/tr/td[2]/span/a/span"));
				    if(GlobalVariables.oElement.getText().equals("doing something")) {
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
				    }
				    else {
				    	GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'doing something' "+" Actual " + GlobalVariables.oElement.getText();
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
								GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				    }
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					
					// Click on 'Undo add intermediate' option under 'Actions' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Undo add intermediate done";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("undoAddIntermediate"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// ASSERTION: When clicked on 'Undo add intermediate' option, the intermediate task should be removed
					GlobalVariables.bIsSuccess = Boolean.FALSE;
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("data-table"));
					List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("td"));
					for (WebElement td: tds) {
						GlobalVariables.sStrCheck=td.getText();
						if (td.getText().equals("doing something")) {
							GlobalVariables.bIsSuccess = Boolean.TRUE;
							break;
						}
					}
					if (GlobalVariables.bIsSuccess == Boolean.TRUE) {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					}
					else
				    {
						GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'doing something' "+" Actual " + GlobalVariables.sStrCheck;
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
								GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				    }

					// Redo- Click on 'Redo add intermediate' option under 'Actions' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Redo add intermediate done";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("redoAddIntermediate"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// ASSERTION: When clicked on 'Undo add intermediate' option, the intermediate task should be removed
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath("/html/body/form/div[5]/div/div[2]/div[2]/span/table/tbody/tr/td[2]/span/a/span"));
				    if(GlobalVariables.oElement.getText().equals("doing something")) {
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
				    }
				    else {
				    	GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'doing something' "+" Actual " + GlobalVariables.oElement.getText();
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
								GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				    }
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
					Alert alert = GlobalVariables.oDriver.switchTo().alert();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					alert.accept();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					
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
					Thread.sleep(2000);
					
					LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
					System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
					
				}
				else
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
		} 
		catch (Exception e) {
			e.printStackTrace();
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
			new CCE009_DoUndoRedoIntermediateTask();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}