package com.mindalliance.uitestscripts;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;
import junit.framework.TestCase;

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

/**
 * TestCase ID: MAV0017_viewAllIssueForm
 * Summary: Verify that form with heading "All Issues" gets loaded on the About Plan window 
 * @author afour
 *
 */
public class MAV0017_viewAllIssueForm extends TestCase{	
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0017_viewAllIssueForm";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="Fail";
	public String blank=""; 
	public String exception="";
	public String browser="";
	public String scriptException;
	
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
	
	/**
	 * This method verifies by clicking on 'All Issues' link under 'Improving' pop up menu 'All Issues' window opens
	 * @throws UIAutomationException
	 * @throws IOException 
	 */
	@Test
	public void testMAV0017_viewAllIssueForm() throws UIAutomationException, IOException{
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

			// Click on 'All Issues' under 'Improving' pop up menu
			stepNo++;
			description="All issues window opened";
			planPage.clickPopupMenu(testData.get("Improving"));
			planPage.clickSubmenu(testData.get("AllIssues"));
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
							
			// Close All Issues Window
			stepNo++;
			description="All issues window closed";
		    planPage.closeAllIssuesWindow();
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
 			
 			Reporting reporting= new Reporting();
		    reporting.generateAutomationReport();
		}catch (UIAutomationException ue) {
			Reporting.getScreenShot(testCaseId);
			Reporting reporting= new Reporting();
		    reporting.generateAutomationReport();
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
	}
	/**
	 * loads test data
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
			File MAV0017_viewAllIssueForm=new File(path + "MAV0017_viewAllIssueForm.xml");
			
			Document docMAV0017_viewAllIssueForm=db.parse(MAV0017_viewAllIssueForm);
			Element eleMAV0017_viewAllIssueForm=docMAV0017_viewAllIssueForm.getDocumentElement();
	              
	        Element oXmlEleMAV0017_viewAllIssueForm = (Element) eleMAV0017_viewAllIssueForm;
	    	           
	        this.testData.put("Improving", oXmlEleMAV0017_viewAllIssueForm.getElementsByTagName("improving").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AllIssues", oXmlEleMAV0017_viewAllIssueForm.getElementsByTagName("allIssues").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL", oXmlEleMAV0017_viewAllIssueForm.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAV0017_viewAllIssueForm.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0017_viewAllIssueForm.xml can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0017_viewAllIssueForm.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0017_viewAllIssueForm.xml can not be parsed.");
		}
	}
	
}
