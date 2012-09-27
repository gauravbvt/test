package com.mindalliance.uitestscripts;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
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
import com.mindalliance.pages.LoginPage;

/**
 * Test Case Id: MAV0003_logoutOnPlanPage
 * Summary: Verify that the page with title 'Channels Information Sharing Planning' renders
 * @author afour
 *
 */
public class MAV0003_SignoutOnHomePage extends TestCase{
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
	public void testMAV0003_SignoutOnHomePage() throws UIAutomationException, InterruptedException {
		try {
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
			    
			// Login page
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
							
			// Sign Out from 'Home' page
			HeaderController headerController=new HeaderController();
			headerController.signOut();
			
		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0003_SignoutOnHomePage");
		
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOut();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	/**
     * Loads Test Data for MAV0003_SignoutOnHomePage.
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
			File MAV0003_SignoutOnHomePage=new File(path + "MAV0003_SignoutOnHomePage.xml");
			
			Document docMAV0003_SignoutOnHomePage=db.parse(MAV0003_SignoutOnHomePage);
			Element eleMAV0003_SignoutOnHomePage=docMAV0003_SignoutOnHomePage.getDocumentElement();
	              
	        Element oXmlEleMAV0003_SignoutOnHomePage = (Element) eleMAV0003_SignoutOnHomePage;
	     	        
			this.testData.put("ChannelsURL",oXmlEleMAV0003_SignoutOnHomePage.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0003_SignoutOnHomePage.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0003_SignoutOnHomePage not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0003_SignoutOnHomePage.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0003_SignoutOnHomePage can not be parsed.");
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