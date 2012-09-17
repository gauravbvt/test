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

public class MAV0168_AdvanceFormOfReceivenfo extends TestCase{


	public Hashtable<String, String> testData;
	
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
	public void testMAV0168_AdvanceFormOfReceivenfo() throws UIAutomationException {
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
					
			// Click Actions pop up menu and Add New Segment
			PlanPage planPage=new PlanPage();
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForSimpleFormOfReceiveInfo"));
				
			// Close Segment window
			planPage.closeSegmentWindow();
			
			// Click on 'Strench Up' form icon
			planPage.clickStrenchUpForm();
			
			// Click on 'Add' in 'Receives' panel
			planPage.clickAddInReceivesPanel();
			
			// Click on 'Show Advanced Form' link
			planPage.clickShowAdvancedFormInReceivesPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
			
			// Click on Remove this segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
			
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();

		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0168_AdvanceFormOfReceivenfo");
			
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
				
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
		
	}
	
	/**
     * Loads Test Data for MAV0168_AdvanceFormOfReceivenfo.
     * @return void
     * @param  void
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
			File MAV0168_AdvanceFormOfReceivenfo=new File(path + "MAV0168_AdvanceFormOfReceivenfo.xml");
			
			Document docMAV0168_AdvanceFormOfReceivenfo=db.parse(MAV0168_AdvanceFormOfReceivenfo);
			Element eleMAV0168_AdvanceFormOfReceivenfo=docMAV0168_AdvanceFormOfReceivenfo.getDocumentElement();
	              
	        Element oXmlEleMAV0168_AdvanceFormOfReceivenfo = (Element) eleMAV0168_AdvanceFormOfReceivenfo;
	       	
	        this.testData.put("Actions",oXmlEleMAV0168_AdvanceFormOfReceivenfo.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAV0168_AdvanceFormOfReceivenfo.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
	       	this.testData.put("SegmentForSimpleFormOfReceiveInfo",oXmlEleMAV0168_AdvanceFormOfReceivenfo.getElementsByTagName("segmentForSimpleFormOfReceiveInfo").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAV0168_AdvanceFormOfReceivenfo.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ShowSimpleFormText",oXmlEleMAV0168_AdvanceFormOfReceivenfo.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0168_AdvanceFormOfReceivenfo.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0168_AdvanceFormOfReceivenfo.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAV0168_AdvanceFormOfReceivenfo.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0168_AdvanceFormOfReceivenfo.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0168_AdvanceFormOfReceivenfo can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0168_AdvanceFormOfReceivenfo.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0168_AdvanceFormOfReceivenfo can not be parsed.");
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

//	public MAV0168_AdvanceFormOfReceivenfo(){
//    	try {
//    		
//    		GlobalVariables.sTestCaseId = "MAV0168_AdvanceFormOfReceivenfo";
//			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
//			LogFunctions.writeLogs(GlobalVariables.sDescription);
//			System.out.println(GlobalVariables.sDescription);
//			// Call login()
//			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
//			if (GlobalVariables.bIsSuccess) {
//				
//				// Click on 'Information Sharing Model' link
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "Navigated to Information Sharing Model";
//				GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("informationSharingModel"))).click();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//
//				// Click 'Add new Segment' option under 'Actions' pop up menu and enter the details
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "New segment added";
//				ApplicationFunctionLibrary.addSegment(GlobalVariables.testData.get("Segment For Receive Send Advance Form"), "New");
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//				
//				// Stretch Up Task Details
//				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathStretchUpShrinkBack"))).click();
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//				// Click 'Add info received' Link. 
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "'Add Info Received' Link Clicked";
////				GlobalVariables.oDriver.findElement(By.linkText("Add info received")).click();
//				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAddInfoReceive"))).click();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//
//				// Click on Show Advance form link
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "Navigated to Advance form";
//				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowAdvanceSimpleFormReceiveInfo")));
//				// Assertion: Verify that Advance forms gets open
//				if(GlobalVariables.oElement.getText().equals(GlobalVariables.viewElements.get("showAdvanceForm"))){
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//				}
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(3000);
//				
//				// Remove Segment
//				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//				// Get a handle to the open alert, prompt or confirmation
//				Alert alert = GlobalVariables.oDriver.switchTo().alert();
//				// And acknowledge the alert (equivalent to clicking "OK")
//				alert.accept();
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//				
//				// Call Logout
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription="Logout Successful";
//				ApplicationFunctionLibrary.logout();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//				
//				LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
//				System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
//				
//			}
//			else
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//    	} 
//    	catch (Exception e) {
//    		if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
//    			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//    					e.getMessage(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
//    			GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.id("stackTrace"));
//    			LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
//    			ApplicationFunctionLibrary.logout();
//    		}
//    		else {
//    			LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//    					e.getMessage(),GlobalVariables.sBlank);
//    			ApplicationFunctionLibrary.logout();	
//    		}
//    		System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
//    	}
//	}
//	public static void main(String args[]) {
//		try {
//			GenericFunctionLibrary.initializeTestData();
//			GenericFunctionLibrary.loadObjectRepository();
//			new MAV0168_AdvanceFormOfReceivenfo();
//			GenericFunctionLibrary.tearDownTestData();
//			ReportFunctions.generateAutomationReport();
//		} 
//		catch (Exception oException) {
//			// TODO Auto-generated catch block
//			oException.printStackTrace();
//		}
//	}
}