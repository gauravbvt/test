package com.mindalliance.testscripts;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;

public class MACU0001_lockAboutPlanDetails 
{
	public MACU0001_lockAboutPlanDetails()
	{
		try
		{
			GlobalVariables.sTestCaseId = "MACU0001_lockAboutPlanDetails";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			
			//User A login to Channels	
			GlobalVariables.oDriverFirst=new FirefoxDriver();
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="User A Login in";
			ApplicationFunctionLibrary.login(GlobalVariables.oDriverFirst, GlobalVariables.oElementFirst, "quamar", "quamar");
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);	
	
			//User B login to Channels
			GlobalVariables.oDriverSecond=new FirefoxDriver();
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="User B Login in";
			ApplicationFunctionLibrary.login(GlobalVariables.oDriverSecond, GlobalVariables.oElementSecond, "siddiqui", "siddiqui");
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
			
		    // Mouse hover for action menu	
			GlobalVariables.oElementFirst = GlobalVariables.oDriverFirst.findElement(By.xpath("//span[@class='menubar']/span[2]/span/span"));
			GlobalVariables.oElementFirst.click();
			
			// Click on 'About plan' option under 'Show' pop up menu 
			GlobalVariables.oDriverFirst.findElement(By.linkText("About plan")).click();
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			
			//Click on INFORMATION SHARING MODEL for User B
			GlobalVariables.iStepNo++ ;
			GlobalVariables.sDescription = "Navigated to Information Sharing Model";
			GlobalVariables.oDriverSecond.findElement(By.linkText("Information sharing model")).click();
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
		
		    // Mouse hover for action menu	
			GlobalVariables.oElementSecond = GlobalVariables.oDriverSecond.findElement(By.xpath("//span[@class='menubar']/span[2]/span/span"));
			GlobalVariables.oElementSecond.click();
	
		    // Click on "About plan" option under 'Show' pop up menu
			GlobalVariables.oDriverSecond.findElement(By.linkText("About plan")).click();
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			
			GlobalVariables.iStepNo++ ;
			GlobalVariables.sDescription = "Lock generated for About plan details";
			GlobalVariables.bIsEnabled = ApplicationFunctionLibrary.isElementEnabled("//form/div[4]/div[@class='floating']/div[@class='plan']/div/div[@class='float-header']/span[@class='menubar']/span[@class='disabled pointer']"
												, GlobalVariables.oDriverSecond, GlobalVariables.oElementSecond, "xpath");
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
			Thread.sleep(3000);  
			
			// Click on 'done' button
			GlobalVariables.oDriverFirst.findElement(By.className("close")).click();
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			
			// Click on 'done' button
			GlobalVariables.oDriverSecond.findElement(By.className("close")).click();
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
		}
		catch (Exception e) {
			if (GlobalVariables.oDriverFirst.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getCause().toString(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
				GlobalVariables.oElementFirst = GlobalVariables.oDriverFirst.findElement(By.id("stackTrace"));
				LogFunctions.writeErrorLogs(GlobalVariables.oElementFirst.getText());
				GlobalVariables.oDriverFirst.quit();
			}
			else {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sBlank);
				ApplicationFunctionLibrary.logout(GlobalVariables.oDriverSecond, GlobalVariables.oElementSecond,"quamar");
			}
			
			if (GlobalVariables.oDriverSecond.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getCause().toString(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
				GlobalVariables.oElementSecond = GlobalVariables.oDriverSecond.findElement(By.id("stackTrace"));
				LogFunctions.writeErrorLogs(GlobalVariables.oElementSecond.getText());
				GlobalVariables.oDriverSecond.quit();
			}
			else {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sBlank);
				ApplicationFunctionLibrary.logout(GlobalVariables.oDriverSecond, GlobalVariables.oElementSecond,"siddiqui");
			}
			System.out.println(e.getMessage());
		}
	}
}
