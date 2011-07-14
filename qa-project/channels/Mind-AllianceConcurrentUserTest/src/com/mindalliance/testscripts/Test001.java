package com.mindalliance.testscripts;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;

public class Test001 {

	public static void LockRemoveThisSegment()
	{
		try
		{
			GlobalVariables.sTestCaseId="Test001";
			GlobalVariables.iStepNo = 0;
			
			//User A login to Channels	
			GlobalVariables.oDriverFirst=new FirefoxDriver();
			boolean isLogin= ApplicationFunctionLibrary.login(GlobalVariables.oDriverFirst, GlobalVariables.oElementFirst, "quamar", "quamar");
			Thread.currentThread();
			Thread.sleep(3000);
	
			//User B login to Channels
			GlobalVariables.oDriverSecond=new FirefoxDriver();
			boolean isLogin2= ApplicationFunctionLibrary.login(GlobalVariables.oDriverSecond, GlobalVariables.oElementSecond, "siddiqui", "siddiqui");
			
			//Click on INFORMATION SHARING MODEL for User A
			GlobalVariables.oDriverFirst.findElement(By.linkText("Information sharing model")).click();
			Thread.currentThread();
			Thread.sleep(3000);
			
			//Click on INFORMATION SHARING MODEL for User B
			GlobalVariables.oDriverSecond.findElement(By.linkText("Information sharing model")).click();
			Thread.currentThread();
			Thread.sleep(10000);
			
			// Mouse hover for action menu	
			GlobalVariables.oElementSecond = GlobalVariables.oDriverSecond.findElement(By.xpath("//span[@class='menubar']/span[3]/span/span"));
			GlobalVariables.oElementSecond.click();
	
			// Verifying lock on "Remove this segment" link	for User B
			ApplicationFunctionLibrary.isElementEnabled("Remove this segment", GlobalVariables.oDriverSecond, GlobalVariables.oElementSecond, "LinkText");
			
			// Log out for User A
			ApplicationFunctionLibrary.logout(GlobalVariables.oDriverFirst, GlobalVariables.oElementFirst,"quamar");
			
			// Log out for User B
			ApplicationFunctionLibrary.logout(GlobalVariables.oDriverSecond, GlobalVariables.oElementSecond,"siddiqui");
			
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
