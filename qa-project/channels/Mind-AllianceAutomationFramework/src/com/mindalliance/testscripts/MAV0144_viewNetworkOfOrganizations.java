package com.mindalliance.testscripts;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class MAV0144_viewNetworkOfOrganizations 
{
	public MAV0144_viewNetworkOfOrganizations()
	{
		try{
			
			GlobalVariables.sTestCaseId = "MAV0144_viewNetworkOfOrganizations";
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
				Thread.sleep(1000);  
				
				// Clicks on 'About plan' link under show pop up menu option
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "About plan section opened";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlan"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);

				// Clicks on 'All organizations' link under show pop up menu option on About plan window 
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="All organizations section opened";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAboutPlanShowMenu"),GlobalVariables.viewElements.get("allOrganizations"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				
				// Create an organizations
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Organizations created";
				GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:tabs:panel:newInvolvedContainer:newInvolved")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:tabs:panel:newInvolvedContainer:newInvolved"));
				String sOrgName = LogFunctions.getDateTime();
				GlobalVariables.oElement.sendKeys(sOrgName);
				GlobalVariables.oElement.sendKeys(Keys.ENTER);	
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				GlobalVariables.oDriver.findElement(By.linkText(sOrgName)).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				
				// Click on Network under show pop up menu
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Organizations Network";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathPhaseShowMenu"),GlobalVariables.viewElements.get("network"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				
				// Assertion: Verify that "Organizations Network" page loaded 
			    GlobalVariables.iStepNo++;
			    GlobalVariables.sDescription="'Organiztions Network details' page gets loaded";
			    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.assertion.get("sXpathPhaseAssertionDetails")));
			    if (GlobalVariables.oElement.getText().equals(GlobalVariables.viewElements.get("network"))) {
			    	// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
	            }
			    else{
			    	GlobalVariables.sVerifyError ="Verification Failed "+"Expected "+GlobalVariables.viewElements.get("network")+" Actual "+GlobalVariables.oElement.getText();
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
				// Remove Organizations
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDeleteOrgs"))).click();
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
			new MAV0144_viewNetworkOfOrganizations();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
