package com.mindalliance.testscripts;

import org.openqa.selenium.By;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;
/**
 * TestCase ID: MAV0013_viewAllSegmentsForm
 * Summary: Verify that form with heading "All Segments" gets loaded on the About Plan window 
 * @author: AFour
 *
 */
public class MAV0013_viewAllSegmentsForm
{
	public MAV0013_viewAllSegmentsForm() 
	{
	  try{
		  GlobalVariables.sTestCaseId = "MAV0013_viewAllSegmentsForm";
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
			  
			  //clicks on 'all segments' link under show pop up menu option on About plan window 
			  GlobalVariables.iStepNo++ ;
			  GlobalVariables.sDescription = "All Segments section opened";
			  ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAboutPlanShowMenu"), GlobalVariables.viewElements.get("allSegments"));
			  // Write Results
			  LogFunctions.writeLogs(GlobalVariables.sDescription);
			  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					  GlobalVariables.sBlank, GlobalVariables.sBlank);
			  // WebElement Synchronization
			  Thread.currentThread();
			  Thread.sleep(1000);
			
			  // Assertion: Verify that "All Segments" page loaded 
			  GlobalVariables.iStepNo++ ;
			  GlobalVariables.sDescription = "'All Segments' Page renders";
			  GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.assertion.get("sXpathAssertionAllSegments")));
			  if (GlobalVariables.oElement.getText().equals(GlobalVariables.viewElements.get("allInterSegmentFlows"))) {
				  // Write Results
				  LogFunctions.writeLogs(GlobalVariables.sDescription);
				  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						  GlobalVariables.sBlank, GlobalVariables.sBlank);  	     
			  }
			  else
			  {
					GlobalVariables.sVerifyError ="Verification Failed "+"Expected " + GlobalVariables.viewElements.get("allInterSegmentFlows")+" Actual "+GlobalVariables.oElement.getText();
				  // Write Results
				  LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
				  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						  GlobalVariables.sBlank, GlobalVariables.sVerifyError);
			  }
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
			new MAV0013_viewAllSegmentsForm();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
