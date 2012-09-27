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
import com.mindalliance.pages.ChannelsAdmin;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test case ID: MAP0004_DeleteUser
 * 	   Summary: Verify that admin is able to delete added user
 * @author afour
 * 
 */
public class MAP0004_DeleteUser extends TestCase{
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
	public void testMAP0004_DeleteUser() throws UIAutomationException{
		try {
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
			    
			// Login page
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
							
			// Click on Channels Admin
			HomePage homePage=new HomePage();
			homePage.clickChannelsAdminLink();
				
			// Add user
			ChannelsAdmin channelsAdmin=new ChannelsAdmin();
			channelsAdmin.addUser(testData.get("User"));
					
			// Delete user
			channelsAdmin.deleteUser(testData.get("User"),testData.get("AddEmailOfUser"));
			
			// Sign Out from 'Admin' page
			HeaderController headerController=new HeaderController();
			headerController.signOutAdmin();
		} 
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAP0004_DeleteUser");
		
			//Sign out from Admin page
			HeaderController headerController=new HeaderController();
			headerController.signOutAdmin();
			
			//Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	/**
     * Loads Test Data for MAP0004_DeleteUser.
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
			File MAP0004_DeleteUser=new File(path + "MAP0004_DeleteUser.xml");
			
			Document docMAP0004_DeleteUser=db.parse(MAP0004_DeleteUser);
			Element eleMAP0004_DeleteUser=docMAP0004_DeleteUser.getDocumentElement();
	              
	        Element oXmlEleMAP0004_DeleteUser = (Element) eleMAP0004_DeleteUser;
	     	        
	        this.testData.put("User",oXmlEleMAP0004_DeleteUser.getElementsByTagName("user").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL",oXmlEleMAP0004_DeleteUser.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAP0004_DeleteUser.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddEmailOfUser",oXmlEleMAP0004_DeleteUser.getElementsByTagName("addEmailOfUser").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAP0004_DeleteUser not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAP0004_DeleteUser.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAP0004_DeleteUser can not be parsed.");
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
