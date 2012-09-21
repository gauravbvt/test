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

public class MAV0209_AddInfoReceiveSendAttachOptions extends TestCase{
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
	public void testMAV0209_AddInfoReceiveSendAttachOptions() throws UIAutomationException {
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
						
			// Add new segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			
			// Close segment window
			planPage.closeSegmentWindow();
						
			// Click on 'strench Up forms' icon 
			planPage.clickStrenchUpForm();		
		
			// click on Add in sends panel
			planPage.clickAddInSendsPanel();
			
			// Click on 'Show Advanced Form' link
			planPage.clickShowAdvancedFormInSendsPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
				
			// Check attach dropdown list is present
			planPage.verifyAttachDropdownInSendsPanel(testData.get("Reference"),testData.get("Policy"),testData.get("Picture"),testData.get("MandatingPolicy"), testData.get("ProhibitingPolicy"));
			
			// Click on 'Add' in receives panel
			planPage.clickAddInReceivesPanel();
			
			// Click on 'Show Advanced Form' link
			planPage.clickShowAdvancedFormInReceivesPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
			
			// Check attach dropdown list is present
			planPage.verifyAttachDropdownInReceivesPanel(testData.get("Reference"),testData.get("Policy"),testData.get("Picture"),testData.get("MandatingPolicy"), testData.get("ProhibitingPolicy"));
						
			// Click on Remove this segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
						
			//Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
		}
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0209_AddInfoReceiveSendAttachOptions");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAV0209_AddInfoReceiveSendAttachOptions.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAV0209_AddInfoReceiveSendAttachOptions=new File(path + "MAV0209_AddInfoReceiveSendAttachOptions.xml");
			
			Document docMAV0209_AddInfoReceiveSendAttachOptions=db.parse(MAV0209_AddInfoReceiveSendAttachOptions);
			Element eleMAV0209_AddInfoReceiveSendAttachOptions=docMAV0209_AddInfoReceiveSendAttachOptions.getDocumentElement();
	              
	        Element oXmlEleMAV0209_AddInfoReceiveSendAttachOptions = (Element) eleMAV0209_AddInfoReceiveSendAttachOptions;
	        
	        this.testData.put("ChannelsURL",oXmlEleMAV0209_AddInfoReceiveSendAttachOptions.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0209_AddInfoReceiveSendAttachOptions.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowSimpleFormText",oXmlEleMAV0209_AddInfoReceiveSendAttachOptions.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0209_AddInfoReceiveSendAttachOptions.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0209_AddInfoReceiveSendAttachOptions.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Actions",oXmlEleMAV0209_AddInfoReceiveSendAttachOptions.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAV0209_AddInfoReceiveSendAttachOptions.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("RemoveThisSegment",oXmlEleMAV0209_AddInfoReceiveSendAttachOptions.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Reference",oXmlEleMAV0209_AddInfoReceiveSendAttachOptions.getElementsByTagName("reference").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Policy",oXmlEleMAV0209_AddInfoReceiveSendAttachOptions.getElementsByTagName("policy").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("MandatingPolicy",oXmlEleMAV0209_AddInfoReceiveSendAttachOptions.getElementsByTagName("mandatingPolicy").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ProhibitingPolicy",oXmlEleMAV0209_AddInfoReceiveSendAttachOptions.getElementsByTagName("prohibitingPolicy").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Picture",oXmlEleMAV0209_AddInfoReceiveSendAttachOptions.getElementsByTagName("picture").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0209_AddInfoReceiveSendAttachOptions not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0209_AddInfoReceiveSendAttachOptions.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0209_AddInfoReceiveSendAttachOptions can not be parsed.");
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