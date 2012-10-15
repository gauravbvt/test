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
 * Test case ID: MAV0075_undoAddNewIssueUnderAction
 * 	   Summary: Verify that user is able to undo add new issue
 * @author afour
 * 
 */
public class MAV0075_undoAddNewIssueUnderAction extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0075_undoAddNewIssueUnderAction";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="Fail";
	public String blank=""; 
	public String exception="";
	
	public MAV0075_undoAddNewIssueUnderAction() throws UIAutomationException{
		setUp();
		testMAV0075_undoAddNewIssueUnderAction();
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
	 * This method verifies added issue should be undone
	 * @throws UIAutomationException
	 */
	@Test
	public void testMAV0075_undoAddNewIssueUnderAction() throws UIAutomationException {
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
					
			// Click 'New Issue' under 'Actions' pop up menu
 			stepNo++;
			description="New issue is added to segment";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewIssue"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);					    
			
			// Close Segment window
 			stepNo++;
			description="About plan segment window closed";
			planPage.closeSegmentWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);					    
			
			// Click on 'Undo Add Issue'
 			stepNo++;
			description="Added issue is undone";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoAddNewIssue"));
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
     * Loads Test Data for MAV0075_undoAddNewIssueUnderAction.
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
			File MAV0075_undoAddNewIssueUnderAction=new File(path + "MAV0075_undoAddNewIssueUnderAction.xml");
			
			Document docMAV0075_undoAddNewIssueUnderAction=db.parse(MAV0075_undoAddNewIssueUnderAction);
			Element eleMAV0075_undoAddNewIssueUnderAction=docMAV0075_undoAddNewIssueUnderAction.getDocumentElement();
	              
	        Element oXmlEleMAV0075_undoAddNewIssueUnderAction = (Element) eleMAV0075_undoAddNewIssueUnderAction;
	       	
	        this.testData.put("Actions",oXmlEleMAV0075_undoAddNewIssueUnderAction.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewIssue",oXmlEleMAV0075_undoAddNewIssueUnderAction.getElementsByTagName("addNewIssue").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("UndoAddNewIssue",oXmlEleMAV0075_undoAddNewIssueUnderAction.getElementsByTagName("undoAddNewIssue").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAV0075_undoAddNewIssueUnderAction.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0075_undoAddNewIssueUnderAction.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0075_undoAddNewIssueUnderAction can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0075_undoAddNewIssueUnderAction.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0075_undoAddNewIssueUnderAction can not be parsed.");
		}
			
	}
}
