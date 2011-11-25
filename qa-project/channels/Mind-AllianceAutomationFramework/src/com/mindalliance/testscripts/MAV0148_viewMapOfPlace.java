package com.mindalliance.testscripts;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class MAV0148_viewMapOfPlace 
{
	public MAV0148_viewMapOfPlace()
	{
		try{
			
			GlobalVariables.sTestCaseId = "MAV0148_viewMapOfPlace";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			if (GlobalVariables.bIsSuccess) {
				
				// Click on 'Information Sharing Model' link
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Navigated to Information Sharing Model";
				GlobalVariables.oDriver.findElement(By.linkText("Information sharing model")).click();
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
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu") , "About plan");
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				
				// Enter the details of Place in locale text box
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Details entered";
//				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanLocale"))).click();
//				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanLocale")));
//				for (int i = 0; i <= 50; i++)
//					GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
				GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:localePanel:name")).clear();
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:localePanel:name"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Places"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				// Click on Done
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				
				// Click on Index under show pop up menu
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Index section opened";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu "), "Index");
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				
				// Select the plan from 'Switch to Places' drop down, located on the top right corner
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Places Selected";
				GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:indexed")));
				List <WebElement> options = GlobalVariables.oDropDown.getOptions();
			    for(WebElement option : options) {
			    	if(option.getText().equals(GlobalVariables.viewElements.get("places"))){
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
			    GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanLocaleLink"))).click();
			    GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanLocaleLink"))).click();
			    // WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				
				// Click on map under show pop up menu
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Organizations map";
				ApplicationFunctionLibrary.MouseOverAndClick("/html/body/form/div[3]/div/div[2]/div/div/span/span/span/span","Map");
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				
				// Assertion: Verify that "Places Map details" page loaded 
			    GlobalVariables.iStepNo++;
			    GlobalVariables.sDescription="'Places-Map details' page gets loaded";
			    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath("/html/body/form/div[3]/div/div[2]/div/div/h1"));
			    if (GlobalVariables.oElement.getText().equals("details")) {
			    	// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
	            }
			    else{
			    	GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'details' "+" Actual "+GlobalVariables.oElement.getText();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				}
			    // WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				// Click on Done
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				GlobalVariables.oDriver.findElement(By.className("close")).click();
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
			new MAV0148_viewMapOfPlace();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
