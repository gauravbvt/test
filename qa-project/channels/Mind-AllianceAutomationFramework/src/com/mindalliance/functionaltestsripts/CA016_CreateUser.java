package com.mindalliance.functionaltestsripts;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class CA016_CreateUser 
{
	public CA016_CreateUser() {
		try {
			
			GlobalVariables.sTestCaseId = "CA016_CreateUser";
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
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					
					// Assertion : Verify that User Created Successfully.
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
								// Write Results
								LogFunctions.writeLogs(GlobalVariables.sDescription);
								LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
									GlobalVariables.sBlank, GlobalVariables.sBlank);
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
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Logout Successful";
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
				new CA016_CreateUser();
				GenericFunctionLibrary.tearDownTestData();
				ReportFunctions.generateAutomationReport();
			} 
			catch (Exception oException) {
				oException.printStackTrace();
			}
		}
}
