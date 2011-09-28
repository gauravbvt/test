package com.mindalliance.functionaltestsripts;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class TE125_PhaseURL
{
	public TE125_PhaseURL() {
		try {
			GlobalVariables.sTestCaseId = "TE125_PhaseURL";
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
					Thread.sleep(10000);
				    
					// Click on 'Add new segment' option under 'Actions' pop up menu
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Segment Added";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewSegment"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);	
					
					// Details of Segment
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Details of Segment enetered";
					GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:name")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:name"));
					for (int i = 0; i <= 8; i++)
						GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Segment For Attach URL To Phase"));
					GlobalVariables.oDriver.findElement(By.className("close")).click();
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
					Thread.sleep(1000);
					
					//Enter the new phase in text box for the plan inside 'Phase' section
					String phase="This is phase";
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Phase added successfully";
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("plan:mo:aspect:phases:phasesDiv:phase:1:name-container:name-input"));
					GlobalVariables.oElement.sendKeys(phase);
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
					GlobalVariables.sDescription="Phase categories added";
					GlobalVariables.oDriver.findElement(By.linkText(phase)).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					
					// Click on 'URL' radio button
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="URL radio button clicked";			
					GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:attachments:container:controls:radios")).click();
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:attachments:container:controls:radios"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					
					//Attach Attachment as a URL
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Attachment attached as a URL";
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathURL"))).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:attachments:container:controls:url"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("URL"));
					GlobalVariables.oElement.sendKeys(Keys.TAB);
					if (GlobalVariables.sBrowser.equals("Internet Explorer")) {
						GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("controls"));
						GlobalVariables.oElement.click();
							GlobalVariables.oElement.sendKeys(Keys.ARROW_UP);
						}
				    // WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					GlobalVariables.oDriver.findElement(By.className("close")).click();
				    // WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					GlobalVariables.oDriver.findElement(By.linkText(phase)).click();
				    // WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);
					//Assertion : Verify that URL gets Attached
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.assertion.get("sXpathPhaseAttachmentAssertion")));
					System.out.println(GlobalVariables.oElement.getText());
					System.out.println(GlobalVariables.testData.get("URLAssertion"));
					if(GlobalVariables.oElement.getText().equals(GlobalVariables.testData.get("URLAssertion"))){
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				    }
			    	else{
			    		GlobalVariables.sVerifyError="Verification Failed. Expected '"+GlobalVariables.testData.get("URLAssertion")+"' Actual '"+GlobalVariables.oElement.getText()+"'";
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sVerifyError, GlobalVariables.sBlank);
				    }
					// Delete Attachment
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDeletePhaseAttachment"))).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
					Alert alert = GlobalVariables.oDriver.switchTo().alert();
					// And acknowledge the alert (equivalent to clicking "OK")
					alert.accept();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
					
					// Click on Done
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Done";
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);			
					// Delete an Phase
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDeletePhase"))).click();
					alert = GlobalVariables.oDriver.switchTo().alert();
					// And acknowledge the alert (equivalent to clicking "OK")
					alert.accept();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
										
					// Click on 'done' button
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Plan closed";
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);;
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
					// Click on 'Remove this segment' under 'Actions' pop up menu
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));					
					// Get a handle to the open alert, prompt or confirmation
					alert = GlobalVariables.oDriver.switchTo().alert();
					alert.accept();
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
			new TE125_PhaseURL();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}