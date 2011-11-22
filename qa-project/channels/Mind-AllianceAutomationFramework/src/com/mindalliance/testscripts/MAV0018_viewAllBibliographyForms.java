package com.mindalliance.testscripts;

import org.openqa.selenium.By;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

/**
 * TestCase ID: MAV0018_viewAllBibliographyForms
 * Summary: Verify that form with heading "Bibliography" gets loaded on the About Plan window
 * @author: AFour
 *
 */
public class MAV0018_viewAllBibliographyForms
{
	public MAV0018_viewAllBibliographyForms()
	{
		try{
			GlobalVariables.sTestCaseId = "MAV0018_viewAllBibliographyForms";
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
			ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"), GlobalVariables.viewElements.get("aboutPlan"));
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(1000);
			
			// Clicks on 'Bibliography' link under show pop up menu option on About plan window 
			GlobalVariables.iStepNo++ ;
			GlobalVariables.sDescription = "Bibliography section opened";
			ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAboutPlanShowMenu"),GlobalVariables.viewElements.get("bibliography"));
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(1000);
				 
			// Assertion: Verify that "Bibliography" page loaded 
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="'Bibliography' gets loaded on the About Plan window";		            		         
			GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.assertion.get("sXpathAssertionBibliography")));    			    
			if (GlobalVariables.oElement.getText().equals(GlobalVariables.viewElements.get("documentsAttached"))) {
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);	     
			}
			else{
				GlobalVariables.sVerifyError ="Verification Failed "+"Expected "+ GlobalVariables.viewElements.get("documentsAttached")+" Actual "+GlobalVariables.oElement.getText();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						GlobalVariables.sBlank, GlobalVariables.sVerifyError);
			}
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(1000);	

			// Click on 'done' button
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="Done";
			GlobalVariables.oDriver.findElement(By.className("close")).click();
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(1000);
			
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
			Thread.sleep(1000);	
			
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
			new MAV0018_viewAllBibliographyForms();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
