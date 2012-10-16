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
 * Testcase ID: MAC0035_RedoAddNewTask
 * Summary: Verify that user is able to redo the added task
 * @author AFour
 */

public class MAC0035_RedoAddNewTask extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="MAC0035_RedoAddNewTask";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="FAIL";
	public String blank=""; 
	public String scriptException;
	
	public MAC0035_RedoAddNewTask() throws UIAutomationException{
		setUp();
		testMAC0035_RedoAddNewTask();
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
	public void testMAC0035_RedoAddNewTask() throws UIAutomationException {
		try{
		    // Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
			    
			// Login page
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
			
			// Plan Page
			HomePage homePage=new HomePage();
			homePage.clickCollaborationPlanLink();	
			
			// Close Plan Map window
			PlanPage planPage=new PlanPage();
			planPage.closePlanMap();
						
			// Click on 'Add New Segment' under 'Actions' pop up menu
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
						
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("segmentForRedoAddTask"));
			
			// Close Segment window
			planPage.closeSegmentWindow();
				
			// Add New Task
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewTask"));
			
			//Enter Task Name
			planPage.enterTaskName(testData.get("TaskName"));
			
			// Click on 'About Plan segment' under 'Show' pop up menu
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
			
			// Open Task Mover
			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
			
			// Verify Task is added
			planPage.verifyTaskNameInTaskMover(testData.get("TaskName"));
			
			// Close Task Mover window
			planPage.closeTaskMoverWindow();
			
			// Close Segment window
			planPage.closeSegmentWindow();
						
			// Undo Update Task
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoUpdateTask"));
				
			// Undo Add new Task
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoAddTask"));
							
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoAddTask"));
			
			// Click on 'About Plan segment' under 'Show' pop up menu
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
			
			// Open Task Mover
			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
			
			// Verify Task is removed
			planPage.verifyTaskIsRemoved(testData.get("TaskName"));
			
			// Close Task Mover window
			planPage.closeTaskMoverWindow();
			
			// Close Segment window
			planPage.closeSegmentWindow();			
			
			// Redo Add New Task
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RedoAddTask"));
			
			// Click on 'About Plan segment' under 'Show' pop up menu
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
			
			// Open Task Mover
			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
			
			// Verify Task is added
			planPage.verifyTaskNameInTaskMover(testData.get("Task2"));
			
			// Close Task Mover window
			planPage.closeTaskMoverWindow();
			
			// Close Segment window
			planPage.closeSegmentWindow();
			
			//Click on Remove this segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
								
			//Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
		}
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAC0035_RedoAddNewTask");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAC0035_RedoAddNewTask.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAC0035_RedoAddNewTask=new File(path + "MAC0035_RedoAddNewTask.xml");
			
			Document docMAC0035_RedoAddNewTask=db.parse(MAC0035_RedoAddNewTask);
			Element eleMAC0035_RedoAddNewTask=docMAC0035_RedoAddNewTask.getDocumentElement();
	              
	        Element oXmlEleMAC0035_RedoAddNewTask = (Element) eleMAC0035_RedoAddNewTask;
	        
	               	
	        this.testData.put("Actions",oXmlEleMAC0035_RedoAddNewTask.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAC0035_RedoAddNewTask.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("segmentForRedoAddTask",oXmlEleMAC0035_RedoAddNewTask.getElementsByTagName("segmentForRedoAddTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AddNewTask",oXmlEleMAC0035_RedoAddNewTask.getElementsByTagName("addNewTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TaskName",oXmlEleMAC0035_RedoAddNewTask.getElementsByTagName("taskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAC0035_RedoAddNewTask.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoUpdateTask",oXmlEleMAC0035_RedoAddNewTask.getElementsByTagName("undoUpdateTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAC0035_RedoAddNewTask.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0035_RedoAddNewTask.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	
			this.testData.put("ActionsInSegment",oXmlEleMAC0035_RedoAddNewTask.getElementsByTagName("actionsInSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("MoveTasksInSegment",oXmlEleMAC0035_RedoAddNewTask.getElementsByTagName("moveTasksInSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	

			this.testData.put("Show",oXmlEleMAC0035_RedoAddNewTask.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AboutPlanSegment",oXmlEleMAC0035_RedoAddNewTask.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	
		 	this.testData.put("UndoAddTask",oXmlEleMAC0035_RedoAddNewTask.getElementsByTagName("undoAddTask").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("RedoAddTask",oXmlEleMAC0035_RedoAddNewTask.getElementsByTagName("redoAddTask").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Task2",oXmlEleMAC0035_RedoAddNewTask.getElementsByTagName("task2").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0035_RedoAddNewTask not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0035_RedoAddNewTask.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0035_RedoAddNewTask can not be parsed.");
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
}