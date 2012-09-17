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

public class MAV0210_AddInfoReceiveSendNewIssue extends TestCase{


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
	public void testMAV0210_AddInfoReceiveSendNewIssue() throws UIAutomationException {
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
			
			// Click on 'Show Advanced Form' link
			planPage.clickShowAdvancedFormInSendsPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
				
			// Click on 'New Issue' under 'Actions' pop up menu in Sends panel
			planPage.clickPopupMenu(testData.get("ActionsInSendsPanel"));
			planPage.clickSubmenu(testData.get("NewIssueInSendsPanel"));	
			
			// Click on 'Add' in receives panel
			planPage.clickAddInReceivesPanel();
			
			// Click on 'Show Advanced Form' link
			planPage.clickShowAdvancedFormInReceivesPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
			
			// Click on 'New Issue' under 'Actions' pop up menu in Sends panel
			planPage.clickPopupMenu(testData.get("ActionsInReceivesPanel"));
			planPage.clickSubmenu(testData.get("NewIssueInReceivesPanel"));	
				
			// Remove This segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
		
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
		}
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0210_AddInfoReceiveSendNewIssue");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAV0210_AddInfoReceiveSendNewIssue.
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
			File MAV0210_AddInfoReceiveSendNewIssue=new File(path + "MAV0210_AddInfoReceiveSendNewIssue.xml");
			
			Document docMAV0210_AddInfoReceiveSendNewIssue=db.parse(MAV0210_AddInfoReceiveSendNewIssue);
			Element eleMAV0210_AddInfoReceiveSendNewIssue=docMAV0210_AddInfoReceiveSendNewIssue.getDocumentElement();
	              
	        Element oXmlEleMAV0210_AddInfoReceiveSendNewIssue = (Element) eleMAV0210_AddInfoReceiveSendNewIssue;
	        
	        this.testData.put("ChannelsURL",oXmlEleMAV0210_AddInfoReceiveSendNewIssue.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0210_AddInfoReceiveSendNewIssue.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowSimpleFormText",oXmlEleMAV0210_AddInfoReceiveSendNewIssue.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0210_AddInfoReceiveSendNewIssue.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Unspecified",oXmlEleMAV0210_AddInfoReceiveSendNewIssue.getElementsByTagName("unspecified").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0210_AddInfoReceiveSendNewIssue.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Actions",oXmlEleMAV0210_AddInfoReceiveSendNewIssue.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AddNewSegment",oXmlEleMAV0210_AddInfoReceiveSendNewIssue.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAV0210_AddInfoReceiveSendNewIssue.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInReceivesPanel",oXmlEleMAV0210_AddInfoReceiveSendNewIssue.getElementsByTagName("actionsInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInSendsPanel",oXmlEleMAV0210_AddInfoReceiveSendNewIssue.getElementsByTagName("actionsInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("NewIssueInReceivesPanel",oXmlEleMAV0210_AddInfoReceiveSendNewIssue.getElementsByTagName("newIssueInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("NewIssueInSendsPanel",oXmlEleMAV0210_AddInfoReceiveSendNewIssue.getElementsByTagName("newIssueInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0210_AddInfoReceiveSendNewIssue not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0210_AddInfoReceiveSendNewIssue.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0210_AddInfoReceiveSendNewIssue can not be parsed.");
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