package com.mindalliance.functionaltestsripts;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

public class TE026_UpdateAgentsAvailabilityForSpecifiedTime 
{
	public TE026_UpdateAgentsAvailabilityForSpecifiedTime() {
		try{
			
			GlobalVariables.sTestCaseId = "TE026_UpdateAgentsAvailabilityForSpecifiedTime";
			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			if (GlobalVariables.bIsSuccess) {

				// Navigate to Information Sharing Model Link
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Navigated to Information Sharing Model Link";
				GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("informationSharingModel"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				//About Plan Window Opened
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "About Plan Window Opened";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlan"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Click on 'Participation' Option under 'Show' Pop up Menu 
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Click on 'Participation' option under 'Show' pop up menu";
				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAboutPlanShowMenu"),GlobalVariables.viewElements.get("participation"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				//Create Agent
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Agent Created";
				GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:participations:participationsTable:participations:body:rows:1:cells:4:cell:entityName")).clear();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("plan:content:mo:aspect:participations:participationsTable:participations:body:rows:1:cells:4:cell:entityName"));
				String sAgentName = LogFunctions.getDateTime();
				GlobalVariables.oElement.sendKeys(sAgentName);
				GlobalVariables.oElement.sendKeys(Keys.ENTER);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				//Click on 'Agent' Details
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAgentContacts"))).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAgent"))).click();
				//Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				//Click on 'Always Available' Check box
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="'Always Available' Checkbox unchecked";
				GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:availability:twentyFourSeven")).click();
				//Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
					GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				//Update Agent's Availability
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Agents Availability Updated for Specified Time";
				GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:availability:timePeriodsContainer:dayAvailability:0:timePeriod:dayOn")).click();
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:availability:timePeriodsContainer:dayAvailability:0:timePeriod:dayOn"));
				GlobalVariables.sunday=GlobalVariables.oElement.isSelected();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:availability:timePeriodsContainer:dayAvailability:6:timePeriod:dayOn")).click();
				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:availability:timePeriodsContainer:dayAvailability:6:timePeriod:dayOn"));
				GlobalVariables.saturday=GlobalVariables.oElement.isSelected();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				//Verify that Agent's Availability gets Updated
				if(GlobalVariables.sunday==false && GlobalVariables.saturday==false) {
					//Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);
				}
				else {
					//Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
							GlobalVariables.sBlank, GlobalVariables.sBlank);					
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				//Check 'Always Available' Checkbox
				GlobalVariables.oDriver.findElement(By.name("entity:content:mo:aspect:mo-details:availability:twentyFourSeven")).click();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				
				// Click on done
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Agents avalibility updated";
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				
				// Click on done
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription="Done";
				GlobalVariables.oDriver.findElement(By.className("close")).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);	
				
				// Call logout()
				GlobalVariables.iStepNo++ ;
				GlobalVariables.sDescription = "Logout is successful";
				ApplicationFunctionLibrary.logout();				
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
						GlobalVariables.sBlank, GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(2000);
				  
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
			new TE026_UpdateAgentsAvailabilityForSpecifiedTime();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} 
		catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}