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


public class MAV0212_AddInfoReceiveSendIssueSeverity extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0212_AddInfoReceiveSendIssueSeverity";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="FAIL";
	public String blank=""; 
	public String browser="";
	public String scriptException;
	
	/**
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
	public void testMAV0212_AddInfoReceiveSendIssueSeverity() throws UIAutomationException, IOException {
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
			
			// Add New Segment
 			stepNo++;
			description="Add New Segment";
			PlanPage planPage= new PlanPage();
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Close segment window
			stepNo++;
			description="Close About Plan Segment";
			planPage.closeSegmentWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Click on 'strench Up forms' icon 
			stepNo++;
			description="Add Info Sends";
			planPage.clickStrenchUpForm();
			// Click on 'Add' in sends panel
			planPage.clickAddInfoSendsPanel();
			// Click on 'Show Advanced Form' link
			planPage.clickShowAdvancedFormInSendsPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Click on 'New Issue' under 'Actions' pop up menu in Sends panel
			stepNo++;
			description="New Issues - Sends Panel";
			planPage.clickPopupMenu(testData.get("ActionsInSendsPanel"));
			planPage.clickSubmenu(testData.get("NewIssueInSendsPanel"));	
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Verify dropdown of severity in issue
			stepNo++;
			description="Sverity Issue - Sends Panel";
			planPage.verifySeverityDropdownInIssueInSends(testData.get("Minor"), testData.get("Major"),testData.get("Severe"), testData.get("Extreme"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Click on 'Add' in receives panel
			stepNo++;
			description="Add Info Receives";
			planPage.clickAddInfoReceivesPanel();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Click on 'New Issue' under 'Actions' pop up menu in Sends panel
			stepNo++;
			description="New Issues - Receives Panel";
			planPage.clickPopupMenu(testData.get("ActionsInReceivesPanel"));
			planPage.clickSubmenu(testData.get("NewIssueInReceivesPanel"));	
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Verify dropdown of severity in issue
			stepNo++;
			description="Severity Issues - Receives Panel";
			planPage.verifySeverityDropdownInIssueInReceives(testData.get("Minor"), testData.get("Major"),testData.get("Severe"), testData.get("Extreme"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
							
			// Remove This segment
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
     * Loads Test Data for MAV0212_AddInfoReceiveSendIssueSeverity.
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
			File MAV0212_AddInfoReceiveSendIssueSeverity=new File(path + "MAV0212_AddInfoReceiveSendIssueSeverity.xml");
			
			Document docMAV0212_AddInfoReceiveSendIssueSeverity=db.parse(MAV0212_AddInfoReceiveSendIssueSeverity);
			Element eleMAV0212_AddInfoReceiveSendIssueSeverity=docMAV0212_AddInfoReceiveSendIssueSeverity.getDocumentElement();
	              
	        Element oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity = (Element) eleMAV0212_AddInfoReceiveSendIssueSeverity;
	        
	        this.testData.put("ChannelsURL",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowSimpleFormText",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Unspecified",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("unspecified").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Actions",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AddNewSegment",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("ActionsInReceivesPanel",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("actionsInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInSendsPanel",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("actionsInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("NewIssueInReceivesPanel",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("newIssueInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("NewIssueInSendsPanel",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("newIssueInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			
			
			this.testData.put("Minor",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("minor").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Major",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("major").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Severe",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("severe").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Extreme",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("extreme").item(0).getChildNodes().item(0).getNodeValue());
			
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0212_AddInfoReceiveSendIssueSeverity not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0212_AddInfoReceiveSendIssueSeverity.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0212_AddInfoReceiveSendIssueSeverity can not be parsed.");
		}
	}
}