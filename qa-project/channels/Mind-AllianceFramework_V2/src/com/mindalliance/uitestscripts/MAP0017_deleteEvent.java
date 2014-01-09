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
import com.mindalliance.configuration.DataController;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.Log4J;
import com.mindalliance.configuration.LogFunctions;
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;
import com.mindalliance.pages.DomainPlanPage;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;
import com.mindalliance.pages.PlanPage;

import junit.framework.Assert;
import junit.framework.TestCase;
/**
 * Test Case ID: MAP0017_deleteEvent
 * Summary: Verify that event can be deleted  from the Segment
 * @author AfourTech
 *
 */
public class MAP0017_deleteEvent extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAP0017_deleteEvent";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="FAIL";
	public String blank=""; 
	public String scriptException;
	public String browser="";
	
	/*
	 * This method will initialize the setup required for every test case
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
			DataController dataController= new DataController();
			dataController.createResultFiles();
			
			GlobalVariables.configuration.addTestCaseIdToJList(testCaseId);	
			// Loads Test Data
			description = "Testcase: " + testCaseId + " execution started";
			loadTestData();
			// Write log			
			LogFunctions.writeLogs(description);
						
			// Creates Browser instance
			BrowserController browserController= new BrowserController();
			browserController.initializeDriver();		
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser initialized");	
			
		}catch(UIAutomationException ue){
			stepNo++;
			description="Unable to initialize the driver";
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo, ue.getErrorMessage(), failed, scriptException, blank);
			Log4J.getlogger(this.getClass()).error(testCaseId +"Unable to initialize the driver");	
			}
	}
	
	@Test
	public void testMAP0017_deleteEvent() throws UIAutomationException, IOException {
		try{
			stepNo++;
			description="URL Entered";
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"URL Entered");	
			
			// Login page
			stepNo++;
			description="Login Successful";
			LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
		    // Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Login Successful");	
							
 		   // Domain Plans
 		    stepNo++;
 			description="Domain Plans";
 			DomainPlanPage domainPlanPage= new DomainPlanPage();
 			domainPlanPage.clickDomainPlans();	
 			// Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Domain Plans");	
			
			// Plan Page
		    stepNo++;
			description="Domain Plan Editor";
			HomePage homePage=new HomePage();
			homePage.clickDomainPlanEditor();	
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Domain Plan Editor");	
			
 			// Click on 'Add New Segment' under 'Actions' pop up menu
 			stepNo++;
			description="Add New Segment";
			PlanPage planPage=new PlanPage();
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));			
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForDeleteEvent"));
			Log4J.getlogger(this.getClass()).info(testCaseId +"Add New Segment");	
			
			// Close Segment window
			stepNo++;
			description="Close About Plan Segment Window";
			planPage.closeSegmentWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
 		    // Click on 'Add Event to The Plan Segment' under 'Show' pop up menu
 			stepNo++;
			description="Add Event To The Plan Segment";
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
			Log4J.getlogger(this.getClass()).info(testCaseId +"Add Event To The Plan Segment");	
			
 			//Click on Scenario tab in the 'About Plan Segment' window
			stepNo++;
			description="Scenario Tab in the About Plan Segment window";
			planPage.clickScenarioTab();
			//Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo,description,passed, blank, blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Scenario Tab in the About Plan Segment window");	
			
			// Select Responding from Phase dropdown list
			stepNo++;
			description="Select Option From Phase Dropdown List";
			planPage.selectFromPhaseDropdown(testData.get("Responding"));
			//Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo,description,passed, blank, blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Select Option From Phase Dropdown List");	
			
			// Enter event
			stepNo++;
			description="Enter Event";
			planPage.enterEventInAboutPlanSegment(testData.get("Event"));
			//Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo,description,passed, blank, blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Enter Event");	
			
			// Select Low from first Rated dropdown list
			stepNo++;
			description="Select Option From First Rated Dropdown";
			planPage.selectFromFirstRatedDropdown(testData.get("Low"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Select Option From First Rated Dropdown");	
			
 		    // Select After from Occurring dropdown list
 			stepNo++;
			description="Select Option From Occurring Dropdown";
 			planPage.selectFromOccurringDropdown(testData.get("After"));
 			//Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo,description,passed, blank, blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Select Option From Occurring Dropdown");	
				
 		    // Enter second event
			stepNo++;
			description="Enter Second Event";
 			planPage.enterSecondEventInAboutPlanSegment(testData.get("Event2"));
 			//Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo,description,passed, blank, blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Enter Second Event");	
			
 	    	// Select High from second Rated dropdown list
			stepNo++;
			description="Select Option From Second Rated Dropdown";
 		    planPage.selectFromSecondRatedDropdown(testData.get("High"));
 			// Write log
 		 	LogFunctions.writeLogs(description);
 		 	LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 		 	Log4J.getlogger(this.getClass()).info(testCaseId +"Select Option From Second Rated Dropdown");	
						
			// Verify event is added
 			stepNo++;
			description="Event Is Added to The Plan Segment";
			planPage.verifyEventAddedInAboutPlanSegment(testData.get("Event2"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Event Is Added to The Plan Segment");	
			
			// Delete Event
 			stepNo++;
			description="Delete Event From The Plan Segment";
			planPage.deleteEvent(testData.get("After")+" "+testData.get("Event")+" ("+testData.get("Low").toLowerCase()+")");
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Delete Event From The Plan Segment");	
			
			// Verify event is deleted
 			stepNo++;
			description="Add Event to The Plan Segment";
			planPage.verifyEventIsDeleted(testData.get("Event2"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add Event to The Plan Segment");	
			
			// Close Segment window
 			stepNo++;
			description="Close About Plan Segment";
			planPage.closeSegmentWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment");	
			
			//Click on Remove this segment
 			stepNo++;
			description="Remove This Segment";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Remove This Segment");	
							
			//Sign Out from 'Plan' page
 			stepNo++;
			description="SignOut Successful";
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"SignOut Successful");	
			
			Reporting reporting= new Reporting();
		    reporting.generateAutomationReport();
		    
		}catch (UIAutomationException ue) {
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo,description,failed, ue.getErrorMessage(), blank);
			Reporting.getScreenShot(testCaseId);
			Log4J.getlogger(this.getClass()).error(testCaseId +ue.getErrorMessage());	
			
			// Sign out from home page
		    stepNo++;
		    description="Logout successful";
			HeaderController headerController=new HeaderController();
			headerController.signOut();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Logout successful");	
				
			Reporting reporting= new Reporting();
		    reporting.generateAutomationReport();
		    
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());		
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
			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser Quit");	
			
		}
	}
	
	/**
     * Loads Test Data for MAP0017_deleteEvent.
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
			File MAP0017_deleteEvent=new File(path + "MAP0017_deleteEvent.xml");
			
			Document docMAP0017_deleteEvent=db.parse(MAP0017_deleteEvent);
			Element eleMAP0017_deleteEvent=docMAP0017_deleteEvent.getDocumentElement();
	              
	        Element oXmlEleMAP0017_deleteEvent = (Element) eleMAP0017_deleteEvent;
 	
	        this.testData.put("Actions",oXmlEleMAP0017_deleteEvent.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAP0017_deleteEvent.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForDeleteEvent",oXmlEleMAP0017_deleteEvent.getElementsByTagName("segmentForDeleteEvent").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAP0017_deleteEvent.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAP0017_deleteEvent.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAP0017_deleteEvent.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Show",oXmlEleMAP0017_deleteEvent.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AboutPlanSegment",oXmlEleMAP0017_deleteEvent.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
	        
	        this.testData.put("After",oXmlEleMAP0017_deleteEvent.getElementsByTagName("after").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Low",oXmlEleMAP0017_deleteEvent.getElementsByTagName("low").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Event",oXmlEleMAP0017_deleteEvent.getElementsByTagName("event").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Event2",oXmlEleMAP0017_deleteEvent.getElementsByTagName("event2").item(0).getChildNodes().item(0).getNodeValue());
	    //    this.testData.put("After",oXmlEleMAP0017_deleteEvent.getElementsByTagName("after").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("High",oXmlEleMAP0017_deleteEvent.getElementsByTagName("high").item(0).getChildNodes().item(0).getNodeValue());	
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAP0017_deleteEvent not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAP0017_deleteEvent.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAP0017_deleteEvent can not be parsed.");
		}
	}
}