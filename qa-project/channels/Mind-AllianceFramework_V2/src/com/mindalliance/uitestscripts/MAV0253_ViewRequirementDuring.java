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
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.LogFunctions;
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;
import com.mindalliance.pages.PlanPage;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MAV0253_ViewRequirementDuring extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0253_ViewRequirementDuring";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="FAIL";
	public String blank=""; 
	public String scriptException;
	public String browser="";
	
	public MAV0253_ViewRequirementDuring() throws UIAutomationException{
		setUp();
		testMAV0253_ViewRequirementDuring();
		tearDown();
	}
	/*
	 * This method will initilize the setup required for every test case
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
			
			// Loads Test Data
			description = "Testcase: " + testCaseId + " execution started";
			loadTestData();
			// Write log		
			LogFunctions.writeLogs(description);
					
			// Creates Browser instance
			description="Browser initialized";
			browser=BrowserController.browserName;	
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
	public void testMAV0253_ViewRequirementDuring() throws UIAutomationException {
		try{
			stepNo++;
			description="URL Entered";
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"),browser);
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
 							
			// Plan Page
		    stepNo++;
			description="Collaboration Plan";
			HomePage homePage=new HomePage();
			homePage.clickCollaborationPlanLink();	
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 						
			// Close Plan Map window
			stepNo++;
			description="Close Plan Map Window";
			PlanPage planPage=new PlanPage();
			planPage.closePlanMap();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
							
			// Click on 'Plan Requirements' under 'Scoping' pop up menu
 			stepNo++;
			description="Plan Requirement";
			planPage.clickPopupMenu(testData.get("Scoping"));
			planPage.clickSubmenu(testData.get("Planrequirement"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Clicks on 'Satisfactions' tab in plan requirements
 			stepNo++;
			description="Satisfaction";
			planPage.clickSatisfactionsTabInPlanRequirement(testData.get("SatisfactionsTab"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 							
			// Click on 'Situation' dropdown and select 'During' option
 			stepNo++;
			description="During";
			planPage.selectOptionFromSituationDropdownInPlanRequirement(testData.get("During"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Close Requirements window
 			stepNo++;
			description="Close Requirement Window";
			planPage.closeRequirementWindow();
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
 			
		}catch (UIAutomationException ue) {
			Reporting.getScreenShot(testCaseId);
		
			// Sign out from plan page
			stepNo++;
			description="SignOut Successful";
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			// Write log
 			LogFunctions.writeLogs(ue.getErrorMessage());
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
 			// Quits the Browser
			stepNo++;
			description="Browser Closed";
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
			// Write log
 			LogFunctions.writeLogs(ue.getErrorMessage());
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
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
     * Loads Test Data for MAV0253_ViewRequirementDuring.
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
			File MAV0253_ViewRequirementDuring=new File(path + "MAV0253_ViewRequirementDuring.xml");
			
			Document docMAV0253_ViewRequirementDuring=db.parse(MAV0253_ViewRequirementDuring);
			Element eleMAV0253_ViewRequirementDuring=docMAV0253_ViewRequirementDuring.getDocumentElement();
	              
	        Element oXmlEleMAV0253_ViewRequirementDuring = (Element) eleMAV0253_ViewRequirementDuring;
	       	
	        this.testData.put("Scoping", oXmlEleMAV0253_ViewRequirementDuring.getElementsByTagName("scoping").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Planrequirement",oXmlEleMAV0253_ViewRequirementDuring.getElementsByTagName("planrequirement").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL",oXmlEleMAV0253_ViewRequirementDuring.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAV0253_ViewRequirementDuring.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("SatisfactionsTab",oXmlEleMAV0253_ViewRequirementDuring.getElementsByTagName("satisfactionsTab").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("During",oXmlEleMAV0253_ViewRequirementDuring.getElementsByTagName("during").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0253_ViewRequirementDuring.xml not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0253_ViewRequirementDuring.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0253_ViewRequirementDuring.xml can not be parsed.");
		}
      
	}
}