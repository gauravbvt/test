package com.mindalliance.functionaltestsripts;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class TE112_PhaseDescription 
{
	public TE112_PhaseDescription() {
		try {
			
			GlobalVariables.sTestCaseId = "TE112_PhaseDescription";
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
					Thread.sleep(3000);
				    
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
					
					//Enter the new phase in text box for the plan inside 'Phase' section
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Phase added successfully";
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("plan:mo:aspect:phases:phasesDiv:phase:1:name-container:name-input"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Phase 1"));
					GlobalVariables.oElement.sendKeys(Keys.TAB);
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					
					// Click on Phase
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Phase name updated";
					GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.testData.get("Phase 1"))).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:description")).clear();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:description"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("This is an Description"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					
					// Click on Done
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Done";
					GlobalVariables.oDriver.findElement(By.className("float-bar-close")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);			
					// Delete an Phase
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDeletePhase"))).click();
					Alert alert = GlobalVariables.oDriver.switchTo().alert();
					// And acknowledge the alert (equivalent to clicking "OK")
					alert.accept();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
										
					// Click on 'done' button
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Plan closed";
					GlobalVariables.oDriver.findElement(By.className("float-bar-close")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);;
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
				e.printStackTrace();
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
			new TE112_PhaseDescription();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}