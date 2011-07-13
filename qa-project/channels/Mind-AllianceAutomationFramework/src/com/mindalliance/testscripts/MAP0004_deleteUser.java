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
 * Test Case ID: MAP0004_deleteUser
 * Summary: Delete User
 * @author AfourTech
 *
 */
public class MAP0004_deleteUser
{
	public MAP0004_deleteUser() {
		
		try {
			GlobalVariables.sTestCaseId = "MAP0004_deleteUser";
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
				Thread.sleep(5000);	
				
				// Enter the details: newUserId
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "User Id Entered";
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("new"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("User for deletion"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				
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
				Thread.sleep(5000);
				
				// Enter the 'Full Name', 'Email', 'Password' and select the role of the user (Admin/Planner/User/Disable)
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Details of New user entered";
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div[2]/div/form/table"));
				List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("td"));
				for(WebElement td: tds){
					if(td.getText().equals(GlobalVariables.testData.get("User for deletion"))){
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
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
				Thread.sleep(5000);
				
				// Click on 'Submit' button
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "New User Created";
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// Assertion:
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div[2]/div/form/table"));
				tds = GlobalVariables.oElement.findElements(By.tagName("td"));
				for(WebElement td: tds){
					if(td.getText().equals(GlobalVariables.testData.get("User for deletion"))){
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
					    break;
				    }
					else{
					   	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
								GlobalVariables.sBlank, GlobalVariables.sVerifyError);
						break;
					}
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				
				// Delete user
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Delete User";
				GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div[2]/div/form/table/tbody/tr/td/span"));
				GlobalVariables.oDriver.findElement(By.name("item:0:group:delete")).click();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("item:0:group:delete"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				
				// Click on 'Submit' button
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "User Deleted";
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div[2]/div/form/table"));
				tds=null;
				tds = GlobalVariables.oElement.findElements(By.tagName("td"));
				GlobalVariables.bIsSuccess=Boolean.FALSE;
				for(WebElement td: tds)
				{
					if(!td.getText().equals(GlobalVariables.testData.get("User for deletion"))){
						GlobalVariables.bIsSuccess=Boolean.TRUE;
						break;
					}
				}		
				if(GlobalVariables.bIsSuccess==Boolean.FALSE){
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
								
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
				
			}else
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
			new MAP0004_deleteUser();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
