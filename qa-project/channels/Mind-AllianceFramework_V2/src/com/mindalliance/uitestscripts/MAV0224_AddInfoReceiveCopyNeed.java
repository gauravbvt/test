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

public class MAV0224_AddInfoReceiveCopyNeed extends TestCase{


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
	public void testMAV0224_AddInfoReceiveCopyNeed() throws UIAutomationException {
		try{
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
			
			// Add New Segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));

			// Enter segment name
			planPage.enterSegmentName(testData.get("SegmentForAddInfoReceiveCopyNeed"));
			
			// Close segment window
			planPage.closeSegmentWindow();
			
			// Click on 'strench up' form icon
			planPage.clickStrenchUpForm();
			
			// Click on 'Add' button under 'Receives' panel
			planPage.clickAddInReceivesPanel();
			 
			// Click on 'Copy Need' under 'Actions' pop up menu
			planPage.clickPopupMenu(testData.get("ActionsInReceivesPanel"));
			planPage.clickSubmenu(testData.get("CopyNeedInReceivesPanel"));	
			
			// Remove This segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
				
			// Sign Out from 'Home' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
		}
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0224_AddInfoReceiveCopyNeed");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAV0224_AddInfoReceiveCopyNeed.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAV0224_AddInfoReceiveCopyNeed=new File(path + "MAV0224_AddInfoReceiveCopyNeed.xml");
			
			Document docMAV0224_AddInfoReceiveCopyNeed=db.parse(MAV0224_AddInfoReceiveCopyNeed);
			Element eleMAV0224_AddInfoReceiveCopyNeed=docMAV0224_AddInfoReceiveCopyNeed.getDocumentElement();
	              
	        Element oXmlEleMAV0224_AddInfoReceiveCopyNeed = (Element) eleMAV0224_AddInfoReceiveCopyNeed;
	        
	               	
	        this.testData.put("ChannelsURL",oXmlEleMAV0224_AddInfoReceiveCopyNeed.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0224_AddInfoReceiveCopyNeed.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ActionsInReceivesPanel",oXmlEleMAV0224_AddInfoReceiveCopyNeed.getElementsByTagName("actionsInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("CopyNeedInReceivesPanel",oXmlEleMAV0224_AddInfoReceiveCopyNeed.getElementsByTagName("copyNeedInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
		    this.testData.put("ChannelsURL",oXmlEleMAV0224_AddInfoReceiveCopyNeed.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0224_AddInfoReceiveCopyNeed.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	
			this.testData.put("Actions",oXmlEleMAV0224_AddInfoReceiveCopyNeed.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AddNewSegment",oXmlEleMAV0224_AddInfoReceiveCopyNeed.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAV0224_AddInfoReceiveCopyNeed.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForAddInfoReceiveCopyNeed",oXmlEleMAV0224_AddInfoReceiveCopyNeed.getElementsByTagName("segmentForAddInfoReceiveCopyNeed").item(0).getChildNodes().item(0).getNodeValue());
			
		
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0224_AddInfoReceiveCopyNeed not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0224_AddInfoReceiveCopyNeed.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0224_AddInfoReceiveCopyNeed can not be parsed.");
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