package com.mindalliance.testscripts;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class MAV0228_MediaNotDeployed 
{
	public MAV0228_MediaNotDeployed() {
		try {
			GlobalVariables.sTestCaseId = "MAV0228_MediaNotDeployed";
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
				    
					//Click on 'About Plan' from show popup manu
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="About plan";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"), GlobalVariables.viewElements.get("aboutPlan"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					//Click on 'all organizations' from show pop-up menu
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="all orgainzation";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAboutPlanShowMenu"), GlobalVariables.viewElements.get("allOrganizations"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Enter name of organization in 'Other organization that should be assigned tasks ' field
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Name Entered";
					GlobalVariables.oDriver.findElement(By.name("plan:mo:aspect:tabs:panel:newInvolvedContainer:newInvolved")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("plan:mo:aspect:tabs:panel:newInvolvedContainer:newInvolved"));
					String sOrgName = LogFunctions.getDateTime();
					GlobalVariables.oElement.sendKeys(sOrgName);
					GlobalVariables.oElement.sendKeys(Keys.ENTER);			
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					//  Enter the details about the selected organization in the details form.
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Organization selected";
					GlobalVariables.oDriver.findElement(By.linkText(sOrgName)).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("index"));
					List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("li"));
					for (WebElement li: tds){
						if (li.getText().equals(sOrgName)){
							li.findElement(By.linkText(sOrgName)).click();
							break;
						}
					}
					if (GlobalVariables.bIsSuccess == Boolean.TRUE) {
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
					}
					else{						
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
					}
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					GlobalVariables.oDriver.findElement(By.linkText(sOrgName)).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					
					// Select Contact information.
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Organization Media Not Deployed";
					if (GlobalVariables.sBrowser.equals("Internet Explorer")) {
						GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.linkText(sOrgName))	;
						GlobalVariables.oElement.click();
						for (int i=1;i<=30;i++)
							GlobalVariables.oElement.sendKeys(Keys.ARROW_DOWN);
					}
					GlobalVariables.oDropDown=new Select(GlobalVariables.oDriver.findElement(By.name("entity:mo:aspect:mo-details:mediaNotDeployedContainer:mediaNotDeployed:0:mediumChoice")));
					List <WebElement> options = GlobalVariables.oDropDown.getOptions();
					if(options.get(1).getText().equals(GlobalVariables.viewElements.get("cell")) &&
					   options.get(2).getText().equals(GlobalVariables.viewElements.get("conferenceCall")) &&
					   options.get(3).getText().equals(GlobalVariables.viewElements.get("courier")) &&
					   options.get(4).getText().equals(GlobalVariables.viewElements.get("email")) &&
					   options.get(5).getText().equals(GlobalVariables.viewElements.get("faceToFace")) &&
					   options.get(6).getText().equals(GlobalVariables.viewElements.get("fax")) &&
					   options.get(7).getText().equals(GlobalVariables.viewElements.get("im")) &&
					   options.get(8).getText().equals(GlobalVariables.viewElements.get("landline")) &&
					   options.get(9).getText().equals(GlobalVariables.viewElements.get("mail")) &&
					   options.get(10).getText().equals(GlobalVariables.viewElements.get("meeting")) &&
					   options.get(11).getText().equals(GlobalVariables.viewElements.get("notificationSystem")) &&
					   options.get(12).getText().equals(GlobalVariables.viewElements.get("onlineChat")) &&
					   options.get(13).getText().equals(GlobalVariables.viewElements.get("pager")) &&
					   options.get(14).getText().equals(GlobalVariables.viewElements.get("paSystems")) &&
					   options.get(15).getText().equals(GlobalVariables.viewElements.get("phone")) &&
					   options.get(16).getText().equals(GlobalVariables.viewElements.get("radio")) &&
					   options.get(17).getText().equals(GlobalVariables.viewElements.get("television")) &&
					   options.get(18).getText().equals(GlobalVariables.viewElements.get("twoWayRadio")) &&
					   options.get(19).getText().equals(GlobalVariables.viewElements.get("newMedium"))) {
							// 	Write Results
							LogFunctions.writeLogs(GlobalVariables.sDescription);
							LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
									GlobalVariables.sBlank, GlobalVariables.sBlank);
					}
					else {
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription+" "+GlobalVariables.sFailed);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
					}
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(2000);					
					
					// Click on 'done' button
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Organization Added";
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// Click on Organization
					GlobalVariables.oDriver.findElement(By.linkText(sOrgName)).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// Remove Organization
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDeleteOrgs"))).click();
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
					Thread.sleep(3000);	
				
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
			new MAV0228_MediaNotDeployed();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}