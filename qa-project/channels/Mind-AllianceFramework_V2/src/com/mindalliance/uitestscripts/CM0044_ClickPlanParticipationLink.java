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
import com.mindalliance.pages.CommunitiesPage;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;

/**
 * Testcase ID: CM0044_ClinkPlanParticipationLink
 * 	   Summary: Verify that plan participation link gets clicked
 * @author Afour
 *
 */

public class CM0044_ClickPlanParticipationLink extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="CM0044_ClickPlanParticipationLink";
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
	public void testCM0044_ClickPlanParticipationLink() throws UIAutomationException, IOException{	
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
		    //Click Plan Participation link
		    communitiesPage.clickPlanParticipationLink();
		    
		    // Sign out from Communities page
			stepNo++;
			description="Logout successful";
			HeaderController headerController=new HeaderController();
			headerController.signOutCommunities();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
							
			Reporting reporting= new Reporting();
			reporting.generateAutomationReport();
		
		}
		catch (UIAutomationException ue) {
			
			
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo,description,failed, ue.getErrorMessage(), blank);
			Reporting.getScreenShot(testCaseId);
					    
			// Sign out from Communities page
			stepNo++;
			description="Logout successful";
			HeaderController headerController=new HeaderController();
			headerController.signOutCommunities();
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
			File CM0044_ClickPlanParticipationLink=new File(path + "CM0044_ClickPlanParticipationLink.xml");
			
			Document docCM0044_ClickPlanParticipationLink=db.parse(CM0044_ClickPlanParticipationLink);
			Element eleCM0044_ClickPlanParticipationLink=docCM0044_ClickPlanParticipationLink.getDocumentElement();
	              
	        Element oXmlEleCM0044_ClickPlanParticipationLink = (Element) eleCM0044_ClickPlanParticipationLink;
	       	
	        this.testData.put("AutomationTestPlan",oXmlEleCM0044_ClickPlanParticipationLink.getElementsByTagName("automationTestPlan").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AuthorAutomationTestPlan",oXmlEleCM0044_ClickPlanParticipationLink.getElementsByTagName("authorAutomationTestPlan").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL",oXmlEleCM0044_ClickPlanParticipationLink.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleCM0044_ClickPlanParticipationLink.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("PlanName",oXmlEleCM0044_ClickPlanParticipationLink.getElementsByTagName("planName").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("plan",oXmlEleCM0044_ClickPlanParticipationLink.getElementsByTagName("plan").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("CTitle",oXmlEleCM0044_ClickPlanParticipationLink.getElementsByTagName("cTitle").item(0).getChildNodes().item(0).getNodeValue());		
	        this.testData.put("PlanTitle",oXmlEleCM0044_ClickPlanParticipationLink.getElementsByTagName("planTitle").item(0).getChildNodes().item(0).getNodeValue());				
		}
		catch(SAXException se){
			throw new UIAutomationException("File CM0044_ClickPlanParticipationLink can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File CM0044_ClickPlanParticipationLink.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File CM0044_ClickPlanParticipationLink can not be parsed.");
		}
			
	}
}
