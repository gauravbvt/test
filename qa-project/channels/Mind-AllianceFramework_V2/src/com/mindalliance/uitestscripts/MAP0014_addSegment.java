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
/**
 * Test Case ID: MAP0014_addSegment
 * Summary: Verify that Segment can be added to the plan
 * @author AfourTech
 *
 */
public class MAP0014_addSegment extends TestCase{

	public Hashtable<String, String> testData;
	String fileName = "PlanPage.xml";
	
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
	public void testMAP0014_addSegment() throws UIAutomationException {
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
			planPage.enterSegmentName(testData.get("SegmentForAddSegment"));
			
			// Close Segment window
			planPage.closeSegmentWindow();
			
			// Verify Segment is added
			planPage.verifySegmentInDropdown(testData.get("SegmentForAddSegment"));
							
			// Click on Remove this segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
			
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();

		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAP0014_addSegment");
			
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
				
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
		
	}
	
	/**
     * Loads Test Data for MAP0014_addSegment.
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
			File MAP0014_addSegment=new File(path + "MAP0014_addSegment.xml");
			
			Document docMAP0014_addSegment=db.parse(MAP0014_addSegment);
			Element eleMAP0014_addSegment=docMAP0014_addSegment.getDocumentElement();
	              
	        Element oXmlEleMAP0014_addSegment = (Element) eleMAP0014_addSegment;
	       	
	        this.testData.put("Actions",oXmlEleMAP0014_addSegment.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAP0014_addSegment.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("SegmentForAddSegment",oXmlEleMAP0014_addSegment.getElementsByTagName("segmentForAddSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAP0014_addSegment.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAP0014_addSegment.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAP0014_addSegment.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Untitled",oXmlEleMAP0014_addSegment.getElementsByTagName("untitled").item(0).getChildNodes().item(0).getNodeValue());
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAP0014_addSegment can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAP0014_addSegment.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAP0014_addSegment can not be parsed.");
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