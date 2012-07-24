package com.mindalliance.functionaltestsripts;

import org.openqa.selenium.By;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class GP0003_ShowGuidePanel {
	public GP0003_ShowGuidePanel(){
		try{
			GlobalVariables.sTestCaseId = "GP0003_ShowGuidePanel";
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
				Thread.sleep(3000);
				
				// Click on close on the plan map window
				GlobalVariables.oDriver.findElement(By.xpath("/html/body/form/div[17]/div/div/div[3]/a/img")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				// Click on Hide panel icon
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Click on Hide panel";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathGuidePanelIcon"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
				GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(10000);
				
//				GlobalVariables.oDriver.navigate().refresh();
				
				//Click on 'Guide' link under 'Show' pop up menu
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Click on 'Guide' under 'Show' popup menu";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowPopUpMenu"))).click();
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowPopUpMenu")));
				// Assertion: Verify that Guide link is present in show popup menu
				if(GlobalVariables.oElement.getText().equals("Guide")){
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);  					
				
				//Quit Browser
				GlobalVariables.oDriver.quit();
				LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
				System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
			}
			else {
				LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
				System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
				
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				System.out.println("Unable to undo intermediate task"+ReportFunctions.getScreenShot("Undo intermediate task failed"));
				GlobalVariables.oDriver.quit();
			}
		}
		catch (Exception e) {
			if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.id("stackTrace"));
				LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
				System.out.println("Unable to undo intermediate task"+ReportFunctions.getScreenShot("Undo intermediate task failed"));
				ApplicationFunctionLibrary.logout();
			}
			else {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sBlank);
				System.out.println("Unable to undo intermediate task"+ReportFunctions.getScreenShot("Undo intermediate task failed"));
				ApplicationFunctionLibrary.logout();	
			}
			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
		}		
	}
	public static void main(String args[]) {
		try {
			GenericFunctionLibrary.initializeTestData();
			GenericFunctionLibrary.loadObjectRepository();
			new GP0003_ShowGuidePanel();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}

}
