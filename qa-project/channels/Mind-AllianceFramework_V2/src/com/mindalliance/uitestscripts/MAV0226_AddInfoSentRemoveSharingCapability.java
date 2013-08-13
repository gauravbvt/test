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


public class MAV0226_AddInfoSentRemoveSharingCapability extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0226_AddInfoSentRemoveSharingCapability";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="FAIL";
	public String blank=""; 
	public String browser="";
	public String scriptException;
	
	/**
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
		}
		catch(UIAutomationException ue){
			stepNo++;
			description="Unable to initialize the driver";
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo, ue.getErrorMessage(), failed, scriptException, blank);
		}
	}
	
	@Test
	public void testMAV0226_AddInfoSentRemoveSharingCapability() throws UIAutomationException, IOException {
		try{
			stepNo++;
			description="URL Entered";
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
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
 							
 		    // Domain Plans
 		    stepNo++;
 			description="Domain Plans";
 			DomainPlanPage domainPlanPage= new DomainPlanPage();
 			domainPlanPage.clickDomainPlans();	
 			// Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Plan Page
		    stepNo++;
			description="Domain Plan Editor";
			HomePage homePage=new HomePage();
			homePage.clickDomainPlanEditor();	
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 						
			// Add New Segment
 			stepNo++;
			description="Add New Segment";
			PlanPage planPage= new PlanPage();
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			
			// Enter Segment name
			planPage.enterSegmentName(testData.get("SegmentForAddInfoSentRemoveSharingcapability"));
			
			// Close segment window
			planPage.closeSegmentWindow();
			
			// Click on 'Add' button under 'Sends' panel
			planPage.clickAddInfoSendsPanel();
					
			// Click on 'Remove Info Sharing Capability' under 'Actions' pop up menu
			planPage.clickPopupMenu(testData.get("ActionsInSendsPanel"));
			planPage.clickSubmenu(testData.get("RemoveInfoSharingCapabilityInSendsPanel"));	
			
			//Sign Out from 'Plan' page
 			stepNo++;
			description="SignOut Successful";
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);

			Reporting reporting= new Reporting();
		    reporting.generateAutomationReport();
		    
		}catch (UIAutomationException ue) {
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo,description,failed, ue.getErrorMessage(), blank);
			Reporting.getScreenShot(testCaseId);
		    
			// Sign out from home page
		    stepNo++;
		    description="Logout successful";
			HeaderController headerController=new HeaderController();
			headerController.signOut();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
				
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
		}
	}
	
	/**
     * Loads Test Data for MAV0226_AddInfoSentRemoveSharingCapability.
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
			File MAV0226_AddInfoSentRemoveSharingCapability=new File(path + "MAV0226_AddInfoSentRemoveSharingCapability.xml");
			
			Document docMAV0226_AddInfoSentRemoveSharingCapability=db.parse(MAV0226_AddInfoSentRemoveSharingCapability);
			Element eleMAV0226_AddInfoSentRemoveSharingCapability=docMAV0226_AddInfoSentRemoveSharingCapability.getDocumentElement();
	              
	        Element oXmlEleMAV0226_AddInfoSentRemoveSharingCapability = (Element) eleMAV0226_AddInfoSentRemoveSharingCapability;
	        
	               	
	        this.testData.put("ChannelsURL",oXmlEleMAV0226_AddInfoSentRemoveSharingCapability.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0226_AddInfoSentRemoveSharingCapability.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ActionsInSendsPanel",oXmlEleMAV0226_AddInfoSentRemoveSharingCapability.getElementsByTagName("actionsInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("RemoveInfoSharingCapabilityInSendsPanel",oXmlEleMAV0226_AddInfoSentRemoveSharingCapability.getElementsByTagName("removeInfoSharingCapabilityInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
		 	
		 	this.testData.put("Actions",oXmlEleMAV0226_AddInfoSentRemoveSharingCapability.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AddNewSegment",oXmlEleMAV0226_AddInfoSentRemoveSharingCapability.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAV0226_AddInfoSentRemoveSharingCapability.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("SegmentForAddInfoSentRemoveSharingcapability",oXmlEleMAV0226_AddInfoSentRemoveSharingCapability.getElementsByTagName("segmentForAddInfoSentRemoveSharingcapability").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0226_AddInfoSentRemoveSharingCapability not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0226_AddInfoSentRemoveSharingCapability.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0226_AddInfoSentRemoveSharingCapability can not be parsed.");
		}
	}
	
}