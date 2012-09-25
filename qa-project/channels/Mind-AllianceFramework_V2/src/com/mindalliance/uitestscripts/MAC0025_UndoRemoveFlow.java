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

public class MAC0025_UndoRemoveFlow extends TestCase{


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
	public void testMAC0025_UndoRemoveFlow() throws UIAutomationException {
		try{
		    // Enter URL of Channels
			BrowserController browserController=new BrowserController();
			browserController.enterURL(testData.get("ChannelsURL"),testData.get("Title"));
			    
			// Login page
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
			
			// Plan Page
			HomePage homePage=new HomePage();
			homePage.clickCollaborationPlanLink();
			
			// Close Plan Map window
			PlanPage planPage=new PlanPage();
			planPage.closePlanMap();
			
			// Click Actions pop up menu and Add New Segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
						
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForUndoRemoveFlow"));
					
			// Close Segment window
			planPage.closeSegmentWindow();
			
			// click on 'strench up forms' icon
			planPage.clickStrenchUpForm();
			
			// Click on 'Add' button under 'Receives' panel
			planPage.clickAddInReceivesPanel();
			
			// Enter Information Name
			planPage.enterInformationNameInReceivesPanel(testData.get("InformationInRecievesPanel"));
			
			// Select 'Other..' option form 'From Task:' dropdown list
			planPage.selectFrom(testData.get("OtherTaskName"));
			
			// Enter From Task name
			planPage.enterFromTaskName(testData.get("FromTaskNameInRecevesPanel"));
			
			// Click on 'Remove flow' under 'Actions' pop up menu
			planPage.clickPopupMenu(testData.get("ActionsInReceivesPanel"));
			planPage.clickSubmenu(testData.get("RemoveFlowInReceivesPanel"));	
			
			// Click on 'Undo remove flow" in actions pop up menu
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoRemoveFlow"));
			
			// Click on Remove this segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
			
			
			// Sign Out from 'Home' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
		}
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAC0025_UndoRemoveFlow");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAC0025_UndoRemoveFlow.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAC0025_UndoRemoveFlow=new File(path + "MAC0025_UndoRemoveFlow.xml");
			
			Document docMAC0025_UndoRemoveFlow=db.parse(MAC0025_UndoRemoveFlow);
			Element eleMAC0025_UndoRemoveFlow=docMAC0025_UndoRemoveFlow.getDocumentElement();
	              
	        Element oXmlEleMAC0025_UndoRemoveFlow = (Element) eleMAC0025_UndoRemoveFlow;
	        
	               	
	        this.testData.put("ChannelsURL",oXmlEleMAC0025_UndoRemoveFlow.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0025_UndoRemoveFlow.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("InformationInRecievesPanel",oXmlEleMAC0025_UndoRemoveFlow.getElementsByTagName("informationInRecievesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("FromTaskNameInRecevesPanel",oXmlEleMAC0025_UndoRemoveFlow.getElementsByTagName("fromTaskNameInRecevesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("OtherTaskName",oXmlEleMAC0025_UndoRemoveFlow.getElementsByTagName("otherTaskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInReceivesPanel",oXmlEleMAC0025_UndoRemoveFlow.getElementsByTagName("actionsInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveFlowInReceivesPanel",oXmlEleMAC0025_UndoRemoveFlow.getElementsByTagName("removeFlowInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
		 	
			this.testData.put("Actions",oXmlEleMAC0025_UndoRemoveFlow.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AddNewSegment",oXmlEleMAC0025_UndoRemoveFlow.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAC0025_UndoRemoveFlow.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForUndoRemoveFlow",oXmlEleMAC0025_UndoRemoveFlow.getElementsByTagName("segmentForUndoRemoveFlow").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoRemoveFlow",oXmlEleMAC0025_UndoRemoveFlow.getElementsByTagName("undoRemoveFlow").item(0).getChildNodes().item(0).getNodeValue());
		 	
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0025_UndoRemoveFlow not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0025_UndoRemoveFlow.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0025_UndoRemoveFlow can not be parsed.");
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

	
//	public MAC0025_UndoRemoveFlow() {
//		try {
//			GlobalVariables.sTestCaseId = "MAC0025_UndoRemoveFlow";
//			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
//			LogFunctions.writeLogs(GlobalVariables.sDescription);
//			System.out.println(GlobalVariables.sDescription);
//			// Call login()
//			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
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
//				Thread.sleep(3000);
//				
//				// Click on 'Add new segment' option under 'Actions' pop up menu
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "Second segment added";
//				ApplicationFunctionLibrary.addSegment(GlobalVariables.testData.get("Segment For Undo Remove Flow"), "New");
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(3000);
//				
//				// Add 'New Task' under Action pop-up menu
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription="New task added";
//				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewTask"));
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(5000);
//				// Add details for New Task
//				GlobalVariables.oDriver.findElement(By.name("segment:part:task")).click();
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:task"));
//				for (int i = 0; i <= 50; i++)
//					GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
//				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Remove Flow"));
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(5000);
//				GlobalVariables.oElement.sendKeys(Keys.TAB);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(5000);
//				// Click on hide details from action pop-menu bar
//				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathTaskShowMenu"),GlobalVariables.viewElements.get("hideDetails"));
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(3000);
//				
//				//  Click on 'add info received' option under 'Receives'  section
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription="Receive Info";
//				// Click on legend for maximize the graph
//				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathStretchUpShrinkBack"))).click();
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);
////				GlobalVariables.oDriver.findElement(By.linkText("Add info received")).click();
//				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAddInfoReceive"))).click();
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(5000);
//				GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:name")).click();
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:name"));
//				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Add Received Info"));
//				GlobalVariables.oElement.sendKeys(Keys.ENTER);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(5000);
//				GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:other:firstChoice")));
//				List<WebElement> options = GlobalVariables.oDropDown.getOptions();
//			    for(WebElement option : options) {
//			    	if(GlobalVariables.viewElements.get("other").equals(option.getText())){
//			    			option.setSelected();
//			    			break;
//			    	}
//			    }
//			    // WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(5000);
//				GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:other:secondChoice:secondChoice-input")).click();
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:receives:flows-div:flows:0:flow:other:secondChoice:secondChoice-input"));
//				for (int i = 0; i <= 50; i++)
//					GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
//				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Undo Remove Flow"));
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(5000);
//				GlobalVariables.oElement.sendKeys(Keys.TAB);
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(5000);
//				
//				// Click on Remove flow under more pop up menu
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription="Remove Flow";
//				// Click on Remove flow under more pop up menu
//				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathReceiveInfoActionMenu"),GlobalVariables.viewElements.get("removeFlow"));
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(3000);
//				GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathStretchUpShrinkBack"))).click();
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(3000);
//				// Get a handle to the open alert, prompt or confirmation
//				Alert alert = GlobalVariables.oDriver.switchTo().alert();
//				// And acknowledge the alert (equivalent to clicking "OK")
//				alert.accept();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(3000);
//				
//				// Click on Undo remove flow under Action pop up menu
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription="Undo Remove flow done";
//				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("undoRemoveFlow"));
//				// ASSERTION: When details entered, the flow should be connected between two nodes
//				GlobalVariables.bIsSuccess = Boolean.FALSE;
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("flow-map"));
//				List<WebElement> areas = GlobalVariables.oElement.findElements(By.tagName("area"));
//				for(WebElement area:areas){
//					GlobalVariables.sStrCheck=area.getAttribute("id");
//					System.out.println(area.getAttribute("id"));
//				if(area.getAttribute("id").equals("node1"))
//					 GlobalVariables.bIsSuccess = Boolean.TRUE;
//					 break;
//			    }
//				if (GlobalVariables.bIsSuccess == Boolean.TRUE) {
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				}
//				else
//			    {
//					GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'edge2' "+" Actual " + GlobalVariables.sStrCheck;
//			    	// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//							GlobalVariables.sBlank, GlobalVariables.sVerifyError);
//			    }					
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(5000);
//				GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("select-segment:sg-sel")));
//				options = GlobalVariables.oDropDown.getOptions();
//			    for(WebElement option : options) {
//			    	if(option.getText().equals("Segment For Undo Remove Flow")){
//			    			option.setSelected();
//			    			break;
//			    	}
//			    }
//			    // WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(3000);
//				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
//				// Get a handle to the open alert, prompt or confirmation
//				alert = GlobalVariables.oDriver.switchTo().alert();
//				alert.accept();
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(3000);
//				
//			    // Call logout()
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "Logout is successful";
//				ApplicationFunctionLibrary.logout();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//
//				LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
//				System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
//			}
//			else{
//				LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
//				System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
//				
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				System.out.println("Unable to Undo Remove Flow" + ReportFunctions.getScreenShot("Undo Remove Flow failed"));
//				GlobalVariables.oDriver.quit();
//			}
//		} 
//		catch (Exception e) {
//			if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						e.getMessage(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.id("stackTrace"));
//				LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
//				System.out.println("Unable to Undo Remove Flow"+ReportFunctions.getScreenShot("Undo Remove Flow failed"));
//				ApplicationFunctionLibrary.logout();
//			}
//			else {
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						e.getMessage(),GlobalVariables.sBlank);
//				System.out.println("Unable to Undo Remove Flow"+ReportFunctions.getScreenShot("Undo Remove Flow failed"));
//				ApplicationFunctionLibrary.logout();	
//			}
//			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
//		}
//	}
//    public static void main(String args[]) {
//		try {
//			GenericFunctionLibrary.initializeTestData();
//			GenericFunctionLibrary.loadObjectRepository();
//			new MAC0025_UndoRemoveFlow();
//			GenericFunctionLibrary.tearDownTestData();
//			ReportFunctions.generateAutomationReport();
//		} 
//		catch (Exception oException) {
//			// TODO Auto-generated catch block
//			oException.printStackTrace();
//			System.out.println("Unable to undo remove flow"+ReportFunctions.getScreenShot("Undo remove flow failed"));
//		}
//	}
}