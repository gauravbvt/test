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
 * TestCase ID: MAV0028_viewGoalsForm
 * Summary: Verify that form with heading "Goals" gets loaded on the About Plan Segment window 
 * @author: AFour
 *
 */

public class MAV0028_viewGoalsForm extends TestCase {

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
	public void testMAV0028_viewGoalsForm() throws UIAutomationException {
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
			
			// Click on 'About Plan Segment' under 'Show' pop up menu
			PlanPage planPage=new PlanPage();
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
					
			// Click on 'Goals' tab
			planPage.clickGoalTab();
			
			// Close 'Goals' window
			planPage.closeAboutPlanSegmentWindow();
							
			// Sign Out from 'Home' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();

		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0028_viewGoalsForm");
				
			//Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			//Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();	
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	
	/**
     * Loads Test Data for MAV0028_viewGoalsForm.
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
			File testData=new File(path + "MAV0028_viewGoalsForm.xml");
			
			Document docMAV0028_viewGoalsForm=db.parse(testData);
			Element eleMAV0028_viewGoalsForm=docMAV0028_viewGoalsForm.getDocumentElement();
	              
	        Element oXmlEleMAV0028_viewGoalsForm = (Element) eleMAV0028_viewGoalsForm;
	       	
	       
	        this.testData.put("Show",oXmlEleMAV0028_viewGoalsForm.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AboutPlanSegment",oXmlEleMAV0028_viewGoalsForm.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL",oXmlEleMAV0028_viewGoalsForm.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAV0028_viewGoalsForm.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0028_viewGoalsForm not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0028_viewGoalsForm.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0028_viewGoalsForm can not be parsed.");
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
