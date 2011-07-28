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
 * Test Case ID: MAP0023_removeGoal
 * Summary: Verify that Goal can be deleted  from the Segment
 * @author AfourTech
 *
 */
public class MAP0023_removeGoal {
	public MAP0023_removeGoal() {
		try {
			GlobalVariables.sTestCaseId = "MAP0023_removeGoal";
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
				    GlobalVariables.sDescription = "New segment added";
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
				    GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:name")).click();
				    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:name"));
				     for (int i = 0; i <= 8; i++)
				      GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
				    GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Segment For Remove Goal"));
				    // Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				    // WebElement Synchronization
				    Thread.currentThread();
				    Thread.sleep(5000);
				    
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
				    Thread.sleep(5000);
				    
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
					Thread.sleep(5000);
					
					// Click on 'goals' option under 'Show' pop up menu
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Goals section opened";
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAbtPlanSegShowMenu"),GlobalVariables.viewElements.get("goals"));
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					
					// Enter the details
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Goal 'Delete Goal' added";
					// Objective
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:goalsDiv:goal:0:kind"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.viewElements.get("mitigate"));
					// Category
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:goalsDiv:goal:0:category"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.viewElements.get("financial"));
					// Magnitude
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:goalsDiv:goal:0:level"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.viewElements.get("minor"));
					// Organization
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:goalsDiv:goal:0:organization:actualOrType"));
					GlobalVariables.oElement.sendKeys(GlobalVariables.viewElements.get("actual"));
					// Name for Goal
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:goalsDiv:goal:0:organization:name"));
					if (!GlobalVariables.oElement.getValue().equals("Add Goal"))
						GlobalVariables.oElement.sendKeys("Delete Goal");
				    // Achieved at end check box
					GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:goalsDiv:goal:0:endsWithSegment")).click();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(3000);
					
					// Click on delete respective goal
					GlobalVariables.iStepNo++ ;
					GlobalVariables.sDescription = "Goal deleted";
					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("aspect"));
					GlobalVariables.oElement.click();
					for(int i=0;i<=5;i++)
						GlobalVariables.oElement.sendKeys(Keys.ARROW_RIGHT);
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDeleteGoals"))).click();
					// Get a handle to the open alert, prompt or confirmation
					Alert alert = GlobalVariables.oDriver.switchTo().alert();
					// And acknowledge the alert (equivalent to clicking "OK")
					alert.accept();
					//Assertion:Verify that goal should get removed.
					GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:goalsDiv:goal:0:organization:name")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(1000);
					GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:goalsDiv:goal:0:organization:name"));
				    if(GlobalVariables.oElement.getText().equals("")){
				    	// Write Results
						LogFunctions.writeLogs(GlobalVariables.sDescription);
						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
								GlobalVariables.sBlank, GlobalVariables.sBlank);
				    	}
				    else{
				    	GlobalVariables.sVerifyError ="Verification Failed "+"Expected '' "+" Actual "+GlobalVariables.oElement.getText();
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
					GlobalVariables.sDescription = "Segment updated";
					GlobalVariables.oDriver.findElement(By.className("close")).click();
					// WebElement Synchronization
					Thread.currentThread();
					Thread.sleep(5000);
					// Remove this segment
					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
					// Get a handle to the open alert, prompt or confirmation
					alert = GlobalVariables.oDriver.switchTo().alert();
					// And acknowledge the alert (equivalent to clicking "OK")
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
			new MAP0023_removeGoal();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
