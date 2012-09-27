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

public class MAV0205_AddInfoReceiveSendButOnlyIfOption extends TestCase{
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
	public void testMAV0205_AddInfoReceiveSendButOnlyIfOption() throws UIAutomationException {
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
			
			// Click on radio button of 'but only if' in sends panel 
			planPage.clickCheckboxOfbutOnlyIfInSendsPanel(testData.get("Unspecified"),testData.get("DifferentLocations"),testData.get("DifferentOrganizations"),
					testData.get("DifferentOverallOrganizations"),testData.get("SameLocation"),testData.get("SameOrganization"),
					testData.get("SameOrganizationAndLocation"),testData.get("SameOverallOrganization"),testData.get("ToASupervisor"),
					testData.get("ToSelf"),testData.get("ToSomeOneElse")
					);
			
			// Select 'in the same location' option from dropdown
			planPage.selectOptionFromButOnlyIfDrodownInSendsPanel(testData.get("SameLocation"));
			
			
			// Click on 'Add' in receives panel
			planPage.clickAddInReceivesPanel();
						
			// Click on 'Show advanced form' in receives panel
			planPage.clickShowAdvancedFormInReceivesPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
						
			// Clicks on radio button of ' but only if' in receives panel
			planPage.clickCheckboxOfbutOnlyIfInReceivesPanel(testData.get("Unspecified"),testData.get("DifferentLocations"),testData.get("DifferentOrganizations"),
					testData.get("DifferentOverallOrganizations"),testData.get("SameLocation"),testData.get("SameOrganization"),
					testData.get("SameOrganizationAndLocation"),testData.get("SameOverallOrganization"),testData.get("ToASupervisor"),
					testData.get("ToSelf"),testData.get("ToSomeOneElse")
					);
			
			// Select 'in the same location' option from dropdown
			planPage.selectOptionFromButOnlyIfDrodownInReceivesPanel(testData.get("SameLocation"));
			
			// Remove This segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
		
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
		}
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0205_AddInfoReceiveSendButOnlyIfOption");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAV0205_AddInfoReceiveSendButOnlyIfOption.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAV0205_AddInfoReceiveSendButOnlyIfOption=new File(path + "MAV0205_AddInfoReceiveSendButOnlyIfOption.xml");
			
			Document docMAV0205_AddInfoReceiveSendButOnlyIfOption=db.parse(MAV0205_AddInfoReceiveSendButOnlyIfOption);
			Element eleMAV0205_AddInfoReceiveSendButOnlyIfOption=docMAV0205_AddInfoReceiveSendButOnlyIfOption.getDocumentElement();
	              
	        Element oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption = (Element) eleMAV0205_AddInfoReceiveSendButOnlyIfOption;
	        
	        this.testData.put("ChannelsURL",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowInTask",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("showInTask").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Details",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("details").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowSimpleFormText",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Unspecified",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("unspecified").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Actions",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AddNewSegment",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("Unspecified",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("unspecified").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("DifferentLocations",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("differentLocations").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("DifferentOrganizations",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("differentOrganizations").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("DifferentOverallOrganizations",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("differentOverallOrganizations").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("SameLocation",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("sameLocation").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SameOrganization",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("sameOrganization").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SameOrganizationAndLocation",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("sameOrganizationAndLocation").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SameOverallOrganization",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("sameOverallOrganization").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("ToASupervisor",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("toASupervisor").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ToSelf",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("toSelf").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ToSomeOneElse",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("toSomeOneElse").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0205_AddInfoReceiveSendButOnlyIfOption not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0205_AddInfoReceiveSendButOnlyIfOption.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0205_AddInfoReceiveSendButOnlyIfOption can not be parsed.");
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