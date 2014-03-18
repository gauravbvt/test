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
 * Testcase ID: MAC0038_RedoPasteTaskUsingCopy
 * 	   Summary: Verify that user is able to redo the task which was pasted using copy command
 * @author AFour
 * 
 */

public class MAC0038_RedoPasteTaskUsingCopy extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="MAC0038_RedoPasteTaskUsingCopy";
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
	public void testMAC0038_RedoPasteTaskUsingCopy() throws UIAutomationException, IOException{
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
			
			// Click on Actions pop up and Add New Segment
 			stepNo++;
			description="Add New Segment";
			PlanPage planPage=new PlanPage();
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));					
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForRedoPasteTaskUsingCopy"));
			 // Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add New Segment");
			
			// Close Segment window
 			stepNo++;
			description="Close About Plan Segment Window";
			planPage.closeSegmentWindow();
			 // Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment Window");
			
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
			description="Update Task Name";
			planPage.enterTaskName(testData.get("TaskName"));
			 // Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Update Task Name");
			
//			// Click on 'About Plan segment' under 'Show' pop up menu
// 			stepNo++;
//			description="About Plan Segment";
//			planPage.clickPopupMenu(testData.get("Show"));
//			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
//			 // Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"About Plan Segment");
//			
//			// Open Task Mover
// 			stepNo++;
//			description="Task Mover";
//			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
//			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
//			 // Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Mover");
//			
//			// Verify Task is added
// 			stepNo++;
//			description="Task Added Successfully";
//			planPage.verifyTaskNameInTaskMover(testData.get("TaskName"));
//			 // Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Added Successfully");
//			
//			// Close Task Mover window
// 			stepNo++;
//			description="Close Task Mover";
//			planPage.closeTaskMoverWindow();
//			 // Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close Task Mover");
//			
//			// Close Segment window
// 			stepNo++;
//			description="Close About Plan Segment Window";
//			planPage.closeSegmentWindow();
//			 // Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment Window");
			
			// Click on Actions popup in Task Panel and also click on 'Copy Task'
 			stepNo++;
			description="Copy Task";
			planPage.clickPopupMenu(testData.get("ActionsInTaskPanel"));
			planPage.clickSubmenu(testData.get("CopyTaskInActionsInTaskPanel"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Copy Task");
			
			// Add New Segment, Click on Actions pop up and Add New Segment
			stepNo++;
			description="Add New Segment";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("OtherSegmentForRedoPasteTask"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add New Segment");
			
			// Close Segment window
			stepNo++;
			description="Close Segment Window";
			planPage.closeSegmentWindow();
			// Click Stretch up forms
			planPage.clickStrenchUpForm();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close Plan Map Window");
			
			// Click on Actions Menu and click on 'Paste Task'
			stepNo++;
			description="Paste Task";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("PasteTask"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Paste Task");
			
//			// Click on 'About Plan segment' under 'Show' pop up menu
//			stepNo++;
//			description="About Plan Segment";
//			planPage.clickPopupMenu(testData.get("Show"));
//			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"About Plan Segment");
//			
//			// Open Task Mover
//			stepNo++;
//			description="Task Mover";
//			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
//			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Mover");
//			
//			// Verify Task is added
//			stepNo++;
//			description="Task Added Successfully";
//			planPage.verifyTaskNameInTaskMover(testData.get("TaskName"));
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Added Successfully");
//			
//			// Close Task Mover window
//			stepNo++;
//			description="Close Task Mover";
//			planPage.closeTaskMoverWindow();
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close Task Mover");
//			
//			// Close Segment window
//			stepNo++;
//			description="Close About Plan Segment Window";
//			planPage.closeSegmentWindow();
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment Window");
 			
			// Click on Actions Menu and click on 'Undo Paste Task'
			stepNo++;
			description="Undo Paste Task";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoPasteTask"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Undo Paste Task");
			
//			// Click on 'About Plan segment' under 'Show' pop up menu
//			stepNo++;
//			description="About Plan Segment";
//			planPage.clickPopupMenu(testData.get("Show"));
//			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"About Plan Segment");
//			
//			// Open Task Mover
//			stepNo++;
//			description="Task Mover";
//			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
//			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Mover");
//			
//			// Verify Task is removed
//			stepNo++;
//			description="Task Removed Successfully";
//			planPage.verifyTaskIsRemoved(testData.get("TaskName"));
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Removed Successfully");
//			
//			// Close Task Mover window
//			stepNo++;
//			description="Close Task Mover";
//			planPage.closeTaskMoverWindow();
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close Task Mover");
//			
//			// Close Segment window
//			stepNo++;
//			description="Close About Plan Segment Window";
//			planPage.closeSegmentWindow();			
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment Window");
				
			// Click on Actions Menu and click on 'Redo Paste Task'
			stepNo++;
			description="Redo Paste Task";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RedoPasteTask"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Redo Paste Task");
			
//			// Click on 'About Plan segment' under 'Show' pop up menu
//			stepNo++;
//			description="About Plan Segment";
//			planPage.clickPopupMenu(testData.get("Show"));
//			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"About Plan Segment");
//			
//			// Open Task Mover
//			stepNo++;
//			description="Task Mover";
//			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
//			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Mover");
//			
//			// Verify Task is removed
//			stepNo++;
//			description="Task Redo Successfully";
//			planPage.verifyTaskNameInTaskMover(testData.get("TaskName"));
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Redo Successfully");
//			
//			// Close Task Mover window
//			stepNo++;
//			description="Close Task Mover";
//			planPage.closeTaskMoverWindow();
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close Task Mover");
//			
//			// Close Segment window
//			stepNo++;
//			description="Close About Plan Segment";
//			planPage.closeSegmentWindow();			
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment");
			
			// Click on Remove this segment
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
     * Loads Test Data for MAC0038_RedoPasteTaskUsingCopy.
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
			File MAC0038_RedoPasteTaskUsingCopy=new File(path + "MAC0038_RedoPasteTaskUsingCopy.xml");
			
			Document docMAC0038_RedoPasteTaskUsingCopy=db.parse(MAC0038_RedoPasteTaskUsingCopy);
			Element eleMAC0038_RedoPasteTaskUsingCopy=docMAC0038_RedoPasteTaskUsingCopy.getDocumentElement();
	              
	        Element oXmlEleMAC0038_RedoPasteTaskUsingCopy = (Element) eleMAC0038_RedoPasteTaskUsingCopy;
	        
	               	
	        this.testData.put("Actions",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForRedoPasteTaskUsingCopy",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("segmentForRedoPasteTaskUsingCopy").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AddNewTask",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("addNewTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TaskName",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("taskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoPasteTask",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("undoPasteTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInTaskPanel",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("actionsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("CopyTaskInActionsInTaskPanel",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("copyTaskInActionsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("OtherSegmentForRedoPasteTask",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("otherSegmentForRedoPasteTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("PasteTask",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("pasteTask").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("ActionsInSegment",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("actionsInSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("MoveTasksInSegment",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("moveTasksInSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Show",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AboutPlanSegment",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("RedoPasteTask",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("redoPasteTask").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0038_RedoPasteTaskUsingCopy not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0038_RedoPasteTaskUsingCopy.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0038_RedoPasteTaskUsingCopy can not be parsed.");
		}
	}
}