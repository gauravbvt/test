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
				
				//Create User
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="First User Created";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathInputUserID"))).clear();
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathInputUserID")));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("user"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				//Assign Access Privilege 'Admin' to User
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Email Details Entered";
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath("//body/div/div[2]/div/form/table[6]/tbody"));
				List<WebElement> trs = GlobalVariables.oElement.findElements(By.tagName("tr"));
				List<WebElement> tds;
				int i=-1;
				for(WebElement tr: trs) {
					i++;	
					tds = tr.findElements(By.tagName("td"));
					for(WebElement td: tds) {
						if(td.getText().contains(GlobalVariables.testData.get("user"))){
							GlobalVariables.oDriver.findElement(By.name("item:"+i+":group:email")).clear();
							GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("item:"+i+":group:email"));
							GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("email"));
							// WebElement Synchronization
							Thread.currentThread();
							Thread.sleep(2000);
							// Write Results
							LogFunctions.writeLogs(GlobalVariables.sDescription);
							LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
									GlobalVariables.sBlank, GlobalVariables.sBlank);
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

				//Create User
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Second User Created";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathInputUserID"))).clear();
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathInputUserID")));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Afourtech"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				//Assign Access Privilege 'Admin' to User
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Email Details Entered";
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath("//body/div/div[2]/div/form/table[6]/tbody"));
				trs = GlobalVariables.oElement.findElements(By.tagName("tr"));
				i=-1;
				for(WebElement tr: trs) {
					i++;	
					tds = tr.findElements(By.tagName("td"));
					for(WebElement td: tds) {
						if(td.getText().contains(GlobalVariables.testData.get("Afourtech"))){
							GlobalVariables.oDriver.findElement(By.name("item:"+i+":group:email")).clear();
							GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("item:"+i+":group:email"));
							GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("email"));
							// WebElement Synchronization
							Thread.currentThread();
							Thread.sleep(2000);
							// Write Results
							LogFunctions.writeLogs(GlobalVariables.sDescription);
							LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
									GlobalVariables.sBlank, GlobalVariables.sBlank);
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

				// Delete Users
				// Select First User
				GlobalVariables.oElement =GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathUserID")));
				trs = GlobalVariables.oElement.findElements(By.tagName("tr"));
				i=-1;
				for(WebElement tr: trs) {
					i++;
					tds = tr.findElements(By.tagName("td"));
					for(WebElement td: tds) {
						if(td.getText().contains(GlobalVariables.testData.get("Afourtech"))){
							GlobalVariables.oDriver.findElement(By.name("item:"+i+":group:delete")).click();
							// WebElement Synchronization
							Thread.currentThread();
							Thread.sleep(2000);
							break;
						}
					}
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Select Second User
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathUserID")));
				trs = GlobalVariables.oElement.findElements(By.tagName("tr"));
				i=-1;
				for(WebElement tr: trs) {
					i++;	
					tds = tr.findElements(By.tagName("td"));
					for(WebElement td: tds) {
						if(td.getText().contains(GlobalVariables.testData.get("user"))){
							GlobalVariables.oDriver.findElement(By.name("item:"+i+":group:delete")).click();
							// WebElement Synchronization
							Thread.currentThread();
							Thread.sleep(2000);
							break;
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
								
				// Logout of Channels
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Logout is Successful";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathLogoutAdminPage"))).click();
				GlobalVariables.oDriver.quit();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);

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