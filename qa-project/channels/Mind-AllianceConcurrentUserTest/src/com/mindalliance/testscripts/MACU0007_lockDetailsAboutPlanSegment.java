package com.mindalliance.testscripts;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;

public class MACU0007_lockDetailsAboutPlanSegment
{
	public MACU0007_lockDetailsAboutPlanSegment()
	{
		try{
			GlobalVariables.sTestCaseId = "MACU0007_lockDetailsAboutPlanSegment";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			
			//User A login to Channels	
			GlobalVariables.oDriverFirst=new FirefoxDriver();
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="User A Login in";
			boolean isLogin= ApplicationFunctionLibrary.login(GlobalVariables.oDriverFirst, GlobalVariables.oElementFirst, "quamar", "quamar");
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);	
	
			//User B login to Channels
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="User B Login in";
			GlobalVariables.oDriverSecond=new FirefoxDriver();
			boolean isLogin2= ApplicationFunctionLibrary.login(GlobalVariables.oDriverSecond, GlobalVariables.oElementSecond, "siddiqui", "siddiqui");
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);	
			
			//Click on INFORMATION SHARING MODEL for User A
			GlobalVariables.iStepNo++ ;
			GlobalVariables.sDescription = "Navigated to Information Sharing Model";
			GlobalVariables.oDriverFirst.findElement(By.linkText("Information sharing model")).click();
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			
			// Click on About Plan Segment under show pop up menu	
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="About plan segment";
			ApplicationFunctionLibrary.MouseOverAndClick("//span[@class='menubar']/span[2]/span/span","About plan segment",GlobalVariables.oDriverFirst,GlobalVariables.oElementFirst);
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			
			//Click on INFORMATION SHARING MODEL for User B
			GlobalVariables.iStepNo++ ;
			GlobalVariables.sDescription="Navigated to Information Sharing Model";
			GlobalVariables.oDriverSecond.findElement(By.linkText("Information sharing model")).click();
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			
			// Click on About Plan Segment under show pop up menu	
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="About plan segment";
			ApplicationFunctionLibrary.MouseOverAndClick("//span[@class='menubar']/span[2]/span/span", "About plan segment",GlobalVariables.oDriverSecond,GlobalVariables.oElementSecond);
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			
			// Assertion: Verify that Lock has been created for User b
			GlobalVariables.iStepNo++ ;
			GlobalVariables.sDescription = "Lock generated for About plan details";
			GlobalVariables.bIsEnabled = ApplicationFunctionLibrary.isElementEnabled("//form/div[4]/div[@class='floating']/div[@class='segmant']/div/div[@class='float-header']/span[@class='menubar']/span[@class='menuitem']/span[@class='disabled pointer']", GlobalVariables.oDriverSecond, GlobalVariables.oElementSecond, "xpath");
				//GlobalVariables.bIsEnabled = ApplicationFunctionLibrary.isElementEnabled("plan:mo:aspect:name", GlobalVariables.oDriverSecond, GlobalVariables.oElementSecond, "name");
				if (GlobalVariables.bIsEnabled) {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(5000);  
			
			// Click on 'done' button
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="Done";
			GlobalVariables.oDriverFirst.findElement(By.className("close")).click();
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			
			// Click on 'done' button
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="Done";
			GlobalVariables.oDriverSecond.findElement(By.className("close")).click();
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			
			
			// Log out for User A
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="User A Logout Sucessfully";
			ApplicationFunctionLibrary.logout(GlobalVariables.oDriverFirst, GlobalVariables.oElementFirst,"quamar");
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			
			// Log out for User B
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="User B Logout Sucessfully";
			ApplicationFunctionLibrary.logout(GlobalVariables.oDriverSecond, GlobalVariables.oElementSecond,"siddiqui");
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			
			LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");

			
		}catch (Exception e) {
			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
		}
	}
	public static void main(String args[]) {
		try {
			new MACU0007_lockDetailsAboutPlanSegment();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
