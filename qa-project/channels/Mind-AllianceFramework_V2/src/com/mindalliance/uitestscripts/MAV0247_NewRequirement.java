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
import com.mindalliance.pages.ChannelsAdmin;
import com.mindalliance.pages.CommunitiesPage;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;
import junit.framework.Assert;
import junit.framework.TestCase;


public class MAV0247_NewRequirement extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0247_NewRequirement";
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
	public void testMAV0247_NewRequirement() throws UIAutomationException, IOException {
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
							
 		    // Click on Channels Admin
 			stepNo++;
 			description="Channels Admin Page";
 			HomePage homePage=new HomePage();
 			homePage.clickChannelsAdminLink();
 			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
 					
 			// Enter Plan name
 			stepNo++;
 			description="Plan Created";
 			ChannelsAdmin channelsAdmin=new ChannelsAdmin();
 			channelsAdmin.enterPlanName(testData.get("AutomationTestPlan"));
 			channelsAdmin.enterPlanOwnerName(testData.get("AuthorAutomationTestPlan"));
 			channelsAdmin.clickSavePlanButton();
 			channelsAdmin.clickProductizePlanButton();
 			channelsAdmin.clickHomeLink();			
 			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
 								
 		    homePage.clickCommunitiesLink();
 		    homePage.selectPlanFromDropDown(testData.get("plan"));
 				    
 		    // Write log
 		    LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
 				
 		    CommunitiesPage communitiesPage=new CommunitiesPage();
 		    //Click Start it button
		    communitiesPage.clickStartItButton(testData.get("PlanName"));
 		    //Click Go button
 		    communitiesPage.clickGoButton();
 			
 		    //Click on 'Collaboration Requirements' link
			communitiesPage.clickCollaborationRequirementsLink();
			communitiesPage.clickNewRequirementButton();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Remove requirement
			//stepNo++;
			//description="Remove Requirement";
			//PlanPage planPage =new PlanPage();
			//planPage.clickRemoveRequirementInPlanRequirement();
			// Write log
 			//LogFunctions.writeLogs(description);
 			//LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
						
			// Close Requirements window
 			//stepNo++;
			//description="Close Requirement Window";
			//planPage.closeRequirementWindow();
			// Write log
 			//LogFunctions.writeLogs(description);
 			//LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
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
     * Loads Test Data for MAV0247_NewRequirement.
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
			File MAV0247_NewRequirement=new File(path + "MAV0247_NewRequirement.xml");
			
			Document docMAV0247_NewRequirement=db.parse(MAV0247_NewRequirement);
			Element eleMAV0247_NewRequirement=docMAV0247_NewRequirement.getDocumentElement();
	              
	        Element oXmlEleMAV0247_NewRequirement = (Element) eleMAV0247_NewRequirement;
	       	
	        this.testData.put("Scoping", oXmlEleMAV0247_NewRequirement.getElementsByTagName("scoping").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Planrequirement",oXmlEleMAV0247_NewRequirement.getElementsByTagName("planrequirement").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL",oXmlEleMAV0247_NewRequirement.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAV0247_NewRequirement.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("RequirementDefinitions",oXmlEleMAV0247_NewRequirement.getElementsByTagName("requirementDefinitions").item(0).getChildNodes().item(0).getNodeValue());
	        
	        
	        this.testData.put("AutomationTestPlan",oXmlEleMAV0247_NewRequirement.getElementsByTagName("automationTestPlan").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AuthorAutomationTestPlan",oXmlEleMAV0247_NewRequirement.getElementsByTagName("authorAutomationTestPlan").item(0).getChildNodes().item(0).getNodeValue()); 
	        this.testData.put("PlanName",oXmlEleMAV0247_NewRequirement.getElementsByTagName("planName").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("plan",oXmlEleMAV0247_NewRequirement.getElementsByTagName("plan").item(0).getChildNodes().item(0).getNodeValue());
	        
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0247_NewRequirement.xml not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0247_NewRequirement.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0247_NewRequirement.xml can not be parsed.");
		}
      
	}
}