package com.mindalliance.functionaltestscripts;

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
 * Test Case ID: ENT0002_CreateAgentWithSpecialCharacters
 * Summary: Verify that agent can be assign to participating agent within an organization
 * @author AfourTech
 *
 */
public class ENT0002_CreateAgentWithSpecialCharacters extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="ENT0002_CreateAgentWithSpecialCharacters";
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
	
	/**
	 * This method creates Agent with special characters
	 * @throws UIAutomationException
	 * @throws IOException 
	 */
	@Test
	public void testENT0002_CreateAgentWithSpecialCharacters() throws UIAutomationException, IOException{
		try {
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
			
			// Click on 'Organizations In Scope' under 'Scoping' pop up menu
 			stepNo++;
			description="Organization In Scope ";
			PlanPage planPage=new PlanPage();
			planPage.clickPopupMenu(testData.get("Scoping"));
			planPage.clickSubmenu(testData.get("OrganizationsInScope"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Organization In Scope");	
			
			// Enter organization name
 			stepNo++;
			description="Organization Name";
			planPage.enterOrganizationName(testData.get("Organization"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Organization Name");	
				
			// Click on 'Organization'
 			stepNo++;
			description="Open Organization Created";
			planPage.clickOnOrganizationEntered(testData.get("Organization"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Open Organization Created");	
			
 			// Click on Structure Tab
 			stepNo++;
			description="Structure Tab";
			planPage.clickOrganizationsStructureTab();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Structure Tab");	
			
			// Enter Agent
 			stepNo++;
			description="Agent Created with Special";
			planPage.enterAgentInOrganizationInScope(testData.get("Agent"));
			// Enter Title
			planPage.enterTitleInOrganizationInScope(testData.get("TitleInOrg"));
			// Enter Role
			planPage.enterRoleInOrganizationInScope(testData.get("Role"));
			// Enter Jurisdiction 
			planPage.enterJurisdictionInOrganizationInScope(testData.get("Jurisdiction"));
			// Enter Supervisor
			planPage.enterSupervisorInOrganizationInScope(testData.get("Supervisor"));
			// Check checkbox of agent 1
			planPage.clickAddButtonOfAgentInOrganizationInScope(testData.get("Agent1"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Role Jurisdiction Created");	
			
			// Close Actual organization window
 			stepNo++;
			description="Close Organization Window";
			planPage.checkCheckboxOfAgentInOrganizationInScope();
			planPage.closeActualOrganizationWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close Organization Window");	
			
			// Remove expectation
 			stepNo++;
			description="Remove Organization";
			planPage.clickRemoveExpectation(testData.get("Organization"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Remove Organization");	
			
			// Close Organization Window
 			stepNo++;
			description="Close Organization Window";
			planPage.closeOrganiztionWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close Organization Window");	
								
			// Sign Out from 'Plan' page
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
     * Loads Test Data for ENT0002_CreateAgentWithSpecialCharacters.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException
	{
		try{
			String startTime=LogFunctions.getDateTime();
			GlobalVariables.configuration.setStartTime(startTime);
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File ENT0002_CreateAgentWithSpecialCharacters=new File(path + "ENT0002_CreateAgentWithSpecialCharacters.xml");
			
			Document docENT0002_CreateAgentWithSpecialCharacters=db.parse(ENT0002_CreateAgentWithSpecialCharacters);
			Element eleENT0002_CreateAgentWithSpecialCharacters=docENT0002_CreateAgentWithSpecialCharacters.getDocumentElement();
	              
	        Element oXmlEleENT0002_CreateAgentWithSpecialCharacters = (Element) eleENT0002_CreateAgentWithSpecialCharacters;
	    	           
	        this.testData.put("Scoping", oXmlEleENT0002_CreateAgentWithSpecialCharacters.getElementsByTagName("scoping").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("OrganizationsInScope", oXmlEleENT0002_CreateAgentWithSpecialCharacters.getElementsByTagName("organizationsInScope").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL", oXmlEleENT0002_CreateAgentWithSpecialCharacters.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleENT0002_CreateAgentWithSpecialCharacters.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Organization",oXmlEleENT0002_CreateAgentWithSpecialCharacters.getElementsByTagName("organization").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Actions",oXmlEleENT0002_CreateAgentWithSpecialCharacters.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleENT0002_CreateAgentWithSpecialCharacters.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForAddRoleJurisdiction",oXmlEleENT0002_CreateAgentWithSpecialCharacters.getElementsByTagName("segmentForAddRoleJurisdiction").item(0).getChildNodes().item(0).getNodeValue());
	        
			this.testData.put("Agent",oXmlEleENT0002_CreateAgentWithSpecialCharacters.getElementsByTagName("agent").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Agent1",oXmlEleENT0002_CreateAgentWithSpecialCharacters.getElementsByTagName("agent1").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Role",oXmlEleENT0002_CreateAgentWithSpecialCharacters.getElementsByTagName("role").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("TitleInOrg",oXmlEleENT0002_CreateAgentWithSpecialCharacters.getElementsByTagName("titleInOrg").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Jurisdiction",oXmlEleENT0002_CreateAgentWithSpecialCharacters.getElementsByTagName("jurisdiction").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Supervisor",oXmlEleENT0002_CreateAgentWithSpecialCharacters.getElementsByTagName("supervisor").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleENT0002_CreateAgentWithSpecialCharacters.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());  
	    }
		catch(SAXException se){
			throw new UIAutomationException("File ENT0002_CreateAgentWithSpecialCharacters.xml can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File ENT0002_CreateAgentWithSpecialCharacters.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File ENT0002_CreateAgentWithSpecialCharacters.xml can not be parsed.");
		}
	}

}