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
 * Test case ID: MAC0034_RedoRemoveGoal
 * 	   Summary: Verify that user is able to redo the removed goal
 * @author AFour
 * 
 */
public class MAC0034_RedoRemoveGoal extends TestCase {

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
			
			//Loads Test Data
			loadTestData();
		}
		catch(UIAutomationException ue){
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
		}
	}
	
	@Test
	public void testMAC0034_RedoRemoveGoal() throws UIAutomationException {
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
			planPage.enterSegmentName(testData.get("SegmentForRedoremovegoal"));
							
			// Click on 'Goals' tab
			planPage.clickGoalTab();

			// Add Goal
			planPage.addGoal(testData.get("Organization"),testData.get("SelectgoalFromList"),testData.get("Type"));
			
			// Delete goal
			planPage.deleteGoal();
					
			// Click on 'Undo Remove Goal
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoRemoveGoal"));		
			
			// click on 'Redo remove goal'
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RedoRemoveGoal"));					
			
			// Close segment window			
			planPage.closeSegmentWindow();
		
			// Click on Remove this segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));		
						
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();

		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAC0034_RedoRemoveGoal");
					
			//Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			//Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAC0034_RedoRemoveGoal.
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
			File MAC0034_RedoRemoveGoal=new File(path + "MAC0034_RedoRemoveGoal.xml");
			
			Document docMAC0034_RedoRemoveGoal=db.parse(MAC0034_RedoRemoveGoal);
			Element eleMAC0034_RedoRemoveGoal=docMAC0034_RedoRemoveGoal.getDocumentElement();
	              
	        Element oXmlEleMAC0034_RedoRemoveGoal = (Element) eleMAC0034_RedoRemoveGoal;
	       	
	        this.testData.put("Actions",oXmlEleMAC0034_RedoRemoveGoal.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Show",oXmlEleMAC0034_RedoRemoveGoal.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAC0034_RedoRemoveGoal.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("SegmentForRedoremovegoal",oXmlEleMAC0034_RedoRemoveGoal.getElementsByTagName("segmentForRedoremovegoal").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AboutPlanSegment",oXmlEleMAC0034_RedoRemoveGoal.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("GoalsTab",oXmlEleMAC0034_RedoRemoveGoal.getElementsByTagName("goalsTab").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Organization",oXmlEleMAC0034_RedoRemoveGoal.getElementsByTagName("organization").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("UndoUpdateSegment",oXmlEleMAC0034_RedoRemoveGoal.getElementsByTagName("undoUpdateSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("RedoRemoveGoal",oXmlEleMAC0034_RedoRemoveGoal.getElementsByTagName("redoRemoveGoal").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("RemoveThisSegment",oXmlEleMAC0034_RedoRemoveGoal.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("UndoRemoveGoal",oXmlEleMAC0034_RedoRemoveGoal.getElementsByTagName("undoRemoveGoal").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL",oXmlEleMAC0034_RedoRemoveGoal.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	     	this.testData.put("Title",oXmlEleMAC0034_RedoRemoveGoal.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	     	this.testData.put("SelectgoalFromList",oXmlEleMAC0034_RedoRemoveGoal.getElementsByTagName("selectgoalFromList").item(0).getChildNodes().item(0).getNodeValue());
	     	this.testData.put("Type",oXmlEleMAC0034_RedoRemoveGoal.getElementsByTagName("type").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0034_RedoRemoveGoal not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0034_RedoRemoveGoal.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0034_RedoRemoveGoal can not be parsed.");
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
