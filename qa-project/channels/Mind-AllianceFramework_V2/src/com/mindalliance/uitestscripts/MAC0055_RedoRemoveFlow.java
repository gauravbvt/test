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

/**
 * Testcase ID: MAC0055_RedoRemoveFlow
 * 	   Summary: Verify that user is able to undo the disIntermediate task
 * @author AFour
 * 
 */

public class MAC0055_RedoRemoveFlow extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="MAC0055_RedoRemoveFlow";
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
	public void testMAC0055_RedoRemoveFlow() throws UIAutomationException, IOException {
		try{
			stepNo++;
			description="URL Entered";
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL();
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
			planPage.enterSegmentName(testData.get("SegmentForRedoDisintermediateTask"));
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
			
			// Click on 'Intermediate' under 'Actions' pop up menu in sends panel
			stepNo++;
			description="Intermediate Task Successfully";
			planPage.clickPopupMenu(testData.get("ActionsInSendsPanel"));
			planPage.clickSubmenu(testData.get("Intermediate"));
			// Verify intermediate task should gets added to the segment
			// Click on 'About Plan segment' under 'Show' pop up menu
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
			// Open Task Mover
			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
			// Verify Task is added
			planPage.verifyTaskNameInTaskMover(testData.get("IntermediateTaskName"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Close Task Mover window
			stepNo++;
			description="Close Task Mover";
			planPage.closeTaskMoverWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Close Segment window
			stepNo++;
			description="Close About Plan Segment";
			planPage.closeAboutPlanSegmentWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Click on intermediate Task
			stepNo++;
			description="Intermediate Task";
			planPage.clickOnIntermediateTask(testData.get("IntermediateTaskName"));
			// Click on Strench up form
			planPage.clickStrenchUpForm();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Click on 'details' under' show' pop up menu in task panel
			stepNo++;
			description="Details of Task";
			planPage.clickPopupMenu(testData.get("ShowInTaskPanel"));
			planPage.clickSubmenu(testData.get("DetailsInTaskPanel"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Click on 'Disintermediate' under 'Actions' pop up of 'Task' panel
			stepNo++;
			description="Disintermediate Task";
			planPage.clickPopupMenu(testData.get("ActionsInTaskPanel"));
			planPage.clickSubmenu(testData.get("Disintermediate"));
			// Verify task is disintermediated
			planPage.verifyDisintermediate(testData.get("UndoDisIntermediate"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Click on 'Undo Disintermediate' under 'Actions' pop up menu
			stepNo++;
			description="Undo Disintermediate";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoDisIntermediate"));					
			// Verify added disintermediate task should be undone
			planPage.verifyUndoDisintermediate(testData.get("RedoDisIntermediate"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Click on 'Redo Disintermediate Task' under 'Actions' pop up menu
			stepNo++;
			description="Redo Disintermediate Task";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RedoDisIntermediate"));
			// Verify undone disintermediate task should be added
			planPage.verifyRedoDisintermediate(testData.get("UndoDisIntermediate"));			
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
     * Loads Test Data for MAC0055_RedoRemoveFlow.
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
			File MAC0055_RedoRemoveFlow=new File(path + "MAC0055_RedoRemoveFlow.xml");
			
			Document docMAC0055_RedoRemoveFlow=db.parse(MAC0055_RedoRemoveFlow);
			Element eleMAC0055_RedoRemoveFlow=docMAC0055_RedoRemoveFlow.getDocumentElement();
	              
	        Element oXmlEleMAC0055_RedoRemoveFlow = (Element) eleMAC0055_RedoRemoveFlow;
	        
	               	
	        this.testData.put("ChannelsURL",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("InformationInSendsPanel",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("informationInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("FromTaskNameInSendsPanel",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("fromTaskNameInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("OtherTaskName",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("otherTaskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Intermediate",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("intermediate").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Disintermediate",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("disintermediate").item(0).getChildNodes().item(0).getNodeValue());
		 	
			this.testData.put("Actions",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInSendsPanel",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("actionsInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AddNewSegment",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForRedoDisintermediateTask",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("segmentForRedoDisintermediateTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoDisIntermediate",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("undoDisIntermediate").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RedoDisIntermediate",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("redoDisIntermediate").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("ActionsInSegment",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("actionsInSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("MoveTasksInSegment",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("moveTasksInSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Show",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AboutPlanSegment",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("IntermediateTaskName",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("intermediateTaskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TaskName",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("taskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ShowInTaskPanel",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("showInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("DetailsInTaskPanel",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("detailsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInTaskPanel",oXmlEleMAC0055_RedoRemoveFlow.getElementsByTagName("actionsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0055_RedoRemoveFlow not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0055_RedoRemoveFlow.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0055_RedoRemoveFlow can not be parsed.");
		}
	}
}