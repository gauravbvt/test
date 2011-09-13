package com.mindalliance.functionaltestsripts;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class HP045_BlankNameAndEmailId 
{
	public HP045_BlankNameAndEmailId(){
		try{			
			GlobalVariables.sTestCaseId = "HP045_BlankNameAndEmailId";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			if (GlobalVariables.bIsSuccess) {
				
				// Click on About me tab on social panel
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="About me tab is present";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathSocialAboutMe"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000); 
				
				// Click on name text filed and Edit it
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Enter blank name";
				GlobalVariables.oDriver.findElement(By.name("social:tabs:panel:userInfo:fullName")).clear();
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("social:tabs:panel:userInfo:fullName"));
				GlobalVariables.oElement.sendKeys(Keys.TAB);
				// Assertion: 1. Verify that error message should be displayed as 'An email address is required'
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.assertion.get("sXpathEmailNameInvalidErrorMessageAssertion")));
				if(GlobalVariables.oElement.getText().equalsIgnoreCase("The name is required")){
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else{
					GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'The name is required' "+" Actual "+GlobalVariables.oElement.getText();
			    	// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
			    }	
			    // WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000); 
				
				// Click on Email id text filed and Edit it
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Enter blank email id";
				GlobalVariables.oDriver.findElement(By.name("social:tabs:panel:userInfo:email")).clear();
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("social:tabs:panel:userInfo:email"));
				GlobalVariables.oElement.sendKeys(Keys.TAB);
				// Assertion: 1. Verify that error message should be displayed as 'An email address is required'
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.assertion.get("sXpathEmailNameInvalidErrorMessageAssertion")));
				if(GlobalVariables.oElement.getText().equalsIgnoreCase("The name is required")){
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else{
					GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'The name is required' "+" Actual "+GlobalVariables.oElement.getText();
			    	// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
			    }	
			    // WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000); 
				
				// Click on Apply
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Apply";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathApplySocialPanel"))).click();
				// Assertion: Verify that error message should be displayed as 'No changes were made'
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.assertion.get("sXpathErrorMessageSocialPanelAssertion")));
			    if(GlobalVariables.oElement.getText().equalsIgnoreCase("No changes were made")){
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else{
					GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'No changes were made' "+" Actual "+GlobalVariables.oElement.getText();
			    	// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
			    }	
			    // WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);
				
				// Call logout()
			    GlobalVariables.iStepNo++ ;
			    GlobalVariables.sDescription = "Logout is successful";
			    GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathLogoutHomePage"))).click();
			    GlobalVariables.oDriver.quit();
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
			new HP045_BlankNameAndEmailId();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}