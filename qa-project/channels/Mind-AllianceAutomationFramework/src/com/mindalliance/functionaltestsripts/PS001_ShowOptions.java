package com.mindalliance.functionaltestsripts;

import org.openqa.selenium.By;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class PS001_ShowOptions 
{
	public PS001_ShowOptions() {
		try {
			
			GlobalVariables.sTestCaseId = "PS001_ShowOptions";
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

				// Click on 'show' Link
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Click on 'Show' Menu";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowPopUpMenu"))).click();
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowPopUpMenu")));
				//Assertion : Verify that After clicking on 'show' menu hide planners, About plan, About plan segment, All segments, Surveys ,All issues , All types ,Index & Help Options Displayed to user
				if(GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowHidePlanners"))).getText().equals(GlobalVariables.viewElements.get("hidePlanners")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowAboutPlan"))).getText().equals(GlobalVariables.viewElements.get("aboutPlan")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowAboutPlanSegment"))).getText().equals(GlobalVariables.viewElements.get("aboutPlanSegment")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowSurveys"))).getText().equals(GlobalVariables.viewElements.get("surveys")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowAllSegments"))).getText().equals(GlobalVariables.viewElements.get("allSegments")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowAllIssues"))).getText().equals(GlobalVariables.viewElements.get("allIssues")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowAllTypes"))).getText().equals(GlobalVariables.viewElements.get("allTypes")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowIndex"))).getText().equals(GlobalVariables.viewElements.get("index")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowHelp"))).getText().equals(GlobalVariables.viewElements.get("help"))){
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else{
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
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
			new PS001_ShowOptions();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
