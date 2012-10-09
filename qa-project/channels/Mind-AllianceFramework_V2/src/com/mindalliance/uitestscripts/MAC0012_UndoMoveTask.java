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
 * Testcase ID: MAC0012_UndoMoveTask
 * 	   Summary: Verify that user is able to undo the tasks which were moved
 * @author AFour
 * 
 */
public class MAC0012_UndoMoveTask extends TestCase {

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
	public void testMAC0012_UndoMoveTask() throws UIAutomationException {
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
						
			// Click on 'Add New Segment' under 'Actions' pop up menu
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
						
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("OtherSegment"));
			
			// Close Segment window
			planPage.closeSegmentWindow();			
			
			// Click on 'Add New Segment' under 'Actions' pop up menu
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
						
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForUndoMoveTask"));
			
			// Close Segment window
			planPage.closeSegmentWindow();
			
			planPage.selectSegmentFromList(testData.get("OtherSegment"));
			
				
//			// Add New Task
//			planPage.clickPopupMenu(testData.get("Actions"));
//			planPage.clickSubmenu(testData.get("AddNewTask"));
//			
//			//Enter Task Name
//			planPage.enterTaskName(testData.get("TaskName1"));
//			
//			// Add New Task
//			planPage.clickPopupMenu(testData.get("Actions"));
//			planPage.clickSubmenu(testData.get("AddNewTask"));
//			
//			//Enter Task Name
//			planPage.enterTaskName(testData.get("TaskName2"));
//			
//			
//			// Click on 'About Plan segment' under 'Show' pop up menu
//			planPage.clickPopupMenu(testData.get("Show"));
//			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
//			
//			// Open Task Mover
//			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
//			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
//			
//			// Verify Task is added
//			planPage.verifyTaskNameInTaskMover(testData.get("TaskName1"));
//			planPage.verifyTaskNameInTaskMover(testData.get("TaskName2"));
//			
//			// Select segment from dropdown list
//			planPage.selectSegmentInTaskMover(testData.get("OtherSegment"));
//			
//			// Select task to move
//			planPage.clickOnCheckboxOfTaskNameInTaskMover(testData.get("TaskName1"));
//			
//			// click on 'Move' button
//			planPage.clickOnMoveInTaskMover();			
//			
//			// Close Task Mover window
//			planPage.closeTaskMoverWindow();
//			
//			// Close Segment window
//			planPage.closeSegmentWindow();
//			
//			// Verify Task is moved
//			// Select other segement from segment dropdown list
//			planPage.selectSegmentFromList(testData.get("OtherSegment"));
//			
//						
//			// Undo Update Task
//			planPage.clickPopupMenu(testData.get("Actions"));
//			planPage.clickSubmenu(testData.get("UndoMoveTask"));	
//			
//			
//				
////			// Verify duplicated task should be undone			
////			// Click on 'About Plan segment' under 'Show' pop up menu
////			planPage.clickPopupMenu(testData.get("Show"));
////			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
////			
////			// Open Task Mover
////			planPage.clickPopupMenu(testData.get("ActionsInSegment"));
////			planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
////			planPage.verifyTaskIsNotDuplicated(testData.get("TaskName"));
////			
////			// Close Task Mover window
////			planPage.closeTaskMoverWindow();
////			
////			// Close Segment window
////			planPage.closeSegmentWindow();
						
			//Click on Remove this segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
								
