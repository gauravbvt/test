package com.mindalliance.uitestscripts;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;
import junit.framework.TestCase;

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
/**
 * Test Case ID: MAP0013_addPhaseDetails
 * Summary: Verify that phases details can be added to the plan
 * @author AfourTech
 *
 */
public class MAP0013_addPhaseDetails extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAP0013_addPhaseDetails";
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
	public void testMAP0013_addPhaseDetails() throws UIAutomationException, IOException{
		try {
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
			
  			// Click on 'Events In Scope' under 'Scoping' pop up menu
 			stepNo++;
			description="Event Phases";
			PlanPage planPage=new PlanPage();
			planPage.clickPopupMenu(testData.get("Scoping"));
			planPage.clickSubmenu(testData.get("EventsInScope"));
			planPage.clickPhaseTab();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Event Phases");	
			
			// Add phase
 			stepNo++;
			description="Create Phase";
			planPage.enterValueInPhaseInEvent(testData.get("Phase"));
			planPage.clickOnPhaseInAboutPlan();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Create Phase");	
			
			// Enter phase description
			stepNo++;
			description="Phase Description";
			planPage.enterValueInPhaseDescriptionInAboutPlan(testData.get("PhaseDescription"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Phase Description");	
			
			// Close phase window
			stepNo++;
			description="Close Phase Window";
			planPage.closeActualPhaseWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close Phase Window");	
			
			// Delete phase
			stepNo++;
			description="Delete Phase";
			planPage.deletePhase();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Delete Phase");	
			
 			// Close phase window
			stepNo++;
			description="Close Events and Phase Window";
			planPage.closeEventsWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close Events and Phase Window");	
			
			// Sign Out from 'Plan' page
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
     * Loads Test Data for MAP0013_addPhaseDetails.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException
	{
		try{
			String startTime=LogFunctions.getDateTime();
			GlobalVariables.configuration.setStartTime(startTime);
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + File.separator+"TestData"+File.separator;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAP0013_addPhaseDetails=new File(path + "MAP0013_addPhaseDetails.xml");
			
			Document docMAP0013_addPhaseDetails=db.parse(MAP0013_addPhaseDetails);
			Element eleMAP0013_addPhaseDetails= docMAP0013_addPhaseDetails.getDocumentElement();
	              
	        Element oXmlEleMAP0013_addPhaseDetails = (Element) eleMAP0013_addPhaseDetails;
	       	
	        this.testData.put("Show",oXmlEleMAP0013_addPhaseDetails.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AboutPlan",oXmlEleMAP0013_addPhaseDetails.getElementsByTagName("aboutPlan").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL",oXmlEleMAP0013_addPhaseDetails.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAP0013_addPhaseDetails.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Scoping", oXmlEleMAP0013_addPhaseDetails.getElementsByTagName("scoping").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("EventsInScope",oXmlEleMAP0013_addPhaseDetails.getElementsByTagName("eventsInScope").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Actions",oXmlEleMAP0013_addPhaseDetails.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAP0013_addPhaseDetails.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForAddPhaseDetails",oXmlEleMAP0013_addPhaseDetails.getElementsByTagName("segmentForAddPhaseDetails").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAP0013_addPhaseDetails.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Phase",oXmlEleMAP0013_addPhaseDetails.getElementsByTagName("phase").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("PhaseDescription",oXmlEleMAP0013_addPhaseDetails.getElementsByTagName("phaseDescription").item(0).getChildNodes().item(0).getNodeValue());
		
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAP0013_addPhaseDetails.xml not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAP0013_addPhaseDetails.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAP0013_addPhaseDetails.xml can not be parsed.");
		}
	}
}