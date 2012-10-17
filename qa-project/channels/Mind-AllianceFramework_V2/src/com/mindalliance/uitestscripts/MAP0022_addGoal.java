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

/**
 * Test Case ID: MAP0022_addGoal
 * Summary: Verify that Goal can be added  from the Segment
 * @author AfourTech
 *
 */
public class MAP0022_addGoal extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="MAP0022_addGoal";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="FAIL";
	public String blank=""; 
	public String scriptException;
	public String browser="";
	
	public MAP0022_addGoal() throws UIAutomationException{
		setUp();
		testMAP0022_addGoal();
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
			
		}catch(UIAutomationException ue){
			stepNo++;
			description="Unable to initialize the driver";
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo, ue.getErrorMessage(), failed, scriptException, blank);
		}
	}
	
	@Test
	public void testMAP0022_addGoal() throws UIAutomationException {
		try {
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
			
			// Click Actions pop up menu and Add New Segment
 			stepNo++;
			description="Add New Segment";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForaddgoal"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Click on 'Goals' tab
 			stepNo++;
			description="About Plan Segment Goals";
			planPage.clickGoalTab();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Add Goal
			stepNo++;
			description="Goal Added";
			planPage.addGoal(testData.get("AddGoal"),testData.get("SelectgoalFromList"),testData.get("Type"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
						
			// Close segment window
 			stepNo++;
			description="Close About Plan Segment Window";
			planPage.closeAboutPlanSegmentWindow();
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
 			
		} catch (UIAutomationException ue) {
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
     * Loads Test Data for MAP0022_addGoal.
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
			File testData=new File(path + "MAP0022_addGoal.xml");
			
			Document docMAP0022_addGoal=db.parse(testData);
			Element eleMAP0022_addGoal=docMAP0022_addGoal.getDocumentElement();
	              
	        Element oXmlEleMAP0022_addGoal = (Element) eleMAP0022_addGoal;
	       	
	        this.testData.put("Actions",oXmlEleMAP0022_addGoal.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Show",oXmlEleMAP0022_addGoal.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAP0022_addGoal.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("SegmentForaddgoal",oXmlEleMAP0022_addGoal.getElementsByTagName("segmentForaddgoal").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AboutPlanSegment",oXmlEleMAP0022_addGoal.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("GoalsTab",oXmlEleMAP0022_addGoal.getElementsByTagName("goalsTab").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddGoal",oXmlEleMAP0022_addGoal.getElementsByTagName("addGoal").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("RemoveThisSegment",oXmlEleMAP0022_addGoal.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL",oXmlEleMAP0022_addGoal.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	     	this.testData.put("Title",oXmlEleMAP0022_addGoal.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	     	this.testData.put("SelectgoalFromList",oXmlEleMAP0022_addGoal.getElementsByTagName("selectgoalFromList").item(0).getChildNodes().item(0).getNodeValue());
	     	this.testData.put("Type",oXmlEleMAP0022_addGoal.getElementsByTagName("type").item(0).getChildNodes().item(0).getNodeValue());
	     	
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAP0022_addGoal not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAP0022_addGoal.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAP0022_addGoal can not be parsed.");
		}
	}
	
}