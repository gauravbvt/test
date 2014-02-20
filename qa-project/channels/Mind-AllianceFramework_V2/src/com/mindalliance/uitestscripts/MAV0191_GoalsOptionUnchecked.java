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
 * TestCase Id: MAV0191_GoalsOptionUnchecked 
 * Summary: Verify when unchecked 'Goals' checkbox goal is removed from task panel
 * @author afour
 *
 */
public class MAV0191_GoalsOptionUnchecked extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0191_GoalsOptionUnchecked";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="FAIL";
	public String blank=""; 
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
	
	@Test
	public void testMAV0191_GoalsOptionUnchecked() throws UIAutomationException, IOException {
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
			description="Navigated to Plan page";
			HomePage homePage=new HomePage();
			homePage.clickDomainPlanEditor();	
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);		
			Log4J.getlogger(this.getClass()).info(testCaseId +"Navigated to Plan page");
					
			// Click on 'Details' under 'Show' pop up under 'Task' panel
			stepNo++;
			description="Details Task";
			PlanPage planPage = new PlanPage();
			planPage.clickPopupMenu(testData.get("ShowInTask"));
			planPage.clickSubmenu(testData.get("Details"));	
			// Click on 'Show Advanced Form' link
			planPage.clickShowAdvancedFormInTaskPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Details Task");
			
 		    // Click on 'Goals' link in task panel
 			stepNo++;
 			description="Goals Link";
 			planPage.clickGoalsLinkInTask();
 			// Write log
 		 	LogFunctions.writeLogs(description);
 		 	LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 		 	Log4J.getlogger(this.getClass()).info(testCaseId +"Goals Link");
			
 		 	// Add goal
			stepNo++;
			description="Add Goal";
			planPage.addGoal(testData.get("SelectgoalFromList"),testData.get("Type"),testData.get("OrganizationForGoal"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add Goal");
						
			// Close segment window
			stepNo++;
			description="Close About Plan Segment Window";
			planPage.closeSegmentWindow();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment Window");
			
			// Select goal from dropdown list
			stepNo++;
			description="Close About Plan Segment Window";
			planPage.selectgoalfromDropDownInTask(testData.get("SelectgoalFromListInTask"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment Window");
			
			// Uncheck checkbox of goal
			stepNo++;
			description="Uncheck Goal";
			planPage.uncheckGoalInTask(testData.get("SelectgoalFromListInTask"),testData.get("ChooseOne"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Uncheck Goal");
			
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
			if(GlobalVariables.configuration.getWebDriver().getTitle().equals("Internal Error")){
				LogFunctions.writeResults(testCaseId,stepNo, description,failed,ue.getMessage(),GlobalVariables.configuration.getErrorLogSubDirectoryPath()+"\\"+testCaseId + ".logs" );
			}
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
     * Loads Test Data for MAV0191_GoalsOptionUnchecked.
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
	        //this.testData.put("SelectGoal",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("selectGoal").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Type",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("type").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("SelectgoalFromListInTask",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("selectgoalFromListInTask").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChooseOne",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("chooseOne").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("OrganizationForGoal",oXmlEleMAV0191_GoalsOptionUnchecked.getElementsByTagName("organizationForGoal").item(0).getChildNodes().item(0).getNodeValue());
		      
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
}