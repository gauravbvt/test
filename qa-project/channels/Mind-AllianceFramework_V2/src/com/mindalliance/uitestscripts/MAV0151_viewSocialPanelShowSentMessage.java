package com.mindalliance.uitestscripts;
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
import com.mindalliance.pages.DomainPlanPage;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;


/**
 * Testcase ID: MAV0151_viewSocialPanelShowSentMessage
 * 	   Summary: Verify that user is able to undo the added segment
 * 		  Note: No 'undo add new segment' option.
 * @author afour
 * 
 */

public class MAV0151_viewSocialPanelShowSentMessage extends TestCase {
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0151_viewSocialPanelShowSentMessage";
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
	public void testMAV0151_viewSocialPanelShowSentMessage() throws UIAutomationException, IOException {
		try{
			// Enter URL of Channels
			stepNo++;
			description="URL Entered";	
			BrowserController browserController=new BrowserController();
			browserController.enterURL();
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
						
			// Domain Plans
			stepNo++;
			description="Domain Plans";
			DomainPlanPage domainPlanPage= new DomainPlanPage();
			domainPlanPage.clickDomainPlans();	
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
						
			 // Click on 'Messages' tab under 'Social Panel'
			 stepNo++;
			 description="Messages tab opened";
			 HomePage homePage=new HomePage();
			 homePage.clickMessagesTabInSocialPanel();
			 // Write log			
			 LogFunctions.writeLogs(description);
			 LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
									
	    	 // Click on 'show sent' link and 'show received' link
			 stepNo++;
			 description="Sent messages are shown";
			 homePage.clickShowSent();
			 // Write log			
			 LogFunctions.writeLogs(description);
			 LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
													
			 // Sign out from 'Home' page
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
					    
			// Sign out from home page
			stepNo++;
		    description="Logout successful";
			HeaderController headerController=new HeaderController();
			headerController.signOut();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
							
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
     * Loads Test Data for MAV0151_viewSocialPanelShowSentMessage.
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
			File MAV0151_viewSocialPanelShowSentMessage=new File(path + "MAV0151_viewSocialPanelShowSentMessage.xml");
			
			Document docMAV0151_viewSocialPanelShowSentMessage=db.parse(MAV0151_viewSocialPanelShowSentMessage);
			Element eleMAV0151_viewSocialPanelShowSentMessage=docMAV0151_viewSocialPanelShowSentMessage.getDocumentElement();
	              
	        Element oXmlEleMAV0151_viewSocialPanelShowSentMessage = (Element) eleMAV0151_viewSocialPanelShowSentMessage;
	       	
	      
			this.testData.put("ChannelsURL",oXmlEleMAV0151_viewSocialPanelShowSentMessage.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0151_viewSocialPanelShowSentMessage.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0151_viewSocialPanelShowSentMessage can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0151_viewSocialPanelShowSentMessage.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0151_viewSocialPanelShowSentMessage can not be parsed.");
		}
			
	}
}