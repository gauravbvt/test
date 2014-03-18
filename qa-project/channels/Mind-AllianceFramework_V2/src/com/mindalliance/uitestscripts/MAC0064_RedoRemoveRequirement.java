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
import com.mindalliance.configuration.DataController;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.Log4J;
import com.mindalliance.configuration.LogFunctions;
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;
import com.mindalliance.pages.DomainPlanPage;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;
import com.mindalliance.pages.PlanPage;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Testcase ID: MAC0064_RedoRemoveRequirement
 * 	   Summary: Verify that user is able to undo the disIntermediate task
 * @author AFour
 * 
 */
public class MAC0064_RedoRemoveRequirement extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="MAC0064_RedoRemoveRequirement";
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
	public void testMAC0064_RedoRemoveRequirement() throws UIAutomationException, IOException {
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
			
 			// Click on 'Plan Requirements' under 'Scoping' pop up menu
 			stepNo++;
			description="Plan Requirement";
			PlanPage planPage=new PlanPage();
			planPage.clickPopupMenu(testData.get("Scoping"));
			planPage.clickSubmenu(testData.get("Planrequirement"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Plan Requirement");	
			
 			// Click on 'New' button in requirement definition
			stepNo++;
			description="New Requirement";
			planPage.clickNewButtonInPlanRequirement(testData.get("RequirementDefinitions"));
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"New Requirement");	
			
			// Remove requirement
			stepNo++;
			description="Remove Requirement";
			planPage.clickRemoveRequirementInPlanRequirement();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Remove Requirement");	
			
 			// Undo Remove Requirement
 			stepNo++;
 			description="Undo Remove Requirement";
 			planPage.clickPopupMenu(testData.get("Actions"));
 			planPage.clickSubmenu(testData.get("UndoRemoveRequirement"));
 			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Undo Remove Requirement");	
			
 			// Redo Remove Requirement
 			stepNo++;
 			description="Redo Remove Requirement";
 			planPage.clickPopupMenu(testData.get("Actions"));
 			planPage.clickSubmenu(testData.get("RedoRemoveRequirement"));
 			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Redo Remove Requirement");	
			
 			// Close Requirements window
 			stepNo++;
			description="Close Requirement Window";
			planPage.closeRequirementWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close Requirement Window");	
			
			//Sign Out from 'Plan' page
 			stepNo++;
			description="SignOut Successful";
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"SignOut Successful");	
			
			Reporting reporting= new Reporting();
		    reporting.generateAutomationReport();
		    
		}catch (UIAutomationException ue) {
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
			Log4J.getlogger(this.getClass()).info(testCaseId +"Logout successful");	
			
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
			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser Quit");	
			
		}
	}
		
	/**
     * Loads Test Data for MAC0064_RedoRemoveRequirement.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			String startTime=LogFunctions.getDateTime();
			GlobalVariables.configuration.setStartTime(startTime);
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + File.separator+"TestData"+File.separator;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAC0064_RedoRemoveRequirement=new File(path + "MAC0064_RedoRemoveRequirement.xml");
			
			Document docMAC0064_RedoRemoveRequirement=db.parse(MAC0064_RedoRemoveRequirement);
			Element eleMAC0064_RedoRemoveRequirement=docMAC0064_RedoRemoveRequirement.getDocumentElement();
	              
	        Element oXmlEleMAC0064_RedoRemoveRequirement = (Element) eleMAC0064_RedoRemoveRequirement;
	               	
	        this.testData.put("ChannelsURL",oXmlEleMAC0064_RedoRemoveRequirement.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0064_RedoRemoveRequirement.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Actions",oXmlEleMAC0064_RedoRemoveRequirement.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Show",oXmlEleMAC0064_RedoRemoveRequirement.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Scoping", oXmlEleMAC0064_RedoRemoveRequirement.getElementsByTagName("scoping").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Planrequirement",oXmlEleMAC0064_RedoRemoveRequirement.getElementsByTagName("planrequirement").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RequirementDefinitions",oXmlEleMAC0064_RedoRemoveRequirement.getElementsByTagName("requirementDefinitions").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoRemoveRequirement",oXmlEleMAC0064_RedoRemoveRequirement.getElementsByTagName("undoRemoveRequirement").item(0).getChildNodes().item(0).getNodeValue());			
			this.testData.put("RedoRemoveRequirement",oXmlEleMAC0064_RedoRemoveRequirement.getElementsByTagName("redoRemoveRequirement").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0064_RedoRemoveRequirement not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0064_RedoRemoveRequirement.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0064_RedoRemoveRequirement can not be parsed.");
		}
	}
}