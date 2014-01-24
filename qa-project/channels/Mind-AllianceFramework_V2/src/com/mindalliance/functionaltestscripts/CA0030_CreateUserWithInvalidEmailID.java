package com.mindalliance.functionaltestscripts;

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
import com.mindalliance.configuration.Log4J;
import com.mindalliance.configuration.LogFunctions;
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;
import com.mindalliance.pages.ChannelsAdmin;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;
import com.mindalliance.pages.PlanPage;

/**
 * Test Case ID: CA0030_CreateUserWithInvalidEmailID
 * Summary: Verify that the invalid Email ID cannot be saved for a user
 * @author Administrator
 *
 */
public class CA0030_CreateUserWithInvalidEmailID extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="CA0030_CreateUserWithInvalidEmailID";
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
			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser Initialized");
			
			
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
	 * This method verifies that invalid email address is not saved for a user
	 * @throws UIAutomationException
	 * @throws IOException 
	 */
	@Test
	public void testCA0030_CreateUserWithInvalidEmailID() throws UIAutomationException, IOException{
		try {
			// Enter URL of Channels
			stepNo++;
			description="URL Entered";
			BrowserController browserController=new BrowserController();
			browserController.enterURL();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"URL Entered");
						
			// Login page
			stepNo++;
			description="Login Sucessful";
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
		    // Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Login successful");
		 			
			// Click on Channels Admin
		    stepNo++;
			description="Channels Admin Page";
			HomePage homePage=new HomePage();
			homePage.clickChannelsAdminLink();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Channels Admin Page");
			
 			//Click Users tab
 			stepNo++;
 			ChannelsAdmin channelsAdmin=new ChannelsAdmin();
 			channelsAdmin.clickUsersTab();
 		    // Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Logout successful");
			
			// Add user
			stepNo++;
			description="User created";
			channelsAdmin.addUser(testData.get("User"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"User Created");
			
 			//Add User Details
 			stepNo++;
 			description="Add User Details";
			channelsAdmin.addUserDetails(testData.get("Email"),testData.get("Password"),testData.get("isAdministrator"),testData.get("isDisabled"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add User Details");
			
			//Sign Out from 'Admin' page
			stepNo++;
			description="SignOut Successful";
			HeaderController headerController=new HeaderController();
			headerController.signOutAdmin();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"SignOut Successful");
			
			Reporting reporting= new Reporting();
		    reporting.generateAutomationReport();
		    
		}catch (UIAutomationException ue) {
			PlanPage planPage= new PlanPage();
			planPage.getStackTraceOnPlanPage();
			
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
			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser quit");
		}
	}
	
	/**
     * Loads Test Data for CA0030_CreateUserWithInvalidEmailID.
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
			File CA0030_CreateUserWithInvalidEmailID=new File(path + "CA0030_CreateUserWithInvalidEmailID.xml");
			
			Document docCA0030_CreateUserWithInvalidEmailID=db.parse(CA0030_CreateUserWithInvalidEmailID);
			Element eleCA0030_CreateUserWithInvalidEmailID=docCA0030_CreateUserWithInvalidEmailID.getDocumentElement();
	              
	        Element oXmlEleCA0030_CreateUserWithInvalidEmailID = (Element) eleCA0030_CreateUserWithInvalidEmailID;
	     	        
	        this.testData.put("User",oXmlEleCA0030_CreateUserWithInvalidEmailID.getElementsByTagName("user").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL",oXmlEleCA0030_CreateUserWithInvalidEmailID.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleCA0030_CreateUserWithInvalidEmailID.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Email",oXmlEleCA0030_CreateUserWithInvalidEmailID.getElementsByTagName("email").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Password",oXmlEleCA0030_CreateUserWithInvalidEmailID.getElementsByTagName("password").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("IsAdministrator",oXmlEleCA0030_CreateUserWithInvalidEmailID.getElementsByTagName("isAdministrator").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("IsDisabled",oXmlEleCA0030_CreateUserWithInvalidEmailID.getElementsByTagName("isDisabled").item(0).getChildNodes().item(0).getNodeValue());
		
		}
		catch(SAXException se){
			throw new UIAutomationException("File CA0030_CreateUserWithInvalidEmailID not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File CA0030_CreateUserWithInvalidEmailID.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File CA0030_CreateUserWithInvalidEmailID can not be parsed.");
		}
	}
	
	
}
