package com.mindalliance.functionaltestsripts;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class CA027_CreateUserWithSameEmailID
{
	public CA027_CreateUserWithSameEmailID() {
		try {
			
			GlobalVariables.sTestCaseId = "CA027_CreateUserWithSameEmailID";
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
				Thread.sleep(2000);
				
				// Create User
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="User Created";
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathInputUserID")));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("user"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);								
				// Click on 'Submit' button
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);								
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				//Assign Access Privilege 'Admin' to User
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Assign Access Privilege 'Admin' to User";
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath("//body/div/div[2]/div/form/table[7]/tbody"));
				List<WebElement> trs = GlobalVariables.oElement.findElements(By.tagName("tr"));
				List<WebElement> tds;
				int i=-1;
				for(WebElement tr: trs) {
					i++;	
					tds = tr.findElements(By.tagName("td"));
					for(WebElement td: tds) {
						if(td.getText().contains(GlobalVariables.testData.get("user"))){
							GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("item:"+i+":group:password"));
							GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Password"));
							// WebElement Synchronization
							Thread.currentThread();
							Thread.sleep(2000);
							GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div[2]/div/form/table[7]/tbody/tr["+(i+1)+"]/td[5]/input")).click();
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

				
				
				
				
				
//				// Click on 'Submit' button
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "New User Created";
//				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//
//				// Enter the details: newUserId
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "User 2 Id Entered";
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("new"));
//				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("AfourTech2"));
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//				
//				// Click on 'Submit' button
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "Details submitted";
//				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//				
//				// Enter the 'Full Name', 'Email', 'Password' and select the role of the user (Admin/Planner/User/Disable)
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "Details of New user entered";
//				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div[2]/div/form/table"));
//				tds = GlobalVariables.oElement.findElements(By.tagName("td"));
//				for(WebElement td: tds)
//				{
//					if(td.getText().equals(GlobalVariables.testData.get("AfourTech2"))){
//						// Write Results
//						LogFunctions.writeLogs(GlobalVariables.sDescription);
//						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//								GlobalVariables.sBlank, GlobalVariables.sBlank);
//						break;
//					}
//				}
//				// Full name
//				GlobalVariables.oDriver.findElement(By.name("item:0:group:fullName")).clear();
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("item:0:group:fullName"));
//				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Full Name"));
//				// Email Id
//				GlobalVariables.oDriver.findElement(By.name("item:0:group:email")).clear();
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("item:0:group:email"));
//				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("email@gmail.com"));
//				// Password
//				GlobalVariables.oDriver.findElement(By.name("item:0:group:password")).clear();
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("item:0:group:password"));
//				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Password"));
//				// Role
//				GlobalVariables.oDriver.findElement(By.name("item:0:group")).click();
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("item:0:group"));
//			    // WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//				
//				// Click on 'Submit' button
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "New User Created";
//				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//				
//				//Deleting user
//				GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div[2]/div/form/table/tbody/tr/td/span"));
//				GlobalVariables.oDriver.findElement(By.name("item:0:group:delete")).click();
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("item:0:group:delete"));
//				// Click on 'Submit' button
//				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//				
//				//Deleting user
//				GlobalVariables.oDriver.findElement(By.xpath("/html/body/div/div[2]/div/form/table/tbody/tr/td/span"));
//				GlobalVariables.oDriver.findElement(By.name("item:0:group:delete")).click();
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("item:0:group:delete"));
//				// Click on 'Submit' button
//				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//				
//				// Call logout()
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "Logout is successful";
//				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathLogoutAdminPage"))).click();
//				GlobalVariables.oDriver.quit();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
			
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
			new CA027_CreateUserWithSameEmailID();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}