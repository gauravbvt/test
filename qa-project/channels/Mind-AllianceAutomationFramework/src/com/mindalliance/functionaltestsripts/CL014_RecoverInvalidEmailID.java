package com.mindalliance.functionaltestsripts;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class CL014_RecoverInvalidEmailID {
	public CL014_RecoverInvalidEmailID(){
		try {
			  
			GlobalVariables.sTestCaseId = "CL014_RecoverInvalidEmailID";
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
			GlobalVariables.sDescription="Can't access your account? link Clicked";
			GlobalVariables.oDriver.findElement(By.linkText("Can't access your account?")).click();
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(2000);
		      
			// Click on Provide user name test box
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="Invalid Username Entered";
			GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("email"));
			GlobalVariables.oElement.sendKeys("priyanka");
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(2000);
			      
			// Click on Request new password
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="Request new password button clicked";
			GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.login.get("sXpathRequestNewPassword"))).click();
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(2000);
						
			// Assertion: Verify that a message is displayed to the user as 'A new password is emailed to you'
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="A new password is emailed to you - message generated";
			GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div[2]/div/span"));
			if(GlobalVariables.oElement.getText().equals("Unrecognized email address.")){
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
			}
			else{
				GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Unrecognized email address.' Actual '"+GlobalVariables.oElement.getText()+"'";
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						GlobalVariables.sBlank, GlobalVariables.sVerifyError);
			}	
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(2000);		
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
				GlobalVariables.oDriver.quit();
			}
			else {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sBlank);
				GlobalVariables.oDriver.quit();
			}
			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
		}
	}
	public static void main(String args[]) {
		try {
			GenericFunctionLibrary.initializeTestData();
			GenericFunctionLibrary.loadObjectRepository();
			new CL014_RecoverInvalidEmailID();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}