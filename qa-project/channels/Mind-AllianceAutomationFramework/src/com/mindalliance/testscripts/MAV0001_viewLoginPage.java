package com.mindalliance.testscripts;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;


/**
 * TestCase ID: MAV0001_viewLoginPage
 * Summary : Verify Login page with 'Channel Information sharing Planning'
 * @author : AFour
 *
 */
public class MAV0001_viewLoginPage{ 
	public MAV0001_viewLoginPage(){
	  try {
		  GlobalVariables.sTestCaseId = "MAV0001_viewLoginPage";
		  GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
		  LogFunctions.writeLogs(GlobalVariables.sDescription);
		  System.out.println(GlobalVariables.sDescription);
		  // WebElement Synchronization
	      Thread.currentThread();
	      Thread.sleep(3000);
			
    	  // Login Page
    	  GlobalVariables.iStepNo++;
    	  GlobalVariables.sDescription="Login Page";
    	  if (GlobalVariables.sBrowser.equals("Mozilla Firefox"))
				GlobalVariables.oDriver = new FirefoxDriver();
    	  if (GlobalVariables.sBrowser.equals("Internet Explorer"))
				GlobalVariables.oDriver = new InternetExplorerDriver();
    	  // WebElement Synchronization
	      Thread.currentThread();
	      Thread.sleep(3000);
		  GlobalVariables.oDriver.get(GlobalVariables.login.get("sChannelURL"));
		  // WebElement Synchronization
	      Thread.currentThread();
	      Thread.sleep(3000);
	      // Assertion: Verify that user is able to view 'Log in' form with title 'ChannelsInformation Sharing Planning'
	      if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.viewElements.get("loginPageTitle"))) {
	    	  // Write Results
	    	  LogFunctions.writeLogs(GlobalVariables.sDescription);
	    	  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
	      }
	      else{
	    	  GlobalVariables.sVerifyError="Verification Failed "+GlobalVariables.oDriver.getTitle()+" expected";
			  // Write Results
			  LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
			  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
					  GlobalVariables.sBlank, GlobalVariables.sVerifyError);
		  }
	      // WebElement Synchronization
	      Thread.currentThread();
	      Thread.sleep(3000);
	      
	      GlobalVariables.oDriver.quit();
	      
	      LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
	      System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
			
		
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
			new MAV0001_viewLoginPage();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
