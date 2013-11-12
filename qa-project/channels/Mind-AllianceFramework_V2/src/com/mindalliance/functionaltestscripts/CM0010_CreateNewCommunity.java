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
import com.mindalliance.configuration.LogFunctions;
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;
import com.mindalliance.pages.ChannelsAdmin;
import com.mindalliance.pages.CommunitiesPage;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;

/**
 * Testcase ID: CM0010_CreateNewCommunity
 * 	   Summary: Verify that user is able to create new community
 * @author Afour
 *
 */
public class CM0010_CreateNewCommunity extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="CM0010_CreateNewCommunity";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="Fail";
	public String blank=""; 	
	public String exception="";
	public String browser="";
	public String scriptException;

	
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
	 * This method verify that login page is displayed after entering the URL of Channels
	 * @throws UIAutomationException
	 * @throws IOException 
	 */
	@Test
	public void testCM0010_CreateNewCommunity() throws UIAutomationException, IOException{	
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
		    communitiesPage.clickStartItButton();
		    //Click Go button
		    communitiesPage.clickGoButton();
		
		}
		catch (UIAutomationException ue) {
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo,description,failed, ue.getErrorMessage(), blank);
			Reporting.getScreenShot(testCaseId);
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
		    Assert.fail(ue.getErrorMessage());	
	 }
	}
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@After
	protected void tearDown(){
		if(GlobalVariables.configuration.getWebDriver()!=null){
			GlobalVariables.configuration.getWebDriver().quit();
		}
	}
	
	/**
     * Loads Test Data for CL0001_LoginPage.
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
			File CM0010_CreateNewCommunity=new File(path + "CM0010_CreateNewCommunity.xml");
			
			Document docCM0010_CreateNewCommunity=db.parse(CM0010_CreateNewCommunity);
			Element eleCM0010_CreateNewCommunity=docCM0010_CreateNewCommunity.getDocumentElement();
	              
	        Element oXmlEleCM0010_CreateNewCommunity = (Element) eleCM0010_CreateNewCommunity;
	       	
	        this.testData.put("AutomationTestPlan",oXmlEleCM0010_CreateNewCommunity.getElementsByTagName("automationTestPlan").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AuthorAutomationTestPlan",oXmlEleCM0010_CreateNewCommunity.getElementsByTagName("authorAutomationTestPlan").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL",oXmlEleCM0010_CreateNewCommunity.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleCM0010_CreateNewCommunity.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("PlanName",oXmlEleCM0010_CreateNewCommunity.getElementsByTagName("planName").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("plan",oXmlEleCM0010_CreateNewCommunity.getElementsByTagName("plan").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("CTitle",oXmlEleCM0010_CreateNewCommunity.getElementsByTagName("cTitle").item(0).getChildNodes().item(0).getNodeValue());		
		}
		catch(SAXException se){
			throw new UIAutomationException("File CM0010_CreateNewCommunity can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File CM0010_CreateNewCommunity.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File CM0010_CreateNewCommunity can not be parsed.");
		}
			
	}
}
