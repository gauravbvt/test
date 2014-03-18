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
 * Testcase ID: MAC0036_RedoCutTask
 * 	   Summary: Verify that user is able to redo the task which was cut
 * @author AFour
 * 
 */

public class MAC0036_RedoCutTask extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="MAC0036_RedoCutTask";
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
	public void testMAC0036_RedoCutTask() throws UIAutomationException, IOException{
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
				
			// Click on Actions pop up and Add New Segment
 			stepNo++;
			description="Add New Segment";
			PlanPage planPage =new PlanPage();
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForRedoCutTask"));
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
			// stretch up forms
			planPage.clickStrenchUpForm();
			// Enter Task Name
			planPage.enterTaskName(testData.get("TaskName"));
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Add New Task");
				
			// Click on 'About Plan segment' under 'Show' pop up menu
			stepNo++;
			description="Task Mover";
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
			// Open Task Mover
			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Mover");
				
			// Verify Task is added
			stepNo++;
			description="Task Added Successfully";
			planPage.verifyTaskNameInTaskMover(testData.get("TaskName"));
			// Close Task Mover window
			planPage.closeTaskMoverWindow();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Added Successfully");
			
			// Close Segment window
			stepNo++;
			description="Close About Plan Segment";
			planPage.closeSegmentWindow();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment");
						
			// Click on Actions popup in Task Panel and also click on 'Cut Task'
			stepNo++;
			description="Cut Task";
			planPage.clickPopupMenu(testData.get("ActionsInTaskPanel"));
			planPage.clickSubmenu(testData.get("CutTaskInActionsInTaskPanel"));
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Cut Task");
				
			// Click on 'About Plan segment' under 'Show' pop up menu
			stepNo++;
			description="Task Mover";
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			// Open Task Mover
			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Mover");
			
			// Verify Task is removed
			stepNo++;
			description="Task Removed Successfully";
			planPage.verifyTaskIsRemoved(testData.get("TaskName"));
			// Close Task Mover window
			planPage.closeTaskMoverWindow();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Removed Successfully");
			
			// Close Segment window
			stepNo++;
			description="Close About Plan Segment";
			planPage.closeSegmentWindow();	
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment");
			
			// Undo Cut Task
			stepNo++;
			description="Undo Cut Task";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoCutTask"));
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Undo Cut Task");
			
			// Click on 'About Plan segment' under 'Show' pop up menu
			stepNo++;
			description="Task Mover";
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
			// Open Task Mover
			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Mover");
			
			// Verify Task is added
			stepNo++;
			description="Task Added Successfully";
			planPage.verifyTaskNameInTaskMover(testData.get("TaskName"));
			// Close Task Mover window
			planPage.closeTaskMoverWindow();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Added Successfully");
			
			// Close Segment window
			stepNo++;
			description="Close About Plan Segment";
			planPage.closeSegmentWindow();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment");
			
			// Redo Cut Task
			stepNo++;
			description="Redo Cut Task";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RedoCutTask"));
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Redo Cut Task");
				
			// Click on 'About Plan segment' under 'Show' pop up menu
			stepNo++;
			description="Task Mover";
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
			// Open Task Mover
			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Mover");
				
			// Verify Task is removed
			stepNo++;
			description="Task Removed Successfully";
			planPage.verifyTaskIsRemoved(testData.get("TaskName"));
			// Close Task Mover window
			planPage.closeTaskMoverWindow();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Removed Successfully");
			
			// Close Segment window
			stepNo++;
			description="Close About Plan Segment";
			planPage.closeSegmentWindow();	
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment");
			
			// Click on Remove this segment
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
			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser Quit");
			
			Reporting reporting= new Reporting();
		    reporting.generateAutomationReport();
		    
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());		
		}
	}
	
	/**
     * Loads Test Data for MAC0036_RedoCutTask.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException
	{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + File.separator+"TestData"+File.separator;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAC0036_RedoCutTask=new File(path + "MAC0036_RedoCutTask.xml");
			
			Document docMAC0036_RedoCutTask=db.parse(MAC0036_RedoCutTask);
			Element eleMAC0036_RedoCutTask=docMAC0036_RedoCutTask.getDocumentElement();
	              
	        Element oXmlEleMAC0036_RedoCutTask = (Element) eleMAC0036_RedoCutTask;
	        
	               	
	        this.testData.put("Actions",oXmlEleMAC0036_RedoCutTask.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAC0036_RedoCutTask.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForRedoCutTask",oXmlEleMAC0036_RedoCutTask.getElementsByTagName("segmentForRedoCutTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AddNewTask",oXmlEleMAC0036_RedoCutTask.getElementsByTagName("addNewTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TaskName",oXmlEleMAC0036_RedoCutTask.getElementsByTagName("taskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAC0036_RedoCutTask.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoCutTask",oXmlEleMAC0036_RedoCutTask.getElementsByTagName("undoCutTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RedoCutTask",oXmlEleMAC0036_RedoCutTask.getElementsByTagName("redoCutTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInTaskPanel",oXmlEleMAC0036_RedoCutTask.getElementsByTagName("actionsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("CutTaskInActionsInTaskPanel",oXmlEleMAC0036_RedoCutTask.getElementsByTagName("cutTaskInActionsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAC0036_RedoCutTask.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0036_RedoCutTask.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	
			this.testData.put("Show",oXmlEleMAC0036_RedoCutTask.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AboutPlanSegment",oXmlEleMAC0036_RedoCutTask.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	
			this.testData.put("ActionsInSegment",oXmlEleMAC0036_RedoCutTask.getElementsByTagName("actionsInSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("MoveTasksInSegment",oXmlEleMAC0036_RedoCutTask.getElementsByTagName("moveTasksInSegment").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0036_RedoCutTask not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0036_RedoCutTask.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0036_RedoCutTask can not be parsed.");
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