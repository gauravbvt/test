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

public class MAP0007_addOrganizationsDetails extends TestCase{
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
		catch(UIAutomationException ue)	{
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
		}
	}
	
	@Test
	public void testMAP0007_addOrganizationsDetails() throws UIAutomationException{
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
			
			// Click on 'Organizations In Scope' under 'Scoping' pop up menu
			planPage.clickPopupMenu(testData.get("Scoping"));
			planPage.clickSubmenu(testData.get("OrganizationsInScope"));
			
			// Enter organization name
			planPage.enterOrganizationName(testData.get("Organization2"));
			
			// Click on organization enetered
			planPage.clickOrganizationName(testData.get("Organization2"));
			
			// Enter description of actual organization
			planPage.enterDescriptionInActualOrganization(testData.get("Organization2Description"));
			
			// Close 'organization 2' window
			planPage.closeActualOrganizationWindow();
			
			// Close Organization Window
			planPage.closeOrganiztionWindow();
								
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();

		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAP0007_addOrganizationsDetails");
		
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	

	
	/**
     * Loads Test Data for MAP0007_addOrganizationsDetails.
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
			File MAP0007_addOrganizationsDetails=new File(path + "MAP0007_addOrganizationsDetails.xml");
			
			Document docMAP0007_addOrganizationsDetails=db.parse(MAP0007_addOrganizationsDetails);
			Element eleMAP0007_addOrganizationsDetails=docMAP0007_addOrganizationsDetails.getDocumentElement();
	              
	        Element oXmlEleMAP0007_addOrganizationsDetails = (Element) eleMAP0007_addOrganizationsDetails;
	    	           
	        this.testData.put("Scoping", oXmlEleMAP0007_addOrganizationsDetails.getElementsByTagName("scoping").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("OrganizationsInScope", oXmlEleMAP0007_addOrganizationsDetails.getElementsByTagName("organizationsInScope").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL", oXmlEleMAP0007_addOrganizationsDetails.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAP0007_addOrganizationsDetails.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Organization2",oXmlEleMAP0007_addOrganizationsDetails.getElementsByTagName("organization2").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Organization2Description",oXmlEleMAP0007_addOrganizationsDetails.getElementsByTagName("organization2Description").item(0).getChildNodes().item(0).getNodeValue());
	        
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAP0007_addOrganizationsDetails.xml can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAP0007_addOrganizationsDetails.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAP0007_addOrganizationsDetails.xml can not be parsed.");
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
