package com.mindalliance.testscripts;

import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.mindalliance.globallibrary.ApplicationFunctionLibrary;
import com.mindalliance.globallibrary.GenericFunctionLibrary;
import com.mindalliance.globallibrary.GlobalVariables;
import com.mindalliance.globallibrary.LogFunctions;
import com.mindalliance.globallibrary.ReportFunctions;

/**
 * Test Case ID: MAP0015_removeSegment Summary: Verify that Segment can be
 * deleted from the plan
 * 
 * @author AfourTech
 * 
 */
public class MAP0015_removeSegment {
	public MAP0015_removeSegment() {
		try {
			GlobalVariables.sTestCaseId = "MAP0015_removeSegment";
			GlobalVariables.sDescription = "Testcase: "
				+ GlobalVariables.sTestCaseId + " execution started";
			LogFunctions.writeLogs(GlobalVariables.sDescription);
			System.out.println(GlobalVariables.sDescription);
			// Call login()
			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
			if (GlobalVariables.bIsSuccess) {

				// Click on 'Information Sharing Model' link
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription = "Navigated to Information Sharing Model";
				GlobalVariables.oDriver.findElement(
						By.linkText(GlobalVariables.viewElements
								.get("informationSharingModel"))).click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId,
						GlobalVariables.iStepNo, GlobalVariables.sDescription,
						GlobalVariables.sPassed, GlobalVariables.sBlank,
						GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(10000);

				// Click on 'Add new segment' option under 'Actions' pop up menu
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription = "New segment added successfully";
				ApplicationFunctionLibrary.MouseOverAndClick(
						GlobalVariables.plan.get("sXpathActionsPopUpMenu"),
						GlobalVariables.viewElements.get("addNewSegment"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId,
						GlobalVariables.iStepNo, GlobalVariables.sDescription,
						GlobalVariables.sPassed, GlobalVariables.sBlank,
						GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);

				// Enter the details for new segment
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription = "Details entered";
				GlobalVariables.oDriver.findElement(
						By.name("sg-editor:mo:aspect:name")).click();
				GlobalVariables.oElement = GlobalVariables.oDriver
				.findElement(By.name("sg-editor:mo:aspect:name"));
				for (int i = 0; i <= 8; i++)
					GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
				GlobalVariables.oElement.sendKeys(GlobalVariables.testData
						.get("Segment For Remove Segment"));
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId,
						GlobalVariables.iStepNo, GlobalVariables.sDescription,
						GlobalVariables.sPassed, GlobalVariables.sBlank,
						GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);

				// Click on 'done' button
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription = "Segment updated";
				GlobalVariables.oDriver.findElement(By.className("close"))
				.click();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId,
						GlobalVariables.iStepNo, GlobalVariables.sDescription,
						GlobalVariables.sPassed, GlobalVariables.sBlank,
						GlobalVariables.sBlank);
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);

				// Click on 'Remove this segment' under 'Actions' pop up menu
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription = "Remove this segment";
				ApplicationFunctionLibrary.MouseOverAndClick(
						GlobalVariables.plan.get("sXpathActionsPopUpMenu"),
						GlobalVariables.viewElements.get("removeThisSegment"));
				// Get a handle to the open alert, prompt or confirmation
				Alert alert = GlobalVariables.oDriver.switchTo().alert();
				alert.accept();
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);
				// Assertion:Verify that segment should get removed.
				GlobalVariables.bIsSuccess = Boolean.FALSE;
				GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver
						.findElement(By.name("select-segment:sg-sel")));
				List<WebElement> options = GlobalVariables.oDropDown
				.getOptions();
				for (WebElement option : options) {
					if (GlobalVariables.testData.get(
							"Segment For Remove Segment").equals(
									option.getText())) {
						// Write Results
						GlobalVariables.bIsSuccess = Boolean.TRUE;
						break;
					}
				}
				if (GlobalVariables.bIsSuccess == Boolean.FALSE) {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId,
							GlobalVariables.iStepNo,
							GlobalVariables.sDescription,
							GlobalVariables.sPassed, GlobalVariables.sBlank,
							GlobalVariables.sBlank);
				} else {
					// Write Results
					LogFunctions.writeLogs(GlobalVariables.sDescription + ""
							+ GlobalVariables.sFailed);
					LogFunctions.writeResults(GlobalVariables.sTestCaseId,
							GlobalVariables.iStepNo,
							GlobalVariables.sDescription,
							GlobalVariables.sFailed, GlobalVariables.sBlank,
							GlobalVariables.sVerifyError);
				}
				// WebElement Synchronization
				Thread.currentThread();
				Thread.sleep(1000);

				// Call logout()
				GlobalVariables.iStepNo++;
				GlobalVariables.sDescription = "Logout is successful";
				ApplicationFunctionLibrary.logout();
				// Write Results
				LogFunctions.writeLogs(GlobalVariables.sDescription);
				LogFunctions.writeResults(GlobalVariables.sTestCaseId,
						GlobalVariables.iStepNo, GlobalVariables.sDescription,
						GlobalVariables.sPassed, GlobalVariables.sBlank,
						GlobalVariables.sBlank);

				LogFunctions.writeLogs("Testcase: "
						+ GlobalVariables.sTestCaseId + " execution completed");
				System.out.println("Testcase: " + GlobalVariables.sTestCaseId
						+ " execution completed");
			} else
				LogFunctions.writeResults(GlobalVariables.sTestCaseId,
						GlobalVariables.iStepNo, GlobalVariables.sDescription,
						GlobalVariables.sFailed, GlobalVariables.sBlank,
						GlobalVariables.sBlank);
		} catch (Exception e) {
			if (GlobalVariables.oDriver.getTitle().equals(
					GlobalVariables.sInternalErrorPageTitle)) {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId,
						GlobalVariables.iStepNo, GlobalVariables.sDescription,
						GlobalVariables.sFailed, e.getMessage(),
						GlobalVariables.sErrorLogSubDirectoryPath + "\\"
						+ GlobalVariables.sTestCaseId + ".logs");
				GlobalVariables.oElement = GlobalVariables.oDriver
				.findElement(By.id("stackTrace"));
				LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
				ApplicationFunctionLibrary.logout();
			} else {
				LogFunctions.writeResults(GlobalVariables.sTestCaseId,
						GlobalVariables.iStepNo, GlobalVariables.sDescription,
						GlobalVariables.sFailed, e.getMessage(),
						GlobalVariables.sBlank);
				ApplicationFunctionLibrary.logout();
			}
			System.out.println("Testcase: " + GlobalVariables.sTestCaseId
					+ " execution failed");
		}
	}

	public static void main(String args[]) {
		try {
			GenericFunctionLibrary.initializeTestData();
			GenericFunctionLibrary.loadObjectRepository();
			new MAP0015_removeSegment();
			GenericFunctionLibrary.tearDownTestData();
			ReportFunctions.generateAutomationReport();
		} catch (Exception oException) {
			// TODO Auto-generated catch block
			oException.printStackTrace();
		}
	}
}
