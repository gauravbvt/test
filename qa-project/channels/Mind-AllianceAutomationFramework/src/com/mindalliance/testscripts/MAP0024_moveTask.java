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
 * Test Case ID: MAP0024_moveTask
 * Summary: Verify that task(s) can be moved from one segment to another segment
 * @author AfourTech
 *
 */
public class MAP0024_moveTask
{
	public MAP0024_moveTask() {
		try {
			GlobalVariables.sTestCaseId = "MAP0024_moveTask";
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
					Thread.sleep(10000);
					
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
					GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:name")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:name"));
					for (int i = 0; i <= 8; i++)
						GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Segment To Verify"));
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);	
				
					// Click on 'Add new segment' option under 'Actions' pop up menu
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewSegment"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:name")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:name"));
					for (int i = 0; i <= 8; i++)
						GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Segment For Move Task"));
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					
					// Select the Segment from 'Select Plan Segment' drop down, located on the top right corner
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Select Plan Segment";
					GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("select-segment:sg-sel")));
					options = GlobalVariables.oDropDown.getOptions();
				    for(WebElement option : options) {
				    	if(GlobalVariables.testData.get("Segment For Move Task").equals(option.getText())){
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
					
					// Add 'New Task' under Action pop-up menu
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewTask"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					// Write Task
					GlobalVariables.oDriver.findElement(By.name("segment:part:task")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:task"));
					for (int i = 0; i <= 50; i++)
						GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
					String task="This is Task 1234";
					GlobalVariables.oElement.sendKeys(task);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					// select category
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:category"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.viewElements.get("audit"));
				    GlobalVariables.oElement.sendKeys(Keys.ENTER);
				    // WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
				    // write location
				    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:location:name"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Location"));
				    GlobalVariables.oElement.sendKeys(Keys.ENTER);
				    // WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
				    // write type
				    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:actor:actualOrType"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.viewElements.get("type"));
				    GlobalVariables.oElement.sendKeys(Keys.ENTER);
				    // WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
				    // Actor name
				    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:actor:name"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Actor"));
				    GlobalVariables.oElement.sendKeys(Keys.ENTER);
				    // WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					// Organization  
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:organization:name"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Afour Tech1234"));
					GlobalVariables.oElement.sendKeys(Keys.ENTER);					
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					// Click on hide details from action pop-menu bar
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathTaskShowMenu"),GlobalVariables.viewElements.get("hideDetails"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
										
					// Click on 'Move task to another segment' under Show pop-up menu
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Move task to segment...";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("moveTasksToSegment"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					
					// Move task to another segment
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Move task";
					GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:destinationSegment")));
					options = GlobalVariables.oDropDown.getOptions();
				    for(WebElement option : options) {
				    	if(GlobalVariables.testData.get("Segment To Verify").equals(option.getText())){
				    			option.setSelected();
				    			break;
				    	}
				    }
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:parts:movableParts:body:rows:1:cells:1:cell:checkBox")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					// Move button
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathMoveTaskButton"))).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);		
					
					// Click on 'done' button
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Task moved";
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);	
					GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("select-segment:sg-sel")));
					options = GlobalVariables.oDropDown.getOptions();
				    for(WebElement option : options) {
				    	if(GlobalVariables.testData.get("Segment To Verify").equals(option.getText())){
				    			option.setSelected();
				    			break;
				    	}
				    }
				    // WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);		
				    // move to segment under action pop-up menu
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("moveTasksToSegment"));
				    // WebElement Synchronization
				    Thread.currentThread();
				    Thread.sleep(5000);
					// Assertion : Verify that task is moved to another segment					
//				    GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath("/html/body/form/div[5]/div/div[2]/div[2]/span/table/tbody/tr/td[2]/span/a/span"));
				    GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.linkText(task));
				    if(GlobalVariables.oElement.getText().equals(task)){
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
				    }
				    else{
				    	GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'This is task 1234' "+" Actual "+GlobalVariables.oElement.getText();
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
								GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				    }
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);					
					GlobalVariables.oDriver.findElement(By.className("close")).click();
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
					
					// Click on 'Remove this segment' under 'Actions' pop up menu
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
					// Get a handle to the open alert, prompt or confirmation
					alert = GlobalVariables.oDriver.switchTo().alert();
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
			new MAP0024_moveTask();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
