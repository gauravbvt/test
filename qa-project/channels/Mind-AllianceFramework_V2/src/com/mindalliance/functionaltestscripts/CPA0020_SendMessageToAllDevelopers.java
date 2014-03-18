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
import com.mindalliance.pages.DomainPlanPage;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;
import com.mindalliance.pages.PlanPage;
/**
 * Test Case ID: CPA0020_SendMessageToAllDevelopers
 * Summary: Send Message to All Developers from Collaboration Panel
 * @author Administrator
 *
 */
public class CPA0020_SendMessageToAllDevelopers extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="CPA0020_SendMessageToAllDevelopers";
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
	 * This method Sends Message to All Developers from Collaboration Panel
	 * @throws UIAutomationException
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testCPA0020_SendMessageToAllDevelopers() throws UIAutomationException, IOException, InterruptedException{
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
 			
			//Click on Collaboration Templates link
			stepNo++;
	 		description="Collaboration Templates";
	 		DomainPlanPage domainPlanPage= new DomainPlanPage();
	 		domainPlanPage.clickDomainPlans();	
	 		// Write log			
	 		LogFunctions.writeLogs(description);
	 		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
	 		Log4J.getlogger(this.getClass()).info(testCaseId +"Collaboration Templates");
	 		
	    	// Plan Page
		    stepNo++;
			description="Collaboration Template Editor";
			HomePage homePage=new HomePage();
			homePage.clickDomainPlanEditor();	
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Collaboration Template Editor");
 			
	 		//Click on Messages Tab in Collaboration Panel
	 		stepNo++;
	 		description="Messages Tab";
	 		PlanPage planPage=new PlanPage();
	 		planPage.clickMessagesTabInCollaborationPanel();
	 	    // Write log			
	 		LogFunctions.writeLogs(description);
	 		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
	 		Log4J.getlogger(this.getClass()).info(testCaseId +"Messages Tab");

	 		//Click To drop down in Collaboration Panel
	 		stepNo++;
	 		description="Click To Drop Down";
	 		planPage.clickToDropDownInCollaborationPanel(testData.get("To"));
	     	// Write log			
	 		LogFunctions.writeLogs(description);
	 		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
	 		Log4J.getlogger(this.getClass()).info(testCaseId +"Click To Drop Down");

	 		//Enter Message in Message text area in Collaboration Panel
	 		stepNo++;
	 		description="Enter Message in Message Text area";
	 		planPage.enterMessage(testData.get("Message"));
	     	// Write log			
	 		LogFunctions.writeLogs(description);
	 		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
	 		Log4J.getlogger(this.getClass()).info(testCaseId +"Enter Message in Message Text area");

	 		//Click on Send button in Collaboration Panel
	 		stepNo++;
	 		description="Click Send button";
	 		planPage.clickSendButtonInCollaborationPanel();
	     	// Write log			
	 		LogFunctions.writeLogs(description);
	 		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
	 		Log4J.getlogger(this.getClass()).info(testCaseId +"Click Send button");
	 		
			Reporting reporting= new Reporting();
		    reporting.generateAutomationReport();
		    
		    // Sign out from home page
		    stepNo++;
		    description="Logout successful";
		    HeaderController headerController=new HeaderController();
			headerController.signOut();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Logout successful");
			
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
     * Loads Test Data for CPA0020_SendMessageToAllDevelopers.
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
			File CPA0020_SendMessageToAllDevelopers=new File(path + "CPA0020_SendMessageToAllDevelopers.xml");
			
			Document docCPA0020_SendMessageToAllDevelopers=db.parse(CPA0020_SendMessageToAllDevelopers);
			Element eleCPA0020_SendMessageToAllDevelopers=docCPA0020_SendMessageToAllDevelopers.getDocumentElement();
	              
	        Element oXmlEleCPA0020_SendMessageToAllDevelopers = (Element) eleCPA0020_SendMessageToAllDevelopers;
	                     	
	        
			this.testData.put("ChannelsURL",oXmlEleCPA0020_SendMessageToAllDevelopers.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleCPA0020_SendMessageToAllDevelopers.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Message",oXmlEleCPA0020_SendMessageToAllDevelopers.getElementsByTagName("message").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("To",oXmlEleCPA0020_SendMessageToAllDevelopers.getElementsByTagName("to").item(0).getChildNodes().item(0).getNodeValue());
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File CPA0020_SendMessageToAllDevelopers not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File CPA0020_SendMessageToAllDevelopers.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File CPA0020_SendMessageToAllDevelopers can not be parsed.");
		}
	}
}