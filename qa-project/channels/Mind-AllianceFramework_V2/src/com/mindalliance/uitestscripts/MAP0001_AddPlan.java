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
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.LogFunctions;
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;
import com.mindalliance.pages.ChannelsAdmin;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;
/**
 * Test Case ID: MAP0001_AddPlan
 * Summary: Verify that the "Plan gets added"
 * @author Administrator
 *
 */
public class MAP0001_AddPlan extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="MAP0001_AddPlan";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="FAIL";
	public String blank=""; 
	public String scriptException;
	
	public MAP0001_AddPlan() throws UIAutomationException{
		setUp();
		testMAP0001_AddPlan();
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
			BrowserController browserController= new BrowserController();
			browserController.initializeDriver();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
		}
		catch(UIAutomationException ue){
			stepNo++;
//			description="Unable to initialize the driver";
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo, ue.getErrorMessage(), failed, scriptException, blank);
		}
	}
	
	/**
	 * This method will add plan to the Channels and verify if the plan is added
	 * @throws UIAutomationException
	 */
	@Test
	public void testMAP0001_AddPlan() throws UIAutomationException{
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
			channelsAdmin.enterPlanName(testData.get("AutomationTestPlan"),testData.get("AuthorAutomationTestPlan"));
			channelsAdmin.deletePlan(testData.get("PlanName"));
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
						
			// Sign Out from 'Admin' page
			stepNo++;
			description="SignOut Successful";
			HeaderController headerController=new HeaderController();
			headerController.signOutAdmin();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
		} 
		catch (UIAutomationException ue) {
			Reporting.getScreenShot(testCaseId);
		
			// Sign out from Admin page
			stepNo++;
			description="Signout";
			HeaderController headerController=new HeaderController();
			headerController.signOutAdmin();
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo, ue.getErrorMessage(), failed, scriptException, blank);
						
			// Quits the Browser
			stepNo++;
			description="Browser Closed";
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo, ue.getErrorMessage(), failed, scriptException, blank);
		}
	}
	
	/*
	 * (non-Javadoc)
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
     * Loads Test Data for MAP0001_AddPlan.
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
			File MAP0001_AddPlan=new File(path + "MAP0001_AddPlan.xml");
			
			Document docMAP0001_AddPlan=db.parse(MAP0001_AddPlan);
			Element eleMAP0001_AddPlan=docMAP0001_AddPlan.getDocumentElement();
	              
	        Element oXmlEleMAP0001_AddPlan = (Element) eleMAP0001_AddPlan;
	       	
	        
	        this.testData.put("AutomationTestPlan",oXmlEleMAP0001_AddPlan.getElementsByTagName("automationTestPlan").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AuthorAutomationTestPlan",oXmlEleMAP0001_AddPlan.getElementsByTagName("authorAutomationTestPlan").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL",oXmlEleMAP0001_AddPlan.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAP0001_AddPlan.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("PlanName",oXmlEleMAP0001_AddPlan.getElementsByTagName("planName").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAP0001_AddPlan can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAP0001_AddPlan.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAP0001_AddPlan can not be parsed.");
		}
			
	}
}
