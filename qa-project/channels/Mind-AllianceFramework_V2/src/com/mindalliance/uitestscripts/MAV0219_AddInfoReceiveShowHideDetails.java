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

public class MAV0219_AddInfoReceiveShowHideDetails extends TestCase{

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
	public void testMAV0219_AddInfoReceiveShowHideDetails() throws UIAutomationException {
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
			
			// Enter Segment name
			planPage.enterSegmentName(testData.get("SegmentForAddInfoRecieveShowhideDetails"));
			
			// Close segment window
			planPage.closeSegmentWindow();
			
			// Click on 'Add' in Receives panel
			planPage.clickAddInReceivesPanel();			
			
			// Click on 'Hide details' under 'show' pop up menu in receives panel
			planPage.clickPopupMenu(testData.get("ShowInreceivesPanel"));
			planPage.clickSubmenu(testData.get("HideDetailsInReceivesPanel"));
			
			// Click on 'Details' under 'show' pop up menu in receives panel
			planPage.clickPopupMenu(testData.get("ShowInreceivesPanel"));
			planPage.clickSubmenu(testData.get("HideDetailsInReceivesPanel"));
						
			// Click on Remove this segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));					
					
			//Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
		}
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0219_AddInfoReceiveShowHideDetails");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAV0219_AddInfoReceiveShowHideDetails.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAV0219_AddInfoReceiveShowHideDetails=new File(path + "MAV0219_AddInfoReceiveShowHideDetails.xml");
			
			Document docMAV0219_AddInfoReceiveShowHideDetails=db.parse(MAV0219_AddInfoReceiveShowHideDetails);
			Element eleMAV0219_AddInfoReceiveShowHideDetails=docMAV0219_AddInfoReceiveShowHideDetails.getDocumentElement();
	        Element oXmlEleMAV0219_AddInfoReceiveShowHideDetails = (Element) eleMAV0219_AddInfoReceiveShowHideDetails;
	        
	               	
	        this.testData.put("Actions",oXmlEleMAV0219_AddInfoReceiveShowHideDetails.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	       	this.testData.put("ChannelsURL",oXmlEleMAV0219_AddInfoReceiveShowHideDetails.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0219_AddInfoReceiveShowHideDetails.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		    this.testData.put("AddNewSegment",oXmlEleMAV0219_AddInfoReceiveShowHideDetails.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("RemoveThisSegment",oXmlEleMAV0219_AddInfoReceiveShowHideDetails.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ShowInreceivesPanel",oXmlEleMAV0219_AddInfoReceiveShowHideDetails.getElementsByTagName("showInreceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
	    	this.testData.put("SegmentForAddInfoRecieveShowhideDetails",oXmlEleMAV0219_AddInfoReceiveShowHideDetails.getElementsByTagName("segmentForAddInfoRecieveShowhideDetails").item(0).getChildNodes().item(0).getNodeValue());
	    	this.testData.put("HideDetailsInReceivesPanel",oXmlEleMAV0219_AddInfoReceiveShowHideDetails.getElementsByTagName("hideDetailsInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
	    	
	    	
	    	
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0219_AddInfoReceiveShowHideDetails not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0219_AddInfoReceiveShowHideDetails.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0219_AddInfoReceiveShowHideDetails can not be parsed.");
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