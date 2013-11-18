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

public class MAV0205_AddInfoReceiveSendButOnlyIfOption extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0205_AddInfoReceiveSendButOnlyIfOption";
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
	public void testMAV0205_AddInfoReceiveSendButOnlyIfOption() throws UIAutomationException, IOException {
		try{
			stepNo++;
			description="URL Entered";
			// Enter the  URL of Channels
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
 							
			// Domain Plans
			stepNo++;
			description="Domain Plans";
			DomainPlanPage domainPlanPage= new DomainPlanPage();
			domainPlanPage.clickDomainPlans();	
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Plan Page
			stepNo++;
			description="Navigated to Plan page";
			HomePage homePage=new HomePage();
			homePage.clickDomainPlanEditor();	
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);		
			
			// Add New Segment
 			stepNo++;
			description="Add New Segment";
			PlanPage planPage= new PlanPage();
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
			
			// Click on 'Add' in sends panel
			stepNo++;
			description="Add Info Sends";
			planPage.clickAddInfoSendsPanel();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Click on 'Show Advanced Form' in sends panel
			stepNo++;
			description="Advance Form - Sends Panel";
			planPage.clickShowAdvancedFormInSendsPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Click on radio button of 'but only if' in sends panel 
			stepNo++;
			description="But Only If - Sends Panel";
			planPage.clickCheckboxOfbutOnlyIfInSendsPanel(testData.get("Unspecified"),testData.get("DifferentLocations"),testData.get("DifferentOrganizations"),
					testData.get("DifferentOverallOrganizations"),testData.get("SameLocation"),testData.get("SameOrganization"),
					testData.get("SameOrganizationAndLocation"),testData.get("SameOverallOrganization"),testData.get("ToASupervisor"),
					testData.get("ToSelf"),testData.get("ToSomeOneElse")
					);
			planPage.selectOptionFromButOnlyIfDrodownInSendsPanel(testData.get("Unspecified"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Select 'in the same location' option from dropdown
			stepNo++;
			description="Location - Sends Panel";
			planPage.selectOptionFromButOnlyIfDrodownInSendsPanel(testData.get("SameLocation"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Click on 'Add' in receives panel
			stepNo++;
			description="Add Info Receives";
			planPage.clickAddInfoReceivesPanel();
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Click on 'Show advanced form' in receives panel
			stepNo++;
			description="Advance Form - Receives Panel";
			planPage.clickShowAdvancedFormInReceivesPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
				
			// Clicks on radio button of ' but only if' in receives panel
			stepNo++;
			description="But Only If - Recives Panel";
			planPage.clickCheckboxOfbutOnlyIfInReceivesPanel(testData.get("Unspecified"),testData.get("DifferentLocations"),testData.get("DifferentOrganizations"),
					testData.get("DifferentOverallOrganizations"),testData.get("SameLocation"),testData.get("SameOrganization"),
					testData.get("SameOrganizationAndLocation"),testData.get("SameOverallOrganization"),testData.get("ToASupervisor"),
					testData.get("ToSelf"),testData.get("ToSomeOneElse")
					);
			planPage.selectOptionFromButOnlyIfDrodownInReceivesPanel(testData.get("Unspecified"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Select 'in the same location' option from dropdown
			stepNo++;
			description="Location - Receives Panel";
			planPage.selectOptionFromButOnlyIfDrodownInReceivesPanel(testData.get("SameLocation"));
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
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo,description,failed, ue.getErrorMessage(), blank);
			Reporting.getScreenShot(testCaseId);
		    
			// Sign out from home page
		    stepNo++;
		    description="Logout successful";
			HeaderController headerController=new HeaderController();
			headerController.signOut();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
				
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
		}
	}
	
	/**
     * Loads Test Data for MAV0205_AddInfoReceiveSendButOnlyIfOption.
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
			File MAV0205_AddInfoReceiveSendButOnlyIfOption=new File(path + "MAV0205_AddInfoReceiveSendButOnlyIfOption.xml");
			
			Document docMAV0205_AddInfoReceiveSendButOnlyIfOption=db.parse(MAV0205_AddInfoReceiveSendButOnlyIfOption);
			Element eleMAV0205_AddInfoReceiveSendButOnlyIfOption=docMAV0205_AddInfoReceiveSendButOnlyIfOption.getDocumentElement();
	              
	        Element oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption = (Element) eleMAV0205_AddInfoReceiveSendButOnlyIfOption;
	        
	        this.testData.put("ChannelsURL",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowInTask",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("showInTask").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Details",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("details").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowSimpleFormText",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Unspecified",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("unspecified").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Actions",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AddNewSegment",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("Unspecified",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("unspecified").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChangeButtonInSendsPanel",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("changeButtonInSendsPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("DifferentLocations",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("differentLocations").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("DifferentOrganizations",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("differentOrganizations").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("DifferentOverallOrganizations",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("differentOverallOrganizations").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("SameLocation",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("sameLocation").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SameOrganization",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("sameOrganization").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SameOrganizationAndLocation",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("sameOrganizationAndLocation").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SameOverallOrganization",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("sameOverallOrganization").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("ChangeButtonInReceivesPanel",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("changeButtonInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ToASupervisor",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("toASupervisor").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ToSelf",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("toSelf").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ToSomeOneElse",oXmlEleMAV0205_AddInfoReceiveSendButOnlyIfOption.getElementsByTagName("toSomeOneElse").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0205_AddInfoReceiveSendButOnlyIfOption not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0205_AddInfoReceiveSendButOnlyIfOption.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0205_AddInfoReceiveSendButOnlyIfOption can not be parsed.");
		}
	}
}