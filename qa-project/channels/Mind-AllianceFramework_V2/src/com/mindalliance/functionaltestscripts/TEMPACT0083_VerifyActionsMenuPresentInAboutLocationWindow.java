package com.mindalliance.functionaltestscripts;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.mindalliance.configuration.BrowserController;
import com.mindalliance.configuration.Configuration;
import com.mindalliance.configuration.DataController;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.LogFunctions;
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.Log4J;
import com.mindalliance.configuration.UIAutomationException;
import com.mindalliance.pages.DomainPlanPage;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.LoginPage;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.PlanPage;

/**
 * Testcase ID: TEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow
 * 	   Summary: Verify that the Actions popup menu is present in About Location window
 * @author Afour
 *
 */

public class TEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="TEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="FAIL";
	public String blank=""; 
	public String scriptException;
	public String browser="";

	/*
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
			DataController dataController= new DataController();
			dataController.createResultFiles();
			
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
			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser initialized");
			
		}
		catch(UIAutomationException ue){
			stepNo++;
			description="Unable to initialize the driver";
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo, ue.getErrorMessage(), failed, scriptException, blank);
			Log4J.getlogger(this.getClass()).error(testCaseId +"Unable to initialize the driver");
		}
	}
	
	@Test
	public void testTEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow() throws UIAutomationException, IOException {
		try{
			stepNo++;
			description="URL Entered";
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"URL Entered");
 			
			// Login page
			stepNo++;
			description="Login Successful";
			LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
		    // Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Login Successful");
 			
 			 // Domain Plans
 		    stepNo++;
 			description="Domain Plans";
 			DomainPlanPage domainPlanPage= new DomainPlanPage();
 			domainPlanPage.clickDomainPlans();	
 			// Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Domain Plans");
 			
			// Plan Page
		    stepNo++;
			description="Domain Plan Editor";
			HomePage homePage=new HomePage();
			homePage.clickDomainPlanEditor();	
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Domain Plan Editor");
 			
 		    // Click on 'Hide details' under 'Show' pop up under 'Task' panel
 			stepNo++;
 			description="Details Task";
 			PlanPage planPage=new PlanPage();
 			planPage.clickPopupMenu(testData.get("ShowInTask"));
 			planPage.clickSubmenu(testData.get("Details"));	
 			// Write log
 		 	LogFunctions.writeLogs(description);
 		 	LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 		 	Log4J.getlogger(this.getClass()).info(testCaseId +"Details Task");
 					
 			//Enter location name 
 			stepNo++;
 			description="Enter location name";
 			planPage.enterLocationInTask(testData.get("Location"));
 			 // Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Enter location name");
 			
 			//Click Location Link
 			stepNo++;
 			description="Click Location Link";
 			planPage.clickLocationLink();
 			 // Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Click Location Link");
 			
 			//Verify whether Actions popup menu is present in About Location window
 			stepNo++;
 			description="Actions popup menu is present";
 			planPage.verifyActionsPopupMenuIsPresentInAboutLocation(testData.get("Actions"));
 			 // Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Actions popup menu is present");
 			
 			// Sign out from home page
		    stepNo++;
		    description="Logout successful";
			HeaderController headerController=new HeaderController();
			headerController.signOut();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Logout Successful");	
			
 			Reporting reporting= new Reporting();
		    reporting.generateAutomationReport();
		    
		}catch (UIAutomationException ue) {
			stepNo++;
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo,description,failed, ue.getErrorMessage(), blank);
			Reporting.getScreenShot(testCaseId);
			Log4J.getlogger(this.getClass()).error(testCaseId +ue.getErrorMessage());
			
			// Sign out from home page
		    stepNo++;
		    description="Logout successful";
			HeaderController headerController=new HeaderController();
			headerController.signOut();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Logout Successful");	
			
			Reporting reporting= new Reporting();
		    reporting.generateAutomationReport();
		    
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());		
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
			Log4J.getlogger(this.getClass()).info(testCaseId +" Browser quit");
		}
	}
	
	/**
     * Loads Test Data for TEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow.
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
			File TEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow=new File(path + "TEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow.xml");
			
			Document docTEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow=db.parse(TEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow);
			Element eleTEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow=docTEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow.getDocumentElement();
	              
	        Element oXmlEleTEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow = (Element) eleTEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow;
	       	
			this.testData.put("ChannelsURL",oXmlEleTEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleTEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Location",oXmlEleTEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow.getElementsByTagName("location").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ShowInTask",oXmlEleTEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow.getElementsByTagName("showInTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Details",oXmlEleTEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow.getElementsByTagName("details").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Actions",oXmlEleTEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
			
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File TEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File TEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File TEMPACT0083_VerifyActionsMenuPresentInAboutLocationWindow can not be parsed.");
		}
			
	}
}