package com.mindalliance.globallibrary;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;
import javax.swing.JList;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class GlobalVariables {
	// Selenium Objects
	public static WebDriver oDriver = null;
	public static WebElement oElement = null;
	public static Select oDropDown = null;
	// Login
	public static String sLogin;
	public static String sXpathLogout;
	// Declared Variables
	public static Hashtable<String,String> login=new Hashtable<String,String>();
	public static Hashtable<String,String> channelsAdmin=new Hashtable<String,String>();
	public static Hashtable<String,String> home=new Hashtable<String,String>();
	public static Hashtable<String,String> plan=new Hashtable<String,String>();
	public static Hashtable<String,String> assertion=new Hashtable<String,String>();
	public static Hashtable<String,String> testData=new Hashtable<String,String>();
	public static Hashtable<String,String> viewElements=new Hashtable<String,String>();	
	public static boolean bIsSuccess = false;
	public static Date dCurrentDate = null;
	public static File fCurrentDir = new File(".");
	public static int iStepNo = 0;
	public static int iIndex = 0;
	public static int noOfViewTestCasesExecuted=0;
	public static int noOfPlanTestCasesExecuted=0;
	public static int noOfCommandTestCasesExecuted=0;
	public static int noOfViewTestCasesPassed=0;
	public static int noOfPlanTestCasesPassed=0;
	public static int noOfCommandTestCasesPassed=0;
	public static int noOfViewTestCasesFailed=0;
	public static int noOfPlanTestCasesFailed=0;
	public static int noOfCommandTestCasesFailed=0;
	public static String sTestCaseId = null;
	public static String sObjectRepository=null;
	public static String sHomePage=null;
	public static String sLoginPage=null;
	public static String sPlanPage=null;
	public static String sDescription = null;
	public static String sPassed = "PASS";
	public static String sFailed = "FAIL";
	public static String sVerifyError;
	public static String sNotRun = "NOT RUN";
	public static String sAutomatesYes = "YES";
	public static String sBlank = "";
	public static String sLogDirectoryName;
	public static String sLogDirectoryPath;
	public static String sErrorLogSubDirectoryPath;
	public static String sReportDirectoryName;
	public static String sReportSrcDirectoryPath;
	public static String sReportDstDirectoryPath;
	public static String sTestDataDirectoryPath;
	public static String sObjectRepositoryDirectoryPath;
	public static String sResultCsvFile;
	public static String sLogFile;
	public static String sErrorLogFile;
	public static String sStartDateTime;
	public static String sEndDateTime;
	public static String sBrowser = "Internet Explorer";
	public static String sStrCheck=null;
	public static boolean sunday=true;
	public static boolean monday=true;
	public static boolean tuesday=true;
	public static boolean wednesday=true;
	public static boolean thursday=true;
	public static boolean friday=true;
	public static boolean saturday=true;
	public static JList jListExecute;
	// Extra
	public static String sInternalErrorPageTitle="Internal Error";
	public static String sAboutPlanPageSubTitle="About Plan: ";
	public static String sAboutHomePage="Channels by Mind-Alliance Systems";
}