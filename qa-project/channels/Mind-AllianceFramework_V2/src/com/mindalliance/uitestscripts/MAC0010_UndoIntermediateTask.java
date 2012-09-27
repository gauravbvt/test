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

/**
 * Testcase ID: MAC0010_UndoInterimediateTask
 * 	   Summary: Verify that user is able to undo the Intermediate flow
 * @author AFour
 * 
 */
public class MAC0010_UndoIntermediateTask extends TestCase {

	
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
	public void testMAC0010_UndoIntermediateTask() throws UIAutomationException {
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
			planPage.enterSegmentName(testData.get("SegmentForUndoIntermediateTask"));
					
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
			
			// Click on Remove this segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
						
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
		}
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAC0010_UndoIntermediateTask");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAC0010_UndoIntermediateTask.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAC0010_UndoIntermediateTask=new File(path + "MAC0010_UndoIntermediateTask.xml");
			
			Document docMAC0010_UndoIntermediateTask=db.parse(MAC0010_UndoIntermediateTask);
			Element eleMAC0010_UndoIntermediateTask=docMAC0010_UndoIntermediateTask.getDocumentElement();
	              
	        Element oXmlEleMAC0010_UndoIntermediateTask = (Element) eleMAC0010_UndoIntermediateTask;
	        
	               	
	        this.testData.put("ChannelsURL",oXmlEleMAC0010_UndoIntermediateTask.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0010_UndoIntermediateTask.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("InformationInRecievesPanel",oXmlEleMAC0010_UndoIntermediateTask.getElementsByTagName("informationInRecievesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("FromTaskNameInRecevesPanel",oXmlEleMAC0010_UndoIntermediateTask.getElementsByTagName("fromTaskNameInRecevesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("OtherTaskName",oXmlEleMAC0010_UndoIntermediateTask.getElementsByTagName("otherTaskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInReceivesPanel",oXmlEleMAC0010_UndoIntermediateTask.getElementsByTagName("actionsInReceivesPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Intermediate",oXmlEleMAC0010_UndoIntermediateTask.getElementsByTagName("intermediate").item(0).getChildNodes().item(0).getNodeValue());
		 	
			this.testData.put("Actions",oXmlEleMAC0010_UndoIntermediateTask.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AddNewSegment",oXmlEleMAC0010_UndoIntermediateTask.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAC0010_UndoIntermediateTask.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForUndoIntermediateTask",oXmlEleMAC0010_UndoIntermediateTask.getElementsByTagName("segmentForUndoIntermediateTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoIntermediate",oXmlEleMAC0010_UndoIntermediateTask.getElementsByTagName("undoIntermediate").item(0).getChildNodes().item(0).getNodeValue());
		 	
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0010_UndoIntermediateTask not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0010_UndoIntermediateTask.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0010_UndoIntermediateTask can not be parsed.");
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

//	public MAC0010_UndoIntermediateTask() {
//		try {
//			GlobalVariables.sTestCaseId = "MAC0010_UndoIntermediateTask";
//			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
//			LogFunctions.writeLogs(GlobalVariables.sDescription);
//			System.out.println(GlobalVariables.sDescription);
//			// Call login()
//			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
//				if (GlobalVariables.bIsSuccess) {
//					
//					// Click on 'Information Sharing Model' link
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "Navigated to Information Sharing Model";
//					GlobalVariables.oDriver.findElement(By.linkText(GlobalVariables.viewElements.get("informationSharingModel"))).click();
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					
//					// Click on 'Add new segment' option under 'Actions' pop up menu
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "New segment added";
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewSegment"));
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//			
//					// Enter the details
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "New segment updated";
//					GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name")).click();
//					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name"));
//					for (int i = 0; i <= 8; i++)
//						GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
//					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Segment For Undo Intermediate"));
//					// Click on 'done' button
//					GlobalVariables.oDriver.findElement(By.className("close")).click();
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//
//					// Update the Information of the default task
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "Task updated";
//					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathDoingSomeThingLink"))).click();
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(5000);
//					GlobalVariables.oDriver.findElement(By.name("segment:part:task")).click();
//					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:task"));
//					for (int i = 0; i <= 15; i++)
//						GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
//					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Task Sender"));
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(5000);
//					GlobalVariables.oElement.sendKeys(Keys.TAB);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(5000);
//					// Click on hide details from action pop-menu bar
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathTaskShowMenu"),GlobalVariables.viewElements.get("hideDetails"));
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//			
//					// Add info sends flow
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "Send Flow added";
////					GlobalVariables.oDriver.findElement(By.linkText("Add info sent")).click();
//					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathAddInfoSend"))).click();
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(2000);
//					
//					// Create Flow between respective nodes
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "Flow created between nodes";
//					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:name"));
//					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("This is a Flow"));
//					GlobalVariables.oElement.sendKeys(Keys.TAB);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(2000);
//					// To
//					GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:other:firstChoice")).click();
//					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:other:firstChoice"));
//					GlobalVariables.oElement.sendKeys("Other...");
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(2000);
//					GlobalVariables.oElement.sendKeys(Keys.ENTER);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(2000);
//					// Enter Task Name of Sender
//					GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:other:secondChoice:secondChoice-input")).click();
//					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:sends:flows-div:flows:0:flow:other:secondChoice:secondChoice-input"));
//					for (int i = 0; i <= 17 ; i++)
//						GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
//					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Task Receiver"));
//					GlobalVariables.oElement.sendKeys(Keys.TAB);
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(5000);
//					
//					// Create Intermediate Flow
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "Intermediate Flow created";
//					// Click on legend for maximize the graph
//					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathStretchUpShrinkBack"))).click();
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathSendInfoActionMenu"),GlobalVariables.viewElements.get("addIntermediate"));
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					// Click on Legend to minimize the information flow details
//					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathStretchUpShrinkBack"))).click();
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					// Click on 'About plan segment' option under 'Show' pop up menu
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlanSegment"));
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(5000);
//					// Click on 'task mover' option under 'Shows' pop up menu
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAbtPlanSegShowMenu"),GlobalVariables.viewElements.get("taskMover"));
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					// ASSERTION: When clicked on 'Add intermediate' option, the task should be created between the selected task and its respective other task
//					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("data-table"));
//					List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("td"));
//					GlobalVariables.bIsSuccess = Boolean.FALSE;
//					for (WebElement td: tds){
//						GlobalVariables.sStrCheck=td.getText();
//						if (td.getText().equals("doing something")){
//							GlobalVariables.bIsSuccess = Boolean.TRUE;
//							break;
//						}
//					}
//					if (GlobalVariables.bIsSuccess == Boolean.TRUE) {
//						// Write Results
//						LogFunctions.writeLogs(GlobalVariables.sDescription);
//						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//								GlobalVariables.sBlank, GlobalVariables.sBlank);
//					}
//					else
//				    {
//						GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'doing something' "+" Actual " + GlobalVariables.sStrCheck;
//				    	// Write Results
//						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
//						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//								GlobalVariables.sBlank, GlobalVariables.sVerifyError);
//				    }
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					
//					// Click on 'Undo add intermediate' option under 'Actions' pop up menu
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "Undo add intermediate done";
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("undoAddIntermediate"));
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					// ASSERTION: When clicked on 'Undo add intermediate' option, the intermediate task should be removed
//					GlobalVariables.bIsSuccess = Boolean.FALSE;
//					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("data-table"));
//					tds = GlobalVariables.oElement.findElements(By.tagName("td"));
//					for (WebElement td: tds){
//						GlobalVariables.sStrCheck=td.getText();
//						if (td.getText().equals("")){
//							GlobalVariables.bIsSuccess = Boolean.TRUE;
//							break;
//						}
//					}
//					if (GlobalVariables.bIsSuccess == Boolean.TRUE) {
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					}
//					else
//				    {
//						GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'doing something' "+" Actual " + GlobalVariables.sStrCheck;
//				    	// Write Results
//						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
//						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//								GlobalVariables.sBlank, GlobalVariables.sVerifyError);
//				    }
//					GlobalVariables.oDriver.findElement(By.className("close")).click();
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
//					Alert alert = GlobalVariables.oDriver.switchTo().alert();
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(1000);
//					alert.accept();
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					
//					// Call logout()
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "Logout is successful";
//					ApplicationFunctionLibrary.logout();
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					
//					LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
//					System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution completed");
//				}
//				else{
//					LogFunctions.writeLogs("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
//					System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
//					
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					System.out.println("Unable to undo intermediate task"+ReportFunctions.getScreenShot("Undo intermediate task failed"));
//					GlobalVariables.oDriver.quit();
//				}
//		} 
//		catch (Exception e) {
//			if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						e.getMessage(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.id("stackTrace"));
//				LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
//				System.out.println("Unable to undo intermediate task"+ReportFunctions.getScreenShot("Undo intermediate task failed"));
//				ApplicationFunctionLibrary.logout();
//			}
//			else {
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						e.getMessage(),GlobalVariables.sBlank);
//				System.out.println("Unable to undo intermediate task"+ReportFunctions.getScreenShot("Undo intermediate task failed"));
//				ApplicationFunctionLibrary.logout();	
//			}
//			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
//		}
//	}
//	public static void main(String args[]) {
//		try {
//			GenericFunctionLibrary.initializeTestData();
//			GenericFunctionLibrary.loadObjectRepository();
//			new MAC0010_UndoIntermediateTask();
//			GenericFunctionLibrary.tearDownTestData();
//			ReportFunctions.generateAutomationReport();
//		} 
//		catch (Exception oException) {
//			// TODO Auto-generated catch block
//			oException.printStackTrace();
//			System.out.println("Unable to undo intermediate task"+ReportFunctions.getScreenShot("Undo intermediate task failed"));
//		}
//	}
}