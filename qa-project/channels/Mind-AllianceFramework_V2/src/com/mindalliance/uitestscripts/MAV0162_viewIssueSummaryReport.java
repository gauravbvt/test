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

public class MAV0162_viewIssueSummaryReport extends TestCase{

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
	public void testMAV0162_viewIssueSummaryReport() throws UIAutomationException, InterruptedException {
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
						
			//Sign Out from 'Issue Summary Report' page
			HeaderController headerController=new HeaderController();
			headerController.signOut();

		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0162_viewIssueSummaryReport");
		
			//Sign out from Issue summary report page
			HeaderController headerController=new HeaderController();
			headerController.signOut();
			
			//Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	/**
     * Loads Test Data for MAV0162_viewIssueSummaryReport.
     * @return void
     * @param  void
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
			File MAV0162_viewIssueSummaryReport=new File(path + "MAV0162_viewIssueSummaryReport.xml");
			
			Document docMAV0162_viewIssueSummaryReport=db.parse(MAV0162_viewIssueSummaryReport);
			Element eleMAV0162_viewIssueSummaryReport=docMAV0162_viewIssueSummaryReport.getDocumentElement();
	              
	        Element oXmlEleMAV0162_viewIssueSummaryReport = (Element) eleMAV0162_viewIssueSummaryReport;
	     	        
			this.testData.put("ChannelsURL",oXmlEleMAV0162_viewIssueSummaryReport.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0162_viewIssueSummaryReport.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("headingOfIssuesSummary",oXmlEleMAV0162_viewIssueSummaryReport.getElementsByTagName("headingOfIssuesSummary").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0162_viewIssueSummaryReport not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0162_viewIssueSummaryReport.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0162_viewIssueSummaryReport can not be parsed.");
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
