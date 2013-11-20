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
import com.mindalliance.configuration.LogFunctions;
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;
/**
 * Testcase ID: CL0001_LoginPage
 * 	   Summary: Verify that login page renders
 * @author Afour
 * @author Afour
 *
 */
public class CL0001_LoginPage extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="CL0001_LoginPage";
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
			
			DataController dc= new DataController();
			dc.createResultFiles();
			
			GlobalVariables.configuration.addTestCaseIdToJList(testCaseId);	
			loadTestData();
			
			// Loads Test Data
			description = "Testcase: " + testCaseId + " execution started";
			// Creates Browser instance
			BrowserController browserController= new BrowserController();
			browserController.initializeDriver();
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
	 * This method verify that login page is displayed after entering the URL of Channels
	 * @throws UIAutomationException
	 * @throws IOException 
	 */
	@Test
	public void testCL0001_LoginPage() throws UIAutomationException, IOException{	
		// Enter URL	
		stepNo++;
		description="URL Entered";
		BrowserController browserController=new BrowserController();
		browserController.enterURL();
		// Write log
		LogFunctions.writeLogs(description);
		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);		
		
		// Quits the Browser
		stepNo++;
		description="Browser closed";
		GlobalVariables.configuration.getWebDriver().quit();
		// Write log
		LogFunctions.writeLogs(description);
		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
	    LogFunctions.writeLogs("Testcase: " + testCaseId + " execution completed");
	    
	    Reporting reporting= new Reporting();
	    reporting.generateAutomationReport();
	 }
	
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@After
	protected void tearDown(){
		if(GlobalVariables.configuration.getWebDriver()!=null){
			GlobalVariables.configuration.getWebDriver().quit();
		}
	}
	
	/**
     * Loads Test Data for CL0001_LoginPage.
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
			File CL0001_LoginPage=new File(path + "CL0001_LoginPage.xml");
			
			Document docCL0001_LoginPage=db.parse(CL0001_LoginPage);
			Element eleCL0001_LoginPage=docCL0001_LoginPage.getDocumentElement();
	              
	        Element oXmlEleCL0001_LoginPage = (Element) eleCL0001_LoginPage;
	       	
	        this.testData.put("ChannelsURL",oXmlEleCL0001_LoginPage.getElementsByTagName("ChannelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleCL0001_LoginPage.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File CL0001_LoginPage.xml not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File CL0001_LoginPage.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File CL0001_LoginPage.xml can not be parsed.");
		}
	}
}
