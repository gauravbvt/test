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
import org.openqa.selenium.WebDriver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.mindalliance.configuration.BrowserController;
import com.mindalliance.configuration.Configuration;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.LogFunctions;
import com.mindalliance.configuration.UIActions;
import com.mindalliance.configuration.UIAutomationException;

/**
 * Test Case ID: MAV0001_viewLoginPage
 * Summary: Verify that the "Channels Login Page" renders
 * @author Administrator
 *
 */
public class MAV0001_viewLoginPage extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0001_viewLoginPage";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="Fail";
	public String blank=""; 	
	public String exception="";
	public String browser="";
	public WebDriver wd=null;
	
	public MAV0001_viewLoginPage() throws UIAutomationException{
		setUp();
		testMAV0001_viewLoginPage();
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
	 * This method verify that login page is displayed after entering th URL of Channels
	 * @throws UIAutomationException
	 */
	@Test
	public void testMAV0001_viewLoginPage() throws UIAutomationException{	
		// Enter URL	
		stepNo++;
		description="URL Entered";
		BrowserController browserController=new BrowserController();
		browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"),browser);
		// Write log
		LogFunctions.writeLogs(description);
		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);		
		
		// Quits the Browser
		stepNo++;
		description="Browser closed";
		wd=UIActions.setDriver(browser);
		wd.quit();
		// Write log
		LogFunctions.writeLogs(description);
		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
	    LogFunctions.writeLogs("Testcase: " + testCaseId + " execution completed");
	 }
	
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@After
	protected void tearDown(){
		wd=UIActions.setDriver(browser);
		if(wd!=null){
			wd.quit();
		}
		String endTime=LogFunctions.getDateTime();
		GlobalVariables.configuration.setEndtime(endTime);
	}
	
	/**
     * Loads Test Data for MAV0001_viewLoginPage.
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
			File MAV0001_viewLoginPage=new File(path + "MAV0001_viewLoginPage.xml");
			
			Document docMAV0001_viewLoginPage=db.parse(MAV0001_viewLoginPage);
			Element eleMAV0001_viewLoginPage=docMAV0001_viewLoginPage.getDocumentElement();
	              
	        Element oXmlEleMAV0001_viewLoginPage = (Element) eleMAV0001_viewLoginPage;
	       	
	        this.testData.put("ChannelsURL",oXmlEleMAV0001_viewLoginPage.getElementsByTagName("ChannelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAV0001_viewLoginPage.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0001_viewLoginPage.xml not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0001_viewLoginPage.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0001_viewLoginPage.xml can not be parsed.");
		}
	}
}