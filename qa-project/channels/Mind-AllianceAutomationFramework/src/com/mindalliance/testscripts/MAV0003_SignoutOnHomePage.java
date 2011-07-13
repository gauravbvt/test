package com.mindalliance.testscripts;

import org.openqa.selenium.By;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

/**
 * Test Case Id: MAV0003_logoutOnPlanPage
 * Summary: Verify that the page with title 'Channels Information Sharing Planning' renders
 * @author : AfourTech
 *
 */
public class MAV0003_SignoutOnHomePage  
{
	public MAV0003_SignoutOnHomePage(){
		  try {
			    GlobalVariables.sTestCaseId = "MAV0003_SignoutOnHomePage";
			    GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				System.out.println(GlobalVariables.sDescription);
				// Call login()
				GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			    if (GlobalVariables.bIsSuccess) {
			    	
					// Call Logout
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Logout is successful";
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathLogoutHomePage"))).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
					
					// Assertion: Verify that 'Channels Information Sharing Planning' renders
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "'Channels Information Sharing Planning' renders";
					//Assertion: Verify that the page with title 'Channels Information Sharing Planning' renders 		         
					if (GlobalVariables.oDriver.getTitle().contains(GlobalVariables.viewElements.get("loginPageTitle"))) {
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
					}
					else{
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
								GlobalVariables.sBlank, GlobalVariables.sVerifyError);
					}
					
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
			new MAV0003_SignoutOnHomePage();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
