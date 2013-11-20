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
 * Testcase ID: MAC0046_RedoRemoveIssue
 * 	   Summary: Verify that user is able to undo the disIntermediate task
 * @author AFour
 * 
 */

public class MAC0046_RedoRemoveIssue extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="MAC0046_RedoRemoveIssue";
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
	public void testMAC0046_RedoRemoveIssue() throws UIAutomationException, IOException {
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
			PlanPage planPage =new PlanPage();
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForRedoRemoveIssue"));
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
			
			// Click on Stretch up form
			planPage.clickStrenchUpForm();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
						
			// Click on 'New Issue' under 'Actions' pop up menu in sends panel
			stepNo++;
			description="Intermediate Task Successfully";
			planPage.clickPopupMenu(testData.get("ActionsInSendsPanel"));
			planPage.clickSubmenu(testData.get("NewIssueInSendsPanel"));
			
            //Click on 'Remove Issue' under Menu in the 'Flow Issues' panel in the Sends panel	
			stepNo++;
			description="Remove Issue";
			planPage.clickPopupMenu(testData.get("MenuInFlowIssuesInSendsPanel"));
			planPage.clickSubmenu(testData.get("RemoveIssueFlowIssuesInSendsPanel"));
			
			//Click on Undo Remove Issue from Actions menu
 			stepNo++;
			description="Undo remove Issue";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoRemoveIssue"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
 			//Click on Redo Remove Issue from Actions menu
 			stepNo++;
			description="Redo remove Issue";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RedoRemoveIssue"));
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
     * Loads Test Data for MAC0046_RedoRemoveIssue.
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
			File MAC0046_RedoRemoveIssue=new File(path + "MAC0046_RedoRemoveIssue.xml");
			
			Document docMAC0046_RedoRemoveIssue=db.parse(MAC0046_RedoRemoveIssue);
			Element eleMAC0046_RedoRemoveIssue=docMAC0046_RedoRemoveIssue.getDocumentElement();
	              
	        Element oXmlEleMAC0046_RedoRemoveIssue = (Element) eleMAC0046_RedoRemoveIssue;
	               	
	        this.testData.put("ChannelsURL",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("InformationInSendsPanel",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("informationInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("FromTaskNameInSendsPanel",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("fromTaskNameInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("OtherTaskName",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("otherTaskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Intermediate",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("intermediate").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Disintermediate",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("disintermediate").item(0).getChildNodes().item(0).getNodeValue());
		 	
			this.testData.put("Actions",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInSendsPanel",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("actionsInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AddNewSegment",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForRedoRemoveIssue",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("segmentForRedoRemoveIssue").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("NewIssueInSendsPanel",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("newIssueInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("MenuInFlowIssuesInSendsPanel",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("menuInFlowIssuesInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("ActionsInSegment",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("actionsInSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("MoveTasksInSegment",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("moveTasksInSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Show",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AboutPlanSegment",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveIssueFlowIssuesInSendsPanel",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("removeIssueFlowIssuesInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TaskName",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("taskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoRemoveIssue",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("undoRemoveIssue").item(0).getChildNodes().item(0).getNodeValue());	
			this.testData.put("RedoRemoveIssue",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("redoRemoveIssue").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("ShowInTaskPanel",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("showInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("DetailsInTaskPanel",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("detailsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInTaskPanel",oXmlEleMAC0046_RedoRemoveIssue.getElementsByTagName("actionsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0046_RedoRemoveIssue not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0046_RedoRemoveIssue.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0046_RedoRemoveIssue can not be parsed.");
		}
	}
}