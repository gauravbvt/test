package com.mindalliance.functionaltestsripts;

import org.openqa.selenium.By;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class CP002_ShowPlanners 
{
	public CP002_ShowPlanners(){
		  try {
			  GlobalVariables.sTestCaseId = "CP002_ShowPlanners";
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
				  
				  // Clicks on Hide planners link under show pop up menu option
				  GlobalVariables.iStepNo++ ;
				  GlobalVariables.sDescription = "Hide Planners";
				  ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("hidePlanners"));
				  // Write Results
				  LogFunctions.writeLogs(GlobalVariables.sDescription);
				  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						  GlobalVariables.sBlank, GlobalVariables.sBlank);
				  // WebElement Synchronization
				  Thread.currentThread();
				  Thread.sleep(1000);  					
				 
			 	  
			 	  // Clicks on Planners link under show pop up menu option 
			 	  GlobalVariables.iStepNo++ ;
				  GlobalVariables.sDescription = "Show Planners";
				  ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("planners"));
				  // Write Results
				  LogFunctions.writeLogs(GlobalVariables.sDescription);
				  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						  GlobalVariables.sBlank, GlobalVariables.sBlank);
				  // WebElement Synchronization
				  Thread.currentThread();
				  Thread.sleep(1000);  				
				  GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowPopUpMenu"))).click();
				  // WebElement Synchronization
			 	  Thread.currentThread();
			 	  Thread.sleep(500);
			 	  
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
			new CP002_ShowPlanners();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
