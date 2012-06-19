package com.mindalliance.functionaltestsripts;

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

public class CCE024_DoUndoRedoRemoveFlow
{
	public CCE024_DoUndoRedoRemoveFlow() {
		try {
			GlobalVariables.sTestCaseId = "CCE024_DoUndoRedoRemoveFlow";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			if (GlobalVariables.bIsSuccess) {
				
				// Click on 'Information Sharing Model' link
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Navigated to Information Sharing Model";
				GlobalVariables.oDriver.findElement(By.linkText("Information sharing plan")).click();
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
				ApplicationFunctionLibrary.addSegment(GlobalVariables.testData.get("Segment For Do Undo Redo Remove Flow"), "New");
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				// Click on 'Add new task' option under 'Actions' pop up menu
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
				GlobalVariables.sDescription = "Task detail updated";
				// Add details for New Task
				GlobalVariables.oDriver.findElement(By.name("segment:part:task")).click();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:task"));
				for (int i = 0; i <= 50; i++)
					GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Remove Flow"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				GlobalVariables.oElement.sendKeys(Keys.TAB);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// Click on hide details from action pop-menu bar
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathTaskShowMenu"),GlobalVariables.viewElements.get("hideDetails"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				//  Click on 'add info received' option under 'Receives'  section
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Receive Info";
				// Click on legend for maximize the graph
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathStretchUpShrinkBack"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
//				GlobalVariables.oDriver.findElement(By.linkText("Add info received")).click();
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAddInfoReceive"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:name")).click();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:name"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Add Received Info"));
				GlobalVariables.oElement.sendKeys(Keys.ENTER);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:other:firstChoice")));
				List<WebElement> options = GlobalVariables.oDropDown.getOptions();
			    for(WebElement option : options) {
			    	if(GlobalVariables.viewElements.get("other").equals(option.getText())){
			    			option.setSelected();
			    			break;
			    	}
			    }
			    // WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:other:secondChoice:secondChoice-input")).click();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:other:secondChoice:secondChoice-input"));
				for (int i = 0; i <= 50; i++)
					GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Undo Remove Flow"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				GlobalVariables.oElement.sendKeys(Keys.TAB);
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				
				// Click on Remove flow under more pop up menu
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Remove Flow";
				// Click on Remove flow under more pop up menu
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathReceiveInfoActionMenu"),GlobalVariables.viewElements.get("removeFlow"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathStretchUpShrinkBack"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				Alert alert = GlobalVariables.oDriver.switchTo().alert();
				// And acknowledge the alert (equivalent to clicking "OK")
				alert.accept();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				// Click on Undo remove flow under Action pop up menu
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Undo Remove flow done";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("undoRemoveFlow"));
				// ASSERTION: When details entered, the flow should be connected between two nodes
				GlobalVariables.bIsSuccess = Boolean.FALSE;
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("flow-map"));
				List<WebElement> areas = GlobalVariables.oElement.findElements(By.tagName("area"));
				for(WebElement area:areas){
					GlobalVariables.sStrCheck=area.getAttribute("id");
					System.out.println(area.getAttribute("id"));
				if(area.getAttribute("id").equals("node1"))
					 GlobalVariables.bIsSuccess = Boolean.TRUE;
					 break;
			    }
				if (GlobalVariables.bIsSuccess == Boolean.TRUE) {
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else
			    {
					GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'edge2' "+" Actual " + GlobalVariables.sStrCheck;
			    	// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
			    }					
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				
				// Click on 'Redo Remove flow' option under Action pop up menu
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Redo Remove Flow";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("redoRemoveFlow"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("select-segment:sg-sel")));
				options = GlobalVariables.oDropDown.getOptions();
			    for(WebElement option : options) {
			    	if(option.getText().equals("Segment For Do Undo Redo Remove Flow")){
			    			option.setSelected();
			    			break;
			    	}
			    }
			    // WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
				alert = GlobalVariables.oDriver.switchTo().alert();
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
			new CCE024_DoUndoRedoRemoveFlow();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}