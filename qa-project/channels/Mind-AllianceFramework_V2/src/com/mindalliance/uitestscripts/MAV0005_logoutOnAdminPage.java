package com.mindalliance.uitestscripts;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.mindalliance.configuration.BrowserController;
import com.mindalliance.configuration.Configuration;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.LogFunctions;
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;

import junit.framework.TestCase;

/**
 * Test Case Id: MAV0005_logoutOnAdminPage
 * Summary: Verify that user is able to Logout from Admin Page
 * @author afour
 *
 */
public class MAV0005_logoutOnAdminPage extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0005_logoutOnAdminPage";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="Fail";
	public String blank=""; 
	public String exception="";
	public String browser="";
	
	public MAV0005_logoutOnAdminPage() throws UIAutomationException{
		setUp();
		testMAV0005_logoutOnAdminPage();
		tearDown();
	}
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
			
			// Loads Test Data
			description = "Testcase: " + testCaseId + " execution started";
			loadTestData();
			// Write log			
			LogFunctions.writeLogs(description);
						
			// Creates Browser instance
			description="Browser initialized";
			browser=BrowserController.browserName;		
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
		}
		catch(UIAutomationException ue){
			stepNo++;
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo,exception,failed, ue.getErrorMessage(), blank);
			
		}
	}
	/**
	 * This method logouts from Admin page
	 * @throws UIAutomationException
	 */
	@Test
	public void testMAV0005_logoutOnAdminPage() throws UIAutomationException {
		try {
			// Enter URL of Channels
			stepNo++;
			description="URL Entered";
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"),browser);
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);		    
			
			// Login page
			stepNo++;
			description="Login successful";			
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
		    // Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);		
							
			// Click on Chanels Settings Link
			stepNo++;
			description="Navigated to Admin page";
			HomePage homePage=new HomePage();
			homePage.clickChannelsAdminLink();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);		
					
			// Sign Out from 'Admin' page
			stepNo++;
			description="Logout successful";
			HeaderController headerController=new HeaderController();
			headerController.signOutAdmin();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			
		} catch (UIAutomationException ue) {
			Reporting.getScreenShot(testCaseId);
			
			// Sign out from admin page
			stepNo++;
			HeaderController headerController=new HeaderController();
			headerController.signOutAdmin();
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo,exception,failed, ue.getErrorMessage(), blank);
				
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
		}
	}
	/**
     * Loads Test Data for MAV0005_logoutOnAdminPage.
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
			File MAV0005_logoutOnAdminPage=new File(path + "MAV0005_logoutOnAdminPage.xml");
			
			Document docMAV0005_logoutOnAdminPage=db.parse(MAV0005_logoutOnAdminPage);
			Element eleMAV0005_logoutOnAdminPage=docMAV0005_logoutOnAdminPage.getDocumentElement();
	              
	        Element oXmlEleMAV0005_logoutOnAdminPage = (Element) eleMAV0005_logoutOnAdminPage;
	     	        
			this.testData.put("ChannelsURL",oXmlEleMAV0005_logoutOnAdminPage.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0005_logoutOnAdminPage.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0005_logoutOnAdminPage not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0005_logoutOnAdminPage.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0005_logoutOnAdminPage can not be parsed.");
		}
	}
	
}