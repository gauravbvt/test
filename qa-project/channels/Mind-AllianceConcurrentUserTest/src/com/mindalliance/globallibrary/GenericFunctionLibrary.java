package com.mindalliance.globallibrary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class GenericFunctionLibrary {

	/**
	 * Create Result Structures
	 * @throws IOException  
	 */
	public static void createResultFiles() throws IOException {
		// Get Current Date
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		GlobalVariables.dCurrentDate = new Date();
		// Create Report Directory
		GlobalVariables.sReportDirectoryName = dateFormat.format(GlobalVariables.dCurrentDate);
		GlobalVariables.sReportSrcDirectoryPath = GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\TestCases\\Mind-AllianceTestCaseSheet.ods"; 
		GlobalVariables.sReportDstDirectoryPath = GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\Reports\\" + GlobalVariables.sReportDirectoryName;
		File Dir = new File(GlobalVariables.sReportDstDirectoryPath);
		if (!Dir.exists())
			Dir.mkdir();
		
		// Create Log Directory
		GlobalVariables.sLogDirectoryName = dateFormat.format(GlobalVariables.dCurrentDate);
		GlobalVariables.sLogDirectoryPath = GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\Logs\\" + GlobalVariables.sLogDirectoryName;
		Dir = new File(GlobalVariables.sLogDirectoryPath);
		if (!Dir.exists())
			Dir.mkdir();
		// Create Errors sub-directory
		GlobalVariables.sErrorLogSubDirectoryPath = GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\Logs\\" + GlobalVariables.sLogDirectoryName + "\\Errors";
		Dir = new File(GlobalVariables.sErrorLogSubDirectoryPath);
		if (!Dir.exists())
			Dir.mkdir();
		
		// Logs Files
		GlobalVariables.sResultCsvFile = GlobalVariables.sLogDirectoryPath + "\\Results.csv";
		GlobalVariables.sLogFile = GlobalVariables.sLogDirectoryPath + "\\Logs.logs";
		
		FileWriter fileWriter = new FileWriter(GlobalVariables.sResultCsvFile, true);
		 BufferedWriter oBWriter = new BufferedWriter(fileWriter);
		 oBWriter.write("TestCaseId,VerificationStepNo,Description,Result,ScriptException,ErrorReport");
		 oBWriter.newLine();
		 oBWriter.flush();
		 oBWriter.close();
		 
		 // TestData Directory
		 GlobalVariables.sTestDataDirectoryPath = GlobalVariables.fCurrentDir.getCanonicalPath().toString() + "\\TestData\\";
	}
	
	/**
	 * Initialize Automation Scripts
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void initializeTestData() throws InterruptedException, IOException {
		System.out.println("Initializing  TestData...");
		GlobalVariables.sStartDateTime = LogFunctions.getDateTime();
		createResultFiles();
		/*GlobalStatic.oDriver = new FirefoxDriver();
		// URL
		GlobalStatic.oDriver.get(GlobalStatic.sLoginURL);
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(5000);
		GlobalStatic.oDriver.manage().deleteAllCookies();
		// Usernames
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("j_username"));
		GlobalStatic.oElement.sendKeys(GlobalStatic.sUsername);
		// Password
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("j_password"));
		GlobalStatic.oElement.sendKeys(GlobalStatic.sPassword);
		GlobalStatic.oDriver.findElement(By.name("_spring_security_remember_me")).click();
		// Sign in
		GlobalStatic.oDriver.findElement(By.xpath("/html/body/div/div[2]/form/div[6]/input")).click();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(10000);
		GlobalStatic.oDriver.findElement(By.linkText("Channels administration")).click();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(3000);
		// newPlanUri 
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("newPlanUri"));
		GlobalStatic.oElement.sendKeys("Automation Test Plan");
		// newPlanClient
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("newPlanClient"));
		GlobalStatic.oElement.sendKeys("Afourtech");
		//Submit
		GlobalStatic.oDriver.findElement(By.name("Submit")).submit();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(10000);
		// Go back
		GlobalStatic.oDriver.navigate().back();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(3000);
		GlobalStatic.oDriver.navigate().back();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(3000);
		GlobalStatic.oDriver.findElement(By.linkText("Information sharing model")).click();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(3000);
		// Select Plan
		GlobalStatic.oDriver.findElement(By.name("switch-plan:plan-sel")).click();
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("switch-plan:plan-sel"));
		GlobalStatic.oElement.sendKeys("New Plan v.1 (dev)");
		GlobalStatic.oElement.sendKeys(Keys.ENTER);
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(5000);
		// Click About plan under Show pop up menu
		ApplicationFunctionLibrary.MouseOverAndClick("//span[@class='menubar']/span[2]/span/span", "About plan");
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(3000);
		// Enter Plan Name 
		GlobalStatic.oDriver.findElement(By.name("plan:mo:aspect:name")).clear();
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("plan:mo:aspect:name"));
		GlobalStatic.oElement.sendKeys("Automation Test Plan");
		// Click done button
		GlobalStatic.oDriver.findElement(By.className("close")).click();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(2000);
		// Update Default Segment
		ApplicationFunctionLibrary.addSegment("Segment 1", "Default");
		// Add New Segment
		ApplicationFunctionLibrary.addSegment("Segment 2", "New");
		// Call logout
		ApplicationFunctionLibrary.logout();*/
		System.out.println("TestData initialization completed");
	}	
	
	/**
	 * Initialize Automation Scripts
	 * @throws InterruptedException 
	 */
	public static void tearDownTestData() throws InterruptedException {
		System.out.println("Performing cleanup TestData...");
		/*GlobalStatic.oDriver = new FirefoxDriver();
		// URL
		GlobalStatic.oDriver.get(GlobalStatic.sLoginURL);
		// Username
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("j_username"));
		GlobalStatic.oElement.sendKeys(GlobalStatic.sUsername);
		// Password
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("j_password"));
		GlobalStatic.oElement.sendKeys(GlobalStatic.sPassword);
		// Sign in
		GlobalStatic.oDriver.findElement(By.xpath("/html/body/div/div[2]/form/div[6]/input")).click();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(3000);
		GlobalStatic.oDriver.findElement(By.linkText("Channels administration")).click();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(3000);
		// Select Plan
		GlobalStatic.oDriver.findElement(By.name("plan-sel")).click();
		GlobalStatic.oElement = GlobalStatic.oDriver.findElement(By.name("plan-sel"));
		GlobalStatic.oElement.sendKeys("Automation Test Plan");
		GlobalStatic.oElement.sendKeys(Keys.ENTER);
		// Delete Plan
		GlobalStatic.oDriver.findElement(By.linkText("Delete plan")).click();
		Alert alert = GlobalStatic.oDriver.switchTo().alert();
		Thread.currentThread();
		Thread.sleep(2000);
		// And acknowledge the alert (equivalent to clicking "OK")
		alert.accept();
		// Thread sleep
		Thread.currentThread();
		Thread.sleep(3000);
		// Logout
		GlobalStatic.oDriver.findElement(By.partialLinkText("Logout ")).click();
		// Webdriver close
		GlobalStatic.oDriver.close();*/
		GlobalVariables.sEndDateTime = LogFunctions.getDateTime();
		System.out.println("TestData cleanup completed");
	}


}
