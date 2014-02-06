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
import com.mindalliance.pages.LoginPage;

/**
 * Test Case ID: CH0004_SendFeedback
 * Summary: Send Feedback from Collaboration Templates Page
 * @author Administrator
 *
 */
public class CH0004_SendFeedback extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="CH0004_SendFeedback";
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
			Log4J.getlogger(this.getClass()).info(testCaseId +"Logout successful");
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
	 * This method verify that home page is displayed after login to the Channels
	 * @throws UIAutomationException
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testCH0004_SendFeedback() throws UIAutomationException, IOException, InterruptedException{
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
			Log4J.getlogger(this.getClass()).info(testCaseId +"Login Successful");
			
			//Click on Collaboration Templates link
			stepNo++;
	 		description="Collaboration Templates";
	 		DomainPlanPage domainPlanPage= new DomainPlanPage();
	 		domainPlanPage.clickDomainPlans();	
	 		// Write log			
	 		LogFunctions.writeLogs(description);
	 		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
	 		Log4J.getlogger(this.getClass()).info(testCaseId +"Logout successful");
	 		
	 		//Click on Send Feedback button, Enter feedback in the Feedback text area , Click on Send button
	 		stepNo++;
	 		description="Click Send Feedback";
	 	    HeaderController headerController=new HeaderController();
	 		headerController.sendFeedback();
	 	    // Write log			
	 		LogFunctions.writeLogs(description);
	 		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
	 		Log4J.getlogger(this.getClass()).info(testCaseId +"Click Send Feedback");
	 		
	 		//Enter Feedback
	 		stepNo++;
	 		description="Enter Feedback";
	 		headerController.enterFeedback(testData.get("Feedback"));
	    	// Write log			
	 		LogFunctions.writeLogs(description);
	 		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
	 		Log4J.getlogger(this.getClass()).info(testCaseId +"Enter Feedback");
	 		
	 		//Click on Send Button
	 		stepNo++;
	 		description="Click Send Button";
	 		headerController.clickSendButton();
	    	// Write log			
	 		LogFunctions.writeLogs(description);
	 		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
	 		Log4J.getlogger(this.getClass()).info(testCaseId +"Click Send Button");
	 		
	 		// Sign out from home page
		    stepNo++;
		    description="Logout successful";
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
			Log4J.getlogger(this.getClass()).info(testCaseId +" Browser quit");
		}
	}
	/**
     * Loads Test Data for CH0004_SendFeedback.
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
			File CH0004_SendFeedback=new File(path + "CH0004_SendFeedback.xml");
			
			Document docCH0004_SendFeedback=db.parse(CH0004_SendFeedback);
			Element eleCH0004_SendFeedback=docCH0004_SendFeedback.getDocumentElement();
	              
	        Element oXmlEleCH0004_SendFeedback = (Element) eleCH0004_SendFeedback;
	                     	
	        
			this.testData.put("ChannelsURL",oXmlEleCH0004_SendFeedback.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleCH0004_SendFeedback.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Feedback",oXmlEleCH0004_SendFeedback.getElementsByTagName("feedback").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File CH0004_SendFeedback not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File CH0004_SendFeedback.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File CH0004_SendFeedback can not be parsed.");
		}
	}
}