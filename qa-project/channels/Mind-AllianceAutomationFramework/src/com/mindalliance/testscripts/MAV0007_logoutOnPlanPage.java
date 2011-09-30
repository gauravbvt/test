package com.mindalliance.testscripts;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;
/**
 * Test Case Id: MAV0007_logout
 * Summary: Verify that the page with title "Channels Information Sharing Planning" renders

 * @author: AFour
 *
 */
public class MAV0007_logoutOnPlanPage 
{
	public  MAV0007_logoutOnPlanPage(){
		  try {
			  GlobalVariables.sTestCaseId = "MAV0007_logoutOnPlanPage";
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
				  				  
				  // Call Logout 
				  GlobalVariables.iStepNo++;
				  GlobalVariables.sDescription="Logout";
				  ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),"Sign out "+GlobalVariables.login.get("sUsername"));
				  Alert alert=GlobalVariables.oDriver.switchTo().alert();
				  alert.accept();
			      // Write Results
				  LogFunctions.writeLogs(GlobalVariables.sDescription);
				  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						  GlobalVariables.sBlank, GlobalVariables.sBlank);
				  // WebElement Synchronization
				  Thread.currentThread();
				  Thread.sleep(1000);  
				  // Assertion: Verify that 'Channel information Sharing system' loaded
				  GlobalVariables.iStepNo++;
				  GlobalVariables.sDescription = "'Channels Information Sharing Planning' renders";
				  if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.viewElements.get("loginPageTitle"))) {
					  // Write Results
					  LogFunctions.writeLogs(GlobalVariables.sDescription);
					  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							  GlobalVariables.sBlank, GlobalVariables.sBlank);
				  }
				  else
				  {
					  // Write Results
					  LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							  GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				  }
				  // WebElement Synchronization
				  Thread.currentThread();
				  Thread.sleep(1000);  
				  GlobalVariables.oDriver.quit();
				  
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
			new MAV0007_logoutOnPlanPage();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			System.out.println(oException.getMessage());
			oException.printStackTrace();
		}
	}
}
