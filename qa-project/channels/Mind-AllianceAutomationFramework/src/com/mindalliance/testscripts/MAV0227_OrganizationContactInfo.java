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

public class MAV0227_OrganizationContactInfo {
	public MAV0227_OrganizationContactInfo() {
		try {
			GlobalVariables.sTestCaseId = "MAV0227_OrganizationContactInfo";
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
				GlobalVariables.sDescription="All orgainzation";
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
				GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:tabs:panel:newInvolvedContainer:newInvolved")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:tabs:panel:newInvolvedContainer:newInvolved"));
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
					GlobalVariables.sStrCheck=li.getText();
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
					GlobalVariables.sVerifyError ="Verification Failed "+"Expected '"+sOrgName+"' "+" Actual " + GlobalVariables.sStrCheck;
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
				GlobalVariables.sDescription="Organization Contact Information";
				if (GlobalVariables.sBrowser.equals("Internet Explorer")) {
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.linkText(sOrgName))	;
					GlobalVariables.oElement.click();
					for (int i=1;i<=30;i++)
						GlobalVariables.oElement.sendKeys(Keys.ARROW_DOWN);
				}
				// Assertion: Verify that contact info is present
				GlobalVariables.bIsSuccess = Boolean.FALSE;
				GlobalVariables.oDropDown=new Select(GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:mediaNotDeployedContainer:mediaNotDeployed:0:mediumChoice")));
				List <WebElement> options = GlobalVariables.oDropDown.getOptions();
				for(WebElement option: options){
					if(option.equals(GlobalVariables.viewElements.get("cell"))&&
							option.equals(GlobalVariables.viewElements.get("conferenceCall"))&&
							option.equals(GlobalVariables.viewElements.get("courier"))&&
							option.equals(GlobalVariables.viewElements.get("email"))&&
							option.equals(GlobalVariables.viewElements.get("faceToFace"))&&
							option.equals(GlobalVariables.viewElements.get("fax"))&&
							option.equals(GlobalVariables.viewElements.get("im"))&&
							option.equals(GlobalVariables.viewElements.get("landline"))&&
							option.equals(GlobalVariables.viewElements.get("mail"))&&
							option.equals(GlobalVariables.viewElements.get("meeting"))&&
							option.equals(GlobalVariables.viewElements.get("notificationSystem"))&&
							option.equals(GlobalVariables.viewElements.get("onlineChat"))&&
							option.equals(GlobalVariables.viewElements.get("pager"))&&
							option.equals(GlobalVariables.viewElements.get("paSystems"))&&
							option.equals(GlobalVariables.viewElements.get("phone"))&&
							option.equals(GlobalVariables.viewElements.get("radio"))&&
							option.equals(GlobalVariables.viewElements.get("television"))&&
							option.equals(GlobalVariables.viewElements.get("twoWayRadio"))&&
							option.equals(GlobalVariables.viewElements.get("newMedium"))
							){
						// Write Results
						GlobalVariables.bIsSuccess = Boolean.TRUE;
						break;
					}
				}	
				if (GlobalVariables.bIsSuccess == Boolean.FALSE) {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId,GlobalVariables.iStepNo,GlobalVariables.sDescription,GlobalVariables.sPassed, 
							GlobalVariables.sBlank,GlobalVariables.sBlank);
				} else {
					GlobalVariables.sVerifyError="Verification failed";
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription+""+GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);	  
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
//					// Click on Organization
//					GlobalVariables.oDriver.findElement(By.linkText(sOrgName)).click();
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
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
			new MAV0227_OrganizationContactInfo();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}