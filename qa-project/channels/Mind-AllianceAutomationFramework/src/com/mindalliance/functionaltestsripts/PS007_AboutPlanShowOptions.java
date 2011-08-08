package com.mindalliance.functionaltestsripts;

import org.openqa.selenium.By;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;
 
public class PS007_AboutPlanShowOptions 
{
	public PS007_AboutPlanShowOptions() {
		try{
			GlobalVariables.sTestCaseId = "PS007_AboutPlanShowOptions";
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
				
				// Click on 'show' Pop up Menu 
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Click on 'Show' Menu";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowMenu"))).click();
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowMenu")));
				//Assertion : Verify that After clicking on 'show' menu hide planners, About plan, About plan segment, All segments, Surveys ,All issues , All types ,Index & Help Options Displayed to user
				if(GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowDetails"))).getText().equals(GlobalVariables.viewElements.get("Details")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowAllEvents"))).getText().equals(GlobalVariables.viewElements.get("allEvents")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowSecrecyClassification"))).getText().equals(GlobalVariables.viewElements.get("secrecyClassification")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowAllOrganizations"))).getText().equals(GlobalVariables.viewElements.get("allOrganizations")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowAllSegments"))).getText().equals(GlobalVariables.viewElements.get("allSegments")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowProcedureMap"))).getText().equals(GlobalVariables.viewElements.get("procedureMap")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowWhosWho"))).getText().equals(GlobalVariables.viewElements.get("whosWho")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowAllIssues"))).getText().equals(GlobalVariables.viewElements.get("allIssues")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowBibliography"))).getText().equals(GlobalVariables.viewElements.get("bibliography")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowIndex"))).getText().equals(GlobalVariables.viewElements.get("index")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowAllTypes"))).getText().equals(GlobalVariables.viewElements.get("allTypes")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowAllTags"))).getText().equals(GlobalVariables.viewElements.get("allTags")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowEvaluation"))).getText().equals(GlobalVariables.viewElements.get("evaluation")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowParticipations"))).getText().equals(GlobalVariables.viewElements.get("participation")) &&
				   GlobalVariables.oElement.findElement(By.xpath(GlobalVariables.plan.get("sXpathAboutPlanShowVersions"))).getText().equals(GlobalVariables.viewElements.get("versions"))){
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

				// Close 'About Plan' Window
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
			new PS007_AboutPlanShowOptions();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
