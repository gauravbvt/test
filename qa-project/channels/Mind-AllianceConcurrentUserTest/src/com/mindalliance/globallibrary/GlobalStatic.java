package com.mindalliance.globallibrary;

import java.io.File;
import java.util.Date;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class GlobalStatic {

	//REDO and UNDO
	public static WebDriver oDriverFirst = null;
	public static WebElement oElementFirst = null;
	
	public static WebDriver oDriverSecond = null;
	public static WebElement oElementSecond = null;
	
	public static Select oDropDown = null;
	
	public static boolean bIsSuccess = false;
	public static Date dCurrentDate = null;
	public static File fCurrentDir = new File(".");
	public static int iStepNo = 0;
	public static int iIndex = 1;
	public static String sTestCaseId = null;
	public static String sDescription = null;
	public static String sPassed = "PASS";
	public static String sFailed = "FAIL";
	public static String sNotRun = "NOT RUN";
	public static String sBlank = "";
	public static String sLogDirectoryName;
	public static String sLogDirectoryPath;
	public static String sReportDirectoryName;
	public static String sReportSrcDirectoryPath;
	public static String sReportDstDirectoryPath;
	public static String sResultCsvFile;
	public static String sLogFile;
	public static String sErrorLogFile;
	public static String sStartDateTime;
	public static String sEndDateTime;

	// TestData
	public static String sLoginURL = "http://192.168.1.7:8081";
	public static String sUsername = "quamarskj";
	public static String sPassword = "@test123";
	
	/*// VIEW
	public static String sMAPageTitle="";  
	public static String sLoginPageTitle="Channels - Sign in";
	public static String sAdminPageTitle="Channels - Administration";
	public static String sPlanPageSubTitle="Channels:";
	public static String sPlanName="New Plan";
	public static String sAboutPlanPageSubTitle="About Plan: "+sPlanName;
	public static String sPlanPageShowPopUpMenuXPath="/html/body/form/div[12]/span/span[2]/span/span"; 
	*/
}
