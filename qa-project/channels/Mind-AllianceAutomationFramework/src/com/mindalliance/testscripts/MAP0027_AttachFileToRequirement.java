package com.mindalliance.testscripts;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class MAP0027_AttachFileToRequirement {
	public MAP0027_AttachFileToRequirement(){
		
		try {
			GlobalVariables.sTestCaseId = "MAP0027_AttachFileToRequirement";
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
				
				//Clicks on About Plan link under show pop up menu option
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Requirement";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("requirements"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);  
				  
				// Click on New Button to Add Requirement
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "New Requirement";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathNewRequirement"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				// Assertion: Verify that New Requirement can be added
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.assertion.get("sXpathAssertionRequirementName")));
				if(GlobalVariables.oElement.getText().equals("UNNAMED")){
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else{
					GlobalVariables.sVerifyError ="Verification Failed Expected " + GlobalVariables.viewElements.get("UNNAMED") + " Actual "+GlobalVariables.oElement.getText();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);			
				
				// Click on Attachment Tab
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Attachment Tab";
				GlobalVariables.oDriver.findElement(By.xpath("/html/body/form/div[4]/div/span/div/div[2]/div/div[2]/div/div/div[2]/div/ul/li[5]/a/span")).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);  
				
				// Attach file
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Attach File";
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:tabs:panel:requirement:reqSection:container:controls:name"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("AttachmentFileName"));
				GlobalVariables .oElement=GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:tabs:panel:requirement:reqSection:container:controls:upload"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.sTestDataDirectoryPath + "CAP.txt");
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:tabs:panel:requirement:reqSection:container:controls:submit")).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);  
				// Click on Edit Requirement
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathEditRequirement"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000); 
				GlobalVariables.oDriver.findElement(By.xpath("/html/body/form/div[4]/div/span/div/div[2]/div/div[2]/div/div/div[2]/div/ul/li[5]/a/span")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000); 
				
				// Assertion: Verify that file is attached
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="File Attached";
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
						GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'This is File 1' "+" Actual "+li.getText();
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
				// Remove Requirement
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathRemoveRequirement"))).click();
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
				Thread.sleep(1000);	
				
				LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
				System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
			}
			else{
				LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
				System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
				
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				System.out.println("Unable to Redo Add New Requirement" + ReportFunctions.getScreenShot("Redo Add New Requirement failed"));
				GlobalVariables.oDriver.quit();
			}
		} 
		catch (Exception e) {
			if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.id("stackTrace"));
				LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
				System.out.println("Unable to Redo Add New Requirement"+ReportFunctions.getScreenShot("Redo Add New Requirement failed"));
				ApplicationFunctionLibrary.logout();
			}
			else {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sBlank);
				System.out.println("Unable to Redo Add New Requirement"+ReportFunctions.getScreenShot("Redo Add New Requirement failed"));
				ApplicationFunctionLibrary.logout();	
			}
			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
		}
	}
	public static void main(String args[]) {
		try {
			GenericFunctionLibrary.initializeTestData();
			GenericFunctionLibrary.loadObjectRepository();
			new MAP0027_AttachFileToRequirement();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
			System.out.println("Unable to redo add new requirement"+ReportFunctions.getScreenShot("Undo add new requirement failed"));
		}
	}
}
