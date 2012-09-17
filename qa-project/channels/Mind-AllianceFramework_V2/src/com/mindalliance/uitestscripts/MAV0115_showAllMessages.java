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
import com.mindalliance.pages.PlanPage;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MAV0115_showAllMessages extends TestCase{

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
	public void testMAV0115_showAllMessages() throws UIAutomationException, InterruptedException {
		try {
			// Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
			    
			// Login page
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
								
			// Plan Page
			HomePage homePage=new HomePage();
			homePage.clickCollaborationPlanLink();	
			
			// Click on 'Messages' tab under 'Collaboration Panel'
			PlanPage planPage=new PlanPage();
			planPage.clickMessagesTab();
			
			// Click on 'hide broadcast' link and 'show all messages' link
			planPage.clickHideBroadcasts(testData.get("HideFlag"));
								
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();

		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0115_showAllMessages");
		
			//Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			//Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	/**
     * Loads Test Data for MAV0115_showAllMessages.
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
			File MAV0115_showAllMessages=new File(path + "MAV0115_showAllMessages.xml");
			
			Document docMAV0115_showAllMessages=db.parse(MAV0115_showAllMessages);
			Element eleMAV0115_showAllMessages=docMAV0115_showAllMessages.getDocumentElement();
	              
	        Element oXmlEleMAV0115_showAllMessages = (Element) eleMAV0115_showAllMessages;
	     	        
			this.testData.put("ChannelsURL",oXmlEleMAV0115_showAllMessages.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0115_showAllMessages.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("HideFlag",oXmlEleMAV0115_showAllMessages.getElementsByTagName("hideFlag").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0115_showAllMessages not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0115_showAllMessages.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0115_showAllMessages can not be parsed.");
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

	
//	public MAV0115_showAllMessages(){
//		try {
//			GlobalVariables.sTestCaseId = "MAV0115_showAllMessages";
//			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
//			LogFunctions.writeLogs(GlobalVariables.sDescription);
//			System.out.println(GlobalVariables.sDescription);
//			// Call login()
//			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
//			  
//			if (GlobalVariables.bIsSuccess) {
//				
//				// Click on 'Information Sharing Model' link
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "Navigated to Information Sharing Model";
//				GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("informationSharingModel"))).click();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);
//				
//				// Maximize Browser Window
//				((JavascriptExecutor) GlobalVariables.oDriver).executeScript("if (window.screen) {window.moveTo(0, 0);window.resizeTo(window.screen.availWidth, window.screen.availHeight);};");
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(2000);
//				
//				// Click on Messages tab from social panel
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription="Messages tab";
//				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathMessages"))).click();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);
//				
//				// Click on 'hide broadcasts' Link
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription="hide broadcasts";
//				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowAllUsers"))).click();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);
//				
//				// Click on 'show all messages' Link
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription="show all messages";
//				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowAllUsers"))).click();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);
//											
//				// Assertion: Verify that show all messages link changes to hide broadcast 
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription="Show all messages link changes to hide broadcast";
//				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathShowAllUsers")));
//				GlobalVariables.oElement=GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("hideBroadcast")));
//				if (GlobalVariables.oElement.getText().equals(GlobalVariables.viewElements.get("hideBroadcast"))) {
//			    	// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//			    }
//			    else{
//			    	GlobalVariables.sVerifyError ="Verification Failed "+"Expected "+GlobalVariables.viewElements.get("hideBroadcast")+" Actual "+GlobalVariables.oElement.getText();
//			    	// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
//			    }
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);	
//				
//				// Call logout()
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "Logout is successful";
//				ApplicationFunctionLibrary.logout();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);	
//				
//				LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
//				System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
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
//				ApplicationFunctionLibrary.logout();
//			}
//			else {
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						e.getMessage(),GlobalVariables.sBlank);
//				ApplicationFunctionLibrary.logout();	
//			}
//			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
//		}
//	}
//	public static void main(String args[]) {
//		try {
//			GenericFunctionLibrary.initializeTestData();
//			GenericFunctionLibrary.loadObjectRepository();
//			new MAV0115_showAllMessages();
//			GenericFunctionLibrary.tearDownTestData();
//			ReportFunctions.generateAutomationReport();
//		} 
//		catch (Exception oException) {
//			// TODO Auto-generated catch block
//			oException.printStackTrace();
//		}
//	}
}
