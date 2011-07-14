package com.mindalliance.testscripts;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;

public class MACU0019_addNewTask
{
	public MACU0019_addNewTask()
	{
		try
		{
			GlobalVariables.sTestCaseId = "MACU0019_addNewTask";
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
			Thread.sleep(5000);
			
			// Click on Add new task user a
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="Add new task User A";
			ApplicationFunctionLibrary.MouseOverAndClick("//span[@class='menubar']/span[3]/span/span","Add new task",GlobalVariables.oDriverFirst,GlobalVariables.oElementFirst);
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			
			// Click on Add new task user a
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="Add new task User B";
			ApplicationFunctionLibrary.MouseOverAndClick("//span[@class='menubar']/span[3]/span/span","Add new task",GlobalVariables.oDriverSecond,GlobalVariables.oElementSecond);
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			
			GlobalVariables.oDriverSecond.navigate().refresh();
			// Assertion: Verifying lock on "Add new task" link for User B
			GlobalVariables.oDriverSecond.findElement(By.name("flow-map")).click();
			List<WebElement> areas = GlobalVariables.oDriverSecond.findElements(By.tagName("area"));
			for(WebElement area:areas){
			 if(area.getAttribute("id").equals("node1"))
			 {
				 // Write Results
				 LogFunctions.writeLogs(GlobalVariables.sDescription);
				 LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						 GlobalVariables.sBlank, GlobalVariables.sBlank);
			 }
			}
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
			new MACU0019_addNewTask();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
