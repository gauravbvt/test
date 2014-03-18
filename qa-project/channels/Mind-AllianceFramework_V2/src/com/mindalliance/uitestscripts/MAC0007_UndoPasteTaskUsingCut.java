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
 * Testcase ID: MAC0007_UndoPasteTaskUsingCut
 * 	   Summary: Verify that user is able to undo the task which was pasted using cut command
 * @author afour 
 */

public class MAC0007_UndoPasteTaskUsingCut extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="MAC0007_UndoPasteTaskUsingCut";
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
	public void testMAC0007_UndoPasteTaskUsingCut() throws UIAutomationException, IOException {
		try{
			stepNo++;
			description="URL Entered";
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add New Segment");
 			
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
 			
			// Click Actions pop up menu and Add New Segment
 			stepNo++;
			description="Add New Segment";
			PlanPage planPage=new PlanPage();
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForUndoPasteTask"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add New Segment");
			
			// Close Segment window
			stepNo++;
			description="Close About Plan Segment";
			planPage.closeSegmentWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment");
 			
			// Add New Task
			stepNo++;
			description="Add New Task";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewTask"));
			// Enter Task Name
			planPage.enterTaskName(testData.get("TaskName"));
			// Click 'Stretch up form'
			planPage.clickStrenchUpForm();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add New Task");
 			
			// Click on Actions popup in Task Panel and also click on 'Cut Task'
			stepNo++;
			description="Remove Task";
			planPage.clickPopupMenu(testData.get("ActionsInTaskPanel"));
			planPage.clickSubmenu(testData.get("RemoveTask"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Remove Task");
 			
			// Add New Segment, Click on Actions pop up and Add New Segment
			stepNo++;
			description="Add New Segment";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("OtherSegmentForUndoPasteTask"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add New Segment");
 			
			// Close Segment window
			stepNo++;
			description="Close About Plan Segment";
			planPage.closeSegmentWindow();
			// Click Stretch up forms
			planPage.clickStrenchUpForm();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment");
 			
			// Click on Actions Menu and click on 'Paste Task'
			stepNo++;
			description="Paste Task";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("PasteTask"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Paste Task");
 			
			// Click on Actions Menu and click on 'Undo Paste Task'
			stepNo++;
			description="Undo Paste Task";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoPasteTask"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Undo Paste Task");
 			
			//Click on Remove this segment
 			stepNo++;
			description="Remove This Segment";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Remove This Segment");
 			
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
     * Loads Test Data for MAC0007_UndoPasteTaskUsingCut.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			String startTime=LogFunctions.getDateTime();
			GlobalVariables.configuration.setStartTime(startTime);
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + File.separator+"TestData"+File.separator;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAC0007_UndoPasteTaskUsingCut=new File(path + "MAC0007_UndoPasteTaskUsingCut.xml");
			
			Document docMAC0007_UndoPasteTaskUsingCut=db.parse(MAC0007_UndoPasteTaskUsingCut);
			Element eleMAC0007_UndoPasteTaskUsingCut=docMAC0007_UndoPasteTaskUsingCut.getDocumentElement();
	              
	        Element oXmlEleMAC0007_UndoPasteTaskUsingCut = (Element) eleMAC0007_UndoPasteTaskUsingCut;
	        
	               	
	        this.testData.put("Actions",oXmlEleMAC0007_UndoPasteTaskUsingCut.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAC0007_UndoPasteTaskUsingCut.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForUndoPasteTask",oXmlEleMAC0007_UndoPasteTaskUsingCut.getElementsByTagName("segmentForUndoPasteTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AddNewTask",oXmlEleMAC0007_UndoPasteTaskUsingCut.getElementsByTagName("addNewTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TaskName",oXmlEleMAC0007_UndoPasteTaskUsingCut.getElementsByTagName("taskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAC0007_UndoPasteTaskUsingCut.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoPasteTask",oXmlEleMAC0007_UndoPasteTaskUsingCut.getElementsByTagName("undoPasteTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInTaskPanel",oXmlEleMAC0007_UndoPasteTaskUsingCut.getElementsByTagName("actionsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveTask",oXmlEleMAC0007_UndoPasteTaskUsingCut.getElementsByTagName("removeTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("OtherSegmentForUndoPasteTask",oXmlEleMAC0007_UndoPasteTaskUsingCut.getElementsByTagName("otherSegmentForUndoPasteTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAC0007_UndoPasteTaskUsingCut.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0007_UndoPasteTaskUsingCut.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("PasteTask",oXmlEleMAC0007_UndoPasteTaskUsingCut.getElementsByTagName("pasteTask").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0007_UndoPasteTaskUsingCut not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0007_UndoPasteTaskUsingCut.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0007_UndoPasteTaskUsingCut can not be parsed.");
		}
	}
	
}