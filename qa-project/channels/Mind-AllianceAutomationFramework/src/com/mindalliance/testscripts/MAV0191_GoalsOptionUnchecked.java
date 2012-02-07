package com.mindalliance.testscripts;

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

public class MAV0191_GoalsOptionUnchecked
{
	public MAV0191_GoalsOptionUnchecked(){
    	try {
    		
    		GlobalVariables.sTestCaseId = "MAV0191_GoalsOptionUnchecked";
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
				ApplicationFunctionLibrary.addSegment(GlobalVariables.testData.get("Segment For Add Task"), "New");
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
				Thread.sleep(2000);
				
				// Click on 'Goals' Link
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Goals Link";
				GlobalVariables.oDriver.findElement(By.linkText("Goals:")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);					
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);

				// Add Goal
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="New Goal Added";
				// Objective
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:goalsDiv:goal:0:kind"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.viewElements.get("mitigate"));
				// Category
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:goalsDiv:goal:0:category"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.viewElements.get("financial"));
				// Magnitude
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:goalsDiv:goal:0:level"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.viewElements.get("minor"));
				// Organization
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:goalsDiv:goal:0:organization:actualOrType"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.viewElements.get("actual"));
				// Name for Goal
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:goalsDiv:goal:0:organization:name"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("AddGoal"));
				// Achieved at end check box
				GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:goalsDiv:goal:0:endsWithSegment")).click();
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
				// Close 'Goals' Window
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);

				// 	Select Goal From Option
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Goals <Options> Selected";
				GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("segment:part:goals:goals:0:goal:goalChoice")));
				List<WebElement> options = GlobalVariables.oDropDown.getOptions();
				options.get(1).setSelected();
				// 	Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);					
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// 	Disselect Goal From Option
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Goals <Options> Disselected";
				GlobalVariables.oDriver.findElement(By.name("segment:part:goals:goals:0:confirmed")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Assertion : Verify that Goals <Options> is Disselected
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("segment:part:goals:goals:0:confirmed"));
				if(GlobalVariables.oElement.isSelected()==false) {
					// 	Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);					
				}
				else {
					// 	Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
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
//				Alert alert = GlobalVariables.oDriver.switchTo().alert();
//				// And acknowledge the alert (equivalent to clicking "OK")
//				alert.accept();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);

				// 	Call Logout
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Logout Successful";
				ApplicationFunctionLibrary.logout();
				// 	Write Results
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
			new MAV0191_GoalsOptionUnchecked();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}