package com.mindalliance.functionaltestsripts;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class CL017_BackToLogin
{
	public CL017_BackToLogin(){
		try {
			
			GlobalVariables.sTestCaseId = "CL017_BackToLogin";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(2000);
		      
			// Login Page
			GlobalVariables.iStepNo=0;
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="Login Page";
			if (GlobalVariables.sBrowser.equals("Mozilla Firefox"))
				GlobalVariables.oDriver = new FirefoxDriver();
			if (GlobalVariables.sBrowser.equals("Internet Explorer"))
				GlobalVariables.oDriver = new InternetExplorerDriver();
			GlobalVariables.oDriver.get(GlobalVariables.login.get("sChannelURL"));
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(2000);
		      
			// Click on Forgot UserId or Password Link
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="Can't access your account? link clicked";
			GlobalVariables.oDriver.findElement(By.linkText("Can't access your account?")).click();
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(2000);
		
			// Back to Login Link Clicked
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="Back to Login Link Clicked";
			// Click on Back to login link
			GlobalVariables.oDriver.findElement(By.linkText("Back to login")).click();
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);	
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(2000);
		      
			// Login page is displayed
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="Login Page";
			// Assertion : Verify that Login page displayed successfully
			if(GlobalVariables.oDriver.getTitle().equals(GlobalVariables.viewElements.get("loginPageTitle"))) {
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);		    	  
			}
			else {
				GlobalVariables.sVerifyError="Verification Failed. Expected '"+GlobalVariables.viewElements.get("loginPageTitle")+"' Actual '"+GlobalVariables.oDriver.getTitle()+"'";
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription+""+GlobalVariables.sFailed);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sVerifyError, GlobalVariables.sBlank);
			}
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(2000);
			GlobalVariables.oDriver.quit();
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(2000);
		     		      
			LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
			
		} 
		catch (Exception e) {
			if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.id("stackTrace"));
				LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathLogoutHomePage"))).click();
				GlobalVariables.oDriver.quit();
			}
			else {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sBlank);
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathLogoutHomePage"))).click();
				GlobalVariables.oDriver.quit();
			}
			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
		}
	}
	public static void main(String args[]) {
		try {
			GenericFunctionLibrary.initializeTestData();
			GenericFunctionLibrary.loadObjectRepository();
			new CL017_BackToLogin();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}