			//Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
		}
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAC0012_UndoMoveTask");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAC0012_UndoMoveTask.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAC0012_UndoMoveTask=new File(path + "MAC0012_UndoMoveTask.xml");
			
			Document docMAC0012_UndoMoveTask=db.parse(MAC0012_UndoMoveTask);
			Element eleMAC0012_UndoMoveTask=docMAC0012_UndoMoveTask.getDocumentElement();
	              
	        Element oXmlEleMAC0012_UndoMoveTask = (Element) eleMAC0012_UndoMoveTask;
	        
	               	
	        this.testData.put("Actions",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForUndoMoveTask",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("segmentForUndoMoveTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("OtherSegment",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("otherSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AddNewTask",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("addNewTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TaskName1",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("taskName1").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TaskName2",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("taskName2").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ShowInTaskPanel",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("showInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("DetailsInTaskPanel",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("detailsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoMoveTask",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("undoMoveTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ActionsInSegment",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("actionsInSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("MoveTasksInSegment",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("moveTasksInSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Show",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AboutPlanSegment",oXmlEleMAC0012_UndoMoveTask.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0012_UndoMoveTask not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0012_UndoMoveTask.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0012_UndoMoveTask can not be parsed.");
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

	
	
//	public MAC0012_UndoMoveTask() {
//		try {
//			GlobalVariables.sTestCaseId = "MAC0012_UndoMoveTask";
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
//					Thread.sleep(10000);
//					
//					// Click on 'Add new segment' option under 'Actions' pop up menu
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "First segment added";
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewSegment"));
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					
//					// Enter the details for new segment
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "First Segment's details entered";
//					GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name")).click();
//					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name"));
//						for (int i = 0; i <= 8; i++)
//							GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
//					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("The Other Segment"));
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					
//					// Click on 'done' button
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "Segment updated";
//					GlobalVariables.oDriver.findElement(By.className("close")).click();
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
//					GlobalVariables.sDescription = "Second segment added";
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewSegment"));
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					
//					// Enter the details for new segment
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "Second Segment's details entered";
//					GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name")).click();
//					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name"));
//						for (int i = 0; i <= 8; i++)
//							GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
//					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Segment For Undo Move Task"));
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					
//					// Click on 'done' button
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "Second Segment Updated";
//					GlobalVariables.oDriver.findElement(By.className("close")).click();
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					
//					// Click 'Add new task' option under 'Actions' pop up menu
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "First Task added";
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewTask"));
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
//					GlobalVariables.sDescription = "First Task updated";
//					GlobalVariables.oDriver.findElement(By.name("segment:part:task")).click();
//					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:task"));
//					for (int i = 0; i <= 15; i++)
//						GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
//					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Task 1"));
//					GlobalVariables.oElement.sendKeys(Keys.TAB);
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					
//					// Click 'Add new task' option under 'Actions' pop up menu
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "Second Task added";
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewTask"));
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
//					GlobalVariables.sDescription = "Second Task updated";
//					GlobalVariables.oDriver.findElement(By.name("segment:part:task")).click();
//					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("segment:part:task"));
//					for (int i = 0; i <= 15; i++)
//						GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
//					GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Task 2"));
//					GlobalVariables.oElement.sendKeys(Keys.TAB);
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					
//					// Click on 'About plan segment' option under 'Show' pop up menu
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "About Plan Segment section opened";
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlanSegment"));
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(5000);
//					
//					// Click on 'Task mover' option under 'Shows' pop up menu
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "Task Mover section opened";
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAbtPlanSegShowMenu"),GlobalVariables.viewElements.get("taskMover"));
//					// Write Results
//					LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					
//					// Select the other segment to move tasks
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "Other Segment selected";
//					GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:destinationSegment")));
//					List <WebElement> options = GlobalVariables.oDropDown.getOptions();
//				    for(WebElement option : options) {
//				    	if(option.getText().equals("The Other Segment")){
//				    			option.setSelected();
//				    			break;
//				    	}
//				    }
//			        // Write Results
//				    LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);   
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//
//					// Select the tasks to move
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "Tasks selected";
//					GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:parts:movableParts:body:rows:2:cells:1:cell:checkBox")).click();
//					GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:parts:movableParts:body:rows:3:cells:1:cell:checkBox")).click();
//			        // Write Results
//				    LogFunctions.writeLogs(GlobalVariables.sDescription);
//					LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//							GlobalVariables.sBlank, GlobalVariables.sBlank);   
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					
//					// Click on 'Move' button
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "Task moved";
//					GlobalVariables.oDriver.findElement(By.xpath(GlobalVariables.plan.get("sXpathMoveTaskButton"))).click();
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(5000);
//					// ASSERTION 1.1: When clicked on 'Move' button, the selected task(s) should be moved from current segment to the destination segment
//					GlobalVariables.bIsSuccess = Boolean.FALSE;
//					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("data-table"));
//					List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("td"));
//					for (WebElement td: tds){
//						if (td.getText().equals("Task 1") && (GlobalVariables.bIsSuccess == Boolean.FALSE)) {
//							GlobalVariables.bIsSuccess = Boolean.TRUE;
//							break;
//						}
//					}
//					if ((GlobalVariables.bIsSuccess == Boolean.TRUE)) {
//						GlobalVariables.bIsSuccess = Boolean.FALSE;
//						for (WebElement td: tds){
//							if (td.getText().equals("Task 2")) {
//								GlobalVariables.bIsSuccess = Boolean.TRUE;
//								break;
//							}
//						}
//					}
//					// Click on 'done' button
//					GlobalVariables.oDriver.findElement(By.className("close")).click();
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("select-segment:sg-sel")));
//					options = GlobalVariables.oDropDown.getOptions();
//				    for(WebElement option : options) {
//				    	if(GlobalVariables.testData.get("The Other Segment").equals(option.getText())){
//				    			option.setSelected();
//				    			break;
//				    	}
//				    }
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//				    // Click on 'About plan segment' option under 'Show' pop up menu
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),"About plan segment");
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					// Click on 'Task mover' option under 'Shows' pop up menu
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAbtPlanSegShowMenu"),"Task mover");
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					// ASSERTION 1.2: When clicked on 'Move' button, the selected task(s) should be moved from current segment to the destination segment
//					if ((GlobalVariables.bIsSuccess == Boolean.FALSE)) {
//						GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("data-table"));
//						tds = GlobalVariables.oElement.findElements(By.tagName("td"));
//						for (WebElement td: tds){
//							if (td.getText().equals("Task 1") && (GlobalVariables.bIsSuccess == Boolean.FALSE)) {
//								GlobalVariables.bIsSuccess = Boolean.TRUE;
//								break;
//							}
//						}
//						if ((GlobalVariables.bIsSuccess == Boolean.TRUE)) {
//							GlobalVariables.bIsSuccess = Boolean.FALSE;
//							for (WebElement td: tds){
//								GlobalVariables.sStrCheck=td.getText();
//								if (td.getText().equals("Task 2")) {
//									GlobalVariables.bIsSuccess = Boolean.TRUE;
//									break;
//								}
//							}
//						}
//						if ((GlobalVariables.bIsSuccess == Boolean.TRUE)){
//								// Write Results
//								LogFunctions.writeLogs(GlobalVariables.sDescription);
//								LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//										GlobalVariables.sBlank, GlobalVariables.sBlank);   
//								// WebElement Synchronization
//								Thread.currentThread();
//								Thread.sleep(3000);
//								}
//						else
//					    {
//							GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Task 2' "+" Actual " + GlobalVariables.sStrCheck;
//					    	// Write Results
//							LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
//							LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//									GlobalVariables.sBlank, GlobalVariables.sVerifyError);
//					    }
//					}
//					
//					// Click on 'Undo move task' option under 'Actions' pop up menu
//					GlobalVariables.iStepNo++ ;
//					GlobalVariables.sDescription = "Undo move tasks done";
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("undoMoveTasks"));
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(5000);	
//					// ASSERTION 2.1: When clicked on 'Undo move task' option under 'Actions' pop up menu
//					GlobalVariables.bIsSuccess = Boolean.FALSE;
//					GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("data-table"));
//					tds = GlobalVariables.oElement.findElements(By.tagName("td"));
//					for (WebElement td: tds){
//						if (td.getText().equals("Task 1") && (GlobalVariables.bIsSuccess == Boolean.FALSE)) {
//							GlobalVariables.bIsSuccess = Boolean.TRUE;
//							break;
//						}
//					}
//					if ((GlobalVariables.bIsSuccess == Boolean.TRUE)) {
//						GlobalVariables.bIsSuccess = Boolean.FALSE;
//						for (WebElement td: tds){
//							if (td.getText().equals("Task 2")) {
//								GlobalVariables.bIsSuccess = Boolean.TRUE;
//								break;
//							}
//						}
//					}
//					// Click on 'done' button
//					GlobalVariables.oDriver.findElement(By.className("close")).click();
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("select-segment:sg-sel")));
//					options = GlobalVariables.oDropDown.getOptions();
//				    for(WebElement option : options) {
//				    	if(GlobalVariables.testData.get("Segment For Undo Move Task").equals(option.getText())){
//				    			option.setSelected();
//				    			break;
//				    	}
//				    }
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//				    // Click on 'About plan segment' option under 'Show' pop up menu
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlanSegment"));
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					// Click on 'Task mover' option under 'Shows' pop up menu
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathAbtPlanSegShowMenu"),GlobalVariables.viewElements.get("taskMover"));
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					// ASSERTION 2.2: When clicked on 'Undo move task' option under 'Actions' pop up menu
//					if ((GlobalVariables.bIsSuccess == Boolean.FALSE)) {
//						GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.className("data-table"));
//						tds = GlobalVariables.oElement.findElements(By.tagName("td"));
//						for (WebElement td: tds){
//							if (td.getText().equals("Task 1") && (GlobalVariables.bIsSuccess == Boolean.FALSE)) {
//								GlobalVariables.bIsSuccess = Boolean.TRUE;
//								break;
//							}
//						}
//						if ((GlobalVariables.bIsSuccess == Boolean.TRUE)) {
//							GlobalVariables.bIsSuccess = Boolean.FALSE;
//							for (WebElement td: tds){
//								GlobalVariables.sStrCheck=td.getText();
//								if (td.getText().equals("Task 2")) {
//									GlobalVariables.bIsSuccess = Boolean.TRUE;
//									break;
//								}
//							}
//						}
//						if ((GlobalVariables.bIsSuccess == Boolean.TRUE)){
//								// Write Results
//								LogFunctions.writeLogs(GlobalVariables.sDescription);
//								LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//										GlobalVariables.sBlank, GlobalVariables.sBlank);   
//								// WebElement Synchronization
//								Thread.currentThread();
//								Thread.sleep(3000);
//								}
//						else
//					    {
//							GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'Task 2' "+" Actual " + GlobalVariables.sStrCheck;	
//							// Write Results
//							LogFunctions.writeLogs(GlobalVariables.sDescription+""+GlobalVariables.sFailed);
//							LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//									GlobalVariables.sBlank, GlobalVariables.sVerifyError);	  
//					    }
//					}
//					GlobalVariables.oDriver.findElement(By.className("close")).click();
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
//					// Get a handle to the open alert, prompt or confirmation
//					Alert alert = GlobalVariables.oDriver.switchTo().alert();
//					// And acknowledge the alert (equivalent to clicking "OK")
//					alert.accept();
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					// Select other segment to remove
//					GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("select-segment:sg-sel")));
//					options = GlobalVariables.oDropDown.getOptions();
//				    for(WebElement option : options) {
//				    	if(option.getText().equals("The Other Segment")){
//				    			option.setSelected();
//				    			break;
//				    	}
//				    }
//				    // WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
//					// Get a handle to the open alert, prompt or confirmation
//					alert = GlobalVariables.oDriver.switchTo().alert();
//					// And acknowledge the alert (equivalent to clicking "OK")
//					alert.accept();
//					// WebElement Synchronization
//					Thread.currentThread();
//					Thread.sleep(3000);
//					
//				    // Call logout()
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
//					System.out.println("Unable to Undo Move Task" + ReportFunctions.getScreenShot("Undo Undo Move Task failed"));
//					GlobalVariables.oDriver.quit();
//				}
//		} 
//		catch (Exception e) {
//			if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						e.getMessage(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.id("stackTrace"));
//				LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
//				System.out.println("Unable to Undo Move Task" + ReportFunctions.getScreenShot("Undo Undo Move Task failed"));
//				ApplicationFunctionLibrary.logout();
//			}
//			else {
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						e.getMessage(),GlobalVariables.sBlank);
//				System.out.println("Unable to Undo Move Task" + ReportFunctions.getScreenShot("Undo Undo Move Task failed"));
//				ApplicationFunctionLibrary.logout();	
//			}
//			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
//		}
//	}
//	public static void main(String args[]) {
//		try {
//			GenericFunctionLibrary.initializeTestData();
//			GenericFunctionLibrary.loadObjectRepository();
//			new MAC0012_UndoMoveTask();
//			GenericFunctionLibrary.tearDownTestData();
//			ReportFunctions.generateAutomationReport();
//		} 
//		catch (Exception oException) {
//			// TODO Auto-generated catch block
//			oException.printStackTrace();
//			System.out.println("Unable to Undo Move Task" + ReportFunctions.getScreenShot("Undo Undo Move Task failed"));
//		}
//	}
}
