package com.mindalliance.testscripts;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

/**
 * Testcase ID: MAC0045_RedoAddIssue
 * 	   Summary: Verify that user is able to undo the newly added issue
 * @author AFour
 * 
 */
public class MAC0045_RedoAddIssue {
    public MAC0045_RedoAddIssue() {
		try {
			GlobalVariables.sTestCaseId = "MAC0045_RedoAddIssue";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			if (GlobalVariables.bIsSuccess) {
				
				// Click on 'Infsormation Sharing Model' link
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
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Segment For Redo Add New Issue"));
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
				
				// Click on 'Add New Issue' option under 'Actions' pop up menu
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "New Issue added";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewIssue"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// Assertion 1.1: Verify that When clicked on 'Add new Issue' option, a issue should be added in the segment
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath("//div[@class='floating']/span/div[@class='segment']/div[@class='aspect']/span/div[@class='issues']/span/ol/li[3]/span/span[@class='menubar']/span/span[@class='dropmenu']/span"));
				if(GlobalVariables.oElement.getText().equalsIgnoreCase("Menu")){
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else{
					GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Menu' "+" Actual " + GlobalVariables.oElement.getText();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);

				// Click on 'Undo add new issue' under 'Actions' pop up menu
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Undo add new issue done";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu") ,GlobalVariables.viewElements.get("undoAddNewIssue"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);	
				
				// Click on Redo add new issue under Action poo up menu
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Redo add new issue done";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu") ,GlobalVariables.viewElements.get("redoAddNewIssue"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// Assertion 1.1: Verify that When clicked on 'Add new Issue' option, a issue should be added in the segment
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath("//div[@class='floating']/span/div[@class='segment']/div[@class='aspect']/span/div[@class='issues']/span/ol/li[3]/span/span[@class='menubar']/span/span[@class='dropmenu']/span"));
				if(GlobalVariables.oElement.getText().equalsIgnoreCase("Menu")){
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else{
					GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Menu' "+" Actual " + GlobalVariables.oElement.getText();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// Click on Done
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
		System.out.println("Unable to redo add new issue"+ReportFunctions.getScreenShot("Redo add new issue failed"));
	}
  }
    public static void main(String args[]) {
		try {
			GenericFunctionLibrary.initializeTestData();
			GenericFunctionLibrary.loadObjectRepository();
			new MAC0045_RedoAddIssue();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
			System.out.println("Unable to redo add new issue"+ReportFunctions.getScreenShot("Redo add new issue failed"));
		}
	}
}
