package com.mindalliance.functionaltestsripts;

import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class CA022_AssignUserToPlan 
{
	public CA022_AssignUserToPlan() {
		try {
			GlobalVariables.sTestCaseId = "CA022_AssignUserToPlan";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			if (GlobalVariables.bIsSuccess) {
				
				// Click on 'Channel Administration' link
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Navigated to Channel Administration";
				GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("channelsAdministration"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				// Enter the details: newPlanUri
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "(New Plan URI) and (owned by) entered";
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("newPlanUri"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("View Plan"));
				// newPlanClient
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("newPlanClient"));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Afourtech"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);				
				// Click on 'Submit' button
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Plan Created";
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);

				//Select Newly Created Plan
				GlobalVariables.oDriver.findElement(By.name("plan-sel")).click();
				GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("plan-sel")));
				List <WebElement> options = GlobalVariables.oDropDown.getOptions();
			    for(WebElement option : options) {
			    	if(GlobalVariables.testData.get("New Plan v.1 (dev)").equals(option.getText())){
			    		option.setSelected();
			    		break;
			    	}
			    }
			    // WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				//Create User
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="User Created";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathInputUserID"))).click();
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathInputUserID")));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("user"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);					
				//Assign Access Privilege 'Planner' to User
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Assign Access Privilege 'Planner' to User (This Plan Tab)";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathUserPassword"))).click();
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathUserPassword")));
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Password"));
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				//Assign Access Privilege
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathThisPlanAccessPrivilegePlanner"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				// Click on 'Submit' button
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);

				// Call logout()
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Logout is successful";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathLogoutAdminPage"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
				
				// Login with newly Created user
			    GlobalVariables.iStepNo++;
			    GlobalVariables.sDescription="Login with newly Created User";
			    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_username"));
			    GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("user"));
			    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_password"));
			    GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Password"));
			    // Write Results
		    	LogFunctions.writeLogs(GlobalVariables.sDescription);
		    	LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
		    	// WebElement Synchronization
			    Thread.currentThread();
			    Thread.sleep(1000);			    
			    
			    // Click on Sign In button
			    GlobalVariables.iStepNo++ ;
			    GlobalVariables.sDescription = "Login Successful";
			    GlobalVariables.oDriver.findElement(By.name("_spring_security_remember_me")).click();
			    GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.login.get("sLogin"))).click();
			    // Write Results
		    	LogFunctions.writeLogs(GlobalVariables.sDescription);
		    	LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
		    	// WebElement Synchronization
			    Thread.currentThread();
			    Thread.sleep(1000);	
			    //Assertion : Verify that User is able to view & access the plan created by Administrator
			    GlobalVariables.iStepNo++ ;
			    GlobalVariables.sDescription = "User is able to view the plan created by Administrator";
			    GlobalVariables.oDriver.findElement(By.className("planTitle")).click();
			    GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.className("planTitle"));
			    if(GlobalVariables.oElement.getText().contains(GlobalVariables.viewElements.get("afourtech")))
			    {
				    // Write Results
			    	LogFunctions.writeLogs(GlobalVariables.sDescription);
			    	LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);			    	
			    }
			    else
			    {
			    	GlobalVariables.sVerifyError="Verification Failed. Expected '"+GlobalVariables.viewElements.get("afourtech")+"' Actual '"+GlobalVariables.oElement.getText()+"'";
			    	// Write Results
			    	LogFunctions.writeLogs(GlobalVariables.sDescription);
			    	LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);			    				    	
			    }
			    // Call logout()
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Logout is successful";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathLogoutHomePage"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);

				// Login with newly Created user
			    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_username"));
			    GlobalVariables.oElement.sendKeys(GlobalVariables.login.get("sUsername"));
			    GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("j_password"));
			    GlobalVariables.oElement.sendKeys(GlobalVariables.login.get("sPassword"));
		    	// WebElement Synchronization
			    Thread.currentThread();
			    Thread.sleep(1000);			    
			    // Click on Sign In button
			    GlobalVariables.oDriver.findElement(By.name("_spring_security_remember_me")).click();
			    GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.login.get("sLogin"))).click();
		    	// WebElement Synchronization
			    Thread.currentThread();
			    Thread.sleep(1000);	
			    //Navigate to Channels Administration Page
				GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("channelsAdministration"))).click();
		    	// WebElement Synchronization
			    Thread.currentThread();
			    Thread.sleep(1000);					
			    GlobalVariables.oDriver.findElement(By.name("plan-sel")).click();
				GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("plan-sel")));
				options = GlobalVariables.oDropDown.getOptions();
			    for(WebElement option : options) {
			    	if(GlobalVariables.testData.get("New Plan v.1 (dev)").equals(option.getText())){
			    		option.setSelected();
			    		break;
			    	}
			    }
			    // WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(3000);
			    // Click on 'Delete Plan' button
				GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.testData.get("Delete plan"))).click();
				Alert alert = GlobalVariables.oDriver.switchTo().alert();
				// Click on 'OK" button of message box in order to confirm it
				alert.accept();
				//Thread sleep
				Thread.currentThread();
				Thread.sleep(3000);
				//Delete Created User
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathDeleteUser"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				GlobalVariables.oDriver.findElement(By.name("Submit")).submit();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);

			    // Call logout()
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Logout is successful";
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.channelsAdmin.get("sXpathLogoutAdminPage"))).click();
				GlobalVariables.oDriver.quit();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				
				LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
				System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
			}
			else
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
		} 
		catch (Exception e) {
			if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.id("stackTrace"));
				LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
				ApplicationFunctionLibrary.logout();
			}
			else {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
						e.getMessage(),GlobalVariables.sBlank);
				ApplicationFunctionLibrary.logout();	
			}
			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
		}
	}
    public static void main(String args[]) {
		try {
			GenericFunctionLibrary.initializeTestData();
			GenericFunctionLibrary.loadObjectRepository();
			new CA022_AssignUserToPlan();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			oException.printStackTrace();
		}
	}
}
