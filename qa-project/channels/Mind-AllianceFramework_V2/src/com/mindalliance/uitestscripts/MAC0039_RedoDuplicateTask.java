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
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;
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
	
	@Before
	protected void setUp(){
		try{
			if (GlobalVariables.configuration == null){
					GlobalVariables.configuration = Configuration.getConfigurationObject();
				
			}
			if(GlobalVariables.configuration.getAttrSearchList() == null){
				new ElementController();
			}
			
			// Creates Browser instance
			BrowserController browserController= new BrowserController();
			browserController.initializeDriver();
			
			// Loads Test Data
			loadTestData();
		}
		catch(UIAutomationException ue){
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
		}
	}
	
	@Test
	public void testMAC0039_RedoDuplicateTask() throws UIAutomationException {
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
			planPage.enterSegmentName(testData.get("SegmentForRedoDuplicateTask"));
			
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
						
			// Click on Actions popup in Task Panel and also click on 'Duplicate Task'
			planPage.clickPopupMenu(testData.get("ActionsInTaskPanel"));
			planPage.clickSubmenu(testData.get("DuplicateTask"));
			
			
			// Verify task gets duplicated			
			// Click on 'About Plan segment' under 'Show' pop up menu
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
			
			// Open Task Mover
			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
			planPage.verifyTaskIsDuplicated(testData.get("TaskName"));
			
			// Close Task Mover window
			planPage.closeTaskMoverWindow();
			
			// Close Segment window
			planPage.closeSegmentWindow();
					
			// Undo Update Task
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoDuplicateTask"));			
				
			// Verify duplicated task should be undone			
			// Click on 'About Plan segment' under 'Show' pop up menu
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
			
			// Open Task Mover
			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
			planPage.verifyTaskIsNotDuplicated(testData.get("TaskName"));
			
			// Close Task Mover window
			planPage.closeTaskMoverWindow();
			
			// Close Segment window
			planPage.closeSegmentWindow();
			
			
			// Click on 'Redo Add New Task' under actions pop up menu
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RedoAddNewTask"));
			
			// Click on 'About Plan segment' under 'Show' pop up menu
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
			
			// Open Task Mover
			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
			planPage.verifyTaskIsDuplicated(testData.get("TaskName"));
			
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
			Reporting.getScreenShot("MAC0039_RedoDuplicateTask");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAC0039_RedoDuplicateTask.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
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