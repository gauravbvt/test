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

public class CCE005_DoUndoRedoCutTask 
{
	public CCE005_DoUndoRedoCutTask() {
		try {
			GlobalVariables.sTestCaseId = "CCE005_DoUndoRedoCutTask";
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
					ApplicationFunctionLibrary.addSegment(GlobalVariables.testData.get("Segment For Do Undo Redo Cut Task"), "New");
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click 'Add new task' option under 'Actions' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "First Task added";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewTask"));
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
					GlobalVariables.oDriver.findElement(By.name("segment:part:task")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:task"));
					for (int i = 0; i <= 15; i++)
						GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Task 1"));
					GlobalVariables.oElement.sendKeys(Keys.TAB);
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click 'Cut task' option under 'Actions' pop up menu of Task section
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Cut task done";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathTaskActionsMenu"),GlobalVariables.viewElements.get("cutTask"));
					// Get a handle to the open alert, prompt or confirmation
					Alert alert = GlobalVariables.oDriver.switchTo().alert();
					Thread.currentThread();
					Thread.sleep(1000);
					// And acknowledge the alert (equivalent to clicking "OK")
					alert.accept();
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
					// ASSERTION: When clicked on 'Cut task' option, the selected task should not be visible in Information Sharing Plan
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("data-table"));
					List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("td"));
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
					
					// Undo- Click 'Undo cut task' option 'Actions' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Undo cut task done";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("undoCutTask"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// ASSERTION:  When details are entered of respective task, the newly added task should be updated in the segment
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath("/html/body/form/div[5]/div/div[2]/div[2]/span/table/tbody/tr[2]/td[2]/span/a/span"));
				    if(GlobalVariables.oElement.getText().equals(GlobalVariables.testData.get("Task 1")))
				    {
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
				    }
				    else{
				    	GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Task 1' "+" Actual " + GlobalVariables.oElement.getText();
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
				    }
					
				    // Redo- Click 'Redo cut task' option 'Actions' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Redo cut task done";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("redoCutTask"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// ASSERTION: When clicked on 'Redo cut task' option, the task should not be shown on Information Sharing Plan
					GlobalVariables.bIsSuccess = Boolean.FALSE;
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("data-table"));
					tds = GlobalVariables.oElement.findElements(By.tagName("td"));
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
					// Click on 'done' button
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
					alert = GlobalVariables.oDriver.switchTo().alert();
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
			new CCE005_DoUndoRedoCutTask();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}