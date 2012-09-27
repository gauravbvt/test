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
 * TestCase Id: MAV0154_viewSocialPanelShowAllMessages 
 * Summary: Verify by clicking on 'show all messages' in Messages tab
 * in 'Social panel', messages should gets displayed
 * @author afour
 *
 */
public class MAV0154_viewSocialPanelShowAllMessages extends TestCase{
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
	public void testMAV0154_viewSocialPanelShowAllMessages() throws UIAutomationException, InterruptedException {
		try {
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
			    
			// Login page
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
									
			// Click on 'Messages' tab under 'Collaboration Panel'
			HomePage homePage=new HomePage();
			homePage.clickMessagesTabInSocialPanel();
		
			// Click on 'hide broadcast' link and 'show all messages' link
			homePage.clickHideBroadcastsInSocialPanel();
											
			// Sign Out from 'Home' page
			HeaderController headerController=new HeaderController();
			headerController.signOut();
		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0154_viewSocialPanelShowAllMessages");
		
			//Sign out from home page
			HeaderController headerController=new HeaderController();
			headerController.signOut();
			
			//Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	/**
     * Loads Test Data for MAV0154_viewSocialPanelShowAllMessages.
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
			File MAV0154_viewSocialPanelShowAllMessages=new File(path + "MAV0154_viewSocialPanelShowAllMessages.xml");
			
			Document docMAV0154_viewSocialPanelShowAllMessages=db.parse(MAV0154_viewSocialPanelShowAllMessages);
			Element eleMAV0154_viewSocialPanelShowAllMessages=docMAV0154_viewSocialPanelShowAllMessages.getDocumentElement();
	              
	        Element oXmlEleMAV0154_viewSocialPanelShowAllMessages = (Element) eleMAV0154_viewSocialPanelShowAllMessages;
	     	        
			this.testData.put("ChannelsURL",oXmlEleMAV0154_viewSocialPanelShowAllMessages.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0154_viewSocialPanelShowAllMessages.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0154_viewSocialPanelShowAllMessages not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0154_viewSocialPanelShowAllMessages.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0154_viewSocialPanelShowAllMessages can not be parsed.");
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

	
//	public MAV0154_viewSocialPanelShowAllMessages()
//	{
//		try{
//			
//			GlobalVariables.sTestCaseId = "MAV0154_viewSocialPanelShowAllMessages";
//			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
//			LogFunctions.writeLogs(GlobalVariables.sDescription);
//			System.out.println(GlobalVariables.sDescription);
//			// Call login()
//			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
//			if (GlobalVariables.bIsSuccess) {
//								
//				// Click on Message tab on social panel
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription="Message tab is present";
//				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathSocialMessages"))).click();
//				// Write Results
//			    LogFunctions.writeLogs(GlobalVariables.sDescription);
//			    LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//			    		GlobalVariables.sBlank, GlobalVariables.sBlank);
//			    // WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);
//				
//				// Click on hide broadcast message link
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription="Hide broadcast";
//				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathHideBroadcast"))).click();
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathHideBroadcast")));
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathHideBroadcast")));
//				// Assertion: Verify that message tab is present on social panel
//				if (GlobalVariables.oElement.getText().equals(GlobalVariables.viewElements.get("showAllMessages"))){
//			    	// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//			    }
//			    else{
//			    	GlobalVariables.sVerifyError ="Verification Failed "+"Expected "+GlobalVariables.viewElements.get("hideBroadcast")+" Actual "+GlobalVariables.oElement.getText();
//			    	// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription+""+GlobalVariables.sFailed);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
//			    }
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//				
//				// Call logout()
//			    GlobalVariables.iStepNo++ ;
//			    GlobalVariables.sDescription = "Logout is successful";
//			    GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.home.get("sXpathLogoutHomePage"))).click();
//			    GlobalVariables.oDriver.quit();
//			    // Write Results
//			    LogFunctions.writeLogs(GlobalVariables.sDescription);
//			    LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//			    		GlobalVariables.sBlank, GlobalVariables.sBlank);
//			    LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
//			    System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
//			}
//			else
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//		} 
//		catch (Exception e) {
//			if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						e.getMessage(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.id("stackTrace"));
//				LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
//				GlobalVariables.oDriver.findElement(By.xpath("/html/body/form/div[2]/div/div[2]/a")).click();
//				GlobalVariables.oDriver.quit();
//			}
//			else {
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						e.getMessage(),GlobalVariables.sBlank);
//				GlobalVariables.oDriver.findElement(By.xpath("/html/body/form/div[2]/div/div[2]/a")).click();
//				GlobalVariables.oDriver.quit();
//			}
//			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
//		}
//	}
//	public static void main(String args[]) {
//		try {
//			GenericFunctionLibrary.initializeTestData();
//			GenericFunctionLibrary.loadObjectRepository();
//			new MAV0154_viewSocialPanelShowAllMessages();
//			GenericFunctionLibrary.tearDownTestData();
//			ReportFunctions.generateAutomationReport();
//		} 
//		catch (Exception oException) {
//			// TODO Auto-generated catch block
//			oException.printStackTrace();
//		}
//	}
}
