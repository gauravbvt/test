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
 * Testcase ID: MAC0038_RedoPasteTaskUsingCopy
 * 	   Summary: Verify that user is able to redo the task which was pasted using copy command
 * @author AFour
 * 
 */
public class MAC0038_RedoPasteTaskUsingCopy extends TestCase {


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
			
			//Creates Browser instance
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
	public void testMAC0038_RedoPasteTaskUsingCopy() throws UIAutomationException, InterruptedException, IOException, ParserConfigurationException, SAXException {
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
			
			// Close Plan Map window
			PlanPage planPage=new PlanPage();
			planPage.closePlanMap();
					
			// Click on Actions pop up and Add New Segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
								
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForRedoPasteTaskUsingCopy"));
			
			// Close Segment window
			planPage.closeSegmentWindow();
				
			// Add New Task
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewTask"));
						
			// Enter Task Name
			planPage.enterTaskName(testData.get("TaskName"));
			
			// Click on 'About Plan segment' under 'Show' pop up menu
					planPage.clickPopupMenu(testData.get("Show"));
					planPage.clickSubmenu(testData.get("AboutPlanSegment"));
					
					// Open Task Mover
					planPage.clickPopupMenu(testData.get("ActionsInSegment"));
					planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
					
					// Verify Task is added
					planPage.verifyTaskNameInTaskMover(testData.get("TaskName"));
					
					// Close Task Mover window
					planPage.closeTaskMoverWindow();
					
					// Close Segment window
					planPage.closeSegmentWindow();
			
			// Click 'Strench up form'
			planPage.clickStrenchUpForm();
			
			// Click on Actions popup in Task Panel and also click on 'Copy Task'
			planPage.clickPopupMenu(testData.get("ActionsInTaskPanel"));
			planPage.clickSubmenu(testData.get("CopyTaskInActionsInTaskPanel"));
			
			// Add New Segment, Click on Actions pop up and Add New Segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("OtherSegmentForRedoPasteTask"));
			
			// Close Segment window
			planPage.closeSegmentWindow();
			
			// Click Strench up forms
			planPage.clickStrenchUpForm();
			
			// Click on Actions Menu and click on 'Paste Task'
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("PasteTask"));
			
			// Click on 'About Plan segment' under 'Show' pop up menu
						planPage.clickPopupMenu(testData.get("Show"));
						planPage.clickSubmenu(testData.get("AboutPlanSegment"));
						
						// Open Task Mover
						planPage.clickPopupMenu(testData.get("ActionsInSegment"));
						planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
						
						// Verify Task is added
						planPage.verifyTaskNameInTaskMover(testData.get("TaskName"));
						
						// Close Task Mover window
						planPage.closeTaskMoverWindow();
						
						// Close Segment window
						planPage.closeSegmentWindow();
			
		
			// Click on Actions Menu and click on 'Undo Paste Task'
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("UndoPasteTask"));
			
			// Click on 'About Plan segment' under 'Show' pop up menu
						planPage.clickPopupMenu(testData.get("Show"));
						planPage.clickSubmenu(testData.get("AboutPlanSegment"));
						
						// Open Task Mover
						planPage.clickPopupMenu(testData.get("ActionsInSegment"));
						planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
						
						// Verify Task is removed
						planPage.verifyTaskIsRemoved(testData.get("TaskName"));
						
						// Close Task Mover window
						planPage.closeTaskMoverWindow();
						
						// Close Segment window
						planPage.closeSegmentWindow();			
						
			// Click on Actions Menu and click on 'Redo Paste Task'
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RedoPasteTask"));
			
			// Click on 'About Plan segment' under 'Show' pop up menu
						planPage.clickPopupMenu(testData.get("Show"));
						planPage.clickSubmenu(testData.get("AboutPlanSegment"));
						
						// Open Task Mover
						planPage.clickPopupMenu(testData.get("ActionsInSegment"));
						planPage.clickSubmenu(testData.get("MoveTasksInSegment"));	
						
						// Verify Task is removed
						planPage.verifyTaskNameInTaskMover(testData.get("TaskName"));
						
						// Close Task Mover window
						planPage.closeTaskMoverWindow();
						
						// Close Segment window
						planPage.closeSegmentWindow();			
			
			
			// Click on Remove this segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
			
			// Click on Remove this segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
			
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();

		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAC0038_RedoPasteTaskUsingCopy");
		
			//Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			//Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAC0038_RedoPasteTaskUsingCopy.
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
			File MAC0038_RedoPasteTaskUsingCopy=new File(path + "MAC0038_RedoPasteTaskUsingCopy.xml");
			
			Document docMAC0038_RedoPasteTaskUsingCopy=db.parse(MAC0038_RedoPasteTaskUsingCopy);
			Element eleMAC0038_RedoPasteTaskUsingCopy=docMAC0038_RedoPasteTaskUsingCopy.getDocumentElement();
	              
	        Element oXmlEleMAC0038_RedoPasteTaskUsingCopy = (Element) eleMAC0038_RedoPasteTaskUsingCopy;
	        
	               	
	        this.testData.put("Actions",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForRedoPasteTaskUsingCopy",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("segmentForRedoPasteTaskUsingCopy").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AddNewTask",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("addNewTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TaskName",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("taskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoPasteTask",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("undoPasteTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInTaskPanel",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("actionsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("CopyTaskInActionsInTaskPanel",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("copyTaskInActionsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("OtherSegmentForRedoPasteTask",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("otherSegmentForRedoPasteTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("PasteTask",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("pasteTask").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("ActionsInSegment",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("actionsInSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("MoveTasksInSegment",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("moveTasksInSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Show",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AboutPlanSegment",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("RedoPasteTask",oXmlEleMAC0038_RedoPasteTaskUsingCopy.getElementsByTagName("redoPasteTask").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0038_RedoPasteTaskUsingCopy not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0038_RedoPasteTaskUsingCopy.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0038_RedoPasteTaskUsingCopy can not be parsed.");
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