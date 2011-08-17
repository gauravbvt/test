package com.mindalliance.functionaltestsripts;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.Select;

import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class SG002_MyInfoSharingGuidelinesForUser 
{
	public SG002_MyInfoSharingGuidelinesForUser(){
    	try {
			GlobalVariables.sTestCaseId = "SG002_MyInfoSharingGuidelinesForUser";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			
			// Create Selenium Webdriver object
			if (GlobalVariables.sBrowser.equals("Mozilla Firefox"))
				GlobalVariables.oDriver = new FirefoxDriver();
			else if (GlobalVariables.sBrowser.equals("Internet Explorer"))
			{
				try{
					GlobalVariables.oDriver = new InternetExplorerDriver();
				}
				catch (Exception e){
					GlobalVariables.oDriver = new InternetExplorerDriver();
				}
			}
			// Enter the URL
			GlobalVariables.oDriver.get(GlobalVariables.login.get("sChannelURL"));
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(2000);
			GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_username"));
			GlobalVariables.oElement.sendKeys("priyanka");
			GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_password"));
			GlobalVariables.oElement.sendKeys("piu");
			// Click on Sign In button
			GlobalVariables.iStepNo++ ;
			GlobalVariables.sDescription = "Login is successful";
			GlobalVariables.oDriver.findElement(By.name("_spring_security_remember_me")).click();
			GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.login.get("sLogin"))).click();
			// Write Results
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed,
					GlobalVariables.sBlank, GlobalVariables.sBlank);
			// WebElement Synchronization
			Thread.currentThread();
			Thread.sleep(3000);
			
			if (GlobalVariables.bIsSuccess==false) {
				
				// Select  the plan from 'Switch to Plan' drop down, located on the top right corner
				GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("switch-plan:plan-sel")));
				List <WebElement> options = GlobalVariables.oDropDown.getOptions();
			    for(WebElement option : options) {
			    	if(GlobalVariables.testData.get("Add Test Plan v.1 (dev)").equals(option.getText())){
			    			option.setSelected();
			    			break;
			    	}
			    }
			    // WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(5000);	
				
				// 'My Information Sharing Guidelines' link
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "My Information Sharing Guidelines Link is Present";
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("myInformationSharingGuidelines")));
				if(GlobalVariables.oElement.getText().equals("My information sharing guidelines")){
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else{
					GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Information sharing guidelines for all participants' "+" Actual "+GlobalVariables.oElement.getText();
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				// Click on 'My Information Sharing Guidelines' link
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Navigated to My Information Sharing Guidelines";
				GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("myInformationSharingGuidelines"))).click();
				if(GlobalVariables.oDriver.getTitle().contains(GlobalVariables.viewElements.get("participantPagesTitle"))){
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				// Click on Home Icon
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Home Page";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathInformationSharingGuidelinesForParticipantsToHome"))).click();
				if (GlobalVariables.oDriver.getTitle().contains(GlobalVariables.viewElements.get("homePageTitle"))) {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else{
			    	  GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Channels - Sign in' "+" Actual "+GlobalVariables.oElement.getText();
			    	  // Write Results
			    	  LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
			    	  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
			    			  GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				}	
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Call Logout
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Logout is successful";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathLogoutHomePage"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				GlobalVariables.oDriver.quit();
				
			}else
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
	} 
	catch (Exception e) {
		if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
					e.getMessage(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
			GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.id("stackTrace"));
			LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
			GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathLogoutHomePage"))).click();
			GlobalVariables.oDriver.quit();
		}
		else {
			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
					e.getMessage(),GlobalVariables.sBlank);
			GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathLogoutHomePage"))).click();	
			GlobalVariables.oDriver.quit();
		}
		System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
	}
  }
    public static void main(String args[]) {
		try {
			GenericFunctionLibrary.initializeTestData();
			GenericFunctionLibrary.loadObjectRepository();
			new SG002_MyInfoSharingGuidelinesForUser();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}