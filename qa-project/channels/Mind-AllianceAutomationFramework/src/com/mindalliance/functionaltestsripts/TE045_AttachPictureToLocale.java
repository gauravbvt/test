package com.mindalliance.functionaltestsripts;

import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class TE045_AttachPictureToLocale 
{
	public TE045_AttachPictureToLocale(){
		try {
			
			GlobalVariables.sTestCaseId = "TE045_AttachPictureToLocale";
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
				Thread.sleep(2000);
								
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
				Thread.sleep(2000);
				
				// Create Locale 
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Locale Created";
				GlobalVariables.oDriver.findElement(By.name("plan:mo:aspect:localePanel:name")).clear();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("plan:mo:aspect:localePanel:name"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Places"));
				GlobalVariables.oElement.sendKeys(Keys.TAB);
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				//Close 'About Plan' Window
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Open Locale Window				
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Locale Window Opened";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("index"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("plan:mo:aspect:indexed")));
				List <WebElement> options = GlobalVariables.oDropDown.getOptions();
			    for(WebElement option : options) {
			    	if(option.getText().equals(GlobalVariables.viewElements.get("places"))){
			    			option.setSelected();
			    			break;
			    	}
			    }
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				//Select Locale 
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanLocaleLink"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);

				// Click on Picture option
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Picture option selected";
				GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:attachments:container:controls:type")));
				options = GlobalVariables.oDropDown.getOptions();
			    for(WebElement option : options) {
			    	if(GlobalVariables.testData.get("Picture").equals(option.getText())){
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
				
				// Attach file to Locale 
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="File Attached";
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:attachments:container:controls:name"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("This is File 1"));
				GlobalVariables .oElement=GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:attachments:container:controls:upload"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.sTestDataDirectoryPath + "CAP.txt");
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:attachments:container:controls:submit")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Assertion: Verify that file is attached
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.assertion.get("sXpathPlacesAttachmentAssertion")));
				if (GlobalVariables.oElement.getText().contains(GlobalVariables.testData.get("This is File 1"))) {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else {
					GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'This is File 1' "+" Actual "+GlobalVariables.oElement.getText();
			    	// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
			    }
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Delete Attachment
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDeletePlacesAttachment"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				Alert alert = GlobalVariables.oDriver.switchTo().alert();
				// And acknowledge the alert (equivalent to clicking "OK")
				alert.accept();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);				
				GlobalVariables.oDriver.findElement(By.className("close")).click();
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
			new TE045_AttachPictureToLocale();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}