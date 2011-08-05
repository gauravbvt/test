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
/**
 * Test Case ID: MAP0002_addNameAndLocalizePlan
 * Summary: Name and Localize Plan
 * @author AfourTech
 *
 */
public class MAP0002_addNameAndLocalizePlan 
{
    public MAP0002_addNameAndLocalizePlan() {
		try {
			GlobalVariables.sTestCaseId = "MAP0002_addNameAndLocalizePlan";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			if (GlobalVariables.bIsSuccess) {
				
				// Click on 'Channel Administration' link
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Navigated to Channel Administration";
				GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("channelsAdministration"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);	
				
				// Enter the details: newPlanUri
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "(New Plan URI) and (owned by) entered";
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("newPlanUri"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Add Test Plan"));
				// newPlanClient
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("newPlanClient"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Afourtech"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Click on 'Submit' button
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Details submitted";
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
			    // Navigate to Home page
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Navigated to Home page";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathHomePageIcon"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Switch Plan
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "New plan selected";
				GlobalVariables.oDropDown= new Select(GlobalVariables.oDriver.findElement(By.name("switch-plan:plan-sel")));
				    List<WebElement> options = GlobalVariables.oDropDown.getOptions(); 
				    for(WebElement option : options){
				    	if(GlobalVariables.testData.get("New Plan v.1 (dev)").equals(option.getText())){
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
				Thread.sleep(2000);
				
				// Click on Information Sharing Model
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Navigated Information Sahring Model";
				GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("informationSharingModel"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);		
				GlobalVariables.oDriver.navigate().refresh();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);		
				
				// Click 'About plan' under 'Show' pop up menu
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "About plan opened";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlan"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				//Enter the Details
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Details entered";
				// Plan Name 
				GlobalVariables.oDriver.findElement(By.name("plan:mo:aspect:name")).clear();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("plan:mo:aspect:name"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Add Test Plan"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Click on 'done' button
				GlobalVariables.iStepNo++ ;
			    GlobalVariables.sDescription="Plan is updated";
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				//Assertion : Verify that Plan Name gets Updated
				//Navigate to Home Page
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathPlanHomeIcon"))).click();
				GlobalVariables.oDriver.navigate().refresh();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				//Navigate to Channels Administration Page
				GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("channelsAdministration"))).click();
				GlobalVariables.oDriver.navigate().refresh();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Select Plan 
				GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("plan-sel")));
				options = GlobalVariables.oDropDown.getOptions();
			    for(WebElement option : options) {
			    	System.out.println(option.getText());
			    	if(GlobalVariables.testData.get("Add Test Plan v.1 (dev)").equals(option.getText())){
			    		// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
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
			new MAP0002_addNameAndLocalizePlan();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
