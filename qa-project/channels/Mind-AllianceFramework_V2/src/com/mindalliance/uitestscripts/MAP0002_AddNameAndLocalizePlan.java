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
import com.mindalliance.pages.ChannelsAdmin;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;
/**
 * Test Case ID: MAP0002_AddNameAndLocalizePlan
 * Summary: Verify that the "Plan gets added"
 * @author Administrator
 *
 */
public class MAP0002_AddNameAndLocalizePlan extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="MAP0002_AddNameAndLocalizePlan";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="FAIL";
	public String blank=""; 
	public String scriptException;
	public String browser="";
	
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
	 * This method will add plan to the Channels and verify if the plan is added
	 * @throws UIAutomationException
	 * @throws IOException 
	 */
	@Test
	public void testMAP0002_AddNameAndLocalizePlan() throws UIAutomationException, IOException{
		try {
			// Enter URL of Channels
			stepNo++;
			description="URL Entered";
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
     * Loads Test Data for MAP0002_AddNameAndLocalizePlan.
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
			File MAP0002_AddNameAndLocalizePlan=new File(path + "MAP0002_AddNameAndLocalizePlan.xml");
			
			Document docMAP0002_AddNameAndLocalizePlan=db.parse(MAP0002_AddNameAndLocalizePlan);
			Element eleMAP0002_AddNameAndLocalizePlan=docMAP0002_AddNameAndLocalizePlan.getDocumentElement();
	              
	        Element oXmlEleMAP0002_AddNameAndLocalizePlan = (Element) eleMAP0002_AddNameAndLocalizePlan;
	       	
	        
	        this.testData.put("AutomationTestPlan",oXmlEleMAP0002_AddNameAndLocalizePlan.getElementsByTagName("automationTestPlan").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AuthorAutomationTestPlan",oXmlEleMAP0002_AddNameAndLocalizePlan.getElementsByTagName("authorAutomationTestPlan").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL",oXmlEleMAP0002_AddNameAndLocalizePlan.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAP0002_AddNameAndLocalizePlan.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("PlanName",oXmlEleMAP0002_AddNameAndLocalizePlan.getElementsByTagName("planName").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAP0002_AddNameAndLocalizePlan can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAP0002_AddNameAndLocalizePlan.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAP0002_AddNameAndLocalizePlan can not be parsed.");
		}
			
	}
}
