package com.mindalliance.testscripts;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;

public class MACU0020_addNewSegment 
{
	public  MACU0020_addNewSegment()
	{
		try
		{
			GlobalVariables.sTestCaseId = "MACU0020_addNewSegment";
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
			Thread.sleep(3000);
			
			// Click on Add new Segment under Action pop up menu	
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="Add new segment";
			ApplicationFunctionLibrary.MouseOverAndClick("//span[@class='menubar']/span[3]/span/span","Add new segment",GlobalVariables.oDriverFirst,GlobalVariables.oElementFirst);
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(5000);	
			
			// Click on Add new segment under Action pop up menu	
			GlobalVariables.iStepNo++;
			GlobalVariables.sDescription="Add new segment";
			ApplicationFunctionLibrary.MouseOverAndClick("//span[@class='menubar']/span[3]/span/span","Add new segment",GlobalVariables.oDriverSecond,GlobalVariables.oElementSecond);
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);	
			
			// Details of segment
			GlobalVariables.sDescription = "Details entered";
			GlobalVariables.oElementFirst = GlobalVariables.oDriverFirst.findElement(By.name("sg-editor:mo:aspect:name"));
				for (int i = 0; i <= 8; i++)
					GlobalVariables.oElementFirst.sendKeys(Keys.BACK_SPACE);
			GlobalVariables.oElementFirst.sendKeys("New segment 1");
			GlobalVariables.oElementFirst=GlobalVariables.oDriverFirst.findElement(By.className("close"));
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);	
			
			// Details of segment
			GlobalVariables.sDescription = "Details entered";
			GlobalVariables.oElementSecond = GlobalVariables.oDriverSecond.findElement(By.name("sg-editor:mo:aspect:name"));
				for (int i = 0; i <= 8; i++)
					GlobalVariables.oElementSecond.sendKeys(Keys.BACK_SPACE);
			GlobalVariables.oElementSecond.sendKeys("New segment 2");
			GlobalVariables.oElementSecond=GlobalVariables.oDriverSecond.findElement(By.className("close"));
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);	
			
			GlobalVariables.oDriverSecond.navigate().refresh();
			
			// Assertion: Verify that Segment can be added
			GlobalVariables.oElementSecond=GlobalVariables.oDriverSecond.findElement(By.name("select-segment:sg-sel"));
			if(GlobalVariables.oElementSecond.getText().equals("New Segment 2"))
			{
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
			}
			
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
			new MACU0020_addNewSegment();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
