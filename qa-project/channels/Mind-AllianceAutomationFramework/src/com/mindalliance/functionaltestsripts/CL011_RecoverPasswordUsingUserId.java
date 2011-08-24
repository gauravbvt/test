package com.mindalliance.functionaltestsripts;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class CL011_RecoverPasswordUsingUserId 
{
	public CL011_RecoverPasswordUsingUserId(){
		  try {
			  GlobalVariables.sTestCaseId = "CL011_RecoverPasswordUsingUserId";
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
		      GlobalVariables.sDescription="Username and Password Entered";
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
	    	  // Write Results
	    	  LogFunctions.writeLogs(GlobalVariables.sDescription);
	    	  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
		      // WebElement Synchronization
		      Thread.currentThread();
		      Thread.sleep(2000);

  			  // Click on 'Channels Administration' link
			  GlobalVariables.iStepNo++ ;
			  GlobalVariables.sDescription = "Navigated to Channels Administration";
			  GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("channelsAdministration"))).click();
			  // Write Results
			  LogFunctions.writeLogs(GlobalVariables.sDescription);
			  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			  // WebElement Synchronization
			  Thread.currentThread();
			  Thread.sleep(2000);
			  
			  // Enter User ID
			  GlobalVariables.iStepNo++;
			  GlobalVariables.sDescription="User ID Entered";
			  GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathInputUserID"))).click();
			  GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathInputUserID")));
			  GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("user"));
			  // Write Results
			  LogFunctions.writeLogs(GlobalVariables.sDescription);
			  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					 GlobalVariables.sBlank, GlobalVariables.sBlank);
			  // WebElement Synchronization
			  Thread.currentThread();
			  Thread.sleep(2000);		
			  
			  // Click on 'Submit' button
			  GlobalVariables.iStepNo++ ;
			  GlobalVariables.sDescription = "User Created";
			  GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
			  // Write Results
			  LogFunctions.writeLogs(GlobalVariables.sDescription);
			  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					 GlobalVariables.sBlank, GlobalVariables.sBlank);
			  // WebElement Synchronization
			  Thread.currentThread();
			  Thread.sleep(2000);
			  
			  // Logout of Channels
			  GlobalVariables.iStepNo++ ;
			  GlobalVariables.sDescription = "Logout Successful";
			  GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
			  // Write Results
			  LogFunctions.writeLogs(GlobalVariables.sDescription);
			  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					 GlobalVariables.sBlank, GlobalVariables.sBlank);
			  GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathLogoutAdminPage"))).click();
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
		      GlobalVariables.sDescription="Username Entered";
		      GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("username"));
		      GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("user"));
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
	    	  // WebElement Synchronization
		      Thread.currentThread();
		      Thread.sleep(10000);
		      // Assertion: Verify that a message is displayed to the user as 'A new password is emailed to you'
		      GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.login.get("sXpathRequestPasswordMessage")));
		      if(GlobalVariables.oElement.getText().equals(GlobalVariables.viewElements.get("newPasswordMsg"))) {
		    	  // Write Results
		    	  LogFunctions.writeLogs(GlobalVariables.sDescription);
		    	  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
		      }
		      else{
		    	  GlobalVariables.sVerifyError ="Verification Failed "+"Expected '"+GlobalVariables.viewElements.get("newPasswordMsg")+" Actual "+GlobalVariables.oElement.getText();
		    	  // Write Results
		    	  LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
		    	  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
		    			  GlobalVariables.sBlank, GlobalVariables.sVerifyError);
		      }	
		      // WebElement Synchronization
		      Thread.currentThread();
		      Thread.sleep(2000);		      
		      // Click on Back to login link
		      GlobalVariables.oDriver.findElement(By.linkText("Back to login")).click();
		      // WebElement Synchronization
		      Thread.currentThread();
		      Thread.sleep(2000);
		      // Enter User name and password
		      GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_username"));
		      GlobalVariables.oElement.sendKeys((String)GlobalVariables.login.get("sUsername"));
		      GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_password"));
		      GlobalVariables.oElement.sendKeys((String)GlobalVariables.login.get("sPassword"));
	    	  // WebElement Synchronization
		      Thread.currentThread();
		      Thread.sleep(2000);		      
		      // Click on Sign In button
		      GlobalVariables.oDriver.findElement(By.name("_spring_security_remember_me")).click();
		      GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.login.get("sLogin"))).click();
		      // WebElement Synchronization
		      Thread.currentThread();
		      Thread.sleep(2000);
  			  // Click on 'Channels Administration' link
			  GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("channelsAdministration"))).click();
			  // WebElement Synchronization
			  Thread.currentThread();
			  Thread.sleep(2000);
			  // Delete Created User
			  GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath("//body/div/div[2]/div/form/table[7]/tbody"));
			  List<WebElement> trs = GlobalVariables.oElement.findElements(By.tagName("tr"));
			  List<WebElement> tds;
			  int i=0;
			  for(WebElement tr: trs) {
				i++;	
				tds = tr.findElements(By.tagName("td"));
				for(WebElement td: tds) {
					if(td.getText().contains(GlobalVariables.testData.get("user"))){
						GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div[2]/div/form/table[7]/tbody/tr["+i+"]/td[12]/input")).click();
					}
				}
			  }
			  // WebElement Synchronization
			  Thread.currentThread();
			  Thread.sleep(2000);
			  GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
			  // WebElement Synchronization
			  Thread.currentThread();
			  Thread.sleep(2000);
			  GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathLogoutAdminPage"))).click();
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
				new CL011_RecoverPasswordUsingUserId();
				GenericFunctionLibrary.tearDownTestData();
				ReportFunctions.generateAutomationReport();
			} 
			catch (Exception oException) {
				// TODO Auto-generated catch block
				oException.printStackTrace();
			}
		}
}