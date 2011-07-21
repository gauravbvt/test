package com.mindalliance.testscripts;

import org.openqa.selenium.By;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class MAV0164_viewHomeFromIssueReport 
{
	public MAV0164_viewHomeFromIssueReport(){
		  try {
			  GlobalVariables.sTestCaseId = "MAV0164_viewHomeFromIssueReport";
			  GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			  LogFunctions.writeLogs(GlobalVariables.sDescription);
			  System.out.println(GlobalVariables.sDescription);
			  // Call login()
			  GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			  if (GlobalVariables.bIsSuccess) {
				  
				  // Click on 'Issue Summary Report' link
				  GlobalVariables.iStepNo++ ;
				  GlobalVariables.sDescription = "Navigated to Issue Summary Report";
				  GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("issueSummaryReport"))).click();
				  // WebElement Synchronization
				  Thread.currentThread();
				  Thread.sleep(2000);
				  // Assertion: Verify that the Issue Report page renders with title 'Channels – <plan name> – Issues Summary Report' 
				  GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.assertion.get("sXpathIssueReport")));
				  if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.viewElements.get("channelsIssueSummaryReport"))) {
					  // Write Results
					  LogFunctions.writeLogs(GlobalVariables.sDescription);
					  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							  GlobalVariables.sBlank, GlobalVariables.sBlank);
				  }
				  else{
					  GlobalVariables.sVerifyError ="Verification Failed "+"Expected "+GlobalVariables.viewElements.get("channelsIssuesSummaryReport")+" Actual "+GlobalVariables.oDriver.getTitle();
					  // Write Results
					  LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							  GlobalVariables.sBlank, GlobalVariables.sVerifyError);
				  }

				  // Click on Home
				  GlobalVariables.iStepNo++;
				  GlobalVariables.sDescription="Back to Home Page";
				  GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.assertion.get("sXpathIssueReportHome"))).click();
				  // Assertion: Verify that Home page renders with title 'Channels by Mind-Alliance' 
			      if (GlobalVariables.oDriver.getTitle().contains(GlobalVariables.viewElements.get("homePageTitle"))) {
			    	  // Write Results
			    	  LogFunctions.writeLogs(GlobalVariables.sDescription);
			    	  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
			      }
			      else{
					  // Write Results
					  LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
					  LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							  GlobalVariables.sBlank, GlobalVariables.sBlank);
				  }
				  // WebElement Synchronization
				  Thread.currentThread();
				  Thread.sleep(1000);
				  
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
				  
				  GlobalVariables.oDriver.quit();
				  
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
			new MAV0164_viewHomeFromIssueReport();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
