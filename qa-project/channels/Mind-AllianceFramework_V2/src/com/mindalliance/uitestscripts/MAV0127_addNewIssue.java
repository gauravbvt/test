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
 * TestCase Id: MAV0127_addNewIssue
 * Summary: Verify that message "Add new Issue" is displayed on the heading section of "Plan" page
 * @author afour
 *
 */
public class MAV0127_addNewIssue extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0127_addNewIssue";
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
	
	/**
	 * This method verifies issue is added
	 * @throws UIAutomationException
	 * @throws IOException 
	 */
	@Test
	public void testMAV0127_addNewIssue() throws UIAutomationException, IOException {
		try{
		    // Enter URL of Channels
			stepNo++;
			description="URL Entered";	
			BrowserController browserController=new BrowserController();
			browserController.enterURL();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);   
			Log4J.getlogger(this.getClass()).info(testCaseId +"URL Entered");	
			
			// Login page
			stepNo++;
			description="Login successful";	
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
		    // Write log
		    LogFunctions.writeLogs(description);
		    LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);   
		    Log4J.getlogger(this.getClass()).info(testCaseId +"Login successful");	
					
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
			description="Navigated to Plan page";
			HomePage homePage=new HomePage();
			homePage.clickDomainPlanEditor();	
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Navigated to Plan page");	
						
			// Click on 'Add' button under 'Receives' panel
			stepNo++;
			description="Info sharing need is added";
			PlanPage planPage = new PlanPage();
			// Click on Stretch up form
			planPage.clickStrenchUpForm();
			planPage.clickAddInfoReceivesPanel();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);   
			Log4J.getlogger(this.getClass()).info(testCaseId +"Info sharing need is added");	
						
			// Click on 'New Issue' under 'Actions' pop up menu
			stepNo++;
			description="New issue is added";
			planPage.clickPopupMenu(testData.get("ActionsInReceivesPanel"));
			planPage.clickSubmenu(testData.get("NewIssueInReceivesPanel"));
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);   
			Log4J.getlogger(this.getClass()).info(testCaseId +"New issue is added");	
					
			// Sign Out from 'Home' page
			stepNo++;
			description="Logout successful";
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);   
			Log4J.getlogger(this.getClass()).info(testCaseId +"Logout successful");	
			
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
	
	/**
	 * (non-Javadoc)
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
     * Loads Test Data for MAV0127_addNewIssue.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + File.separator+"TestData"+File.separator;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAV0127_addNewIssue=new File(path + "MAV0127_addNewIssue.xml");
			
			Document docMAV0127_addNewIssue=db.parse(MAV0127_addNewIssue);
			Element eleMAV0127_addNewIssue=docMAV0127_addNewIssue.getDocumentElement();
	              
	        Element oXmlEleMAV0127_addNewIssue = (Element) eleMAV0127_addNewIssue;
	        
	               	
	        this.testData.put("ChannelsURL",oXmlEleMAV0127_addNewIssue.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0127_addNewIssue.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ActionsInReceivesPanel",oXmlEleMAV0127_addNewIssue.getElementsByTagName("actionsInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("NewIssueInReceivesPanel",oXmlEleMAV0127_addNewIssue.getElementsByTagName("newIssueInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
		 }
		catch(SAXException se){
			throw new UIAutomationException("File MAV0127_addNewIssue not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0127_addNewIssue.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0127_addNewIssue can not be parsed.");
		}
	}
}
