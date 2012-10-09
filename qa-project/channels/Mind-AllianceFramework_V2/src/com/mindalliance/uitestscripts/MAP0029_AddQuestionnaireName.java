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
 * Test Case ID: MAP0029_AddQuestionnaireName
 * Summary: Verify that Questionnaire name can be changed
 * @author afour
 *
 */
public class MAP0029_AddQuestionnaireName extends TestCase {

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
	public void testMAP0029_AddQuestionnaireName() throws UIAutomationException{
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
			Reporting.getScreenShot("MAP0029_AddQuestionnaireName");
		
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	/**
     * Loads Test Data for MAP0029_AddQuestionnaireName
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
			File MAP0029_AddQuestionnaireName=new File(path + "MAP0029_AddQuestionnaireName.xml");
			
			Document docMAP0029_AddQuestionnaireName=db.parse(MAP0029_AddQuestionnaireName);
			Element eleMAP0029_AddQuestionnaireName=docMAP0029_AddQuestionnaireName.getDocumentElement();
	              
	        Element oXmlEleMAP0029_AddQuestionnaireName = (Element) eleMAP0029_AddQuestionnaireName;
	    	           
	        this.testData.put("Participations", oXmlEleMAP0029_AddQuestionnaireName.getElementsByTagName("participations").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AllSurveys", oXmlEleMAP0029_AddQuestionnaireName.getElementsByTagName("allSurveys").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Questionnaire", oXmlEleMAP0029_AddQuestionnaireName.getElementsByTagName("questionnaire").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL", oXmlEleMAP0029_AddQuestionnaireName.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAP0029_AddQuestionnaireName.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Questionnaire1",oXmlEleMAP0029_AddQuestionnaireName.getElementsByTagName("questionnaire1").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAP0029_AddQuestionnaireName.xml can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAP0029_AddQuestionnaireName.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAP0029_AddQuestionnaireName.xml can not be parsed.");
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
