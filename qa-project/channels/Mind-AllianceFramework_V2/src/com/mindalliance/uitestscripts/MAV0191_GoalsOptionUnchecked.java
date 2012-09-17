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

public class MAV0191_GoalsOptionUnchecked extends TestCase{

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
	public void testMAV0191_GoalsOptionUnchecked() throws UIAutomationException {
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
						
			PlanPage planPage=new PlanPage();
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			
			// Close segment window
			planPage.closeSegmentWindow();
						
			// Click on 'strench Up forms' icon 
			planPage.clickStrenchUpForm();
		
			// Click on 'Details' under 'Show' pop up under 'Task' panel
			planPage.clickPopupMenu(testData.get("ShowInTask"));
			planPage.clickSubmenu(testData.get("Details"));	
			
			// Click on 'Show Advanced Form' link
			planPage.clickShowAdvancedFormInTaskPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
				
			// Click on 'Goals' link in task panel
			planPage.clickGoalsLinkInTask();
			
			// Add goal
			planPage.addGoal(testData.get("AddGoal"),testData.get("SelectgoalFromList"),testData.get("Type"));
							
			// Close segment window
			planPage.closeSegmentWindow();
			
			// Strench up form
			planPage.clickStrenchUpForm();
			
			// Select goal from dropdown list
			planPage.selectgoalfromDropDownInTask(testData.get("SelectgoalFromListInTask"));
			
			// Uncheck checkbox of goal
			planPage.uncheckGoalInTask(testData.get("SelectgoalFromListInTask"),testData.get("ChooseOne"));
			
			// Click on Remove this segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
						
			//Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
		}
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0191_GoalsOptionUnchecked");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAV0191_GoalsOptionUnchecked.
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
			File MAV0191_GoalsOptionUnchecked=new File(path + "MAV0191_GoalsOptionUnchecked.xml");
			
			Document docMAV0191_GoalsOptionUnchecked=db.parse(MAV0191_GoalsOptionUnchecked);
			Element eleMAV0191_GoalsOptionUnchecked=docMAV0191_GoalsOptionUnchecked.getDocumentElement();
	              
	        Element oXmlEleMAV0191_GoalsOptionUnchecked = (Element) eleMAV0191_GoalsOptionUnchecked;
	        
	        this.testData.put("ChannelsURL",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowInTask",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("showInTask").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Details",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("details").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowSimpleFormText",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Actions",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("RemoveThisSegment",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("SelectgoalFromList",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("selectgoalFromList").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddGoal",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("addGoal").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Type",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("type").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("SelectgoalFromListInTask",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("selectgoalFromListInTask").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChooseOne",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("chooseOne").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0191_GoalsOptionUnchecked not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0191_GoalsOptionUnchecked.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0191_GoalsOptionUnchecked can not be parsed.");
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