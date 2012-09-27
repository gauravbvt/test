package com.mindalliance.uitestscripts;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.mindalliance.configuration.BrowserController;
import com.mindalliance.configuration.Configuration;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * TestCase Id: MAV0164_viewHomeFromIssueReport 
 * Summary: Verify by clicking on 'Home' icon on 'Issues Summary Report', home page gets displayed 
 * @author afour
 *
 */
public class MAV0164_viewHomeFromIssueReport extends TestCase{
	public Hashtable<String, String> testData;
	
	@Before
	protected void setUp(){
		try{
			if (GlobalVariables.configuration == null){
					GlobalVariables.configuration = Configuration.getConfigurationObject();
				
			}
			if(GlobalVariables.configuration.getAttrSearchList() == null){
				new ElementController();
			}
			
			// Creates Browser instance
			BrowserController browserController= new BrowserController();
			browserController.initializeDriver();
			
			// Loads Test Data
			loadTestData();
		}
		catch(UIAutomationException ue){
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
		}
	}
	
	@Test
	public void testMAV0164_viewHomeFromIssueReport() throws UIAutomationException, InterruptedException {
		try {
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
			    
			// Login page
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
								
			// Click on 'Issue Report' link
			HomePage homePage=new HomePage();
			homePage.clickIssuesReportLink(testData.get("headingOfIssuesSummary"));
			
			// Go to 'Home' page
			HeaderController headerController=new HeaderController();
			headerController.clickHomeImageLink(testData.get("homePageTitle"));
						
			//Sign Out from 'Plan' page
			headerController.signOut();

		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0164_viewHomeFromIssueReport");
		
			//Sign out from Issue summary report page
			HeaderController headerController=new HeaderController();
			headerController.signOut();
			
			//Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	/**
     * Loads Test Data for MAV0164_viewHomeFromIssueReport.
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
			File MAV0164_viewHomeFromIssueReport=new File(path + "MAV0164_viewHomeFromIssueReport.xml");
			
			Document docMAV0164_viewHomeFromIssueReport=db.parse(MAV0164_viewHomeFromIssueReport);
			Element eleMAV0164_viewHomeFromIssueReport=docMAV0164_viewHomeFromIssueReport.getDocumentElement();
	              
	        Element oXmlEleMAV0164_viewHomeFromIssueReport = (Element) eleMAV0164_viewHomeFromIssueReport;
	     	        
			this.testData.put("ChannelsURL",oXmlEleMAV0164_viewHomeFromIssueReport.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0164_viewHomeFromIssueReport.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("headingOfIssuesSummary",oXmlEleMAV0164_viewHomeFromIssueReport.getElementsByTagName("headingOfIssuesSummary").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("homePageTitle",oXmlEleMAV0164_viewHomeFromIssueReport.getElementsByTagName("homePageTitle").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0164_viewHomeFromIssueReport not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0164_viewHomeFromIssueReport.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0164_viewHomeFromIssueReport can not be parsed.");
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
	}
}
