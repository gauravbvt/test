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

				// Enter User ID
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="User ID Entered";
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("new"));
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

				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath("//body/div/div[2]/div/form/table[7]/tbody"));
				List<WebElement> trs = GlobalVariables.oElement.findElements(By.tagName("tr"));
				List<WebElement> tds;
				GlobalVariables.bIsSuccess=false;
				int cnt=0;
				for(WebElement tr: trs)
				{
					cnt++;
					tds = tr.findElements(By.tagName("td"));
					for(WebElement td: tds)
					{
						if(td.getText().equals(GlobalVariables.testData.get("user")))
						{
							GlobalVariables.bIsSuccess=true;
							break;
						}
					}
					if(GlobalVariables.bIsSuccess==true)
						break;
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Disable Newly Created User
				if(cnt==1)
				{
					GlobalVariables.oDriver.findElement(By.name("item:"+(cnt-1)+":group:password")).clear();
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("item:"+(cnt-1)+":group:password"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Password"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// Assign Access Privilege
					GlobalVariables.oDriver.findElement(By.xpath("//body/div/div[2]/div/form/table[7]/tbody/tr/td[8]/input")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
				}
				else
				{
					GlobalVariables.oDriver.findElement(By.name("item:"+(cnt-1)+":group:password")).clear();
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("item:"+(cnt-1)+":group:password"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Password"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// Assign Access Privilege
					GlobalVariables.oDriver.findElement(By.xpath("//body/div/div[2]/div/form/table[7]/tbody/tr["+ (cnt-1) +"]/td[8]/input")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
				}
				// Click on 'Submit' Button
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
								
			  	// Click on 'Signout<user name>' Link
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
			    GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("user"));
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
			    // WebElement Synchronization
			    Thread.currentThread();
			    Thread.sleep(2000);
			    
			    // Navigate to Channels Login
			    GlobalVariables.oDriver.navigate().back();
			    // WebElement Synchronization
			    Thread.currentThread();
			    Thread.sleep(2000);

				// Login as an Administrator
			    GlobalVariables.oDriver.findElement(By.name("j_username")).clear();
			    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_username"));
			    GlobalVariables.oElement.sendKeys("jf");
			    GlobalVariables.oDriver.findElement(By.name("j_password")).click();
			    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_password"));
			    GlobalVariables.oElement.sendKeys("Mind-Alliance");
		    	// WebElement Synchronization
			    Thread.currentThread();
			    Thread.sleep(2000);			    
			    // Click on Sign In button
			    GlobalVariables.oDriver.findElement(By.name("_spring_security_remember_me")).click();
			    GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.login.get("sLogin"))).click();
		    	// WebElement Synchronization
			    Thread.currentThread();
			    Thread.sleep(2000);
			    // Navigate to Channels Administration Page
			    GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("channelsAdministration"))).click();
		    	// WebElement Synchronization
			    Thread.currentThread();
			    Thread.sleep(2000);
			    // Delete Created User 
			    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath("//body/div/div[2]/div/form/table[7]/tbody"));
				trs = GlobalVariables.oElement.findElements(By.tagName("tr"));
				GlobalVariables.bIsSuccess=false;
				cnt=0;
				for(WebElement tr: trs) {
					cnt++;
					tds = tr.findElements(By.tagName("td"));
					for(WebElement td: tds) {
						if(td.getText().equals(GlobalVariables.testData.get("user"))) {
							GlobalVariables.bIsSuccess=true;
							break;
						}
					}
					if(GlobalVariables.bIsSuccess==true)
						break;
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);

				//Delete Created User
				if(cnt==1)
					GlobalVariables.oDriver.findElement(By.xpath("//body/div/div[2]/div/form/table[7]/tbody/tr/td[12]/input")).click();
				else
					GlobalVariables.oDriver.findElement(By.xpath("//body/div/div[2]/div/form/table[7]/tbody/tr["+cnt+"]/td[12]/input")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);

				// Click on 'Signout<user name>' Link
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathLogoutAdminPage"))).click();
				GlobalVariables.oDriver.quit();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);				
			    
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
