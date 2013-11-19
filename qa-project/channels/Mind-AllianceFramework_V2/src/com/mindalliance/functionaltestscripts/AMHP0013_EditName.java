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
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.HeaderController;
/**
 * Testcase ID: AMHP0013_EditName
 * 	   Summary: Verify that name can be edited by the user
 * @author Afour
 *
 */

public class AMHP0013_EditName extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="AMHP0013_EditName";
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
	public void testAMHP0013_EditName() throws UIAutomationException, IOException {
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
			description="Login Successful";
			LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
		    // Write log
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
 			
 			//Edit name in Name text field in About me tab
 			stepNo++;
 			description="Edit name";
 			HomePage homePage=new HomePage();
 			homePage.enterNameInAboutMe(testData.get("Name"));
 		    // Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
 			
 			//Enter email address
 			description="Enter email address";
 			homePage.enterEmailInAboutMe(testData.get("Email"));
 		    // Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
 			
 			//Click Apply button
 			stepNo++;
 			description="Click Apply button";
 			homePage.clickApplyButtonInAboutMe();
 	    	// Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
 			
 			
 			//Sign out from home page
 			stepNo++;
		    description="Logout successful";
 			HeaderController headerController=new HeaderController();
 			headerController.signOut();
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
     * Loads Test Data for AMHP0013_EditName.
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
			File AMHP0013_EditName=new File(path + "AMHP0013_EditName.xml");
			
			Document docAMHP0013_EditName=db.parse(AMHP0013_EditName);
			Element eleAMHP0013_EditName=docAMHP0013_EditName.getDocumentElement();
	              
	        Element oXmlEleAMHP0013_EditName = (Element) eleAMHP0013_EditName;
	       	
			this.testData.put("ChannelsURL",oXmlEleAMHP0013_EditName.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleAMHP0013_EditName.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UserName",oXmlEleAMHP0013_EditName.getElementsByTagName("userName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Password",oXmlEleAMHP0013_EditName.getElementsByTagName("password").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Name",oXmlEleAMHP0013_EditName.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue());
//			this.testData.put("Email",oXmlEleAMHP0013_EditName.getElementsByTagName("email").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File AMHP0013_EditName can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File AMHP0013_EditName.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File AMHP0013_EditName can not be parsed.");
		}
			
	}
}