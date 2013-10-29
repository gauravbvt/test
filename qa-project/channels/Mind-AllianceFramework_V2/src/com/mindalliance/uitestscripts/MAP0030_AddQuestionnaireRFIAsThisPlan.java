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
import com.mindalliance.pages.DomainPlanPage;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;
import com.mindalliance.pages.PlanPage;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test Case ID: MAP0030_AddQuestionnaireRFIAsThisPlan
 * Summary: Verify that Questionnaire name can be changed
 * @author afour
 */
public class MAP0030_AddQuestionnaireRFIAsThisPlan extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAP0030_AddQuestionnaireRFIAsThisPlan";
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

			DataController dataController=new DataController();
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
	public void testMAP0030_AddQuestionnaireRFIAsThisPlan() throws UIAutomationException, IOException{
		try {
			stepNo++;
			description="URL Entered";
			// Enter URL of Channels
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
				
			// Click on 'All Surveys' under 'Participations' pop up menu
 			stepNo++;
			description="All Surveys";
			PlanPage planPage=new PlanPage();
			planPage.clickPopupMenu(testData.get("Participations"));
			planPage.clickSubmenu(testData.get("AllSurveys"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Click on 'Questionnaire' tab
 			stepNo++;
			description="Questionnaire Tab";
			planPage.clickQuestionnaireTab();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Add Questionnaire
 			stepNo++;
			description="Add Questionnaire";
			planPage.clickAddNewQuestionnaire();
			// Edit questionnaire name
			planPage.enterQuestionnaireName(testData.get("Questionnaire1"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Select 'THIS PLAN' option from 'Questionnaire from surveys about'
 			stepNo++;
			description="Questionnaire - This Template";
			planPage.selectOptionFromQuestionnaireFromSurveysAbout(testData.get("ThisPlan"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Verify Questionnaire is added
 			stepNo++;
			description="Questionnaire - This Plan Added";
			planPage.verifyQuestionnaireIsAdded(testData.get("Questionnaire1"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Delete questionnaire added
 			stepNo++;
			description="Delete Questionnaire";
			planPage.deleteQuestionnaire();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Close 'All Surveys' window
 			stepNo++;
			description="Closed All Surveys Window";
			planPage.closeAllSurveysWindow();
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
     * Loads Test Data for MAP0030_AddQuestionnaireRFIAsThisPlan
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
			File MAP0030_AddQuestionnaireRFIAsThisPlan=new File(path + "MAP0030_AddQuestionnaireRFIAsThisPlan.xml");
			
			Document docMAP0030_AddQuestionnaireRFIAsThisPlan=db.parse(MAP0030_AddQuestionnaireRFIAsThisPlan);
			Element eleMAP0030_AddQuestionnaireRFIAsThisPlan=docMAP0030_AddQuestionnaireRFIAsThisPlan.getDocumentElement();
	              
	        Element oXmlEleMAP0030_AddQuestionnaireRFIAsThisPlan = (Element) eleMAP0030_AddQuestionnaireRFIAsThisPlan;
	    	           
	        this.testData.put("Participations", oXmlEleMAP0030_AddQuestionnaireRFIAsThisPlan.getElementsByTagName("participations").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AllSurveys", oXmlEleMAP0030_AddQuestionnaireRFIAsThisPlan.getElementsByTagName("allSurveys").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Questionnaire", oXmlEleMAP0030_AddQuestionnaireRFIAsThisPlan.getElementsByTagName("questionnaire").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL", oXmlEleMAP0030_AddQuestionnaireRFIAsThisPlan.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAP0030_AddQuestionnaireRFIAsThisPlan.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Questionnaire1",oXmlEleMAP0030_AddQuestionnaireRFIAsThisPlan.getElementsByTagName("questionnaire1").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ThisPlan",oXmlEleMAP0030_AddQuestionnaireRFIAsThisPlan.getElementsByTagName("thisPlan").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAP0030_AddQuestionnaireRFIAsThisPlan.xml can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAP0030_AddQuestionnaireRFIAsThisPlan.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAP0030_AddQuestionnaireRFIAsThisPlan.xml can not be parsed.");
		}
	}
}
