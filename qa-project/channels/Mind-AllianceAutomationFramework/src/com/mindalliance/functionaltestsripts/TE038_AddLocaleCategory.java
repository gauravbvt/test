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

public class TE038_AddLocaleCategory 
{
	public TE038_AddLocaleCategory() {
		try{
			
			GlobalVariables.sTestCaseId = "TE038_AddLocaleCategory";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			if (GlobalVariables.bIsSuccess) {

				// Navigate to Information Sharing Model Link
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Navigated to Information Sharing Model Link";
				GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("informationSharingModel"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				//About Plan Window Opened
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "About Plan Window Opened";
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
				
				// Add Locale's Category				
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Locale's Category Added";
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
				//Select Locale to add Category
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanLocaleLink"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				//Add Locale's Category
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:types:typesDiv:types:1:typeContainer:newType"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Category 1"));
				GlobalVariables.oElement.sendKeys(Keys.TAB);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				//Close 'About Plan' Window
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				//Select Locale to add Category
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanLocaleLink"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);				
				// Assertion : Verify that Locale's Category is added
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAgentCategory")));
				if(GlobalVariables.oElement.getText().equals(GlobalVariables.testData.get("Category 1"))) {
	    			// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);					
				}
				else {
					GlobalVariables.sVerifyError="Verification Failed. Expected '"+GlobalVariables.testData.get("Category 1")+"' Actual '"+GlobalVariables.oElement.getValue()+"'";
	    			// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sVerifyError, GlobalVariables.sBlank);					
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Remove Locale's Category
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDeleteAgentCategory"))).click();
				Alert alert = GlobalVariables.oDriver.switchTo().alert();
				// Click on 'OK" button of message box in order to confirm it
				alert.accept();
				//Thread sleep
				Thread.currentThread();
				Thread.sleep(2000);				
				//Close 'Locale' Window
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				//Close 'About Plan' Window
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
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathLogoutHomePage"))).click();
				GlobalVariables.oDriver.quit();
			}
			else {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sBlank);
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathLogoutHomePage"))).click();
				GlobalVariables.oDriver.quit();
			}
			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
		}
	}
	public static void main(String args[]) {
		try {
			GenericFunctionLibrary.initializeTestData();
			GenericFunctionLibrary.loadObjectRepository();
			new TE038_AddLocaleCategory();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}