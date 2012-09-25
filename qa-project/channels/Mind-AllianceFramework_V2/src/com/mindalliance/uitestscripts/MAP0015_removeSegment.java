package com.mindalliance.uitestscripts;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.mindalliance.configuration.BrowserController;
import com.mindalliance.configuration.Configuration;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;
import com.mindalliance.pages.PlanPage;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test Case ID: MAP0015_removeSegment Summary: Verify that Segment can be
 * deleted from the plan
 * @author afour
 */
public class MAP0015_removeSegment extends TestCase {


	public Hashtable<String, String> testData;
	String fileName = "PlanPage.xml";
	
	@Before
	protected void setUp(){
		try{
			if (GlobalVariables.configuration == null){
					GlobalVariables.configuration = Configuration.getConfigurationObject();
			}
			if(GlobalVariables.configuration.getAttrSearchList() == null){
				new ElementController();
			}
			
			//Creates Browser instance
			BrowserController browserController= new BrowserController();
			browserController.initializeDriver();
			
			//Loads Test data 
			loadTestData();
		}
		catch(UIAutomationException ue){
			Assert.fail("Unable to initialize the driver "+ue.getErrorMessage());
		}
	}
	
	@Test
	public void testMAP0015_removeSegment() throws UIAutomationException {
		try {
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
		    browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
		 		    
			// Login page
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
				
			// Plan Page
			HomePage homePage=new HomePage();
			homePage.clickCollaborationPlanLink();
			
			// Close Plan Map window
			PlanPage planPage=new PlanPage();
			planPage.closePlanMap();
					
			// Click Actions pop up menu and Add New Segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
						
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForRemoveSegment"));
			
			// Close Segment window
			planPage.closeSegmentWindow();
			
			// Click on Remove this segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
			
			// Verify segment is removed
			planPage.verifySegmentIsDeleted(testData.get("SegmentForRemoveSegment"));
						
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();

		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAP0015_removeSegment");
			
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
				
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
		
	}
	
	/**
     * Loads Test Data for MAP0015_removeSegment.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException
	{		
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAP0015_removeSegment=new File(path + "MAP0015_removeSegment.xml");
			
			Document docMAP0015_removeSegment=db.parse(MAP0015_removeSegment);
			Element eleMAP0015_removeSegment=docMAP0015_removeSegment.getDocumentElement();
	              
	        Element oXmlEleMAP0015_removeSegment = (Element) eleMAP0015_removeSegment;
	       	
	        this.testData.put("Actions",oXmlEleMAP0015_removeSegment.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAP0015_removeSegment.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("SegmentForRemoveSegment",oXmlEleMAP0015_removeSegment.getElementsByTagName("segmentForRemoveSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAP0015_removeSegment.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAP0015_removeSegment.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAP0015_removeSegment.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAP0015_removeSegment can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAP0015_removeSegment.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAP0015_removeSegment can not be parsed.");
		}
			
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	
	@After
	protected void tearDown(){
		if(GlobalVariables.configuration.getWebDriver()!=null){
			GlobalVariables.configuration.getWebDriver().quit();
		}
	}

	
//	public MAP0015_removeSegment() {
//		try {
//			GlobalVariables.sTestCaseId = "MAP0015_removeSegment";
//			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
//			LogFunctions.writeLogs(GlobalVariables.sDescription);
//			System.out.println(GlobalVariables.sDescription);
//			// Call login()
//			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
//			if (GlobalVariables.bIsSuccess) {
//				
//				// Select the plan from 'Switch to Plan' drop down, located on the top right corner
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "Switch to plan";
//				GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("switch-plan:plan-sel")));
//				List <WebElement> options = GlobalVariables.oDropDown.getOptions();
//			    for(WebElement option : options) {
//			    	if(GlobalVariables.testData.get("Automation Test Plan v.1 (dev)").equals(option.getText())){
//			    			option.setSelected();
//			    			break;
//			    	}
//			    }
//			    // Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(3000);	
//
//				// Click on 'Information Sharing Model' link
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription = "Navigated to Information Sharing Model";
//				GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("informationSharingModel"))).click();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(10000);
//
//				// Click on 'Add new segment' option under 'Actions' pop up menu
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription = "New segment added successfully";
//				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewSegment"));
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);
//
//				// Enter the details for new segment
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription = "Details entered";
//				GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name")).click();
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name"));
//				for (int i = 0; i <= 8; i++)
//					GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
//				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Segment For Remove Segment"));
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);
//
//				// Click on 'done' button
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription = "Segment updated";
//				GlobalVariables.oDriver.findElement(By.className("close")).click();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);
//
//				// Click on 'Remove this segment' under 'Actions' pop up menu
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription = "Remove this segment";
//				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
//				// Get a handle to the open alert, prompt or confirmation
//				Alert alert = GlobalVariables.oDriver.switchTo().alert();
//				alert.accept();
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);
//				// Assertion:Verify that segment should get removed.
//				GlobalVariables.bIsSuccess = Boolean.FALSE;
//				GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("select-segment:sg-sel")));
//				options = GlobalVariables.oDropDown.getOptions();
//				for (WebElement option : options) {
//					if (GlobalVariables.testData.get("Segment For Remove Segment").equals(option.getText())) {
//						// Write Results
//						GlobalVariables.bIsSuccess = Boolean.TRUE;
//						break;
//					}
//				}
//				if (GlobalVariables.bIsSuccess == Boolean.FALSE) {
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId,GlobalVariables.iStepNo,GlobalVariables.sDescription,GlobalVariables.sPassed, 
//							GlobalVariables.sBlank,GlobalVariables.sBlank);
//				} else {
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription+""+GlobalVariables.sFailed);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//							GlobalVariables.sBlank, GlobalVariables.sVerifyError);	  
//				}
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);
//
//				// Call logout()
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription = "Logout is successful";
//				ApplicationFunctionLibrary.logout();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId,GlobalVariables.iStepNo,GlobalVariables.sDescription,GlobalVariables.sPassed, 
//						GlobalVariables.sBlank,GlobalVariables.sBlank);
//
//				LogFunctions.writeLogs("Testcase: "+ GlobalVariables.sTestCaseId + " execution completed");
//				System.out.println("Testcase: " + GlobalVariables.sTestCaseId+ " execution completed");
//			} 
//			else{
//				LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
//				System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
//				
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				System.out.println("Unable to Remove Segment" + ReportFunctions.getScreenShot("Remove Segment failed"));
//				GlobalVariables.oDriver.quit();
//			}
//		} 
//		catch (Exception e) {
//			if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						e.getMessage(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.id("stackTrace"));
//				LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
//				System.out.println("Unable to Remove Segment"+ReportFunctions.getScreenShot("Remove Segment failed"));
//				ApplicationFunctionLibrary.logout();
//			}
//			else {
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						e.getMessage(),GlobalVariables.sBlank);
//				System.out.println("Unable to Remove Segment"+ReportFunctions.getScreenShot("Remove Segment failed"));
//				ApplicationFunctionLibrary.logout();	
//			}
//			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
//		}
//	}
//    public static void main(String args[]) {
//		try {
//			GenericFunctionLibrary.initializeTestData();
//			GenericFunctionLibrary.loadObjectRepository();
//			new MAP0015_removeSegment();
//			GenericFunctionLibrary.tearDownTestData();
//			ReportFunctions.generateAutomationReport();
//		} 
//		catch (Exception oException) {
//			// TODO Auto-generated catch block
//			oException.printStackTrace();
//			System.out.println("Unable to Remove Segment"+ReportFunctions.getScreenShot("Remove Segment failed"));
//		}
//	}
}