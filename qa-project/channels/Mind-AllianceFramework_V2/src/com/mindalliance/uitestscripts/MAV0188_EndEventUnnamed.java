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
 * TestCase Id: MAV0188_EndEventUnnamed 
 * Summary: Verify when Clicked on 'Can Event Unnamed' checkbox
 * task can ends with responding while unnamed
 * @author afour
 *
 */
public class MAV0188_EndEventUnnamed extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0188_EndEventUnnamed";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="FAIL";
	public String blank=""; 
	public String scriptException;
	public String browser="";
	
	/**
	 * This method will initialize the setup required for every test case
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
			
			GlobalVariables.configuration.addTestCaseIdToJList(testCaseId);	
			// Loads Test Data
			description = "Testcase: " + testCaseId + " execution started";
			loadTestData();
			// Write log			
			LogFunctions.writeLogs(description);
						
			// Creates Browser instance
			BrowserController browserController= new BrowserController();
			browserController.initializeDriver();		
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
		}
		catch(UIAutomationException ue){
			stepNo++;
			description="Unable to initialize the driver";
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo, ue.getErrorMessage(), failed, scriptException, blank);
		}
	}
	
	@Test
	public void testMAV0188_EndEventUnnamed() throws UIAutomationException, IOException {
		try{
			stepNo++;
			description="URL Entered";
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Login page
			stepNo++;
			description="Login Successful";
			LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
		    // Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 							
			// Plan Page
		    stepNo++;
			description="Collaboration Plan";
			HomePage homePage=new HomePage();
			homePage.clickCollaborationPlanLink();	
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 						
			// Close Plan Map window
			stepNo++;
			description="Close Plan Map Window";
			PlanPage planPage=new PlanPage();
			planPage.closePlanMap();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Add New Segment
 			stepNo++;
			description="Add New Segment";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Close segment window
			stepNo++;
			description="Close About Plan Segment Window";
			planPage.closeSegmentWindow();
			// Click on 'strench Up forms' icon 
			planPage.clickStrenchUpForm();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Click on 'Details' under 'Show' pop up under 'Task' panel
			stepNo++;
			description="Details Task";
			planPage.clickPopupMenu(testData.get("ShowInTask"));
			planPage.clickSubmenu(testData.get("Details"));	
			// Click on 'Show Advanced Form' link
			planPage.clickShowAdvancedFormInTaskPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Check checkbox for 'Can end event' in task panel
			stepNo++;
			description="Can End Event";
			planPage.clickCanEndEvent();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
									
			// Remove This segment
 			stepNo++;
			description="Remove This Segment";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
						
			//Sign Out from 'Plan' page
 			stepNo++;
			description="SignOut Successful";
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
 			Reporting reporting= new Reporting();
		    reporting.generateAutomationReport();
		}catch (UIAutomationException ue) {
			Reporting.getScreenShot(testCaseId);
			Reporting reporting= new Reporting();
		    reporting.generateAutomationReport();
		
			// Sign out from plan page
			stepNo++;
			description="SignOut Successful";
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			// Write log
 			LogFunctions.writeLogs(ue.getErrorMessage());
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
 			// Quits the Browser
			stepNo++;
			description="Browser Closed";
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
			// Write log
 			LogFunctions.writeLogs(ue.getErrorMessage());
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
		}
	}
	
	/*
	 * This method will perform cleanup actions
	 * @see junit.framework.TestCase#tearDown()
	 */
	
	@After
	protected void tearDown(){
		if(GlobalVariables.configuration.getWebDriver()!=null){
			GlobalVariables.configuration.getWebDriver().quit();
		}
	}
	
	/**
     * Loads Test Data for MAV0188_EndEventUnnamed.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			String startTime=LogFunctions.getDateTime();
			GlobalVariables.configuration.setStartTime(startTime);
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAV0188_EndEventUnnamed=new File(path + "MAV0188_EndEventUnnamed.xml");
			
			Document docMAV0188_EndEventUnnamed=db.parse(MAV0188_EndEventUnnamed);
			Element eleMAV0188_EndEventUnnamed=docMAV0188_EndEventUnnamed.getDocumentElement();
	              
	        Element oXmlEleMAV0188_EndEventUnnamed = (Element) eleMAV0188_EndEventUnnamed;
	        
	        this.testData.put("ChannelsURL",oXmlEleMAV0188_EndEventUnnamed.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowInTask",oXmlEleMAV0188_EndEventUnnamed.getElementsByTagName("showInTask").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Details",oXmlEleMAV0188_EndEventUnnamed.getElementsByTagName("details").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0188_EndEventUnnamed.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowSimpleFormText",oXmlEleMAV0188_EndEventUnnamed.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0188_EndEventUnnamed.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0188_EndEventUnnamed.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Actions",oXmlEleMAV0188_EndEventUnnamed.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAV0188_EndEventUnnamed.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("RemoveThisSegment",oXmlEleMAV0188_EndEventUnnamed.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0188_EndEventUnnamed not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0188_EndEventUnnamed.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0188_EndEventUnnamed can not be parsed.");
		}
	}
}