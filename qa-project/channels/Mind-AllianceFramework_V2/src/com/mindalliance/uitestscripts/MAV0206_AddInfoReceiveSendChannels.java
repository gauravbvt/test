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


public class MAV0206_AddInfoReceiveSendChannels extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0206_AddInfoReceiveSendChannels";
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
			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser initialized");
 					
			// Creates Browser instance
			BrowserController browserController= new BrowserController();
			browserController.initializeDriver();		
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser initialized");
 			
		}
		catch(UIAutomationException ue){
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
	public void testMAV0206_AddInfoReceiveSendChannels() throws UIAutomationException, IOException {
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
			description="Navigated to Plan page";
			HomePage homePage=new HomePage();
			homePage.clickDomainPlanEditor();	
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Navigated to Plan page");
 			
			// Add New Segment
 			stepNo++;
			description="Add New Segment";
			PlanPage planPage= new PlanPage();
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add New Segment");
 			
			// Close segment window
			stepNo++;
			description="Close About Plan Segment Window";
			planPage.closeSegmentWindow();
			// Click on 'stretch Up forms' icon 
			planPage.clickStrenchUpForm();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment Window");
 			
			// Click on 'Add' in sends panel
			stepNo++;
			description="Add Info Sends";
			planPage.clickAddInfoSendsPanel();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add Info Sends");
 			
			// Click on 'Show Advanced Form' in sends panel
			stepNo++;
			description="Advance Form - Sends Panel";
			planPage.clickShowAdvancedFormInSendsPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Advance Form - Sends Panel");
 			
			// Click on 'Channels' dropdown in sends panel
			stepNo++;
			description="Channels - Sends Panel";
			planPage.clickChannelsDropdownInSendsPanel(testData.get("Cell"), testData.get("ConferenceCall"), testData.get("Courier"), testData.get("Email"),
					testData.get("FaceToFace"), testData.get("Fax"), testData.get("IM"),testData.get("Landline"), testData.get("Mail"), testData.get("Meeting"), testData.get("NotificationSystem"), 
					testData.get("OnlineChat"),testData.get("Pager"),testData.get("PASystem"),testData.get("Phone"),testData.get("Radio"),testData.get("Television"),testData.get("TwoWayRadio"),testData.get("NewMedium"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Channels - Sends Panel");
 			
			// Click on 'Add' in receives panel
			stepNo++;
			description="Add Info Receives";
			planPage.clickAddInfoReceivesPanel();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add Info Receives");
 			
			// Click on 'Show advanced form' in receives panel
			stepNo++;
			description="Advance Form - Receives Panel";
			planPage.clickShowAdvancedFormInReceivesPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Advance Form - Receives Panel");
 					
			// Click on 'Channels' dropdown in receives panel
			stepNo++;
			description="Channels - Receives Panel";
			planPage.clickChannelsDropdownInReceivesPanel(testData.get("Cell"), testData.get("ConferenceCall"), testData.get("Courier"), testData.get("Email"),
					testData.get("FaceToFace"), testData.get("Fax"), testData.get("IM"),testData.get("Landline"), testData.get("Mail"), testData.get("Meeting"), testData.get("NotificationSystem"), 
					testData.get("OnlineChat"),testData.get("Pager"),testData.get("PASystem"),testData.get("Phone"),testData.get("Radio"),testData.get("Television"),testData.get("TwoWayRadio"),testData.get("NewMedium"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Channels - Receives Panel");
 			
			// Remove This segment
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
			Log4J.getlogger(this.getClass()).info(testCaseId +ue.getErrorMessage());
 			
			// Sign out from home page
		    stepNo++;
		    description="Logout successful";
			HeaderController headerController=new HeaderController();
			headerController.signOut();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser Quit");
 			
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
     * Loads Test Data for MAV0206_AddInfoReceiveSendChannels.
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
			File MAV0206_AddInfoReceiveSendChannels=new File(path + "MAV0206_AddInfoReceiveSendChannels.xml");
			
			Document docMAV0206_AddInfoReceiveSendChannels=db.parse(MAV0206_AddInfoReceiveSendChannels);
			Element eleMAV0206_AddInfoReceiveSendChannels=docMAV0206_AddInfoReceiveSendChannels.getDocumentElement();
	              
	        Element oXmlEleMAV0206_AddInfoReceiveSendChannels = (Element) eleMAV0206_AddInfoReceiveSendChannels;
	        
	        this.testData.put("ChannelsURL",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowInTask",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("showInTask").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Details",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("details").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowSimpleFormText",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Unspecified",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("unspecified").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Actions",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AddNewSegment",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("Cell",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("cell").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ConferenceCall",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("conferenceCall").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Courier",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("courier").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Email",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("email").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("FaceToFace",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("faceToFace").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Fax",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("fax").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("IM",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("IM").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Landline",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("landline").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Mail",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("mail").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Meeting",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("meeting").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("NotificationSystem",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("notificationSystem").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("OnlineChat",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("onlineChat").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Pager",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("pager").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("PASystem",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("PASystem").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Phone",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("phone").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Radio",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("radio").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Television",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("television").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TwoWayRadio",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("twoWayRadio").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("NewMedium",oXmlEleMAV0206_AddInfoReceiveSendChannels.getElementsByTagName("newMedium").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0206_AddInfoReceiveSendChannels not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0206_AddInfoReceiveSendChannels.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0206_AddInfoReceiveSendChannels can not be parsed.");
		}
	}
}