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
 * Testcase ID: MAC0039_RedoDuplicateTask
 * 	   Summary: Verify that user is able to redo the task which was duplicated
 * @author AFour
 * 
 */

public class MAC0039_RedoDuplicateTask extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="MAC0039_RedoDuplicateTask";
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
			Log4J.getlogger(this.getClass()).error(testCaseId +ue.getErrorMessage());
			
		}
	}
	
	@Test
	public void testMAC0039_RedoDuplicateTask() throws UIAutomationException, IOException {
		try{
			stepNo++;
			description="URL Entered";
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser initialized");
			
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
			description="Add new Segment";
			PlanPage planPage=new PlanPage();
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));		
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForRedoDuplicateTask"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Paste Task");
			
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
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add New Task");
			
			// Enter Task Name
 			stepNo++;
			description="Update Task";
			planPage.enterTaskName(testData.get("TaskName"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Update Task");
			
//			// Click on 'About Plan segment' under 'Show' pop up menu
// 			stepNo++;
//			description="About Plan Segment";
//			planPage.clickPopupMenu(testData.get("Show"));
//			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"About Plan Segment");
//			
//			// Open Task Mover
// 			stepNo++;
//			description="Task Mover";
//			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
//			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Mover");
//			
//			// Verify Task is added
// 			stepNo++;
//			description="Task Added Successfully";
//			planPage.verifyTaskNameInTaskMover(testData.get("TaskName"));
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Added Successfully");
//			
//			// Close Task Mover window
// 			stepNo++;
//			description="Close Task Mover";
//			planPage.closeTaskMoverWindow();
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close Task Mover");
//			
//			// Close Segment window
// 			stepNo++;
//			description="Close About Plan Segment Window";
//			planPage.closeSegmentWindow();
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment Window");
			
			// Click on Actions popup in Task Panel and also click on 'Duplicate Task'
 			stepNo++;
			description="Duplicate Task";
			planPage.clickPopupMenu(testData.get("ActionsInTaskPanel"));
			planPage.clickSubmenu(testData.get("DuplicateTask"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Duplicate Task");
			
//			// Verify task gets duplicated			
//			// Click on 'About Plan segment' under 'Show' pop up menu
// 			stepNo++;
//			description="Task Duplicated";
//			planPage.clickPopupMenu(testData.get("Show"));
//			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
//			// Open Task Mover
//			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
//			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
//			planPage.verifyTaskIsDuplicated(testData.get("TaskName"));
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Duplicated");
//			
//			// Close Task Mover window
// 			stepNo++;
//			description="Close Task Mover Window";
//			planPage.closeTaskMoverWindow();
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close Task Mover Window");
//			
//			// Close Segment window
// 			stepNo++;
//			description="Close About Plan Segment";
//			planPage.closeSegmentWindow();
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close Task Mover Window");
			
			// Undo Update Task
 			stepNo++;
			description="Undo Duplicate Task";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoDuplicateTask"));			
			// Verify duplicated task should be undone			
			// Click on 'About Plan segment' under 'Show' pop up menu
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Undo Duplicate Task");
			
//			// Open Task Mover
// 			stepNo++;
//			description="Task Mover";
//			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
//			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
//			planPage.verifyTaskIsNotDuplicated(testData.get("TaskName"));
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Mover");
//			
//			// Close Task Mover window
// 			stepNo++;
//			description="Close Task Mover Window";
//			planPage.closeTaskMoverWindow();
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser initialized");
//			
//			// Close Segment window
// 			stepNo++;
//			description="Close About Plan Segment";
//			planPage.closeSegmentWindow();
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment");
			
			// Click on 'Redo Add New Task' under actions pop up menu
 			stepNo++;
			description="Redo Add New Task";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RedoAddNewTask"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Redo Add New Task");
			
//			// Click on 'About Plan segment' under 'Show' pop up menu
// 			stepNo++;
//			description="About Plan Segment";
//			planPage.clickPopupMenu(testData.get("Show"));
//			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"About Plan Segment");
//			
//			// Open Task Mover
// 			stepNo++;
//			description="Task Mover";
//			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
//			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
//			planPage.verifyTaskIsDuplicated(testData.get("TaskName"));
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Mover");
//			
//			// Close Task Mover window
// 			stepNo++;
//			description="Close Task Mover";
//			planPage.closeTaskMoverWindow();
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close Task Mover");
//			
//			// Close Segment window
// 			stepNo++;
//			description="Close About Plan Segment";
//			planPage.closeSegmentWindow();			
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment");
					
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
			Log4J.getlogger(this.getClass()).info(testCaseId +ue.getErrorMessage());
			
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
     * Loads Test Data for MAC0039_RedoDuplicateTask.
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
			File MAC0039_RedoDuplicateTask=new File(path + "MAC0039_RedoDuplicateTask.xml");
			
			Document docMAC0039_RedoDuplicateTask=db.parse(MAC0039_RedoDuplicateTask);
			Element eleMAC0039_RedoDuplicateTask=docMAC0039_RedoDuplicateTask.getDocumentElement();
	              
	        Element oXmlEleMAC0039_RedoDuplicateTask = (Element) eleMAC0039_RedoDuplicateTask;
	        
	               	
	        this.testData.put("Actions",oXmlEleMAC0039_RedoDuplicateTask.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAC0039_RedoDuplicateTask.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForRedoDuplicateTask",oXmlEleMAC0039_RedoDuplicateTask.getElementsByTagName("segmentForRedoDuplicateTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AddNewTask",oXmlEleMAC0039_RedoDuplicateTask.getElementsByTagName("addNewTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TaskName",oXmlEleMAC0039_RedoDuplicateTask.getElementsByTagName("taskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAC0039_RedoDuplicateTask.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoDuplicateTask",oXmlEleMAC0039_RedoDuplicateTask.getElementsByTagName("undoDuplicateTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAC0039_RedoDuplicateTask.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0039_RedoDuplicateTask.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ActionsInSegment",oXmlEleMAC0039_RedoDuplicateTask.getElementsByTagName("actionsInSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("MoveTasksInSegment",oXmlEleMAC0039_RedoDuplicateTask.getElementsByTagName("moveTasksInSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("DuplicateTask",oXmlEleMAC0039_RedoDuplicateTask.getElementsByTagName("duplicateTask").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Show",oXmlEleMAC0039_RedoDuplicateTask.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AboutPlanSegment",oXmlEleMAC0039_RedoDuplicateTask.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ActionsInTaskPanel",oXmlEleMAC0039_RedoDuplicateTask.getElementsByTagName("actionsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RedoAddNewTask",oXmlEleMAC0039_RedoDuplicateTask.getElementsByTagName("redoAddNewTask").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0039_RedoDuplicateTask not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0039_RedoDuplicateTask.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0039_RedoDuplicateTask can not be parsed.");
		}
	}
}