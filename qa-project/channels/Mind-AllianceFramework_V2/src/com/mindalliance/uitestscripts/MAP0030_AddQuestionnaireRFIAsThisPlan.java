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
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;
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

	@Before
	protected void setUp(){
		try{
			if (GlobalVariables.configuration == null){
					GlobalVariables.configuration = Configuration.getConfigurationObject();
				
			}
			if(GlobalVariables.configuration.getAttrSearchList() == null){
				new ElementController();
			}
			
			// Creates Browser instance
			BrowserController browserController= new BrowserController();
			browserController.initializeDriver();
			
			// Loads Test Data
			loadTestData();
		}
		catch(UIAutomationException ue){
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
		}
	}
	
	@Test
	public void testMAP0030_AddQuestionnaireRFIAsThisPlan() throws UIAutomationException{
		try {
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
			    
			// Login page
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
									
			// Plan Page
			HomePage homePage=new HomePage();
			homePage.clickCollaborationPlanLink();	
			
			// Close Plan Map window
			PlanPage planPage=new PlanPage();
			planPage.closePlanMap();
				
			// Click on 'All Surveys' under 'Participations' pop up menu
			planPage.clickPopupMenu(testData.get("Participations"));
			planPage.clickSubmenu(testData.get("AllSurveys"));
			
			// Click on 'Questionnaire' tab
			planPage.clickQuestionnaireTab();
			
			// Add Questionnaire
			planPage.clickAddNewQuestionnaire();
			
			// Edit questionnaire name
			planPage.enterQuestionnaireName(testData.get("Questionnaire1"));
			
			// Select 'THIS PLAN' option from 'Questionnaire from surveys about'
			planPage.selectOptionFromQuestionnaireFromSurveysAbout(testData.get("ThisPlan"));
			
			// Verify Questionnaire is added
			planPage.verifyQuestionnaireIsAdded(testData.get("Questionnaire1"));
			
			// Delete questionnaire added
			planPage.deleteQuestionnaire();
			
			// Close 'All Surveys' window
			planPage.closeAllSurveysWindow();
			
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();

		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAP0030_AddQuestionnaireRFIAsThisPlan");
		
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	/**
     * Loads Test Data for MAP0030_AddQuestionnaireRFIAsThisPlan
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException
	{
		try{
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	
	@After
	protected void tearDown(){
		if(GlobalVariables.configuration.getWebDriver()!=null){
			GlobalVariables.configuration.getWebDriver().quit();
		}
	}
}
