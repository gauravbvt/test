package com.mindalliance.testscripts;

import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class MAV0079_pasteAttachmentUnerAction
{
	public MAV0079_pasteAttachmentUnerAction() {
		try {
			GlobalVariables.sTestCaseId = "MAV0079_pasteAttachmentUnerAction";
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
				
				// Click on About Plan segment under show pop up menu
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "About Plan Segment";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlanSegment"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);  	
				
				// Attach a File to the segment
				String file="This is File 1";
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:attachments:container:controls:name"));
				GlobalVariables.oElement.sendKeys(file);
				GlobalVariables .oElement=GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:attachments:container:controls:upload"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.sTestDataDirectoryPath + "CAP.txt");
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:attachments:container:controls:submit")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// Copy Attachment
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathCutAttachment"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// delete attachment
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDeletePlanSegAttachment"))).click();
				Alert alert = GlobalVariables.oDriver.switchTo().alert();
				// And acknowledge the alert (equivalent to clicking "OK")
				alert.accept();
				// Click on Done
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				// Click on Paste attachment under Action pop up menu
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Paste Attachment done";
		    	ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("pasteAttachment"));
				// WebElement Synchronization
			    Thread.currentThread();
			    Thread.sleep(3000);
			    // Assertion: Verify that attachment has been added
			    ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlanSegment"));
			    // WebElement Synchronization
			    Thread.currentThread();
			    Thread.sleep(3000);
			    GlobalVariables.oDriver.findElement(By.linkText(file)).click();
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("attach"));
				List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("li"));
				for (WebElement li: tds){
					if (li.getText().equals(GlobalVariables.viewElements.get("file1"))){
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
						break;
					}
					else{
						GlobalVariables.sVerifyError ="Verification Failed "+"Expected "+GlobalVariables.viewElements.get("file1")+" Actual "+li.getText();
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
								GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				    }
				}
				// WebElement Synchronization
			    Thread.currentThread();
			    Thread.sleep(3000);
			    
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
				System.out.println(e.getMessage()+"HI.......");
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
			new MAV0079_pasteAttachmentUnerAction();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
