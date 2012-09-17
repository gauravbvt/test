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
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;

public class MAV0153_viewSocialPanelHideBroadCast extends TestCase{

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
	public void testMAV0153_viewSocialPanelHideBroadCast() throws UIAutomationException, InterruptedException {
		try {
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
			    
			// Login page
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
									
			// Click on 'Messages' tab under 'Collaboration Panel'
			HomePage homePage=new HomePage();
			homePage.clickMessagesTabInSocialPanel();
		
			// Click on 'hide broadcast' link and 'show all messages' link
			homePage.clickHideBroadcastsInSocialPanel();
											
			// Sign Out from 'Home' page
			HeaderController headerController=new HeaderController();
			headerController.signOut();
		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0153_viewSocialPanelHideBroadCast");
		
			//Sign out from home page
			HeaderController headerController=new HeaderController();
			headerController.signOut();
			
			//Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	/**
     * Loads Test Data for MAV0153_viewSocialPanelHideBroadCast.
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
			File MAV0153_viewSocialPanelHideBroadCast=new File(path + "MAV0153_viewSocialPanelHideBroadCast.xml");
			
			Document docMAV0153_viewSocialPanelHideBroadCast=db.parse(MAV0153_viewSocialPanelHideBroadCast);
			Element eleMAV0153_viewSocialPanelHideBroadCast=docMAV0153_viewSocialPanelHideBroadCast.getDocumentElement();
	              
	        Element oXmlEleMAV0153_viewSocialPanelHideBroadCast = (Element) eleMAV0153_viewSocialPanelHideBroadCast;
	     	        
			this.testData.put("ChannelsURL",oXmlEleMAV0153_viewSocialPanelHideBroadCast.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0153_viewSocialPanelHideBroadCast.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0153_viewSocialPanelHideBroadCast not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0153_viewSocialPanelHideBroadCast.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0153_viewSocialPanelHideBroadCast can not be parsed.");
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
