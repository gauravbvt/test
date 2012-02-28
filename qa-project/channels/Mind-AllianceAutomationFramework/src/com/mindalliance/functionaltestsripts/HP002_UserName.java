package com.mindalliance.functionaltestsripts;

import org.openqa.selenium.By;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class HP002_UserName
{
	public HP002_UserName() {
		try {
			
			GlobalVariables.sTestCaseId = "HP002_UserName";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			if (GlobalVariables.bIsSuccess) {
				// Assertion: Verify that Home page displayed logged in user name as "Welcome <User Name>!"
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Verify User name";
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath("/html/body/form/div[2]/div[2]/div/h1/span"));
				if(GlobalVariables.oElement.getText().equals(GlobalVariables.login.get("sUsernameFullName"))){
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else{
					GlobalVariables.sVerifyError ="Verification Failed "+"Expected '"+GlobalVariables.login.get("sUsernameFullName")+"' Actual "+GlobalVariables.oElement.getText();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				}	
				// Call logout()
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Logout is successful";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathLogoutHomePage"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				GlobalVariables.oDriver.quit();
		      
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
			new HP002_UserName();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}