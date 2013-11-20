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
 * Test Case ID: MAP0010_addRoleJuridiction
 * Summary: Verify that Role, Title, Jurisdiction and supervisor can be assign to participating agent within an organization
 * @author AfourTech
 *
 */
public class MAP0010_addRoleJuridiction extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAP0010_addRoleJuridiction";
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
	
	/**
	 * This method adds organization to the plan and also its details to the plan
	 * @throws UIAutomationException
	 * @throws IOException 
	 */
	@Test
	public void testMAP0010_addRoleJuridiction() throws UIAutomationException, IOException{
		try {
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
 			
//			//Click on 'Add New Segment' under 'Actions' pop up menu
 			
// 			stepNo++;
//			description="Add New Segment";
//			planPage.clickPopupMenu(testData.get("Actions"));
//			planPage.clickSubmenu(testData.get("AddNewSegment"));
//			planPage.enterSegmentName(testData.get("SegmentForAddRoleJurisdiction"));
//			
 			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
// 			
//			// Close segment window
 			
// 			stepNo++;
//			description="Closed Segment";
//			planPage.closeSegmentWindow();
//			// Write log
// 			LogFunctions.writeLogs(description);
// 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Click on 'Organizations In Scope' under 'Scoping' pop up menu
 			stepNo++;
			description="Organization In Scope ";
			PlanPage planPage=new PlanPage();
			planPage.clickPopupMenu(testData.get("Scoping"));
			planPage.clickSubmenu(testData.get("OrganizationsInScope"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Enter organization name
 			stepNo++;
			description="Organization Name";
			planPage.enterOrganizationName(testData.get("Organization"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 				
			// Click on 'Organization'
 			stepNo++;
			description="Open Organization Created";
			planPage.clickOnOrganizationEntered(testData.get("Organization"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
 			// Click on Structure Tab
 			stepNo++;
			description="Structure Tab";
			planPage.clickOrganizationsStructureTab();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Enter Agent
 			stepNo++;
			description="Role Jurisdiction Created";
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
			planPage.checkCheckboxOfAgentInOrganizationInScope();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Close Actual organization window
 			stepNo++;
			description="Close Organization Window";
			planPage.closeActualOrganizationWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Click on 'Organization'
 			stepNo++;
			description="Remove Organization Details";
			planPage.clickOnOrganizationEntered(testData.get("Organization"));
			planPage.clickOnOrganizationEntered(testData.get("Organization"));
			// Check checkbox of agent 1
			planPage.checkCheckboxOfAgentInOrganizationInScope();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Close Actual organization window
 			stepNo++;
			description="Close Organization Window";
			planPage.closeActualOrganizationWindow();
			planPage.clickOnOrganizationEntered(testData.get("Organization"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Close Actual organization window
 			stepNo++;
			description="Close Actual Organization Window";
			planPage.closeActualOrganizationWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Remove expectation
 			stepNo++;
			description="Remove Organization";
			planPage.clickRemoveExpectation(testData.get("Organization"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Close Organization Window
 			stepNo++;
			description="Close Organization Window";
			planPage.closeOrganiztionWindow();
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
 								
			// Sign Out from 'Plan' page
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
     * Loads Test Data for MAP0010_addRoleJuridiction.
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
			File MAP0010_addRoleJuridiction=new File(path + "MAP0010_addRoleJuridiction.xml");
			
			Document docMAP0010_addRoleJuridiction=db.parse(MAP0010_addRoleJuridiction);
			Element eleMAP0010_addRoleJuridiction=docMAP0010_addRoleJuridiction.getDocumentElement();
	              
	        Element oXmlEleMAP0010_addRoleJuridiction = (Element) eleMAP0010_addRoleJuridiction;
	    	           
	        this.testData.put("Scoping", oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("scoping").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("OrganizationsInScope", oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("organizationsInScope").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL", oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Organization",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("organization").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Actions",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForAddRoleJurisdiction",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("segmentForAddRoleJurisdiction").item(0).getChildNodes().item(0).getNodeValue());
	        
			this.testData.put("Agent",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("agent").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Role",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("role").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("TitleInOrg",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("titleInOrg").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Jurisdiction",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("jurisdiction").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Supervisor",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("supervisor").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());  
	    }
		catch(SAXException se){
			throw new UIAutomationException("File MAP0010_addRoleJuridiction.xml can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAP0010_addRoleJuridiction.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAP0010_addRoleJuridiction.xml can not be parsed.");
		}
	}

}