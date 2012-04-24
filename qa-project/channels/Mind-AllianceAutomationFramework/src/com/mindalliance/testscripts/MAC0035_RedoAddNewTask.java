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
 * Testcase ID: MAC0035
 * 	   Summary: Verify that user is able to redo the added task
 * 	      Note: NEED TO MODIFY
 * @author AFour
 * 
 */
public class MAC0035_RedoAddNewTask {
	public MAC0035_RedoAddNewTask() {
		try {
			GlobalVariables.sTestCaseId = "MAC0035_RedoAddNewTask";
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
				
				// Click 'Add new Segment' option under 'Actions' pop up menu and enter the details
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "New segment added";
				ApplicationFunctionLibrary.addSegment(GlobalVariables.testData.get("Segment For Redo Add Task"), "New");
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				// Click 'Add new task' option under 'Actions' pop up menu 
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "New task added";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewTask"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				// Enter the details
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "New Task updated";
				GlobalVariables.oDriver.findElement(By.name("segment:part:task")).click();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:task"));
				for (int i = 0; i <= 15; i++)
					GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Task 1"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDoingSomeThingLink"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// Click on 'About plan segment' option under 'Show' pop up menu
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlanSegment"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				// Click on 'task mover' option under 'Shows' pop up menu
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAbtPlanSegShowMenu"),GlobalVariables.viewElements.get("taskMover"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// ASSERTION:  When details are entered of respective task, the newly added task should be updated in the segment
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("data-table"));
				List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("td"));
				for (WebElement td: tds){
					if (td.getText().equals("")){
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
						break;
					}
					else{
						GlobalVariables.sVerifyError ="Verification Failed "+"Expected '' "+" Actual " + td.getText();
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
								GlobalVariables.sBlank, GlobalVariables.sVerifyError);
						break;
				    }
				}
				// Click on 'done' button
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);

				// Click 'Undo add new task' option under 'Action' pop up menu
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Undo add new task done";
				// Undo update task
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("undoUpdateTask"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// Undo add new task
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("undoAddNewTask"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// Click on 'About plan segment' option under 'Show' pop up menu
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlanSegment"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				// Click on 'task mover' option under 'Shows' pop up menu
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAbtPlanSegShowMenu"),GlobalVariables.viewElements.get("taskMover"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// ASSERTION:  When clicked on 'Undo add new task' option the newly addded task should get removed from the segment.
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("data-table"));
				tds = GlobalVariables.oElement.findElements(By.tagName("td"));
				GlobalVariables.bIsSuccess = Boolean.FALSE;
				for (WebElement td: tds){
					GlobalVariables.sStrCheck=td.getText();
					if (td.getText().equals("")){
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
				else{
					GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Task 1' "+" Actual " + GlobalVariables.sStrCheck;
			    	// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
			    }
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				// Redo- Click on 'Redo add new task' under 'Actions' pop up menu
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Redo add new task done";
				// Redo add new task
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("redoAddNewTask"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// ASSERTION:  When clicked on 'Redo add new task' option the newly added task should get added to the segment.
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("data-table"));
				tds = GlobalVariables.oElement.findElements(By.tagName("td"));
				GlobalVariables.bIsSuccess = Boolean.FALSE;
				for (WebElement td: tds){
					GlobalVariables.sStrCheck=td.getText();
					if (td.getText().equals("")){
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
				else{
					GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Task 1' "+" Actual '" + GlobalVariables.sStrCheck+"'";
			    	// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
			    }
				// Click on 'done' button
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
				// Get a handle to the open alert, prompt or confirmation
				Alert alert = GlobalVariables.oDriver.switchTo().alert();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
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
			else{
				LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
				System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
				
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				System.out.println("Unable to Redo Add new Task" + ReportFunctions.getScreenShot("Redo Add new Task failed"));
				GlobalVariables.oDriver.quit();
			}
		} 
		catch (Exception e) {
			if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.id("stackTrace"));
				LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
				System.out.println("Unable to Redo Add new Task"+ReportFunctions.getScreenShot("Redo Add new Task failed"));
				ApplicationFunctionLibrary.logout();
			}
			else {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sBlank);
				System.out.println("Unable to Redo Add new Task"+ReportFunctions.getScreenShot("Redo Add new Task failed"));
				ApplicationFunctionLibrary.logout();	
			}
			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
		}
	}
	public static void main(String args[]) {
		try {
			GenericFunctionLibrary.initializeTestData();
			GenericFunctionLibrary.loadObjectRepository();
			new MAC0035_RedoAddNewTask();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
			System.out.println("Unable to Redo add new task"+ReportFunctions.getScreenShot("Redo add new task failed"));
		}
	}
}