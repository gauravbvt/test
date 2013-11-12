package com.mindalliance.functionaltestscripts;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;
import junit.framework.TestCase;

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
import com.mindalliance.pages.LoginPage;

/**
 * Testcase ID: CL0012_LoginWithInvalidUsernamePassword
 * 	   Summary: Verify that user is not able to login when invalid username and password is entered
 * @author Afour
 *
 */

public class CL0012_LoginWithInvalidUsernamePassword extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="CL0012_LoginWithInvalidUsernamePassword";
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
	public void testCL0012_LoginWithInvalidUsernamePassword() throws UIAutomationException, IOException {
		try{
			stepNo++;
			description="URL Entered";
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
			// Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
			// Login page
			stepNo++;
			description="Login Failed";
			LoginPage loginPage = new LoginPage();
		    loginPage.Login(testData.get("UserName"),testData.get("Password"));
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
		    
//			// Sign out from home page
//		    stepNo++;
//		    description="Logout successful";
//			HeaderController headerController=new HeaderController();
//			headerController.signOut();
//			// Write log			
//			LogFunctions.writeLogs(description);
//			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
				
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
     * Loads Test Data for CL0012_LoginWithInvalidUsernamePassword.
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
			File CL0012_LoginWithInvalidUsernamePassword=new File(path + "CL0012_LoginWithInvalidUsernamePassword.xml");
			
			Document docCL0012_LoginWithInvalidUsernamePassword=db.parse(CL0012_LoginWithInvalidUsernamePassword);
			Element eleCL0012_LoginWithInvalidUsernamePassword=docCL0012_LoginWithInvalidUsernamePassword.getDocumentElement();
	              
	        Element oXmlEleCL0012_LoginWithInvalidUsernamePassword = (Element) eleCL0012_LoginWithInvalidUsernamePassword;
	       	
			this.testData.put("ChannelsURL",oXmlEleCL0012_LoginWithInvalidUsernamePassword.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleCL0012_LoginWithInvalidUsernamePassword.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UserName",oXmlEleCL0012_LoginWithInvalidUsernamePassword.getElementsByTagName("userName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Password",oXmlEleCL0012_LoginWithInvalidUsernamePassword.getElementsByTagName("password").item(0).getChildNodes().item(0).getNodeValue());
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File CL0012_LoginWithInvalidUsernamePassword can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File CL0012_LoginWithInvalidUsernamePassword.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File CL0012_LoginWithInvalidUsernamePassword can not be parsed.");
		}
			
	}
}