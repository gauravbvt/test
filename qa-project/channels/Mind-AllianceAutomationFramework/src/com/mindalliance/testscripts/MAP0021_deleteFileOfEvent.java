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
 * Test Case ID: MAP0021_deleteFileOfEvent
 * Summary: Verify that Attachment can be deleted from the Event of the Segment
 * @author AfourTech
 *
 */
public class MAP0021_deleteFileOfEvent 
{
	
	public MAP0021_deleteFileOfEvent() {
		try {
			GlobalVariables.sTestCaseId = "MAP0021_deleteFileOfEvent";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
				if (GlobalVariables.bIsSuccess) {
					
					// Select  the plan from 'Switch to Plan' drop down, located on the top right corner
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Switch to plan";
					GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("switch-plan:plan-sel")));
					List <WebElement> options = GlobalVariables.oDropDown.getOptions();
				    for(WebElement option : options) {
				    	if(GlobalVariables.testData.get("Automation Test Plan v.1 (dev)").equals(option.getText())){
				    			option.setSelected();
				    			break;
				    	}
				    }
				    // Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(8000);	
					
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
					Thread.sleep(10000);
				    
					// Click on 'Add new segment' option under 'Actions' pop up menu
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Segment Added";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewSegment"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(10000);
					
					// Details of Segment
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Details of Segment enetered";
					GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name"));
					for (int i = 0; i <= 8; i++)
						GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Segment for delete file of Event"));
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(10000);
					
					// Select the Segment from 'Select Plan Segment' drop down, located on the top right corner
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Select Plan Segment";
					GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("select-segment:sg-sel")));
					options = GlobalVariables.oDropDown.getOptions();
				    for(WebElement option : options) {
				    	if(GlobalVariables.testData.get("Segment for delete file of Event").equals(option.getText())){
				    			option.setSelected();
				    			break;
				    	}
				    }
				    // Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(8000);
					
					// Add 'About plan segment' under Show pop-up menu
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
					
					//Create an event
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Create an Event";
					GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:context:container:creationDiv:event")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:context:container:creationDiv:event"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("This is an event"));	
					//switch between level
					GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:context:container:creationDiv:level")).click();
					GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:context:container:creationDiv:level")));
					options = GlobalVariables.oDropDown.getOptions();
				    for(WebElement option : options) {
				    	if(GlobalVariables.viewElements.get("high").equals(option.getText())){
				    			option.setSelected();
				    			break;
				    	}
				    }
				    GlobalVariables.oElement.sendKeys(Keys.TAB);
				    // WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(8000);
					GlobalVariables.oDriver.findElement(By.xpath("/html/body/form/div[5]/div/span/div/div[2]/table/tbody/tr[5]/td/span/div/div/table/tbody/tr/td/span[2]/a/span")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					
					// Attach a File to an event
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Attach a file to an event";
					GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:attachments:container:controls:name")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:attachments:container:controls:name"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("This is File 1 for deletion"));
					GlobalVariables .oElement=GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:attachments:container:controls:upload"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.sTestDataDirectoryPath + "CAP.txt");
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(8000);
					GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:attachments:container:controls:submit")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(8000);
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);	 
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(8000);
					
					// Delete an File of an event
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="Delete an Attachment of event";
					GlobalVariables.oDriver.findElement(By.xpath("/html/body/form/div[3]/div/span/div/div[2]/div/table/tbody/tr[6]/td/ul/span/li/ul/li[2]/a/img")).click();
					Alert alert = GlobalVariables.oDriver.switchTo().alert();
					// And acknowledge the alert (equivalent to clicking "OK")
					alert.accept();
					// Assertion: verify that file is deleted
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.className("grouped"));
					List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("td"));
					for (WebElement li: tds){
						if (li.getText().equals("")){
							// Write Results
							LogFunctions.writeLogs(GlobalVariables.sDescription);
							LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
									GlobalVariables.sBlank, GlobalVariables.sBlank);
							break;
						}
						else{
							GlobalVariables.sVerifyError ="Verification Failed "+"Expected '' "+" Actual "+li.getText();
					    	// Write Results
							LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
							LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
									GlobalVariables.sBlank, GlobalVariables.sVerifyError);
					    }
					}			
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
					
					// Click on done
					GlobalVariables.iStepNo++;
					GlobalVariables.sDescription="File is Deleted";
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank); 
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(8000);			
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(8000);					
					// Click on 'Remove this segment' under 'Actions' pop up menu
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
					// Get a handle to the open alert, prompt or confirmation
					alert = GlobalVariables.oDriver.switchTo().alert();
					alert.accept();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(8000);
					
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
					Thread.sleep(8000);
					
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
			new MAP0021_deleteFileOfEvent();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
