package com.mindalliance.functionaltestsripts;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class TE088_EventDescription 
{
	public TE088_EventDescription(){
		try {
			
			GlobalVariables.sTestCaseId = "TE088_EventDescription";
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
				Thread.sleep(2000);
								
				// Clicks on 'About plan' link under show pop up menu option
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "About plan section opened";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlan"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Click on 'All Event' under show pop up menu of About plan window
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="All Events";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAboutPlanShowMenu"),GlobalVariables.viewElements.get("allEvents"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Create an event
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Add Event";
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:incidents:eventsDiv:event:1:name-container:name-input"));
				String sEventName = LogFunctions.getDateTime();
//				GlobalVariables.oElement.sendKeys(sEventName);
//				GlobalVariables.oElement.sendKeys(Keys.ENTER);
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
				
				Thread.currentThread();
				Thread.sleep(3000);
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath("/html/body/form/div[4]/div/span/div/div[2]/span/div/table/tbody"));
				List<WebElement> trs = GlobalVariables.oElement.findElements(By.tagName("tr"));
				List<WebElement> tds;
				int i=0;
				for(WebElement tr: trs) {
					tds = tr.findElements(By.tagName("td"));
					//WebElement td: tds;
					//for(WebElement td: tds) {
//						System.out.println(td.getText().isEmpty());
//						if(td.getText().isEmpty())
//						{
//							
//						}
						if(tds.get(0).getText().equals("")){
							// Write Results
							GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:incidents:eventsDiv:event:"+(i)+":name-container:name-input"));
							GlobalVariables.oElement.sendKeys(sEventName);
							GlobalVariables.oElement.sendKeys(Keys.ENTER);     
							// WebElement Synchronization
							Thread.currentThread();
							Thread.sleep(3000);
							GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:incidents:eventsDiv:event:"+(i)+":confirmed")).click();
							//break;
						}
					//}
						i++; 
				}
				// Select check box
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Event Added";
				//GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:incidents:eventsDiv:event:1:confirmed")).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Click on Event
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Event clicked";
				GlobalVariables.oDriver.findElement(By.linkText(sEventName)).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Click on text field and Update event name
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Update event description";
				GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:description")).clear();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:description"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("This is an Description"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Assertion: Verify that description is updated
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:description"));
				if(GlobalVariables.oElement.getValue().equalsIgnoreCase(GlobalVariables.viewElements.get("description"))){
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else{
					GlobalVariables.sVerifyError ="Verification Failed "+"Expected '"+GlobalVariables.viewElements.get("description")+"' Actual "+GlobalVariables.oElement.getValue();
			    	// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
			    }
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);				
				// About Plan
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlan"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// All events
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAboutPlanShowMenu"),GlobalVariables.viewElements.get("allEvents"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Click on check-box to delete the event 
				GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:incidents:eventsDiv:event:0:confirmed")).click();
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
			new TE088_EventDescription();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}