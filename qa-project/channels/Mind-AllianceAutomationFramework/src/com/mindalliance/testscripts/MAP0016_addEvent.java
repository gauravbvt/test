package com.mindalliance.testscripts;

import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;
/**
 * Test Case ID: MAP0016_addEvent
 * Summary: Verify that event can be added to the Segment
 * @author AfourTech
 *
 */
public class MAP0016_addEvent
{
	public MAP0016_addEvent() {
		try {
			GlobalVariables.sTestCaseId = "MAP0016_addEvent";
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
					Thread.sleep(5000);
					
					// Click on 'Add new segment' option under 'Actions' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "New segment added successfully";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewSegment"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					
					// Enter the details for new segment
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Details entered";
					GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name"));
						for (int i = 0; i <= 8; i++)
							GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Segment For Add Event"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					
					// Click on 'done' button
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					
					//Click on 'About Plan segment' from show popup manu
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="About plan segment";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlanSegment"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					
					// Enter details of event inside 'context' section.
					// Switch between context
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Event Added";
					GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:context:container:creationDiv:timing")));
					List<WebElement> options = GlobalVariables.oDropDown.getOptions(); 
				    for(WebElement option : options) {
					    if(GlobalVariables.viewElements.get("after").equals(option.getText())){
					    	option.setSelected();
					    	break;
					    }
				    }
				    // WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					//Create an event
				    GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:context:container:creationDiv:event")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:context:container:creationDiv:event"));
					String event="This is an event";
					GlobalVariables.oElement.sendKeys(event);	
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					//switch between level
					GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:context:container:creationDiv:level")).click();
				    GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:context:container:creationDiv:level")));
				    options = GlobalVariables.oDropDown.getOptions(); 
				    for(WebElement option : options) {
					    if(GlobalVariables.viewElements.get("low").equals(option.getText())){
					    	option.setSelected();
					    	break;
					    }
				    }
				    // WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);	
				    //Assertions: Verify that the event is added
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.linkText(event));
					if(GlobalVariables.oElement.getText().equals(event)){
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
					}
					else{
						GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'This is an event' "+" Actual "+GlobalVariables.oElement.getText();
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
								GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				    }
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);	
					
					// Click on 'done' button
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Segment section closed";
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);						
					// Click on 'Remove this segment' under 'Actions' pop up menu
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
					// Get a handle to the open alert, prompt or confirmation
					Alert alert = GlobalVariables.oDriver.switchTo().alert();
					alert.accept();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					
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
			new MAP0016_addEvent();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
