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
import com.mindalliance.configuration.DataController;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.LogFunctions;
import com.mindalliance.configuration.Reporting1;
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
	public String failed="FAIL";
	public String blank=""; 
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	
	@Before
	protected void setUp() throws IOException, XMLStreamException{	
		try{
			
			if (GlobalVariables.configuration == null){
					GlobalVariables.configuration = Configuration.getConfigurationObject();
			
				
			}
			if(GlobalVariables.configuration.getAttrSearchList() == null){
				new ElementController();
			}
			
			DataController.createResultFiles();
			// Write log		
			String startTime=LogFunctions.getDateTime();
			GlobalVariables.configuration.setStartTime(startTime);
			description = "Testcase: " + testCaseId + " execution started";
			
			LogFunctions.writeLogs(description);
						
			// Creates Browser instance
			BrowserController browserController= new BrowserController();
//			browserController.initializeDriver();
			browserController.initializeDriver("Mozilla Firefox");
			
			
			// Write log
			description="Browser initialized";
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			
			// Loads Test Data
			loadTestData();
			
		
			// Enter URL		
			testMAV0001_viewLoginPage();
//			Reporting1.generateAutomationReport();
			tearDown();
			
			
			
		}
		catch(UIAutomationException ue){
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
		}
		
	}
	
	@Test
	public void testMAV0001_viewLoginPage() throws UIAutomationException{
	     // Enter URL of Channels
		BrowserController browserController=new BrowserController();

		// Enter URL		
		browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
		// Write log
		description="URL Entered";
		stepNo++;
		LogFunctions.writeLogs(description);
		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);		
		
		// Quits the Browser
		GlobalVariables.configuration.getWebDriver().quit();
		// Write log
		description="Quited browser";
		stepNo++;
		LogFunctions.writeLogs(description);
		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
	    LogFunctions.writeLogs("Testcase: " + testCaseId + " execution completed");
	 }
	
	/**
     * Loads Test Data for MAV0001_viewLoginPage.
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
	
	public static void main(String args[]) throws IOException, XMLStreamException{		
		org.junit.runner.JUnitCore.runClasses(MAV0001_viewLoginPage.class);		
	}
}