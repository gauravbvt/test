package com.mindalliance.functionaltestsripts;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class PS024_MoveTaskToSegment 
{
	public PS024_MoveTaskToSegment() {
		try {
			GlobalVariables.sTestCaseId = "PS024_MoveTaskToSegment";
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
					
					// Click on 'Add new segment' option under 'Actions' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "First segment added";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewSegment"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Enter the details for new segment
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "First Segment's details entered";
					GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name"));
						for (int i = 0; i <= 8; i++)
							GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("The Other Segment"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click on 'done' button
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Segment updated";
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click on 'Add new segment' option under 'Actions' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Second segment added";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewSegment"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Enter the details for new segment
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Second Segment's details entered";
					GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name"));
						for (int i = 0; i <= 8; i++)
							GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("View Segment"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click on 'done' button
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Second Segment Updated";
					GlobalVariables.oDriver.findElement(By.className("close")).click();
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
					Thread.sleep(4000);
					
					// Update the Information of the default task
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "First Task updated";
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
					
					// Click 'Add new task' option under 'Actions' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Second Task added";
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
					GlobalVariables.sDescription = "Second Task updated";
					GlobalVariables.oDriver.findElement(By.name("segment:part:task")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:task"));
					for (int i = 0; i <= 15; i++)
						GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Task 2"));
					GlobalVariables.oElement.sendKeys(Keys.TAB);
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click on 'About plan segment' option under 'Show' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "About Plan Segment section opened";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlanSegment"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					
					// Click on 'Task mover' option under 'Shows' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Task Mover section opened";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAbtPlanSegShowMenu"),GlobalVariables.viewElements.get("taskMover"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Select the other segment to move tasks
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Other Segment selected";
					GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:destinationSegment")));
					List <WebElement> options = GlobalVariables.oDropDown.getOptions();
				    for(WebElement option : options) {
				    	if(option.getText().equals("The Other Segment")){
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
					Thread.sleep(3000);

					// Select the tasks to move
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Tasks selected";
					GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:parts:movableParts:body:rows:2:cells:1:cell:checkBox")).click();
					GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:parts:movableParts:body:rows:3:cells:1:cell:checkBox")).click();
			        // Write Results
				    LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);   
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click on 'Move' button
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Task moved";
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathMoveTaskButton"))).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					// ASSERTION 1.1: When clicked on 'Move' button, the selected task(s) should be moved from current segment to the destination segment
					GlobalVariables.bIsSuccess = Boolean.FALSE;
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("data-table"));
					List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("td"));
					for (WebElement td: tds){
						if (td.getText().equals("Task 1") && (GlobalVariables.bIsSuccess == Boolean.FALSE)) {
							GlobalVariables.bIsSuccess = Boolean.TRUE;
							break;
						}
					}
					if ((GlobalVariables.bIsSuccess == Boolean.TRUE)) {
						GlobalVariables.bIsSuccess = Boolean.FALSE;
						for (WebElement td: tds){
							if (td.getText().equals("Task 2")) {
								GlobalVariables.bIsSuccess = Boolean.TRUE;
								break;
							}
						}
					}
					// Click on 'done' button
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("select-segment:sg-sel")));
					options = GlobalVariables.oDropDown.getOptions();
				    for(WebElement option : options) {
				    	if(GlobalVariables.testData.get("The Other Segment").equals(option.getText())){
				    			option.setSelected();
				    			break;
				    	}
				    }
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
				    // Click on 'About plan segment' option under 'Show' pop up menu
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),"About plan segment");
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// Click on 'Task mover' option under 'Shows' pop up menu
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAbtPlanSegShowMenu"),"Task mover");
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// ASSERTION 1.2: When clicked on 'Move' button, the selected task(s) should be moved from current segment to the destination segment
					if ((GlobalVariables.bIsSuccess == Boolean.FALSE)) {
						GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("data-table"));
						tds = GlobalVariables.oElement.findElements(By.tagName("td"));
						for (WebElement td: tds){
							if (td.getText().equals("Task 1") && (GlobalVariables.bIsSuccess == Boolean.FALSE)) {
								GlobalVariables.bIsSuccess = Boolean.TRUE;
								break;
							}
						}
						if ((GlobalVariables.bIsSuccess == Boolean.TRUE)) {
							GlobalVariables.bIsSuccess = Boolean.FALSE;
							for (WebElement td: tds){
								GlobalVariables.sStrCheck=td.getText();
								if (td.getText().equals("Task 2")) {
									GlobalVariables.bIsSuccess = Boolean.TRUE;
									break;
								}
							}
						}
						if ((GlobalVariables.bIsSuccess == Boolean.TRUE)){
								// Write Results
								LogFunctions.writeLogs(GlobalVariables.sDescription);
								LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
										GlobalVariables.sBlank, GlobalVariables.sBlank);   
								// WebElement Synchronization
								Thread.currentThread();
								Thread.sleep(3000);
								}
						else
					    {
							GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Task 2' "+" Actual " + GlobalVariables.sStrCheck;
					    	// Write Results
							LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
							LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
									GlobalVariables.sBlank, GlobalVariables.sVerifyError);
					    }
					}
					
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
			new PS024_MoveTaskToSegment();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
