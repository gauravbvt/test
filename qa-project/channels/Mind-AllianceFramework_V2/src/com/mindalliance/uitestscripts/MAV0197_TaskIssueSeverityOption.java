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
 * TestCase Id: MAV0197_TaskIssueSeverityOption 
 * Summary: Verify minor,major,severe,extreme are present in 'Severity' dropdown in Issues in task panel
 * @author afour
 *
 */
public class MAV0197_TaskIssueSeverityOption extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0197_TaskIssueSeverityOption";
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
	public void testMAV0197_TaskIssueSeverityOption() throws UIAutomationException, IOException {
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
			
			// Add New Segment
 			stepNo++;
			description="Add New Segment";
			PlanPage planPage = new PlanPage();
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add New Segment");
			
			// Close segment window
			stepNo++;
			description="Close About Plan Segment Window";
			planPage.closeSegmentWindow();
			// Click on 'stretch Up forms' icon 
			planPage.clickStrenchUpForm();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Close About Plan Segment Window");
			
			// Click on 'Hide details' under 'Show' pop up under 'Task' panel
			stepNo++;
			description="Details Task";
			planPage.clickPopupMenu(testData.get("ShowInTask"));
			planPage.clickSubmenu(testData.get("Details"));	
			// Click on 'Show Advanced Form' link
			planPage.clickShowAdvancedFormInTaskPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Details Task");
			
			// Click on 'New Issue' under 'Actions' pop up menu in task panel
			stepNo++;
			description="New Issues";
			planPage.clickPopupMenu(testData.get("ActionsInTask"));
			planPage.clickSubmenu(testData.get("NewIssueInTask"));	
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"New Issues");
			
			// Enter details in description textbox
			stepNo++;
			description="Issues Description";
			planPage.enterDescriptionInIssue(testData.get("Description"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Issues Description");
			
			// Verify drop-down of severity in issue
			stepNo++;
			description="Severity Issues";
			planPage.verifySeverityDropdownInIssueInTask(testData.get("Minor"), testData.get("Major"),testData.get("Severe"), testData.get("Extreme"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Severity Issues");
			
			// Remove This segment
 			stepNo++;
			description="Remove This Segment";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Remove This Segment");
					
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
     * Loads Test Data for MAV0197_TaskIssueSeverityOption.
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
			File MAV0197_TaskIssueSeverityOption=new File(path + "MAV0197_TaskIssueSeverityOption.xml");
			
			Document docMAV0197_TaskIssueSeverityOption=db.parse(MAV0197_TaskIssueSeverityOption);
			Element eleMAV0197_TaskIssueSeverityOption=docMAV0197_TaskIssueSeverityOption.getDocumentElement();
	              
	        Element oXmlEleMAV0197_TaskIssueSeverityOption = (Element) eleMAV0197_TaskIssueSeverityOption;
	        
	        this.testData.put("ChannelsURL",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowInTask",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("showInTask").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Details",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("details").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowSimpleFormText",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Unspecified",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("unspecified").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ActionsInTask",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("actionsInTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("NewIssueInTask",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("newIssueInTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Actions",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AddNewSegment",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Description",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("description").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Minor",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("minor").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Major",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("major").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Severe",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("severe").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Extreme",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("extreme").item(0).getChildNodes().item(0).getNodeValue());
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0197_TaskIssueSeverityOption not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0197_TaskIssueSeverityOption.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0197_TaskIssueSeverityOption can not be parsed.");
		}
	}
}