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
 * Test Case ID: MAP0003_addNewUserToPlan
 * Summary: Add user to Plan
 * @author AfourTech
 *
 */
public class MAP0003_addNewUserToPlan 
{
	public MAP0003_addNewUserToPlan() {
		try {
			GlobalVariables.sTestCaseId = "MAP0003_addNewUserToPlan";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			if (GlobalVariables.bIsSuccess) {
				
				// Click on 'Channel Administration' link
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Navigated to Channel Administration";
				GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("channelsAdministration"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);	
				
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
				Thread.sleep(3000);
				
				// Click on 'Submit' button
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Details submitted";
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
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
				Thread.sleep(3000);
				
				// Click on 'Submit' button
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "New User Created";
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// Assertion:
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div[2]/div/form/table"));
				tds = GlobalVariables.oElement.findElements(By.tagName("td"));
				for(WebElement td: tds)
				{
					if(td.getText().equals(GlobalVariables.testData.get("New Plan URI:"))){
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
						break;
					}
					else{
						GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'AfourTech' "+" Actual "+td.getText();
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription+""+GlobalVariables.sFailed);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
								GlobalVariables.sBlank, GlobalVariables.sVerifyError);	  
						break;
					}
				}
				//Deleting user
				GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div[2]/div/form/table/tbody/tr/td/span"));
				GlobalVariables.oDriver.findElement(By.name("item:0:group:delete")).click();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("item:0:group:delete"));
				// Click on 'Submit' button
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				// Call logout()
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Logout is successful";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathLogoutAdminPage"))).click();
				GlobalVariables.oDriver.quit();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
			
				LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
				System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
			
			}
			else{
				LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
				System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
				
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				System.out.println("Unable to Add New User To The Plan" + ReportFunctions.getScreenShot("Add New User To The Plan failed"));
				GlobalVariables.oDriver.quit();
			}
		} 
		catch (Exception e) {
			if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.id("stackTrace"));
				LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
				System.out.println("Unable to Add New User To The Plan"+ReportFunctions.getScreenShot("Add New User To The Plan failed"));
				ApplicationFunctionLibrary.logout();
			}
			else {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sBlank);
				System.out.println("Unable to Add New User To The Plan"+ReportFunctions.getScreenShot("Add New User To The Plan failed"));
				ApplicationFunctionLibrary.logout();	
			}
			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
		}
	}
    public static void main(String args[]) {
		try {
			GenericFunctionLibrary.initializeTestData();
			GenericFunctionLibrary.loadObjectRepository();
			new MAP0003_addNewUserToPlan();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
			System.out.println("Unable to Add New User To The Plan"+ReportFunctions.getScreenShot("Add New User To The Plan failed"));
		}
	}
}
