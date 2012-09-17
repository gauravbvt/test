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
import com.mindalliance.pages.PlanPage;

public class MAV0211_AddInfoReceiveSendIssueTypeOption extends TestCase{
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
	public void testMAV0211_AddInfoReceiveSendIssueTypeOption() throws UIAutomationException {
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
			
			// Verify dropdown of type in issue
			planPage.verifyTypeDropdownInIssueInSends(testData.get("Validity"), testData.get("Completeness"), testData.get("Robustness"));
					
			// Click on 'Add' in receives panel
			planPage.clickAddInReceivesPanel();
			
			// Click on 'Show Advanced Form' link
			planPage.clickShowAdvancedFormInReceivesPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
			
			// Click on 'New Issue' under 'Actions' pop up menu in Sends panel
			planPage.clickPopupMenu(testData.get("ActionsInReceivesPanel"));
			planPage.clickSubmenu(testData.get("NewIssueInReceivesPanel"));	
			
			// Verify dropdown of type in issue
			planPage.verifyTypeDropdownInIssueInReceives(testData.get("Validity"), testData.get("Completeness"), testData.get("Robustness"));
								
			// Remove This segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
		
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
		}
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0211_AddInfoReceiveSendIssueTypeOption");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAV0211_AddInfoReceiveSendIssueTypeOption.
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
			File MAV0211_AddInfoReceiveSendIssueTypeOption=new File(path + "MAV0211_AddInfoReceiveSendIssueTypeOption.xml");
			
			Document docMAV0211_AddInfoReceiveSendIssueTypeOption=db.parse(MAV0211_AddInfoReceiveSendIssueTypeOption);
			Element eleMAV0211_AddInfoReceiveSendIssueTypeOption=docMAV0211_AddInfoReceiveSendIssueTypeOption.getDocumentElement();
	              
	        Element oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption = (Element) eleMAV0211_AddInfoReceiveSendIssueTypeOption;
	        
	        this.testData.put("ChannelsURL",oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowSimpleFormText",oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Unspecified",oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption.getElementsByTagName("unspecified").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Actions",oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AddNewSegment",oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("ActionsInReceivesPanel",oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption.getElementsByTagName("actionsInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInSendsPanel",oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption.getElementsByTagName("actionsInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("NewIssueInReceivesPanel",oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption.getElementsByTagName("newIssueInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("NewIssueInSendsPanel",oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption.getElementsByTagName("newIssueInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			
			
			this.testData.put("Validity",oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption.getElementsByTagName("validity").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Completeness",oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption.getElementsByTagName("completeness").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Robustness",oXmlEleMAV0211_AddInfoReceiveSendIssueTypeOption.getElementsByTagName("robustness").item(0).getChildNodes().item(0).getNodeValue());
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0211_AddInfoReceiveSendIssueTypeOption not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0211_AddInfoReceiveSendIssueTypeOption.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0211_AddInfoReceiveSendIssueTypeOption can not be parsed.");
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