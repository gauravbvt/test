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

public class MAV0167_SimpleFormOfReceiveInfo extends TestCase{

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
			
			//Creates Browser instance
			BrowserController browserController= new BrowserController();
			browserController.initializeDriver();
			
			//Loads Test data 
			loadTestData();
		}
		catch(UIAutomationException ue){
			Assert.fail("Unable to initialize the driver "+ue.getErrorMessage());
		}
	}
	
	@Test
	public void testMAV0167_SimpleFormOfReceiveInfo() throws UIAutomationException {
		try {
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
					
			// Click Actions pop up menu and Add New Segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForSimpleFormOfReceiveInfo"));
				
			// Close Segment window
			planPage.closeSegmentWindow();
			
			// Click on 'Strench Up' form icon
			planPage.clickStrenchUpForm();
			
			// Click on 'Add' in 'Receives' panel
			planPage.clickAddInReceivesPanel();
			
			// Click on 'Show Advanced Form' link
			planPage.clickShowAdvancedFormInReceivesPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
			
			// Click on Remove this segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
			
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();

		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0167_SimpleFormOfReceiveInfo");
			
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
				
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
		
	}
	
	/**
     * Loads Test Data for MAV0167_SimpleFormOfReceiveInfo.
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
			File MAV0167_SimpleFormOfReceiveInfo=new File(path + "MAV0167_SimpleFormOfReceiveInfo.xml");
			
			Document docMAV0167_SimpleFormOfReceiveInfo=db.parse(MAV0167_SimpleFormOfReceiveInfo);
			Element eleMAV0167_SimpleFormOfReceiveInfo=docMAV0167_SimpleFormOfReceiveInfo.getDocumentElement();
	              
	        Element oXmlEleMAV0167_SimpleFormOfReceiveInfo = (Element) eleMAV0167_SimpleFormOfReceiveInfo;
	       	
	        this.testData.put("Actions",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
	       	this.testData.put("SegmentForSimpleFormOfReceiveInfo",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("segmentForSimpleFormOfReceiveInfo").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ShowSimpleFormText",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0167_SimpleFormOfReceiveInfo can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0167_SimpleFormOfReceiveInfo.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0167_SimpleFormOfReceiveInfo can not be parsed.");
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