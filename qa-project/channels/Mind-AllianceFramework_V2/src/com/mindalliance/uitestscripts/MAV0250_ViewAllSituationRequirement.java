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

public class MAV0250_ViewAllSituationRequirement extends TestCase{

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
			
			//Creates Browser instance
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
	public void testMAV0250_ViewAllSituationRequirement() throws UIAutomationException, InterruptedException, IOException, ParserConfigurationException, SAXException {
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
							
			// Click on 'Plan Requirements' under 'Scoping' pop up menu
			planPage.clickPopupMenu(testData.get("Scoping"));
			planPage.clickSubmenu(testData.get("Planrequirement"));
			
			// Clicks on 'Satisfactions' tab in plan requirements
			planPage.clickSatisfactionsTabInPlanRequirement(testData.get("SatisfactionsTab"));
								
			// Click on 'Situation' dropdown
			planPage.selectOptionFromSituationDropdownInPlanRequirement(testData.get("DuringBeforeAfter"));
			
			// Close Requirements window
			planPage.closeRequirementWindow();
			
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();

		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0250_ViewAllSituationRequirement");
		
			//Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			//Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}

	
	/**
     * Loads Test Data for MAV0250_ViewAllSituationRequirement.
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
			File MAV0250_ViewAllSituationRequirement=new File(path + "MAV0250_ViewAllSituationRequirement.xml");
			
			Document docMAV0250_ViewAllSituationRequirement=db.parse(MAV0250_ViewAllSituationRequirement);
			Element eleMAV0250_ViewAllSituationRequirement=docMAV0250_ViewAllSituationRequirement.getDocumentElement();
	              
	        Element oXmlEleMAV0250_ViewAllSituationRequirement = (Element) eleMAV0250_ViewAllSituationRequirement;
	       	
	        this.testData.put("Scoping", oXmlEleMAV0250_ViewAllSituationRequirement.getElementsByTagName("scoping").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Planrequirement",oXmlEleMAV0250_ViewAllSituationRequirement.getElementsByTagName("planrequirement").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL",oXmlEleMAV0250_ViewAllSituationRequirement.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAV0250_ViewAllSituationRequirement.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("SatisfactionsTab",oXmlEleMAV0250_ViewAllSituationRequirement.getElementsByTagName("satisfactionsTab").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("DuringBeforeAfter",oXmlEleMAV0250_ViewAllSituationRequirement.getElementsByTagName("duringBeforeAfter").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0250_ViewAllSituationRequirement.xml not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0250_ViewAllSituationRequirement.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0250_ViewAllSituationRequirement.xml can not be parsed.");
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