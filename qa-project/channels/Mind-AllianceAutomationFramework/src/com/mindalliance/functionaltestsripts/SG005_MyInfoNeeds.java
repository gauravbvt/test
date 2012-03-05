package com.mindalliance.functionaltestsripts;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class SG005_MyInfoNeeds {
	public SG005_MyInfoNeeds(){
    	try {
			GlobalVariables.sTestCaseId = "SG005_MyInfoNeeds";
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
			GlobalVariables.oElement.sendKeys("user");
			GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_password"));
			GlobalVariables.oElement.sendKeys("@test123");
			// Click on Sign In button
			GlobalVariables.iStepNo=1 ;
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
				
				// Click on 'My Information Sharing Guidelines' link
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Navigated to My Info Needs";
				GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("myInformationNeeds"))).click();
				if(GlobalVariables.oDriver.getTitle().contains("Channels - Participant Information Needs Report for User")){
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
			new SG005_MyInfoNeeds();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
				
}