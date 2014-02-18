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
 * Testcase ID: MAC0013_UndoConnectFlow
 * 	   Summary: Verify that user is able to undo the tasks which were moved
 * @author AFour
 * 
 */

public class MAC0013_UndoConnectFlow extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="MAC0013_UndoConnectFlow";
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
	public void testMAC0013_UndoConnectFlow() throws UIAutomationException, IOException {
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
			planPage.enterSegmentName(testData.get("segmentForUndoConnectFlow"));
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
			//Enter Task Name
			planPage.enterTaskName(testData.get("TaskName1"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add New Task");
 			
 			// Click on Add info Receives
 			stepNo++;
			description="Add Info Receives";
			planPage.clickStrenchUpForm();
			planPage.clickAddInfoReceivesPanel();
			planPage.enterInformationNameInReceivesPanel(testData.get("undoConnectFlow"));
			planPage.selectFrom(testData.get("Other"));
			planPage.enterFromTaskNameInReceives(testData.get("taskName2"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add Info Receives");
 			
//			// Click on 'About Plan segment' under 'Show' pop up menu
//			stepNo++;
//			description="Task Mover";
//			planPage.clickPopupMenu(testData.get("Show"));
//			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
//			// Open Task Mover
//			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
//			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
//			// Verify Task is added
//			planPage.verifyTaskNameInTaskMover(testData.get("TaskName1"));
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
//			
//			// Select segment from dropdown list
//			stepNo++;
//			description="Move Task";
//			planPage.selectSegmentInTaskMover(testData.get("OtherSegment"));
//			// Select task to move
//			planPage.clickOnCheckboxOfTaskNameInTaskMover(testData.get("TaskName1"));
//			// click on 'Move' button
//			planPage.clickOnMoveInTaskMover();			
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
//			
//			// Close Task Mover window
//			stepNo++;
//			description="Close Task Mover";
//			planPage.closeTaskMoverWindow();
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
//			
//			// Close Segment window
//			stepNo++;
//			description="Close About Plan Segment";
//			planPage.closeSegmentWindow();
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
//			
//			// Verify Task is moved
//			// Select other segement from segment dropdown list
//			stepNo++;
//			description="Task Moved Successfully";
//			planPage.selectSegmentFromList(testData.get("OtherSegment"));
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
//						
//			// Undo Update Task
//			stepNo++;
//			description="Close About Plan Segment";
//			planPage.clickPopupMenu(testData.get("Actions"));
//			planPage.clickSubmenu(testData.get("UndoMoveTask"));	
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
//			
//			// Verify duplicated task should be undone			
//			// Click on 'About Plan segment' under 'Show' pop up menu
//			stepNo++;
//			description="Task Mover";
//			planPage.clickPopupMenu(testData.get("Show"));
//			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
//			// Open Task Mover
//			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
//			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
//			planPage.verifyTaskIsNotDuplicated(testData.get("TaskName"));
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
//			
//			// Close Task Mover window
//			stepNo++;
//			description="Close Task Mover";
//			planPage.closeTaskMoverWindow();
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
//			
//			// Close Segment window
//			stepNo++;
//			description="Close About Plan Segment";
//			planPage.closeSegmentWindow();
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
					
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
			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser quit");
		}
	}
	
	/**
     * Loads Test Data for MAC0013_UndoConnectFlow.
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
			File MAC0012_UndoMoveTask=new File(path + "MAC0013_UndoConnectFlow.xml");
			
			Document docMAC0012_UndoMoveTask=db.parse(MAC0012_UndoMoveTask);
			Element eleMAC0012_UndoMoveTask=docMAC0012_UndoMoveTask.getDocumentElement();
	              
	        Element oXmlEleMAC0012_UndoMoveTask = (Element) eleMAC0012_UndoMoveTask;
	        
	               	
	        this.testData.put("Actions",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("segmentForUndoConnectFlow",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("segmentForUndoConnectFlow").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("OtherSegment",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("otherSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AddNewTask",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("addNewTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TaskName1",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("taskName1").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TaskName2",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("taskName2").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ShowInTaskPanel",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("showInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("DetailsInTaskPanel",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("detailsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoMoveTask",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("undoMoveTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ActionsInSegment",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("actionsInSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("MoveTasksInSegment",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("moveTasksInSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Show",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AboutPlanSegment",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("undoConnectFlow",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("undoConnectFlow").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Other",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("other").item(0).getChildNodes().item(0).getNodeValue());
		 	
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0012_UndoMoveTask not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0012_UndoMoveTask.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0012_UndoMoveTask can not be parsed.");
		}
	}
}
