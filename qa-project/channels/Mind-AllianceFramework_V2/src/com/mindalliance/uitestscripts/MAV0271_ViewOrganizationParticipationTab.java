package com.mindalliance.uitestscripts;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;
import junit.framework.TestCase;

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


public class MAV0271_ViewOrganizationParticipationTab  extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0271_ViewOrganizationParticipationTab";
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
	 * This method adds organization to the plan and verify if the organization is added
	 * @throws UIAutomationException
	 * @throws IOException 
	 */
	@Test
	public void testMAV0271_ViewOrganizationParticipationTab() throws UIAutomationException, IOException{
		try {
			// Enter URL of Channels
			stepNo++;
			description="URL Entered";
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
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
 			
			// Click on 'Organizations In Scope' under 'Scoping' pop up menu
 			stepNo++;
			description="Organization in Scope";
			PlanPage planPage=new PlanPage();
			planPage.clickPopupMenu(testData.get("Scoping"));
			planPage.clickSubmenu(testData.get("OrganizationsInScope"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Enter organization name
 			stepNo++;
			description="Organization Created";
			planPage.enterOrganizationName(testData.get("Organization1"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
 			// Click on organization entered
			stepNo++;
			description="Click Organization For Details";
			planPage.clickOrganizationName(testData.get("Organization1"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 				
 			// Click Organization's Participations Tab
 			stepNo++;
			description="Participations Tab";
			planPage.clickOrganizationsParticipationTab();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Sign Out from 'Plan' page
 			stepNo++;
			description="Signout Successful";
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
     * Loads Test Data for MAV0271_ViewOrganizationParticipationTab.
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
			File MAV0271_ViewOrganizationParticipationTab=new File(path + "MAV0271_ViewOrganizationParticipationTab.xml");
			
			Document docMAV0271_ViewOrganizationParticipationTab=db.parse(MAV0271_ViewOrganizationParticipationTab);
			Element eleMAV0271_ViewOrganizationParticipationTab=docMAV0271_ViewOrganizationParticipationTab.getDocumentElement();
	              
	        Element oXmlEleMAV0271_ViewOrganizationParticipationTab = (Element) eleMAV0271_ViewOrganizationParticipationTab;
	    	           
	        this.testData.put("ChannelsURL", oXmlEleMAV0271_ViewOrganizationParticipationTab.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Scoping", oXmlEleMAV0271_ViewOrganizationParticipationTab.getElementsByTagName("scoping").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("OrganizationsInScope", oXmlEleMAV0271_ViewOrganizationParticipationTab.getElementsByTagName("organizationsInScope").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAV0271_ViewOrganizationParticipationTab.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Organization1",oXmlEleMAV0271_ViewOrganizationParticipationTab.getElementsByTagName("organization1").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0271_ViewOrganizationParticipationTab.xml can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0271_ViewOrganizationParticipationTab.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0271_ViewOrganizationParticipationTab.xml can not be parsed.");
		}
	}

}