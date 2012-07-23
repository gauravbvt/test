package com.mindalliance.testscripts;

import org.openqa.selenium.By;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

/**
 * TestCase Id: MAV0006_redirectToPlanPage
 * Summary: Verify that the "Plan" page renders
 * @author: AFour
 *
 */
public class MAV0006_redirectToPlanPage
{
	public MAV0006_redirectToPlanPage(){
		  try {
			  GlobalVariables.sTestCaseId = "MAV0006_redirectToPlanPage";
			  GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			  LogFunctions.writeLogs(GlobalVariables.sDescription);
			  System.out.println(GlobalVariables.sDescription);
			  // Call login()
			  GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			  
			  if (GlobalVariables.bIsSuccess) {
				  
				  // Click on 'Information Sharing Model' link
				  GlobalVariables.iStepNo++ ;
				  GlobalVariables.sDescription = "Navigated to Collaboration Plan";
				  GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("informationSharingModel"))).click();
			      // Assertion Verify Plan page displays	 
				  GlobalVariables.iStepNo++;
			      GlobalVariables.sDescription="'Plan' page gets redirected from admin page";
			      if (GlobalVariables.oDriver.getTitle().contains(GlobalVariables.viewElements.get("planPageSubTitle"))) {
			    	  	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
			      }
			      else{
			    	  GlobalVariables.sVerifyError="Verification failed. Expected "+GlobalVariables.viewElements.get("planPageSubTitle")+"Actual Result "+ GlobalVariables.oDriver.getTitle();
			    	  //Write Results
			    	  LogFunctions.writeLogs(GlobalVariables.sDescription);
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
			new MAV0006_redirectToPlanPage();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}