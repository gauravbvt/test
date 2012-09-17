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
 * Test case ID: MAV0076_redoAddNewIssueUnderAction
 * 	   Summary: Verify that user is able to redo add new issue
 * @author AFour
 * 
 */

public class MAV0076_redoAddNewIssueUnderAction extends TestCase{


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
	public void testMAV0076_redoAddNewIssueUnderAction() throws UIAutomationException {
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
					
			// Click Actions pop up menu and Add New Segment
			PlanPage planPage=new PlanPage();
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewIssue"));
				
			// Click on 'Undo Add Issue'
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoAddNewIssue"));		
					
			// Click on 'Redo Add Issue'
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RedoAddNewIssue"));		
									
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();

		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0076_redoAddNewIssueUnderAction");
			
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
				
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
		
	}
	
	/**
     * Loads Test Data for MAV0076_redoAddNewIssueUnderAction.
     * @return void
     * @param  void
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
			File MAV0076_redoAddNewIssueUnderAction=new File(path + "MAV0076_redoAddNewIssueUnderAction.xml");
			
			Document docMAV0076_redoAddNewIssueUnderAction=db.parse(MAV0076_redoAddNewIssueUnderAction);
			Element eleMAV0076_redoAddNewIssueUnderAction=docMAV0076_redoAddNewIssueUnderAction.getDocumentElement();
	              
	        Element oXmlEleMAV0076_redoAddNewIssueUnderAction = (Element) eleMAV0076_redoAddNewIssueUnderAction;
	       	
	        this.testData.put("Actions",oXmlEleMAV0076_redoAddNewIssueUnderAction.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewIssue",oXmlEleMAV0076_redoAddNewIssueUnderAction.getElementsByTagName("addNewIssue").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("RedoAddNewIssue",oXmlEleMAV0076_redoAddNewIssueUnderAction.getElementsByTagName("redoAddNewIssue").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("UndoAddNewIssue",oXmlEleMAV0076_redoAddNewIssueUnderAction.getElementsByTagName("undoAddNewIssue").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAV0076_redoAddNewIssueUnderAction.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0076_redoAddNewIssueUnderAction.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0076_redoAddNewIssueUnderAction can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0076_redoAddNewIssueUnderAction.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0076_redoAddNewIssueUnderAction can not be parsed.");
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
