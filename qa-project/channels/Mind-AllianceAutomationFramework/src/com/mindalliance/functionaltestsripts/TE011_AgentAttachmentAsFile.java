package com.mindalliance.functionaltestsripts;

import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class TE011_AgentAttachmentAsFile 
{
	public TE011_AgentAttachmentAsFile() {
		try {
			GlobalVariables.sTestCaseId = "TE011_AgentAttachmentAsFile";
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
					Thread.sleep(1000);
				   
					// Clicks on 'About plan' link under show pop up menu option
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "About plan section opened";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlan"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);

					// Clicks on 'Participations' link under show pop up menu option on About plan window 
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Participation section opened";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAboutPlanShowMenu"),GlobalVariables.viewElements.get("participation"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
					
					//Create Agent
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Agent Created";
					GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:participations:participationsTable:participations:body:rows:1:cells:4:cell:entityName")).clear();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:participations:participationsTable:participations:body:rows:1:cells:4:cell:entityName"));
					String sAgentName = LogFunctions.getDateTime();
					GlobalVariables.oElement.sendKeys(sAgentName);
					GlobalVariables.oElement.sendKeys(Keys.ENTER);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					//Click on 'Agent' Details
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAgentContacts"))).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAgent"))).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Attach file to Agent
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Attach File";
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:attachments:container:controls:name"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("This is File 1"));
					GlobalVariables .oElement=GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:attachments:container:controls:upload"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.sTestDataDirectoryPath + "CAP.txt");
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:attachments:container:controls:submit")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// Assertion: verify that file is attached
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath("//a[contains(@href,'uploads/CAP.txt')]"));
					List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("li"));
					for (WebElement li: tds){
						if (li.getText().equals(GlobalVariables.testData.get("AttachmentFileName"))){
							// Write Results
							LogFunctions.writeLogs(GlobalVariables.sDescription);
							LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
									GlobalVariables.sBlank, GlobalVariables.sBlank);
							break;
						}
						else{
							GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'CAP' "+" Actual " + li.getText();
					    	// Write Results
							LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
							LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
									GlobalVariables.sBlank, GlobalVariables.sVerifyError);
							break;
					    }
					}
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// Delete Attachment
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDeleteAgentURL"))).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
					Alert alert = GlobalVariables.oDriver.switchTo().alert();
					// And acknowledge the alert (equivalent to clicking "OK")
					alert.accept();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);							
					
					// Click on done
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Done";
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);	
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);	
					
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
			new TE011_AgentAttachmentAsFile();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}