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

public class MAV0212_AddInfoReceiveSendIssueSeverity extends TestCase{

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
	public void testMAV0212_AddInfoReceiveSendIssueSeverity() throws UIAutomationException {
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
			
			// Click on 'Show Advanced Form' link
			planPage.clickShowAdvancedFormInSendsPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
				
			// Click on 'New Issue' under 'Actions' pop up menu in Sends panel
			planPage.clickPopupMenu(testData.get("ActionsInSendsPanel"));
			planPage.clickSubmenu(testData.get("NewIssueInSendsPanel"));	
			
			// Verify dropdown of severity in issue
			planPage.verifySeverityDropdownInIssueInSends(testData.get("Minor"), testData.get("Major"),testData.get("Severe"), testData.get("Extreme"));
					
			// Click on 'Add' in receives panel
			planPage.clickAddInReceivesPanel();
			
		
			// Click on 'New Issue' under 'Actions' pop up menu in Sends panel
			planPage.clickPopupMenu(testData.get("ActionsInReceivesPanel"));
			planPage.clickSubmenu(testData.get("NewIssueInReceivesPanel"));	
			
			// Verify dropdown of severity in issue
			planPage.verifySeverityDropdownInIssueInReceives(testData.get("Minor"), testData.get("Major"),testData.get("Severe"), testData.get("Extreme"));
										
			// Remove This segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
		
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
		}
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0212_AddInfoReceiveSendIssueSeverity");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAV0212_AddInfoReceiveSendIssueSeverity.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAV0212_AddInfoReceiveSendIssueSeverity=new File(path + "MAV0212_AddInfoReceiveSendIssueSeverity.xml");
			
			Document docMAV0212_AddInfoReceiveSendIssueSeverity=db.parse(MAV0212_AddInfoReceiveSendIssueSeverity);
			Element eleMAV0212_AddInfoReceiveSendIssueSeverity=docMAV0212_AddInfoReceiveSendIssueSeverity.getDocumentElement();
	              
	        Element oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity = (Element) eleMAV0212_AddInfoReceiveSendIssueSeverity;
	        
	        this.testData.put("ChannelsURL",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowSimpleFormText",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Unspecified",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("unspecified").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Actions",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AddNewSegment",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("ActionsInReceivesPanel",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("actionsInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInSendsPanel",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("actionsInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("NewIssueInReceivesPanel",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("newIssueInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("NewIssueInSendsPanel",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("newIssueInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			
			
			this.testData.put("Minor",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("minor").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Major",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("major").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Severe",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("severe").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Extreme",oXmlEleMAV0212_AddInfoReceiveSendIssueSeverity.getElementsByTagName("extreme").item(0).getChildNodes().item(0).getNodeValue());
			
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0212_AddInfoReceiveSendIssueSeverity not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0212_AddInfoReceiveSendIssueSeverity.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0212_AddInfoReceiveSendIssueSeverity can not be parsed.");
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