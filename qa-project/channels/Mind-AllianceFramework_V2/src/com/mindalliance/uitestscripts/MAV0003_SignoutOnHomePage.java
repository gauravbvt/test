package com.mindalliance.uitestscripts;

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
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.LoginPage;

/**
 * Test Case Id: MAV0003_logoutOnPlanPage
 * Summary: Verify that the page with title 'Channels Information Sharing Planning' renders
 * @author afour
 *
 */
public class MAV0003_SignoutOnHomePage extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0003_SignoutOnHomePage";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="Fail";
	public String blank=""; 
	public String exception="";
	public String browser="";
	
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
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo,exception,failed, ue.getErrorMessage(), blank);
			Log4J.getlogger(this.getClass()).error(testCaseId +"Unable to initialize the driver");
			
		}
	}
	/**
	 * This method sign out from home page
	 * @throws UIAutomationException
	 * @throws IOException 
	 */
	@Test
	public void testMAV0003_SignoutOnHomePage() throws UIAutomationException, IOException {
		try {
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
			
			// Sign Out from 'Home' page
			stepNo++;
			description="Signout on Home Page Successful";
			HeaderController headerController=new HeaderController();
			headerController.signOut();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Signout on Home Page Successful");
			
			Reporting reporting= new Reporting();
		    reporting.generateAutomationReport();
		    
		}catch (UIAutomationException ue) {
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo,description,failed, ue.getErrorMessage(), blank);
			Reporting.getScreenShot(testCaseId);
			Log4J.getlogger(this.getClass()).info(testCaseId +ue.getErrorMessage());
			
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
     * Loads Test Data for MAV0003_SignoutOnHomePage.
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
			File MAV0003_SignoutOnHomePage=new File(path + "MAV0003_SignoutOnHomePage.xml");
			
			Document docMAV0003_SignoutOnHomePage=db.parse(MAV0003_SignoutOnHomePage);
			Element eleMAV0003_SignoutOnHomePage=docMAV0003_SignoutOnHomePage.getDocumentElement();
	              
	        Element oXmlEleMAV0003_SignoutOnHomePage = (Element) eleMAV0003_SignoutOnHomePage;
	     	        
			this.testData.put("ChannelsURL",oXmlEleMAV0003_SignoutOnHomePage.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0003_SignoutOnHomePage.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0003_SignoutOnHomePage not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0003_SignoutOnHomePage.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0003_SignoutOnHomePage can not be parsed.");
		}
	}
	
}
