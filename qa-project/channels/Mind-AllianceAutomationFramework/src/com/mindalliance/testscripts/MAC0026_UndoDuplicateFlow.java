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

public class MAC0026_UndoDuplicateFlow
{
	public MAC0026_UndoDuplicateFlow() {
		try {
			GlobalVariables.sTestCaseId = "MAC0026_UndoDuplicateFlow";
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
				GlobalVariables.sDescription = "Second segment added";
				ApplicationFunctionLibrary.addSegment(GlobalVariables.testData.get("Segment For Undo duplicate Flow"), "New");
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
				// Task Name
				GlobalVariables.oDriver.findElement(By.name("segment:part:task")).click();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:task"));
				for (int i = 0; i <= 15; i++)
					GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Duplicate Flow"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// select category
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:category"));
				GlobalVariables.oElement.sendKeys("Audit");
			    GlobalVariables.oElement.sendKeys(Keys.ENTER);
			    // Click on hide details from action pop-menu bar
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathTaskShowMenu"),GlobalVariables.viewElements.get("Hide details"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				//  Click on 'add info received' option under 'Receives'  section
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Duplicate Flow";
				// Click on legend for maximize the graph
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathLegend"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAddInfoReceive"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:name")).click();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:name"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Duplicate this flow Info"));
				GlobalVariables.oElement.sendKeys(Keys.ENTER);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:other:firstChoice")));
				List<WebElement> options = GlobalVariables.oDropDown.getOptions();
			    for(WebElement option : options) {
			    	if(GlobalVariables.viewElements.get("Other").equals(option.getText())){
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
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Undo Duplicate Flow"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				GlobalVariables.oElement.sendKeys(Keys.TAB);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Click on Break up flow under more pop up menu
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathReceiveFlowMoreMenu"),GlobalVariables.viewElements.get("duplicateFlow"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathLegend"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				// Click on 'Undo duplicate flow' under Action pop up menu
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Undo Duplicate Flow";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("undoDuplicateFlow"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(8000);
				GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("select-segment:sg-sel")));
				options = GlobalVariables.oDropDown.getOptions();
			    for(WebElement option : options) {
			    	if(option.getText().equals("Segment For Undo duplicate Flow")){
			    			option.setSelected();
			    			break;
			    	}
			    }
			    // WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
				Alert alert = GlobalVariables.oDriver.switchTo().alert();
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
			new MAC0026_UndoDuplicateFlow();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
