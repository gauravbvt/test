package com.mindalliance.globallibrary;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.*;

public class ApplicationFunctionLibrary {

	/**
	 *  Login TestCase
	 */
	public static boolean login() {
		try {

			GlobalVariables.iStepNo = 0;
			GlobalVariables.iStepNo++ ;
			GlobalVariables.sDescription = "URL opened";
			// Create Selenium Webdriver object
			if (GlobalVariables.sBrowser.equals("Mozilla Firefox"))
				GlobalVariables.oDriver = new FirefoxDriver();
			else if (GlobalVariables.sBrowser.equals("Internet Explorer"))
			{
				System.out.println("Hie............1");
				GlobalVariables.oDriver = new InternetExplorerDriver();
				System.out.println("Hie............2");
			}
			
			
			System.out.println("Hie............3");
			// Maximize Browser Window
			((JavascriptExecutor) GlobalVariables.oDriver).executeScript("if (window.screen) {window.moveTo(0, 0);window.resizeTo(window.screen.availWidth, window.screen.availHeight);};");
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(2000);
			
			System.out.println("Hie............4");
			// Enter the URL
			GlobalVariables.oDriver.get(GlobalVariables.login.get("sChannelURL"));
			System.out.println("Hie............5");
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(2000);
			String title=GlobalVariables.oDriver.getTitle();
			System.out.println("Hie............1" + title);
			if (GlobalVariables.sBrowser.equals("Internet Explorer")){
				if(title.equalsIgnoreCase(GlobalVariables.viewElements.get("planPageSubTitle")))
				{
					ApplicationFunctionLibrary.logout();
				}
				else if(title.equalsIgnoreCase("Channels - Information Sharing Planning"))
				{
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathLogoutHomePage"))).click();
				}
				else if(title.equalsIgnoreCase("Channels - Administration"))
				{
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathLogoutAdminPage"))).click();
				}
				else if(title.equalsIgnoreCase("Channels - All procedures in Automation Test Plan"))
				{
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.login.get("sXpathSignoutOnAssignmentsCommitments"))).click();
				}
				else if(title.equalsIgnoreCase("Channels - Participants Pages"))
				{
					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.login.get("sSignoutOnParticipantsPage"))).click();
				}
			}
			System.out.println("Hie............6");
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed,
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// Enter username and password
			GlobalVariables.iStepNo++ ;
			GlobalVariables.sDescription = "Username and password entered";
			GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_username"));
			GlobalVariables.oElement.sendKeys((String)GlobalVariables.login.get("sUsername"));
			GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_password"));
			GlobalVariables.oElement.sendKeys((String)GlobalVariables.login.get("sPassword"));
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed,
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// Click on Sign In button
			System.out.println("Hie............7");
			GlobalVariables.iStepNo++ ;
			GlobalVariables.sDescription = "Login is successful";
			GlobalVariables.oDriver.findElement(By.name("_spring_security_remember_me")).click();
			GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.login.get("sLogin"))).click();
			System.out.println("Hie............8\n\n");
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed,
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			// Returns TRUE
			return Boolean.TRUE;
		} catch (Exception e) {
			System.out.println("\n\n"+e.getMessage()+"\n\n");
			// Returns FALSE
			return Boolean.FALSE;
		}
	}

	/**
	 *  Logout TestCase
	 */
	public static boolean logout() {
		try {
			MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),
					"Sign out " + GlobalVariables.login.get("sUsername"));
			Alert alert = GlobalVariables.oDriver.switchTo().alert();
			// And acknowledge the alert (equivalent to clicking "OK")
			alert.accept();
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			// Returns TRUE
			GlobalVariables.oDriver.quit();
			return Boolean.TRUE;
		} catch (Exception e) {
			// Returns FALSE
			if(GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle))
			{
			GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.login.get("sXpathStackTrace"))).click();
			ApplicationFunctionLibrary.logout();
			}
			return Boolean.FALSE;
		}
	}

	/**
	 * MouseOverAndClick
	 * @param xPath
	 * @param optionName
	 * @throws InterruptedException
	 */
	public static void MouseOverAndClick(String popUpMenu, String optionName) throws InterruptedException {
		if (popUpMenu != null && optionName != null) {
		// Mouse hover on popUpMenu
		GlobalVariables.oDriver.findElement(By.xpath(popUpMenu)).click();
		// Click on optionName option under xPath
		GlobalVariables.oDriver.findElement(By.partialLinkText(optionName)).click();
		}
			/*GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath(xPath));
			// Mouseover on xPath
			((RenderedWebElement) GlobalVariables.oElement).hover();*/
			/*RenderedRemoteWebElement result = (RenderedRemoteWebElement) GlobalVariables.oDriver.findElement(By.xpath(xPath));
			((HasInputDevices) GlobalVariables.oDriver).getMouse().mouseMove(result.getCoordinates());*/
			// Click on optionName option under xPath
			/*RenderedRemoteWebElement result1 = (RenderedRemoteWebElement) GlobalVariables.oDriver.findElement(By.linkText(optionName));
			((HasInputDevices) GlobalVariables.oDriver).getMouse().click(result1.getCoordinates());*/
			//GlobalVariables.oDriver.findElement(By.linkText(optionName)).click();
			//GlobalVariables.oDriver.findElement(By.xpath("/html/body/form/div[13]/span/span[3]/span/ul/li[8]/span/a/span")).click();
	}

	/**
	 * Add Segment in InitializeAutomationScript method
	 * @param segmentName
	 * @throws InterruptedException
	 */
	public static void addSegment(String segmentName, String segmentType) throws InterruptedException {
		if (segmentType.equals("Default"))
			MouseOverAndClick(
					"//span[@class='menubar']/span[2]/span/span",
					"About plan segment");
		else
			MouseOverAndClick(
					"//span[@class='menubar']/span[3]/span/span",
					"Add new segment");
		// WebElement Synchronization
		Thread.currentThread();
		Thread.sleep(2000);
		// Enter the details for new segment
		GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:name")).click();
		GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:mo:aspect:name"));
			for (int i = 0; i <= 8; i++)
				GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
		GlobalVariables.oElement.sendKeys(segmentName);
		// WebElement Synchronization
		Thread.currentThread();
		Thread.sleep(2000);
		// Click on 'done' button
		GlobalVariables.oDriver.findElement(By.className("close")).click();
		// WebElement Synchronization
		Thread.currentThread();
		Thread.sleep(2000);
	}

}
