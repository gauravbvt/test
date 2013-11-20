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
 * Testcase ID: MAC0045_RedoAddIssue
 * 	   Summary: Verify that user is able to undo the disIntermediate task
 * @author AFour
 * 
 */

public class MAC0045_RedoAddIssue extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="MAC0045_RedoAddIssue";
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
	public void testMAC0045_RedoAddIssue() throws UIAutomationException, IOException {
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
			// click on 'strench up forms' icon
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
			description="New issue added successfully";
			planPage.clickPopupMenu(testData.get("ActionsInSendsPanel"));
			planPage.clickSubmenu(testData.get("NewIssueInSendsPanel"));
			
			// Click on 'Undo New Issue' under 'Actions' pop up menu
			stepNo++;
			description="Undo new issue";
			planPage.clickPopupMenu(testData.get("ActionsInSendsPanel"));
			planPage.clickSubmenu(testData.get("UndoNewIssueInSendsPanel"));
			
			// Click on 'Redo New Issue' under 'Actions' pop up menu
			stepNo++;
			description="Undo Disintermediate";
			planPage.clickPopupMenu(testData.get("ActionsInSendsPanel"));
			planPage.clickSubmenu(testData.get("RedoNewIssueInSendsPanel"));
		
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
     * Loads Test Data for MAC0045_RedoAddIssue.
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
			File MAC0045_RedoAddIssue=new File(path + "MAC0045_RedoAddIssue.xml");
			
			Document docMAC0045_RedoAddIssue=db.parse(MAC0045_RedoAddIssue);
			Element eleMAC0045_RedoAddIssue=docMAC0045_RedoAddIssue.getDocumentElement();
	              
	        Element oXmlEleMAC0045_RedoAddIssue = (Element) eleMAC0045_RedoAddIssue;
	        
	               	
	        this.testData.put("ChannelsURL",oXmlEleMAC0045_RedoAddIssue.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0045_RedoAddIssue.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("InformationInSendsPanel",oXmlEleMAC0045_RedoAddIssue.getElementsByTagName("informationInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("NewIssueInSendsPanel",oXmlEleMAC0045_RedoAddIssue.getElementsByTagName("newIssueInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());		
		 	
			this.testData.put("Actions",oXmlEleMAC0045_RedoAddIssue.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInSendsPanel",oXmlEleMAC0045_RedoAddIssue.getElementsByTagName("actionsInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AddNewSegment",oXmlEleMAC0045_RedoAddIssue.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAC0045_RedoAddIssue.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoNewIssueInSendsPanel",oXmlEleMAC0045_RedoAddIssue.getElementsByTagName("undoNewIssueInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RedoNewIssueInSendsPanel",oXmlEleMAC0045_RedoAddIssue.getElementsByTagName("redoNewIssueInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());

			this.testData.put("Show",oXmlEleMAC0045_RedoAddIssue.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AboutPlanSegment",oXmlEleMAC0045_RedoAddIssue.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
//			this.testData.put("IntermediateTaskName",oXmlEleMAC0045_RedoAddIssue.getElementsByTagName("intermediateTaskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TaskName",oXmlEleMAC0045_RedoAddIssue.getElementsByTagName("taskName").item(0).getChildNodes().item(0).getNodeValue());
		    this.testData.put("ShowInTaskPanel",oXmlEleMAC0045_RedoAddIssue.getElementsByTagName("showInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("DetailsInTaskPanel",oXmlEleMAC0045_RedoAddIssue.getElementsByTagName("detailsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
//			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0045_RedoAddIssue not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0045_RedoAddIssue.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0045_RedoAddIssue can not be parsed.");
		}
	}
}