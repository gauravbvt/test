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


public class MAC0026_UndoDuplicateFlow extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAC0026_UndoDuplicateFlow";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="FAIL";
	public String blank=""; 
	public String scriptException;
	public String browser="";
	
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
			DataController dataController= new DataController();
			dataController.createResultFiles();
			
			// Loads Test Data
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
	public void testMAC0026_UndoDuplicateFlow() throws UIAutomationException, IOException {
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
			
			// Click Actions pop up menu and Add New Segment
 			stepNo++;
			description="Add New Segment";
			PlanPage planPage=new PlanPage();
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForUndoDuplicateFlow"));
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
 			
			// click on 'strench up forms' icon
			planPage.clickStrenchUpForm();
			// Click on 'Add' button under 'Receives' panel
			stepNo++;
			description="Add Info Receive";
			planPage.clickAddInfoReceivesPanel();
			// Enter Information Name
			planPage.enterInformationNameInReceivesPanel(testData.get("InformationInRecievesPanel"));
			// Select 'Other..' option form 'From Task:' dropdown list
			planPage.selectFrom(testData.get("OtherTaskName"));
			// Enter From Task name
			planPage.enterFromTaskNameInReceives(testData.get("FromTaskNameInRecevesPanel"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Click on 'Duplicate flow' under 'Actions' pop up menu
			stepNo++;
			description="Duplicate Flow";
			planPage.clickPopupMenu(testData.get("ActionsInReceivesPanel"));
			planPage.clickSubmenu(testData.get("DuplicateFlowInReceivesPanel"));	
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Click on 'Undo duplicate flow" in actions pop up menu
			stepNo++;
			description="Undo Duplicate Flow";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoDuplicateFlow"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			//Click on Remove this segment
 			stepNo++;
			description="Remove This Segment";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 								
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
     * Loads Test Data for MAC0026_UndoDuplicateFlow.
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
			File MAC0026_UndoDuplicateFlow=new File(path + "MAC0026_UndoDuplicateFlow.xml");
			
			Document docMAC0026_UndoDuplicateFlow=db.parse(MAC0026_UndoDuplicateFlow);
			Element eleMAC0026_UndoDuplicateFlow=docMAC0026_UndoDuplicateFlow.getDocumentElement();
	              
	        Element oXmlEleMAC0026_UndoDuplicateFlow = (Element) eleMAC0026_UndoDuplicateFlow;
	        
	               	
	        this.testData.put("ChannelsURL",oXmlEleMAC0026_UndoDuplicateFlow.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0026_UndoDuplicateFlow.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("InformationInRecievesPanel",oXmlEleMAC0026_UndoDuplicateFlow.getElementsByTagName("informationInRecievesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("FromTaskNameInRecevesPanel",oXmlEleMAC0026_UndoDuplicateFlow.getElementsByTagName("fromTaskNameInRecevesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("OtherTaskName",oXmlEleMAC0026_UndoDuplicateFlow.getElementsByTagName("otherTaskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInReceivesPanel",oXmlEleMAC0026_UndoDuplicateFlow.getElementsByTagName("actionsInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("DuplicateFlowInReceivesPanel",oXmlEleMAC0026_UndoDuplicateFlow.getElementsByTagName("duplicateFlowInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
		 	
			this.testData.put("Actions",oXmlEleMAC0026_UndoDuplicateFlow.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AddNewSegment",oXmlEleMAC0026_UndoDuplicateFlow.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAC0026_UndoDuplicateFlow.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForUndoDuplicateFlow",oXmlEleMAC0026_UndoDuplicateFlow.getElementsByTagName("segmentForUndoDuplicateFlow").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoDuplicateFlow",oXmlEleMAC0026_UndoDuplicateFlow.getElementsByTagName("undoDuplicateFlow").item(0).getChildNodes().item(0).getNodeValue());
		 	
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0026_UndoDuplicateFlow not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0026_UndoDuplicateFlow.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0026_UndoDuplicateFlow can not be parsed.");
		}
	}
	
}