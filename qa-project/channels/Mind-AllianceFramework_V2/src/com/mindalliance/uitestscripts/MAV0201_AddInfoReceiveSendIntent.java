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

public class MAV0201_AddInfoReceiveSendIntent extends TestCase{

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
	public void testMAV0201_AddInfoReceiveSendIntent() throws UIAutomationException {
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
			
			// Add New Segment
			PlanPage planPage=new PlanPage();
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
			
			// Verify Intent dropdown list in sends panel
			planPage.verifyIntentDropdownInSendPanel(testData.get("Unspecified"),testData.get("Alarm"), testData.get("Announcement"),testData.get("Command"), testData.get("Feedback"), testData.get("Report"));
			
			// Click on 'Show Advanced Form' in sends panel
			planPage.clickShowAdvancedFormInSendsPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
						
			// Click on 'Add' in receives panel
			planPage.clickAddInReceivesPanel();
						
			// Click on 'Show advanced form' in receives panel
			planPage.clickShowAdvancedFormInReceivesPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
						
			// Verify Intent dropdown list in receives panel					
			planPage.verifyIntentDropdownInReceivesPanel(testData.get("Unspecified"),testData.get("Alarm"), testData.get("Announcement"),testData.get("Command"), testData.get("Feedback"), testData.get("Report"));
			
			// Remove This segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
		
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
		}
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0201_AddInfoReceiveSendIntent");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAV0201_AddInfoReceiveSendIntent.
     * @return void
     * @param  void
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAV0201_AddInfoReceiveSendIntent=new File(path + "MAV0201_AddInfoReceiveSendIntent.xml");
			
			Document docMAV0201_AddInfoReceiveSendIntent=db.parse(MAV0201_AddInfoReceiveSendIntent);
			Element eleMAV0201_AddInfoReceiveSendIntent=docMAV0201_AddInfoReceiveSendIntent.getDocumentElement();
	              
	        Element oXmlEleMAV0201_AddInfoReceiveSendIntent = (Element) eleMAV0201_AddInfoReceiveSendIntent;
	        
	        this.testData.put("ChannelsURL",oXmlEleMAV0201_AddInfoReceiveSendIntent.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0201_AddInfoReceiveSendIntent.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowSimpleFormText",oXmlEleMAV0201_AddInfoReceiveSendIntent.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0201_AddInfoReceiveSendIntent.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0201_AddInfoReceiveSendIntent.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Actions",oXmlEleMAV0201_AddInfoReceiveSendIntent.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AddNewSegment",oXmlEleMAV0201_AddInfoReceiveSendIntent.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAV0201_AddInfoReceiveSendIntent.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SendsInformation",oXmlEleMAV0201_AddInfoReceiveSendIntent.getElementsByTagName("sendsInformation").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ReceivesInformation",oXmlEleMAV0201_AddInfoReceiveSendIntent.getElementsByTagName("receivesInformation").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Unspecified",oXmlEleMAV0201_AddInfoReceiveSendIntent.getElementsByTagName("unspecified").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Alarm",oXmlEleMAV0201_AddInfoReceiveSendIntent.getElementsByTagName("alarm").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Announcement",oXmlEleMAV0201_AddInfoReceiveSendIntent.getElementsByTagName("announcement").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Command",oXmlEleMAV0201_AddInfoReceiveSendIntent.getElementsByTagName("command").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Feedback",oXmlEleMAV0201_AddInfoReceiveSendIntent.getElementsByTagName("feedback").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Report",oXmlEleMAV0201_AddInfoReceiveSendIntent.getElementsByTagName("report").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0201_AddInfoReceiveSendIntent not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0201_AddInfoReceiveSendIntent.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0201_AddInfoReceiveSendIntent can not be parsed.");
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