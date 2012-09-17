package com.mindalliance.uitestscripts;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;

import junit.framework.TestCase;

/**
 * Test Case Id: MAV0005_logoutOnAdminPage
 * Summary: Verify that user is able to Logout from Admin Page
 * @author : AfourTech
 *
 */
public class MAV0005_logoutOnAdminPage extends TestCase{

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
	public void testMAV0005_logoutOnAdminPage() throws UIAutomationException, InterruptedException {
		try {
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
			    
			// Login page
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
						
			// Click on Chanels Settings Link
			HomePage homePage=new HomePage();
			homePage.clickChannelsAdminLink();
					
			// Sign Out from 'Admin' page
			HeaderController headerController=new HeaderController();
			headerController.signOutAdmin();
			
		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0005_logoutOnAdminPage");
	
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutAdmin();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	/**
     * Loads Test Data for MAV0005_logoutOnAdminPage.
     * @return void
     * @param  void
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
			File MAV0005_logoutOnAdminPage=new File(path + "MAV0005_logoutOnAdminPage.xml");
			
			Document docMAV0005_logoutOnAdminPage=db.parse(MAV0005_logoutOnAdminPage);
			Element eleMAV0005_logoutOnAdminPage=docMAV0005_logoutOnAdminPage.getDocumentElement();
	              
	        Element oXmlEleMAV0005_logoutOnAdminPage = (Element) eleMAV0005_logoutOnAdminPage;
	     	        
			this.testData.put("ChannelsURL",oXmlEleMAV0005_logoutOnAdminPage.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0005_logoutOnAdminPage.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0005_logoutOnAdminPage not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0005_logoutOnAdminPage.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0005_logoutOnAdminPage can not be parsed.");
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