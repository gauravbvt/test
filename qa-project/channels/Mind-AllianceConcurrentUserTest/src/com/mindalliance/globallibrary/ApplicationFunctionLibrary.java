package com.mindalliance.globallibrary;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ApplicationFunctionLibrary {

	/**
	 *  Login TestCase
	 */
	public static boolean login(WebDriver oDriver,WebElement oElement,String sUserName,String sPassword) {
		try {
			//GlobalStatic.iStepNo = 0;
			GlobalVariables.iStepNo++ ;
			GlobalVariables.sDescription = "URL opened";
			// Enter the URL 'http://afourtech.mind-alliance.com/'
			oDriver.get(GlobalVariables.sLoginURL);
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// Enter username and password
			GlobalVariables.iStepNo++ ;
			GlobalVariables.sDescription = "Username and password entered";
			oElement = oDriver.findElement(By.name("j_username"));
			oElement.sendKeys(sUserName);
			oElement = oDriver.findElement(By.name("j_password"));
			oElement.sendKeys(sPassword);
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// Click on Sign In button
			GlobalVariables.iStepNo++ ;
			GlobalVariables.sDescription = "Login is successful for user "+sUserName;
			oDriver.findElement(By.name("_spring_security_remember_me")).click();
			oDriver.findElement(By.xpath("/html/body/div/div[2]/form/div[6]/input")).click();
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			/*// Thread sleep
			Thread.currentThread();
			Thread.sleep(5000);*/
			oDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			// Returns TRUE
			return Boolean.TRUE;
		} catch (Exception e) {
			// Returns FALSE
			return Boolean.FALSE;
		}
	}
	
	/**
	 *  Logout TestCase
	 */
	public static boolean logout(WebDriver oDriver,WebElement oElement,String sUsername) {
		try {
			MouseOverAndClick("//span[@class='menubar']/span[3]/span/span",
					"Logout " + sUsername,oDriver,oElement);
			Alert alert = oDriver.switchTo().alert();
			Thread.currentThread();
			Thread.sleep(2000);
			// And acknowledge the alert (equivalent to clicking "OK")
			alert.accept();
			// Returns TRUE
			oDriver.quit();
			return Boolean.TRUE;
		} catch (Exception e) {
			// Returns FALSE
			oDriver.quit();
			return Boolean.FALSE;
		}
	}
	
	/**
	 * MouseOverAndClick
	 * @param xPath
	 * @param optionName
	 * @throws InterruptedException 
	 */
	public static void MouseOverAndClick(String popUpMenu, String optionName,WebDriver oDriver,WebElement oElement) {
		if (popUpMenu != null && optionName != null) { 
			
			// Mouse hover on popUpMenu
			/*GlobalVariables.oDriverFirst.findElement(By.xpath(popUpMenu)).click();
			// Click on optionName option under xPath
			GlobalVariables.oDriverFirst.findElement(By.partialLinkText(optionName)).click();
			
			GlobalVariables.oDriverSecond.findElement(By.xpath(popUpMenu)).click();
			// Click on optionName option under xPath
			GlobalVariables.oDriverSecond.findElement(By.partialLinkText(optionName)).click();*/
			
			oDriver.findElement(By.xpath(popUpMenu)).click();
			oDriver.findElement(By.partialLinkText(optionName)).click();
			
			}
		}
	
	/**
	 * Add Segment in InitializeAutomationScript method
	 * @param segmentName
	 * @throws InterruptedException
	 */
	public static void addSegment(String segmentName, int segmentNo,WebDriver oDriver,WebElement oElement) throws InterruptedException {
		if (segmentNo == 1)
			MouseOverAndClick(
					"//span[@class='menubar']/span[2]/span/span",
					"About plan segment",oDriver,oElement);
		else
			MouseOverAndClick(
					"//span[@class='menubar']/span[3]/span/span",
					"Add new segment",oDriver,oElement);
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(2000);
		// Enter the details for new segment
		oElement = oDriver.findElement(By.name("sg-editor:mo:aspect:name"));
			for (int i = 0; i <= 8; i++)
				oElement.sendKeys(Keys.BACK_SPACE);
		oElement.sendKeys(segmentName);
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(2000);
		// Click on 'done' button
		oDriver.findElement(By.className("close")).click();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(2000);
	}
	
	/***
	 * function to check verify lock on channels element
	 */
	public static boolean isElementEnabled(String sFindElement,WebDriver oDriver,WebElement oElement,String sOption)
	{
		try
		{
			if (sOption.equals("linkText"))
				oElement = oDriver.findElement(By.linkText(sFindElement));
			else if (sOption.equals("name")) 
				oElement = oDriver.findElement(By.name(sFindElement));
			else if (sOption.equals("xpath")) 
				oElement = oDriver.findElement(By.xpath(sFindElement));
			else if (sOption.equals("partialLinkText")) 
				oElement = oDriver.findElement(By.partialLinkText(sFindElement));
			else if (sOption.equals("id")) 
				oElement = oDriver.findElement(By.id(sFindElement));
			else if (sOption.equals("class")) 
				oElement = oDriver.findElement(By.className(sFindElement));
			else if (sOption.equals("tagName")) 
				oElement = oDriver.findElement(By.tagName(sFindElement));
			return Boolean.TRUE;
		}
		catch (Exception e) {
			// TODO: handle exception
			return Boolean.FALSE;
		}
	}
	
}
