package com.mindalliance.testscripts;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

/**
 * Test Case ID: MAP0022_addGoal
 * Summary: Verify that Goal can be added  from the Segment
 * @author AfourTech
 *
 */
public class MAP0022_addGoal {
	public MAP0022_addGoal() {
		try {
			GlobalVariables.sTestCaseId = "MAP0022_addGoal";
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
					
					// Click on 'Add new segment' option under 'Actions' pop up menu
					GlobalVariables.iStepNo++ ;
				    GlobalVariables.sDescription = "New segment added";
				    ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewSegment"));
				    // Write Results
				    LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				    // WebElement Synchronization
				    Thread.currentThread();
				    Thread.sleep(3000);
					
				    // Enter the details for new segment
				    GlobalVariables.iStepNo++ ;
				    GlobalVariables.sDescription = "Details entered";
				    GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name")).click();
				    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name"));
				     for (int i = 0; i <= 8; i++)
				      GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
				    GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Segment For Add Goal"));
				    // Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				    // WebElement Synchronization
				    Thread.currentThread();
				    Thread.sleep(3000);
					
					// Click on 'done' button
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Segment updated";
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click on 'About plan segment' option under 'Show' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "About Plan Segment section opened";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlanSegment"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click on 'goals' option under 'Shows' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Goals section opened";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAbtPlanSegShowMenu"),GlobalVariables.viewElements.get("goals"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Enter the details
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Goal 'Add Goal' added";
					// Objective
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:goalsDiv:goal:0:kind"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.viewElements.get("mitigate"));
					// Category
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:goalsDiv:goal:0:category"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.viewElements.get("financial"));
					// Magnitude
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:goalsDiv:goal:0:level"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.viewElements.get("minor"));
					// Organization
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:goalsDiv:goal:0:organization:actualOrType"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.viewElements.get("actual"));
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					// Name for Goal
					GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:goalsDiv:goal:0:organization:name")).click();
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:goalsDiv:goal:0:organization:name"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("AddGoal"));
					// Achieved at end check box
					GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:goalsDiv:goal:0:endsWithSegment")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);	
					// ASSERTION: When clicked on 'Achieved at end' checkbox, Goal 'Goal1' should be added 
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:goalsDiv:goal:0:organization:name"));
					if (GlobalVariables.oElement.getValue().equals(GlobalVariables.testData.get("AddGoal"))){
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
					}
					else{
						GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Add Goal' "+" Actual "+GlobalVariables.oElement.getValue();
						// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
								GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				    }
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);	
						
					// Click on 'done' button
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Segment updated";
					GlobalVariables.oDriver.findElement(By.className("close")).click();				
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click on 'Remove this segment' under 'Actions' pop up menu
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
					// Get a handle to the open alert, prompt or confirmation
//					Alert alert = GlobalVariables.oDriver.switchTo().alert();
//					alert.accept();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
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
			new MAP0022_addGoal();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
