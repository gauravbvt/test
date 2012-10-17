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
import com.mindalliance.configuration.LogFunctions;
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;
import com.mindalliance.pages.PlanPage;

import junit.framework.Assert;
import junit.framework.TestCase;
/**
 * Test Case ID: MAP0016_addEvent
 * Summary: Verify that event can be added to the Segment
 * @author AfourTech
 *
 */
public class MAP0016_addEvent extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAP0016_addEvent";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="FAIL";
	public String blank=""; 
	public String scriptException;
	public String browser="";
	
	public MAP0016_addEvent() throws UIAutomationException{
		setUp();
		testMAP0016_addEvent();
		tearDown();
	}
	
	/*
	 * This method will initilize the setup required for every test case
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	protected void setUp() throws UIAutomationException{
		try{
			if (GlobalVariables.configuration == null){
					GlobalVariables.configuration = Configuration.getConfigurationObject();
			}
			if(GlobalVariables.configuration.getAttrSearchList() == null){
				new ElementController();
			}
			
			// Loads Test Data
			description = "Testcase: " + testCaseId + " execution started";
			loadTestData();
			// Write log		
			LogFunctions.writeLogs(description);
					
			// Creates Browser instance
			description="Browser initialized";
			browser=BrowserController.browserName;	
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
		}catch(UIAutomationException ue){
			stepNo++;
			description="Unable to initialize the driver";
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo, ue.getErrorMessage(), failed, scriptException, blank);
		}
	}
	
	@Test
	public void testMAP0016_addEvent() throws UIAutomationException {
		try{
			stepNo++;
			description="URL Entered";
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"),browser);
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Login page
			stepNo++;
			description="Login Successful";
			LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
		    // Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 							
			// Plan Page
		    stepNo++;
			description="Collaboration Plan";
			HomePage homePage=new HomePage();
			homePage.clickCollaborationPlanLink();	
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 						
			// Close Plan Map window
			stepNo++;
			description="Close Plan Map Window";
			PlanPage planPage=new PlanPage();
			planPage.closePlanMap();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
 			// Click on 'Add New Segment' under 'Actions' pop up menu
 			stepNo++;
			description="Add New Segment";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));			
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForAddEvent"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Close Segment window
 			stepNo++;
			description="Close About Plan Segment";
			planPage.closeSegmentWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Click on 'Add Event to The Plan Segment' under 'Show' pop up menu
 			stepNo++;
			description="Add Event To The Plan Segment";
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
			// Select After from context dropdown list
			planPage.selectFromContextDropdown(testData.get("After"));
			// Enter event
			planPage.enterEventInAboutPlanSegment(testData.get("Event"));
			// Select Low from between dropdown list
			planPage.selectFromBetweenDropdown(testData.get("Low"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 				
			// Verify event is added
			stepNo++;
			description="Event Added To About Plan Segment";
			planPage.verifyEventAddedInAboutPlanSegment(testData.get("Event"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Close Segment window
			stepNo++;
			description="Closed About Plan Segment";
			planPage.closeSegmentWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Click on Remove this segment
 			stepNo++;
			description="Remove This Segment";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 								
			// Sign Out from 'Plan' page
 			stepNo++;
			description="SignOut Successful";
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
		} catch (UIAutomationException ue) {
			Reporting.getScreenShot(testCaseId);
		
			// Sign out from plan page
			stepNo++;
			description="SignOut Successful";
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			// Write log
 			LogFunctions.writeLogs(ue.getErrorMessage());
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
 			// Quits the Browser
			stepNo++;
			description="Browser Closed";
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
			// Write log
 			LogFunctions.writeLogs(ue.getErrorMessage());
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
		}
	}

	/*
	 * This method will perform cleanup actions
	 * @see junit.framework.TestCase#tearDown()
	 */
	
	@After
	protected void tearDown(){
		if(GlobalVariables.configuration.getWebDriver()!=null){
			GlobalVariables.configuration.getWebDriver().quit();
		}
	}
	/**
     * Loads Test Data for MAP0016_addEvent.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			String startTime=LogFunctions.getDateTime();
			GlobalVariables.configuration.setStartTime(startTime);
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAP0016_addEvent=new File(path + "MAP0016_addEvent.xml");
			
			Document docMAP0016_addEvent=db.parse(MAP0016_addEvent);
			Element eleMAP0016_addEvent=docMAP0016_addEvent.getDocumentElement();
	              
	        Element oXmlEleMAP0016_addEvent = (Element) eleMAP0016_addEvent;
	        
	               	
	        this.testData.put("Actions",oXmlEleMAP0016_addEvent.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAP0016_addEvent.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForAddEvent",oXmlEleMAP0016_addEvent.getElementsByTagName("segmentForAddEvent").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAP0016_addEvent.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAP0016_addEvent.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAP0016_addEvent.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Show",oXmlEleMAP0016_addEvent.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AboutPlanSegment",oXmlEleMAP0016_addEvent.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
	        
	        this.testData.put("After",oXmlEleMAP0016_addEvent.getElementsByTagName("after").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Low",oXmlEleMAP0016_addEvent.getElementsByTagName("low").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Event",oXmlEleMAP0016_addEvent.getElementsByTagName("event").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAP0016_addEvent not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAP0016_addEvent.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAP0016_addEvent can not be parsed.");
		}
	}
	
}