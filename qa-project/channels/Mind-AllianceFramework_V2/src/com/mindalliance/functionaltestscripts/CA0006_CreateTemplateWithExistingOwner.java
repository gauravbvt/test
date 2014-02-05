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
/**
 * Test Case ID: CA0006_CreateTemplateWithExistingOwner
 * Summary: Verify that the new template with existing owner name gets added 
 * @author Administrator
 *
 */
public class CA0006_CreateTemplateWithExistingOwner extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="CA0006_CreateTemplateWithExistingOwner";
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
			// Loads the Test Data
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
	 * This method will add plan with existing plan owner name to the Channels and verify if the plan is added
	 * @throws UIAutomationException
	 * @throws IOException 
	 */
	@Test
	public void testCA0006_CreateTemplateWithExistingOwner() throws UIAutomationException, IOException{
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
			description="Login Successful";
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
		    // Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Login Successful");
				
			// Click on Channels Admin
			stepNo++;
			description="Channels Admin Page";
			HomePage homePage=new HomePage();
			homePage.clickChannelsAdminLink();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Channels Admin Page");
				
			// Enter Plan name
			stepNo++;
			description="Enter Template name";
			ChannelsAdmin channelsAdmin=new ChannelsAdmin();
			channelsAdmin.enterPlanName(testData.get("AutomationTestPlan"));
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Enter Template name");
						
			//Click Add button
			stepNo++;
			description="Click Add button";
			channelsAdmin.clickAddPlanButton();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Click Add button");
			
			// Enter Owner name
			stepNo++;
			description="Enter Owner name";
			channelsAdmin.enterPlanOwnerName(testData.get("AuthorAutomationTestPlan"));
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Enter Owner name");
			
			//Click Save button to Save plan
			stepNo++;
			description="Click Save button";
			channelsAdmin.clickSavePlanButton();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Click Save button");
			
			// Enter Plan name for second plan
			stepNo++;
			description="Enter New Template name";
			channelsAdmin.enterPlanName(testData.get("AutomationTestPlan1"));
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Enter Template name");
									
			//Click Add button
			stepNo++;
			description="Click Add button";
			channelsAdmin.clickAddPlanButtonSamePlan();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Click Add button");
						
			// Enter Same(Existing) Owner name
			stepNo++;
			description="Enter Same Owner name";
			channelsAdmin.enterPlanOwnerName(testData.get("AuthorAutomationTestPlan"));
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Enter existing Owner name");
						
			//Click Save button to Save plan
			stepNo++;
			description="Click Save button";
			channelsAdmin.clickSavePlanButton();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Click Save button");
			
			// Sign Out from 'Admin' page
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
			Log4J.getlogger(this.getClass()).info(testCaseId +" Browser quit");
		}
	}
	
	/**
     * Loads Test Data for CA0006_CreateTemplateWithExistingOwner.
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
			File CA0006_CreateTemplateWithExistingOwner=new File(path + "CA0006_CreateTemplateWithExistingOwner.xml");
			
			Document docCA0006_CreateTemplateWithExistingOwner=db.parse(CA0006_CreateTemplateWithExistingOwner);
			Element eleCA0006_CreateTemplateWithExistingOwner=docCA0006_CreateTemplateWithExistingOwner.getDocumentElement();
	              
	        Element oXmlEleCA0006_CreateTemplateWithExistingOwner = (Element) eleCA0006_CreateTemplateWithExistingOwner;
	       	
	        
	        this.testData.put("AutomationTestPlan",oXmlEleCA0006_CreateTemplateWithExistingOwner.getElementsByTagName("automationTestPlan").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AutomationTestPlan1",oXmlEleCA0006_CreateTemplateWithExistingOwner.getElementsByTagName("automationTestPlan1").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AuthorAutomationTestPlan",oXmlEleCA0006_CreateTemplateWithExistingOwner.getElementsByTagName("authorAutomationTestPlan").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL",oXmlEleCA0006_CreateTemplateWithExistingOwner.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleCA0006_CreateTemplateWithExistingOwner.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("PlanName",oXmlEleCA0006_CreateTemplateWithExistingOwner.getElementsByTagName("planName").item(0).getChildNodes().item(0).getNodeValue());
	     //   this.testData.put("AuthorAutomationTestPlan",oXmlEleCA0006_CreateTemplateWithExistingOwner.getElementsByTagName("authorAutomationTestPlan").item(0).getChildNodes().item(0).getNodeValue());
		
		}
		catch(SAXException se){
			throw new UIAutomationException("File CA0006_CreateTemplateWithExistingOwner can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File CA0006_CreateTemplateWithExistingOwner.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File CA0006_CreateTemplateWithExistingOwner can not be parsed.");
		}
			
	}
}
