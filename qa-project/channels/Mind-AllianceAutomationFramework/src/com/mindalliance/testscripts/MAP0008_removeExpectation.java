package com.mindalliance.testscripts;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;
/**
 * Test Case ID: MAP0008_removeExpectation
 * Summary: Verify that expectation can be removed from the plan
 * @author AfourTech
 *
 */
public class MAP0008_removeExpectation
{
	public MAP0008_removeExpectation() {
		try {
			GlobalVariables.sTestCaseId = "MAP0008_removeExpectation";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
				if (GlobalVariables.bIsSuccess) {
					
					// Select  the plan from 'Switch to Plan' drop down, located on the top right corner
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Switch to plan";
					GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("switch-plan:plan-sel")));
					List <WebElement> options = GlobalVariables.oDropDown.getOptions();
				    for(WebElement option : options) {
				    	if(GlobalVariables.testData.get("Automation Test Plan v.1 (dev)").equals(option.getText())){
				    			option.setSelected();
				    			break;
				    	}
				    }
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);	
					
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
					Thread.sleep(3000);
				    					
					//Click on 'About Plan' from show popup manu
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="About plan";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"), GlobalVariables.viewElements.get("aboutPlan"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					//Click on 'all organizations' from show pop-up menu
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="All orgainzation";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAboutPlanShowMenu"), GlobalVariables.viewElements.get("allOrganizations"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					
					// Enter name of organization in 'Other organization that should be assigned tasks ' field
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription = "Organization Added";
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:tabs:panel:newInvolvedContainer:newInvolved"));
					String sOrgName = LogFunctions.getDateTime();
					GlobalVariables.oElement.sendKeys(sOrgName);
					GlobalVariables.oElement.sendKeys(Keys.ENTER);			
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					
					//  Enter the details about the selected organization in the details form.
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Organization Removed";
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("index"));
					List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("li"));
					for (WebElement li: tds){
						if (li.getText().equals(sOrgName)){
							GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDeleteOrgs"))).click();
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
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
//					// Remove Organization
//					GlobalVariables.iStepNo++;
//					GlobalVariables.sDescription="Organization Removed";
//					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDeleteOrgs"))).click();
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(2000);
//					// Assertion: Verify that Organization Gets Removed
//					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.className("index"));
//					if (GlobalVariables.oElement.getText().equals("")) {
//						// Write Results
//						LogFunctions.writeLogs(GlobalVariables.sDescription);
//						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//								GlobalVariables.sBlank, GlobalVariables.sBlank);
//					}
//					else{
//						GlobalVariables.sVerifyError ="Verification Failed "+"Expected '"+sOrgName+"'"+" Actual "+GlobalVariables.oElement.getText();
//						// Write Results
//						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
//						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//								GlobalVariables.sBlank, GlobalVariables.sVerifyError);
//				    }
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(2000);
					
					// Click on 'done' button
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Plan Updated";
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(200);
					
					// Call logout()
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Logout is successful";
					ApplicationFunctionLibrary.logout();
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
			new MAP0008_removeExpectation();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}