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
import com.mindalliance.pages.PlanPage;

import junit.framework.TestCase;


/**
 * TestCase Id: MAV0103_viewHelpFormWithFeedback
 * Summary: Verify that Help Window with Feedback gets opened
 * @author afour
 */
public class MAV0103_viewHelpFormWithFeedback extends TestCase{


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
	public void testMAV0103_viewHelpFormWithFeedback() throws UIAutomationException{
		try{
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
			    
			// Login page
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
			
		    // Click on 'Collaboration Plan' link
		    HomePage homePage=new HomePage();
		    homePage.clickCollaborationPlanLink();
		    
		    // Close Plan Map window
 			PlanPage planPage=new PlanPage();
 			planPage.closePlanMap();
		    
		    // Click on 'Help' button
		    HeaderController headerController=new HeaderController();
		    headerController.clickHelpOnPlanPage();
		    
		    // Click on 'Send Feedback' button on 'Help' page
		    headerController.sendFeedbackOnHelp();
		    
		    // Switch to 'Plan Page'
		    headerController.switchToPlanPage();
		   
			// Sign Out from 'Plan' page
			headerController.signOutPlan();
			
		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0103_viewHelpFormWithFeedback");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAV0103_viewHelpFormWithFeedback.
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
			File MAV0103_viewHelpFormWithFeedback=new File(path + "MAV0103_viewHelpFormWithFeedback.xml");
			
			Document docMAV0103_viewHelpFormWithFeedback=db.parse(MAV0103_viewHelpFormWithFeedback);
			Element eleMAV0103_viewHelpFormWithFeedback=docMAV0103_viewHelpFormWithFeedback.getDocumentElement();
	              
	        Element oXmlEleMAV0103_viewHelpFormWithFeedback = (Element) eleMAV0103_viewHelpFormWithFeedback;
	                     	
	        
			this.testData.put("ChannelsURL",oXmlEleMAV0103_viewHelpFormWithFeedback.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0103_viewHelpFormWithFeedback.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0103_viewHelpFormWithFeedback not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0103_viewHelpFormWithFeedback.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0103_viewHelpFormWithFeedback can not be parsed.");
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
