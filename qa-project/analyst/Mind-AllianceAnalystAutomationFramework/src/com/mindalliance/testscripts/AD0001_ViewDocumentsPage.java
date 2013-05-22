package com.mindalliance.testscripts;
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
import com.mindalliance.configuration.DataController;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.LogFunctions;
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;

import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.LoginPage;
import com.mindalliance.pages.ProjectsPage;

import junit.framework.TestCase;

public class AD0001_ViewDocumentsPage extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="AD0001_ViewDocumentsPage";
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
	 * This method verify that new project can be added successfully
	 * @throws UIAutomationException
	 * @throws IOException 
	 */
	
	@Test
	public void testAL0001_Login() throws UIAutomationException, IOException{	
		
		// Enter URL	
		stepNo++;
		description="URL Entered";
		BrowserController browserController=new BrowserController();
		browserController.enterURL(testData.get("AnalystURL"),testData.get("Title"));
		// Write log
		LogFunctions.writeLogs(description);
		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
	    
		// Enter URL	
		stepNo++;
		description="Login to Analyst Successful";
		LoginPage loginpage= new LoginPage();
		loginpage.Login(testData.get("UserName"),testData.get("Password"));
		// Write log
		LogFunctions.writeLogs(description);
		LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);

		LogFunctions.writeLogs("Testcase: " + testCaseId + " execution completed");
	    Reporting reporting= new Reporting();
	    reporting.generateAutomationReport();
	    
	    //Click Documents button
	    ProjectsPage project2=new ProjectsPage();
	    project2.clickDocumentsButton();
	    
	    //Click Sign-out button
	    stepNo++;
		description="Signout Successful";
		HeaderController headerController=new HeaderController();
		headerController.signOut();
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
     * Loads Test Data for AD0001_ViewDocumentsPage.
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
			File AD0001_ViewDocumentsPage=new File(path + "AD0001_ViewDocumentsPage.xml");
			
			Document docAD0001_ViewDocumentsPage=db.parse(AD0001_ViewDocumentsPage);
			Element eleAD0001_ViewDocumentsPage=docAD0001_ViewDocumentsPage.getDocumentElement();
	              
	        Element oXmlEleAD0001_ViewDocumentsPage = (Element) eleAD0001_ViewDocumentsPage;
	       	
	        this.testData.put("AnalystURL",oXmlEleAD0001_ViewDocumentsPage.getElementsByTagName("analystURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleAD0001_ViewDocumentsPage.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("UserName",oXmlEleAD0001_ViewDocumentsPage.getElementsByTagName("userName").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Password",oXmlEleAD0001_ViewDocumentsPage.getElementsByTagName("password").item(0).getChildNodes().item(0).getNodeValue());
	       
		}
		catch(SAXException se){
			throw new UIAutomationException("File AD0001_ViewDocumentsPage.xml not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File AD0001_ViewDocumentsPage.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File AD0001_ViewDocumentsPage.xml can not be parsed.");
		}
	}
}
