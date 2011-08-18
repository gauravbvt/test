package com.mindalliance.functionaltestsripts;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class CL003_LoginValidUserName
{
	public CL003_LoginValidUserName(){
		  try {
			  GlobalVariables.sTestCaseId = "CL003_LoginValidUserName";
			  GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			  LogFunctions.writeLogs(GlobalVariables.sDescription);
			  System.out.println(GlobalVariables.sDescription);
			  // WebElement Synchronization
		      Thread.currentThread();
		      Thread.sleep(2000);
		      
		      // Login Page
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
		      
		      // Enter User name and password
		      GlobalVariables.iStepNo++;
		      GlobalVariables.sDescription="Valid Username and Password Entered";
		      GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_username"));
		      GlobalVariables.oElement.sendKeys((String)GlobalVariables.login.get("sUsername"));
		      GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_password"));
		      GlobalVariables.oElement.sendKeys((String)GlobalVariables.login.get("sPassword"));
		      // Write Results
	    	  LogFunctions.writeLogs(GlobalVariables.sDescription);
	    	  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
	    	  // WebElement Synchronization
		      Thread.currentThread();
		      Thread.sleep(2000);
		      
		      // Click on Sign In button
		      GlobalVariables.iStepNo++ ;
		      GlobalVariables.sDescription = "Login is successful";
		      GlobalVariables.oDriver.findElement(By.name("_spring_security_remember_me")).click();
		      GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.login.get("sLogin"))).click();
		      // Assertion: Verify that is Login is successful
		      if (GlobalVariables.oDriver.getTitle().contains(GlobalVariables.viewElements.get("homePageTitle"))) {
		    	  // Write Results
		    	  LogFunctions.writeLogs(GlobalVariables.sDescription);
		    	  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
		      }
		      else{
		    	  GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Channels - Sign in' "+" Actual "+GlobalVariables.oElement.getText();
		    	  // Write Results
		    	  LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
		    	  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
		    			  GlobalVariables.sBlank, GlobalVariables.sVerifyError);
		      }	
		      // WebElement Synchronization
		      Thread.currentThread();
		      Thread.sleep(2000);
		      
		      // Call Logout
		      GlobalVariables.iStepNo++ ;
		      GlobalVariables.sDescription = "Logout is successful";
		      GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathLogoutHomePage"))).click();
		      // Write Results
		      LogFunctions.writeLogs(GlobalVariables.sDescription);
		      LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
		    		  GlobalVariables.sBlank, GlobalVariables.sBlank);
		      // WebElement Synchronization
		      Thread.currentThread();
		      Thread.sleep(2000);
		      
		      GlobalVariables.oDriver.quit();
		      GlobalVariables.iStepNo=0;

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
				new CL003_LoginValidUserName();
				GenericFunctionLibrary.tearDownTestData();
				ReportFunctions.generateAutomationReport();
			} 
			catch (Exception oException) {
				// TODO Auto-generated catch block
				oException.printStackTrace();
			}
		}
}