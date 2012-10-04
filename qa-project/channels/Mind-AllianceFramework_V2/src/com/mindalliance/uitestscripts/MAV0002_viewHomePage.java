package com.mindalliance.uitestscripts;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

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
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.LogFunctions;
import com.mindalliance.configuration.Reporting1;
import com.mindalliance.configuration.UIAutomationException;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.LoginPage;
/**
 * Test Case ID: MAV0002_viewHomePage
 * Summary: Verify that the "Home" page renders
 * @author Administrator
 *
 */

public class MAV0002_viewHomePage extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0002_viewHomePage";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="FAIL";
	public String blank=""; 
	@Before
	protected void setUp(){
		try{
				if (GlobalVariables.configuration == null){
					GlobalVariables.configuration = Configuration.getConfigurationObject();
			
				
			}
			if(GlobalVariables.configuration.getAttrSearchList() == null){
				new ElementController();
			}
			
			// Write log		
			String startTime=LogFunctions.getDateTime();
			GlobalVariables.configuration.setStartTime(startTime);
			description = "Testcase: " + testCaseId + " execution started";
			Configuration.createResultFiles();
			LogFunctions.writeLogs(description);
						
			// Creates Browser instance
			BrowserController browserController= new BrowserController();
	//		browserController.initializeDriver();
			browserController.initializeDriver("Mozilla Firefox");
			
			
			// Write log
			description="Browser initialized";
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Loads Test Data
			loadTestData();
		
			// Enter URL		
			testMAV0002_viewHomePage();
			tearDown();
		}
		catch(UIAutomationException ue){
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
		}
	}
	
	@Test
	public void testMAV0002_viewHomePage() throws UIAutomationException{
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
			
			// Write log
			description="URL Entered";
			stepNo++;
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);		
			    
			// Login page
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
		    description="Login successful";
			stepNo++;
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);		
		    

			// Sign Out from 'Home' page
			HeaderController headerController=new HeaderController();
			headerController.signOut();
			description="Logout successful";
			stepNo++;
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
	}
	
	/**
     * Loads Test Data for MAV0002_viewHomePage.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException
	{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAV0002_viewHomePage=new File(path + "MAV0002_viewHomePage.xml");
			
			Document docMAV0002_viewHomePage=db.parse(MAV0002_viewHomePage);
			Element eleMAV0002_viewHomePage=docMAV0002_viewHomePage.getDocumentElement();
	              
	        Element oXmlEleMAV0002_viewHomePage = (Element) eleMAV0002_viewHomePage;
	                     	
	        
			this.testData.put("ChannelsURL",oXmlEleMAV0002_viewHomePage.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0002_viewHomePage.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0002_viewHomePage not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0002_viewHomePage.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0002_viewHomePage can not be parsed.");
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
		}
		String endTime=LogFunctions.getDateTime();
		GlobalVariables.configuration.setEndtime(endTime);
	}
//	public static void main(String args[]) throws IOException, XMLStreamException{	
//		org.junit.runner.JUnitCore.runClasses(MAV0002_viewHomePage.class);
////		Reporting1.generateAutomationReport();
//	}
}