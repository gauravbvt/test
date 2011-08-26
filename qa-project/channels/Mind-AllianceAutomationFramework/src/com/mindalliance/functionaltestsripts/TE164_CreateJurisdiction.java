package com.mindalliance.functionaltestsripts;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class TE164_CreateJurisdiction 
{
	public TE164_CreateJurisdiction(){
		try {
			GlobalVariables.sTestCaseId = "TE164_CreateJurisdiction";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
				if (GlobalVariables.bIsSuccess) {
					
					// Click on 'Information Sharing Model' link
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Navigated to Information Sharing Model";
					GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("informationSharingModel"))).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
										
					//Click on 'About Plan' under show pop-up menu
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="About plan";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlan"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					//Click on 'All organization' under show pop-up menu
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="All Organization";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAboutPlanShowMenu"),GlobalVariables.viewElements.get("allOrganizations"));
					// WebElement Synchronization
				    Thread.currentThread();
				    Thread.sleep(3000);
				    // enter into the text box the name of organization
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("plan:mo:aspect:tabs:panel:newInvolvedContainer:newInvolved"));
					String sOrgName = LogFunctions.getDateTime();
					GlobalVariables.oElement.sendKeys(sOrgName);
					GlobalVariables.oElement.sendKeys(Keys.ENTER);
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click on any organization from list
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Select Organization";
					GlobalVariables.bIsSuccess = Boolean.FALSE;
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("indices-table"));
					List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("li"));
					for (WebElement li: tds){
						if (li.getText().equals(sOrgName)){
							GlobalVariables.bIsSuccess = Boolean.TRUE;
							li.click();
							// WebElement Synchronization
							Thread.currentThread();
							Thread.sleep(3000);
							break;
						}
					}
					if (GlobalVariables.bIsSuccess == Boolean.TRUE) {
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
					}
					else{						
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
					}
					if (GlobalVariables.sBrowser.equals("Internet Explorer")) {
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("index"));
					GlobalVariables.oElement.click();
						GlobalVariables.oElement.sendKeys(Keys.END);
					
					}
					GlobalVariables.oDriver.findElement(By.linkText(sOrgName)).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					
					// Enter  Agent details
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Jurisdiction Created";
					String Agent="Agent 1";
					String Title="Title 1";
					String Role ="Role 1";
					String Jurisdiction ="Jurisdiction 1";
					if (GlobalVariables.sBrowser.equals("Internet Explorer")) {
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.linkText(sOrgName))	;
					GlobalVariables.oElement.click();
					for (int i=1;i<=30;i++)
						GlobalVariables.oElement.sendKeys(Keys.ARROW_DOWN);
					}
					// agent name
					GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:tabContainer:tabs:panel:jobsDiv:jobs:0:actor:entity-field")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:tabContainer:tabs:panel:jobsDiv:jobs:0:actor:entity-field"));
					GlobalVariables .oElement.sendKeys(Agent);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// Title
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:tabContainer:tabs:panel:jobsDiv:jobs:0:title"));
//					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Title"));
					GlobalVariables .oElement.sendKeys(Title);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// Role
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:tabContainer:tabs:panel:jobsDiv:jobs:0:role:entity-field"));
//					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Role"));
					GlobalVariables.oElement.sendKeys(Role);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// Jury
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:tabContainer:tabs:panel:jobsDiv:jobs:0:jurisdiction:entity-field"));
//					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Jurisdiction"));
					GlobalVariables.oElement.sendKeys(Jurisdiction);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// Supervisor
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:tabContainer:tabs:panel:jobsDiv:jobs:0:supervisor:entity-field"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Supervisor"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// Check box
					GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:tabContainer:tabs:panel:jobsDiv:jobs:0:confirmed")).click();
					 // WebElement Synchronization
				    Thread.currentThread();
				    Thread.sleep(3000);
					// Assertion: Verify that Role, Title, Jurisdiction and supervisor can be assign to participating agent within an organization
				    GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.linkText(Jurisdiction));
					if(GlobalVariables.oElement.getText().equals(GlobalVariables.testData.get("Jurisdiction 1")))
					{
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
					}
					else{
						GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Agent 1' "+" Actual "+GlobalVariables.oElement.getText();
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription+""+GlobalVariables.sFailed);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
								GlobalVariables.sBlank, GlobalVariables.sVerifyError);
					}
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					
					// Click on 'done' button
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Plan closed";
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					// Clear details
					GlobalVariables.oDriver.findElement(By.linkText(sOrgName)).click();
					GlobalVariables.oDriver.findElement(By.linkText(sOrgName)).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					// Check box
					GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:tabContainer:tabs:panel:jobsDiv:jobs:0:confirmed")).click();
					 // WebElement Synchronization
				    Thread.currentThread();
				    Thread.sleep(3000);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);					
					// Click on Organization
					GlobalVariables.oDriver.findElement(By.linkText(sOrgName)).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// Remove Organization
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDeleteOrgs"))).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					
					// Call logout()
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Logout is successful";
					ApplicationFunctionLibrary.logout();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				
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
			new TE164_CreateJurisdiction();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}