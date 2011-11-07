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
 * Testcase ID: MAC0008_UndoPasteTaskUsingCopy
 * 	   Summary: Verify that user is able to undo the task which was pasted using copy command
 * @author AFour
 * 
 */
public class MAC0008_UndoPasteTaskUsingCopy {
	public MAC0008_UndoPasteTaskUsingCopy() {
		try {
			GlobalVariables.sTestCaseId = "MAC0008_UndoPasteTaskUsingCopy";
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
					Thread.sleep(10000);
					
					// Click 'Add new Segment' option under 'Actions' pop up menu and enter the details
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "New segment added";
					ApplicationFunctionLibrary.addSegment(GlobalVariables.testData.get("Segment For Undo Paste Task Using Copy"), "New");
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click 'Add new task' option under 'Actions' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "New Task added";
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
					
					// Click 'Copy task' option under 'Actions' pop up menu of Task section
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Copy task done";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathTaskActionsMenu"),GlobalVariables.viewElements.get("copyTask"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// ASSERTION: When clicked on 'Copy task' option, the task should be copied and  a message ' Task copied' should be displayed at the top of the window
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.assertion.get("sXpathCopyAttachmentAssertion")));
					if (GlobalVariables.oElement.getText().equals("Task copied")) {
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
					}
					else{
						GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Task copied' "+" Actual " + GlobalVariables.oElement.getText();
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
								GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				    }
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click 'Add new Segment' option under 'Actions' pop up menu and enter the details
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "New segment added";
					ApplicationFunctionLibrary.addSegment(GlobalVariables.testData.get("The Other Segment"), "New");
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click 'Paste task' option under 'Actions' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Paste task done";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("pasteTask"));
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
					// ASSERTION: When clicked on 'Paste task' option, the selected cut task should be pasted in Information Sharing Plan in other segment
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath("/html/body/form/div[5]/div/div[2]/div[2]/span/table/tbody/tr[2]/td[2]/span/a/span"));
				    if(GlobalVariables.oElement.getText().equals(GlobalVariables.testData.get("Task 1"))){
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
				    }
				    else{
				    	GlobalVariables.sVerifyError ="Verification Failed "+"Expected '"+GlobalVariables.testData.get("Task 1")+"' "+" Actual " + GlobalVariables.oElement.getText();
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
				    }
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click 'Undo paste task' option under 'Actions' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Undo paste task done";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("undoPasteTask"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// ASSERTION: When clicked on 'Undo paste task' option, the task which is pasted should be removed from Information Sharing Plan in other segment
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("data-table"));
					List <WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("td"));
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
								GlobalVariables.sBlank, GlobalVariables.sBlank);
				    }
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// Click on 'done' button
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
					Alert alert = GlobalVariables.oDriver.switchTo().alert();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
					alert.accept();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("select-segment:sg-sel")));
					List <WebElement> options = GlobalVariables.oDropDown.getOptions();
				    for(WebElement option : options) {
				    	if("Segment For Undo Paste Task Using Copy".equals(option.getText())){
				    			option.setSelected();
				    			break;
				    	}
				    }
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
			new MAC0008_UndoPasteTaskUsingCopy();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}