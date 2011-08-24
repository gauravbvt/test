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
		      Thread.sleep(1000);
		      
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
		      Thread.sleep(1000);
		      
		      // Click on Forgot UserId or Password Link
		      GlobalVariables.iStepNo++;
		      GlobalVariables.sDescription="Forgot UserId or Passowrd Link";
		      GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div[2]/form/div[6]/a")).click();
	    	  // Write Results
	    	  LogFunctions.writeLogs(GlobalVariables.sDescription);
	    	  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
	    	  // WebElement Synchronization
		      Thread.currentThread();
		      Thread.sleep(1000);
		      
		      // Click on Provide user name test box
		      GlobalVariables.iStepNo++;
		      GlobalVariables.sDescription="Enter user name";
		      GlobalVariables.oDriver.findElement(By.name("username")).click();
		      GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("username"));
		      GlobalVariables.oElement.sendKeys("priyanka");
		      // Write Results
	    	  LogFunctions.writeLogs(GlobalVariables.sDescription);
	    	  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
	    	  // WebElement Synchronization
		      Thread.currentThread();
		      Thread.sleep(1000);
		      
		      // Click on Request new password
		      GlobalVariables.iStepNo++;
		      GlobalVariables.sDescription="Request new password ";
		      GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div[2]/form/div[6]/input")).click();
		      // Write Results
	    	  LogFunctions.writeLogs(GlobalVariables.sDescription);
	    	  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
	    	  // WebElement Synchronization
		      Thread.currentThread();
		      Thread.sleep(1000);
		    	 
		      // Click on Back to Login Link
		      GlobalVariables.iStepNo++;
		      GlobalVariables.sDescription="Back to Login Page";
		      GlobalVariables.oDriver.findElement(By.linkText("Back to login")).click();
		      // Write Results
	    	  LogFunctions.writeLogs(GlobalVariables.sDescription);
	    	  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
	    	  // WebElement Synchronization
		      Thread.currentThread();
		      Thread.sleep(5000);
		      
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
