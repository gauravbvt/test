package com.mindalliance.testscripts;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

/**
 * TestCase ID: MAV0025_closeAboutPlanWindow
 * Summary: Verify that window with title "About Plan:<Plan Name>" gets closed 
 * @author: AFour
 *
 */

public class MAV0025_closeAboutPlanWindow 
{
	public MAV0025_closeAboutPlanWindow()
	{
		try{
			 GlobalVariables.sTestCaseId = "MAV0025_closeAboutPlanWindow";
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
				
				 // Clicks on 'About plan segment' link under show pop up menu option
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
				
				 // Click on done
				 GlobalVariables.iStepNo++;
				 GlobalVariables.sDescription="About plan segment section closed";
				 GlobalVariables.oDriver.findElement(By.className("close")).click();
				 // Write Results
				 LogFunctions.writeLogs(GlobalVariables.sDescription);
				 LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						 GlobalVariables.sBlank, GlobalVariables.sBlank);
				 // WebElement Synchronization
				 Thread.currentThread();
				 Thread.sleep(1000);  
				 			
				 // Assertion: Verify that  About Plan:<Plan Name> window gets closed when clicked on "done" tab 
				 GlobalVariables.iStepNo++;
				 GlobalVariables.sDescription="Verify that window with title '"+GlobalVariables.viewElements.get("aboutPlanPageSubTitle")+"' gets closed";		            		         
				 List<WebElement> objList =GlobalVariables.oDriver.findElements(By.className("close"));	    
				 if (objList.isEmpty() == false){
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
			new MAV0025_closeAboutPlanWindow();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
