package com.mindalliance.functionaltestsripts;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class CA021_DisableUser 
{
	public CA021_DisableUser() {
		try {
			GlobalVariables.sTestCaseId = "CA021_DisableUser";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			if (GlobalVariables.bIsSuccess) {
				
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
				Thread.sleep(1000);
				
				// Enter the details: newUserId
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "User Id Entered";
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("new"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Afourtech"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Click on 'Submit' button
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="User Created";
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed,
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);

				// Enter the 'Full Name', 'Email', 'Password' and select the role of the user (Admin/Planner/User/Disable)
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Details of New user entered";
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div[2]/div/form/table"));
				List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("td"));
				for(WebElement td: tds)
				{
					if(td.getText().equals(GlobalVariables.testData.get("AfourTech"))){
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
						break;
					}
				}
				// Full name
				GlobalVariables.oDriver.findElement(By.name("item:0:group:fullName")).clear();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("item:0:group:fullName"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Full Name"));
				// Password
				GlobalVariables.oDriver.findElement(By.name("item:0:group:password")).clear();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("item:0:group:password"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Password"));
				// Role
				GlobalVariables.oDriver.findElement(By.name("item:0:group")).click();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("item:0:group"));
			    // WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Click on 'Submit' button
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();				

				// Disable Existing User
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Existing User Disabled";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathDisableUser"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);							
				// Click on 'Submit' button
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
								
			  	// Click on 'Sign out <user name>' Link
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Logout Successful";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathLogoutAdminPage"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				
				//Assertion : Verify that the Disable user should not be able to login into system
				// Enter User name and password
			    GlobalVariables.iStepNo++;
			    GlobalVariables.sDescription="Login with Disabled User";
			    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_username"));
			    GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("AfourTech"));
			    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_password"));
			    GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Password"));
			    // Write Results
		    	LogFunctions.writeLogs(GlobalVariables.sDescription);
		    	LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
		    	// WebElement Synchronization
			    Thread.currentThread();
			    Thread.sleep(1000);			    
			    // Click on Sign In button
			    GlobalVariables.iStepNo++ ;
			    GlobalVariables.sDescription = "Login Unsuccessful";
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
			    	GlobalVariables.sVerifyError ="Verification Failed "+"Expected "+GlobalVariables.viewElements.get("homePageTitle")+" Actual "+GlobalVariables.oElement.getText();
			    	// Write Results
			    	LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
			    	LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
			    		  GlobalVariables.sBlank, GlobalVariables.sVerifyError);
			    }
			    GlobalVariables.oDriver.quit();
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
				new CA021_DisableUser();
				GenericFunctionLibrary.tearDownTestData();
				ReportFunctions.generateAutomationReport();
			} 
			catch (Exception oException) {
				// TODO Auto-generated catch block
				oException.printStackTrace();
			}
		}
}
