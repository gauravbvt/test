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
import com.mindalliance.configuration.LogFunctions;
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;
import com.mindalliance.pages.PlanPage;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * TestCase ID: MAV0025_closeAboutPlanWindow
 * Summary: Verify that window with title "About Plan:<Plan Name>" gets closed 
 * @author afour
 */

public class MAV0025_closeAboutPlanWindow extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0025_closeAboutPlanWindow";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="Fail";
	public String blank=""; 
	public String exception="";
	
	public MAV0025_closeAboutPlanWindow() throws UIAutomationException{
		setUp();
		testMAV0025_closeAboutPlanWindow();
		tearDown();
	}
	/**
	 * This method will initialize the setup required for every test case
	 * @throws UIAutomationException 
	 * @see junit.framework.TestCase#setUp()
	 */	
	@Before
	protected void setUp() throws UIAutomationException{
		try{
			if (GlobalVariables.configuration == null){
					GlobalVariables.configuration = Configuration.getConfigurationObject();
			}
			if(GlobalVariables.configuration.getAttrSearchList() == null){
				new ElementController();
			}
			
			// Loads Test Data
			description = "Testcase: " + testCaseId + " execution started";
			loadTestData();
			// Write log			
			LogFunctions.writeLogs(description);
						
			// Creates Browser instance
			description="Browser initialized";
			BrowserController browserController= new BrowserController();
			browserController.initializeDriver("Mozilla Firefox");			
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
		}
		catch(UIAutomationException ue){
			stepNo++;
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo,exception,failed, ue.getErrorMessage(), blank);
			
		}
	}
	/**
	 * This method verifies about plan window is closed 
	 * @throws UIAutomationException
	 */
	@Test
	public void testMAV0025_closeAboutPlanWindow() throws UIAutomationException{
		try {
			// Enter URL of Channels
			stepNo++;
			description="URL Entered";	
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);					    
			    
			// Login page
			stepNo++;
			description="Login successful";	
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
		    // Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);		   
									
			// Plan Page
			stepNo++;
			description="Navigated to plan page";
			HomePage homePage=new HomePage();
			homePage.clickCollaborationPlanLink();	
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);						
			
			// Close Plan Map window
			stepNo++;
			description="Plan Map window closed";
			PlanPage planPage=new PlanPage();
			planPage.closePlanMap();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);						
				
			// Click on 'About Plan' under 'Show' pop up menu
			stepNo++;
			description="About plan window opened";
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlan"));
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);						
						
			// Close About Plan Window
			stepNo++;
			description="About plan window closed";
			planPage.closeAboutPlanWindow();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);						
						
			// Sign Out from 'Plan' page
			stepNo++;
			description="Logout successful";
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	

		} catch (UIAutomationException ue) {
			Reporting.getScreenShot(testCaseId);
			
			// Sign out from plan page
			stepNo++;
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo,exception,failed, ue.getErrorMessage(), blank);
				
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());	
		}
	}
	/**
	 * (non-Javadoc)
	 * This method will perform cleanup actions
	 * @see junit.framework.TestCase#tearDown()
	*/	
	
	@After
	protected void tearDown(){
		if(GlobalVariables.configuration.getWebDriver()!=null){
			GlobalVariables.configuration.getWebDriver().quit();
		}
		String endTime=LogFunctions.getDateTime();
		GlobalVariables.configuration.setEndtime(endTime);
	}
	
	/**
     * Loads Test Data for MAV0025_closeAboutPlanWindow.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException
	{
		try{
			String startTime=LogFunctions.getDateTime();
			GlobalVariables.configuration.setStartTime(startTime);
			
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAV0025_closeAboutPlanWindow=new File(path + "MAV0025_closeAboutPlanWindow.xml");
			
			Document docMAV0025_closeAboutPlanWindow=db.parse(MAV0025_closeAboutPlanWindow);
			Element eleMAV0025_closeAboutPlanWindow= docMAV0025_closeAboutPlanWindow.getDocumentElement();
	              
	        Element oXmlEleMAV0025_closeAboutPlanWindow = (Element) eleMAV0025_closeAboutPlanWindow;
	       	
	        this.testData.put("Show",oXmlEleMAV0025_closeAboutPlanWindow.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AboutPlan",oXmlEleMAV0025_closeAboutPlanWindow.getElementsByTagName("aboutPlan").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL",oXmlEleMAV0025_closeAboutPlanWindow.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAV0025_closeAboutPlanWindow.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0025_closeAboutPlanWindow.xml not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0025_closeAboutPlanWindow.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0025_closeAboutPlanWindow.xml can not be parsed.");
		}
	}
}
