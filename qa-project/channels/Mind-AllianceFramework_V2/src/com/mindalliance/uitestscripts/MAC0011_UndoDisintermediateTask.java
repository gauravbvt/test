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
 * Testcase ID: MAC0011_UndoDisintermediateTask
 * 	   Summary: Verify that user is able to undo the disIntermediate task
 * @author AFour
 * 
 */

public class MAC0011_UndoDisintermediateTask extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAC0011_UndoDisintermediateTask";
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
	public void testMAC0011_UndoDisintermediateTask() throws UIAutomationException, IOException {
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
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser initialized");
		
			// Click Actions pop up menu and Add New Segment
 			stepNo++;
			description="Add New Segment";
			PlanPage planPage=new PlanPage();
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForUndoDisintermediateTask"));
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
			
			// Click on default task 
			stepNo++;
			description="Default Task";
			planPage.clickPopupMenu(testData.get("ShowInTaskPanel"));
			planPage.clickSubmenu(testData.get("DetailsInTaskPanel"));
			planPage.enterTaskName(testData.get("TaskName"));
			// click on 'stretch up forms' icon
			planPage.clickStrenchUpForm();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Default Task");
			
			// Click on 'Add' button under 'Sends' panel
			stepNo++;
			description="Add Info Sends";
			planPage.clickAddInfoSendsPanel();
			// Enter Information Name
			planPage.enterInformationNameInSendsPanel(testData.get("InformationInSendsPanel"));
			// Select 'Other..' option form 'From Task:' dropdown list
			planPage.selectFromInSends(testData.get("OtherTaskName"));
			// Enter From Task name
			planPage.enterFromTaskName(testData.get("FromTaskNameInSendsPanel"));
			// Click on Stretch up form
			planPage.clickStrenchUpForm();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add Info Sends");
			
			// Click on 'Intermediate' under 'Actions' pop up menu in sends panel
			stepNo++;
			description="Intermediate Task";
			planPage.clickPopupMenu(testData.get("ActionsInSendsPanel"));
			planPage.clickSubmenu(testData.get("Intermediate"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Intermediate Task");
			
			// Verify intermediate task should gets added to the segment
			// Click on 'About Plan segment' under 'Show' pop up menu
			stepNo++;
			description="Task Mover";
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
			// Open Task Mover
			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
			// Verify Task is added
			planPage.verifyTaskNameInTaskMover(testData.get("IntermediateTaskName"));
			// Close Task Mover window
			planPage.closeTaskMoverWindow();
			// Close Segment window
			planPage.closeAboutPlanSegmentWindow();
			// Click on intermediate Task
			planPage.clickOnIntermediateTask(testData.get("IntermediateTaskName"));
			// Click on Stretch up form
			planPage.clickStrenchUpForm();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Task Mover");
			
			// Click on 'details' under' show' pop up menu in task panel
			stepNo++;
			description="Disintermediate Task";
			planPage.clickPopupMenu(testData.get("ShowInTaskPanel"));
			planPage.clickSubmenu(testData.get("DetailsInTaskPanel"));
			// Click on 'Disintermediate' under 'Actions' pop up of 'Task' panel
			planPage.clickPopupMenu(testData.get("ActionsInTaskPanel"));
			planPage.clickSubmenu(testData.get("Disintermediate"));
			// Verify task is disintermediated
			planPage.verifyDisintermediate(testData.get("UndoDisIntermediate"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Disintermediate Task");
			
			// Click on 'Undo Disintermediate' under 'Actions' pop up menu
			stepNo++;
			description="Undo Disintermediate";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoDisIntermediate"));					
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Undo Disintermediate");
			
			// Verify added disintermediate task should be undone
			stepNo++;
			description="Redo Disintermediate";
			planPage.verifyUndoDisintermediate(testData.get("RedoDisIntermediate"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Redo Disintermediate");
			
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
     * Loads Test Data for MAC0025_UndoRemoveFlow.
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
			File MAC0011_UndoDisintermediateTask=new File(path + "MAC0011_UndoDisintermediateTask.xml");
			
			Document docMAC0011_UndoDisintermediateTask=db.parse(MAC0011_UndoDisintermediateTask);
			Element eleMAC0011_UndoDisintermediateTask=docMAC0011_UndoDisintermediateTask.getDocumentElement();
	              
	        Element oXmlEleMAC0011_UndoDisintermediateTask = (Element) eleMAC0011_UndoDisintermediateTask;
	        
	               	
	        this.testData.put("ChannelsURL",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("InformationInSendsPanel",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("informationInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("FromTaskNameInSendsPanel",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("fromTaskNameInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("OtherTaskName",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("otherTaskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Intermediate",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("intermediate").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Disintermediate",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("disintermediate").item(0).getChildNodes().item(0).getNodeValue());
		 	
			this.testData.put("Actions",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInSendsPanel",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("actionsInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AddNewSegment",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForUndoDisintermediateTask",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("segmentForUndoDisintermediateTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoDisIntermediate",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("undoDisIntermediate").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RedoDisIntermediate",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("redoDisIntermediate").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("ActionsInSegment",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("actionsInSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("MoveTasksInSegment",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("moveTasksInSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Show",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AboutPlanSegment",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("IntermediateTaskName",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("intermediateTaskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TaskName",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("taskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ShowInTaskPanel",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("showInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("DetailsInTaskPanel",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("detailsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInTaskPanel",oXmlEleMAC0011_UndoDisintermediateTask.getElementsByTagName("actionsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0011_UndoDisintermediateTask not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0011_UndoDisintermediateTask.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0011_UndoDisintermediateTask can not be parsed.");
		}
	}
}