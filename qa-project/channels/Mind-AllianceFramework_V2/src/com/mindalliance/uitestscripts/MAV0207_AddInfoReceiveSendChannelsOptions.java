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

public class MAV0207_AddInfoReceiveSendChannelsOptions extends TestCase{

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
	public void testMAV0207_AddInfoReceiveSendChannelsOptions() throws UIAutomationException {
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
			
			// Close segment window
			planPage.closeSegmentWindow();
			
			// Click on 'strench Up forms' icon 
			planPage.clickStrenchUpForm();
		
			// Click on 'Add' in sends panel
			planPage.clickAddInSendsPanel();
			
			// Click on 'Show Advanced Form' in sends panel
			planPage.clickShowAdvancedFormInSendsPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
			
			// Click on 'Channels' dropdown in sends panel
			planPage.clickChannelsDropdownInSendsPanel(testData.get("Cell"), testData.get("ConferenceCall"), testData.get("Courier"), testData.get("Email"),
			testData.get("FaceToFace"), testData.get("Fax"), testData.get("IM"),testData.get("Landline"), testData.get("Mail"), testData.get("Meeting"), testData.get("NotificationSystem"), 
			testData.get("OnlineChat"),testData.get("Pager"),testData.get("PASystem"),testData.get("Phone"),testData.get("Radio"),testData.get("Television"),testData.get("TwoWayRadio"),testData.get("NewMedium"));
			
			// Select 'New Medium' from dropdown
			planPage.selectOptionFromChannelsInSend(testData.get("NewMedium"));
			
			// Click on 'Remove Info Sharing Capability' under 'Actions' pop up menu
			planPage.clickPopupMenu(testData.get("ActionsInSendsPanel"));
			planPage.clickSubmenu(testData.get("RemoveInfoSharingCapabilityInSendsPanel"));	
			
			// Click on 'Add' in receives panel
			planPage.clickAddInReceivesPanel();
						
			// Click on 'Show advanced form' in receives panel
			planPage.clickShowAdvancedFormInReceivesPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
						
			// Click on 'Channels' dropdown in receives panel
			planPage.clickChannelsDropdownInReceivesPanel(testData.get("Cell"), testData.get("ConferenceCall"), testData.get("Courier"), testData.get("Email"),
			testData.get("FaceToFace"), testData.get("Fax"), testData.get("IM"),testData.get("Landline"), testData.get("Mail"), testData.get("Meeting"), testData.get("NotificationSystem"), 
			testData.get("OnlineChat"),testData.get("Pager"),testData.get("PASystem"),testData.get("Phone"),testData.get("Radio"),testData.get("Television"),testData.get("TwoWayRadio"),testData.get("NewMedium"));
			
			// Select 'New Medium' from dropdown
			planPage.selectOptionFromChannelsInReceives(testData.get("NewMedium"));
			
			// Click on 'Remove Info Need' under 'Actions' pop up menu
			planPage.clickPopupMenu(testData.get("ActionsInReceivesPanel"));
			planPage.clickSubmenu(testData.get("RemoveInfoNeedInReceivesPanel"));	
			
			// Remove This segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
		
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
		}
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0207_AddInfoReceiveSendChannelsOptions");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAV0207_AddInfoReceiveSendChannelsOptions.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAV0207_AddInfoReceiveSendChannelsOptions=new File(path + "MAV0207_AddInfoReceiveSendChannelsOptions.xml");
			
			Document docMAV0207_AddInfoReceiveSendChannelsOptions=db.parse(MAV0207_AddInfoReceiveSendChannelsOptions);
			Element eleMAV0207_AddInfoReceiveSendChannelsOptions=docMAV0207_AddInfoReceiveSendChannelsOptions.getDocumentElement();
	              
	        Element oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions = (Element) eleMAV0207_AddInfoReceiveSendChannelsOptions;
	        
	        this.testData.put("ChannelsURL",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowInTask",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("showInTask").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Details",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("details").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowSimpleFormText",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Unspecified",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("unspecified").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Actions",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AddNewSegment",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("Cell",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("cell").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ConferenceCall",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("conferenceCall").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Courier",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("courier").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Email",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("email").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("FaceToFace",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("faceToFace").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Fax",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("fax").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("IM",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("IM").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Landline",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("landline").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Mail",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("mail").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Meeting",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("meeting").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("NotificationSystem",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("notificationSystem").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("OnlineChat",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("onlineChat").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Pager",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("pager").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("PASystem",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("PASystem").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Phone",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("phone").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Radio",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("radio").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Television",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("television").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TwoWayRadio",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("twoWayRadio").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("NewMedium",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("newMedium").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("ActionsInReceivesPanel",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("actionsInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveInfoNeedInReceivesPanel",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("removeInfoNeedInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInSendsPanel",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("actionsInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveInfoSharingCapabilityInSendsPanel",oXmlEleMAV0207_AddInfoReceiveSendChannelsOptions.getElementsByTagName("removeInfoSharingCapabilityInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0207_AddInfoReceiveSendChannelsOptions not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0207_AddInfoReceiveSendChannelsOptions.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0207_AddInfoReceiveSendChannelsOptions can not be parsed.");
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