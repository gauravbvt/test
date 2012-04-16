package com.mindalliance.functionaltestsripts;

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

public class CA005_CreatePlanWithExistingURI 
{
	public CA005_CreatePlanWithExistingURI() {
		try {
			GlobalVariables.sTestCaseId = "CA005_CreatePlanWithExistingURI";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
				if (GlobalVariables.bIsSuccess) {
				
					// Click on 'Channels Administration' link
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Navigated to Channels Administration";
					GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("channelsAdministration"))).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					
					// Enter the details: newPlanUri
					GlobalVariables.oDriver.findElement(By.name("newPlanUri")).clear();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("newPlanUri"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Add Test Plan"));
					GlobalVariables.oDriver.findElement(By.name("newPlanClient")).clear();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("newPlanClient"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Afourtech"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);					
					// Click on 'Submit' button
					GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					
					// Enter the details: of existing newPlanUri
					GlobalVariables.iStepNo++ ;
					GlobalVariables.oDriver.findElement(By.name("newPlanUri")).clear();
					GlobalVariables.sDescription = "Existing (New Plan URI) and (owned by) entered";
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("newPlanUri"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Add Test Plan"));
					GlobalVariables.oDriver.findElement(By.name("newPlanClient")).clear();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("newPlanClient"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Afourtech"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					
					// Verify that plan should not get created with existing plan name
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Plan Not Created";
					GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathSamePlanErrorMessage")));
					if(GlobalVariables.oElement.getText().equals(GlobalVariables.viewElements.get("samePlan"))){
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
			    	}
					else{
						GlobalVariables.sVerifyError ="Verification Failed "+"Expected "+GlobalVariables.viewElements.get("samePlan")+" Actual "+GlobalVariables.oElement.getText();
			    		// Write Results
			    		LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
			    		LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
			    				GlobalVariables.sBlank, GlobalVariables.sVerifyError);
					}
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);		
					
					// Delete Created Plan
					GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("plan-sel")));
					List <WebElement> options = GlobalVariables.oDropDown.getOptions();
				    for(WebElement option : options) {
				    	if(GlobalVariables.testData.get("New Plan v.1 (dev)").equals(option.getText())){
				    		option.setSelected();
				    		break;
				    	}
				    }
				    // WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// Click on 'Delete Plan' button
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathDeletePlan"))).click();
					Alert alert = GlobalVariables.oDriver.switchTo().alert();
					// Click on 'OK" button of message box in order to confirm it
					alert.accept();
					//Thread sleep
					Thread.currentThread();
					Thread.sleep(2000);
					
					// Click on 'Signout<user name>' Link
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Logout Successful";
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathLogoutAdminPage"))).click();
					GlobalVariables.oDriver.quit();
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
				new CA005_CreatePlanWithExistingURI();
				GenericFunctionLibrary.tearDownTestData();
				ReportFunctions.generateAutomationReport();
			} 
			catch (Exception oException) {
				// TODO Auto-generated catch block
				oException.printStackTrace();
			}
		}
}
