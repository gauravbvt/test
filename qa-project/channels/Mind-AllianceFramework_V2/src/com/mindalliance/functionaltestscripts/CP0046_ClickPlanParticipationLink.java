package com.mindalliance.functionaltestscripts;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
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
import com.mindalliance.pages.CommunitiesPage;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.LoginPage;
import com.mindalliance.pages.HomePage;

/**
 * Test Case ID: CP0046_ClickPlanParticipationLink
 * Summary: Click on Plan Participation link
 * @author Administrator
 *
 */
public class CP0046_ClickPlanParticipationLink extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="CP0046_ClickPlanParticipationLink";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="Fail";
	public String blank=""; 
	public String exception="";
	public String browser="";
	
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
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo,exception,failed, ue.getErrorMessage(), blank);
			Log4J.getlogger(this.getClass()).error(testCaseId +"Unable to initialize the driver");
			
		}
	}
	/**
	 * This method verify that Plan Participation page is shown after Clicking the Plan Participation
	 * @throws UIAutomationException
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testCP0046_ClickPlanParticipationLink() throws UIAutomationException, IOException, InterruptedException{
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
			
			// Click on Channels Settings Link
			stepNo++;
			description="Navigated to Collaboration Plans page";
			HomePage homePage=new HomePage();
			homePage.clickCommunitiesLink();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);		
			Log4J.getlogger(this.getClass()).info(testCaseId +"Navigated to Collaboration Plans page");
		    
	 		// Click on Start It button
	 		stepNo++;
	 		description="Start It";
		 	CommunitiesPage communitiesPage= new CommunitiesPage();
		 	communitiesPage.clickStartItButton(testData.get("PlanName"));
		 	// Write log			
	 		LogFunctions.writeLogs(description);
	 		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
	 		Log4J.getlogger(this.getClass()).info(testCaseId +"Start It");
	 		
	 		// Click on Go button
	 		stepNo++;
	 		description="Go";
		 	communitiesPage.clickGoButton();
		 	// Write log			
	 		LogFunctions.writeLogs(description);
	 		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
	 		Log4J.getlogger(this.getClass()).info(testCaseId +"Go");
	 		
	 		// Click Collaboration Requirement
	 		stepNo++;
	 		description="Plan Participations";
		 	communitiesPage.clickPlanParticipationLink();
		 	// Write log			
	 		LogFunctions.writeLogs(description);
	 		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
	 		Log4J.getlogger(this.getClass()).info(testCaseId +"Plan Participations");
	 		
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
	 * (non-Javadoc)
	 * 
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
     * Loads Test Data for CP0046_ClickPlanParticipationLink.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException
	{
		try{
			String startTime=LogFunctions.getDateTime();
			GlobalVariables.configuration.setStartTime(startTime);
			
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + File.separator+"TestData"+File.separator;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File CP0046_ClickPlanParticipationLink=new File(path + "CP0046_ClickPlanParticipationLink.xml");
			
			Document docCP0046_ClickPlanParticipationLink=db.parse(CP0046_ClickPlanParticipationLink);
			Element eleCP0046_ClickPlanParticipationLink=docCP0046_ClickPlanParticipationLink.getDocumentElement();
	              
	        Element oXmlEleCP0046_ClickPlanParticipationLink = (Element) eleCP0046_ClickPlanParticipationLink;
	                     	
	        
			this.testData.put("ChannelsURL",oXmlEleCP0046_ClickPlanParticipationLink.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleCP0046_ClickPlanParticipationLink.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("PlanName",oXmlEleCP0046_ClickPlanParticipationLink.getElementsByTagName("planName").item(0).getChildNodes().item(0).getNodeValue());
		
		}
		catch(SAXException se){
			throw new UIAutomationException("File CP0046_ClickPlanParticipationLink not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File CP0046_ClickPlanParticipationLink.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File CP0046_ClickPlanParticipationLink can not be parsed.");
		}
	}
}