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
 * Testcase ID: MAC0037_RedoPasteTaskUsingCut
 * 	   Summary: Verify that user is able to redo the task which was pasted using cut command
 * @author AFour
 * 
 */
public class MAC0037_RedoPasteTaskUsingCut extends TestCase{

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
	public void testMAC0037_RedoPasteTaskUsingCut() throws UIAutomationException, InterruptedException, IOException, ParserConfigurationException, SAXException {
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
			planPage.enterSegmentName(testData.get("SegmentForRedoPasteTask"));
			
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
			
			// Click on Actions popup in Task Panel and also click on 'Cut Task'
			planPage.clickPopupMenu(testData.get("ActionsInTaskPanel"));
			planPage.clickSubmenu(testData.get("CutTaskInActionsInTaskPanel"));
			
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
					
			// Click on 'Redo Paste Task' under 'Actions' pop up menu
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
			Reporting.getScreenShot("MAC0037_RedoPasteTaskUsingCut");
		
			//Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			//Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAC0037_RedoPasteTaskUsingCut.
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
			File MAC0037_RedoPasteTaskUsingCut=new File(path + "MAC0037_RedoPasteTaskUsingCut.xml");
			
			Document docMAC0037_RedoPasteTaskUsingCut=db.parse(MAC0037_RedoPasteTaskUsingCut);
			Element eleMAC0037_RedoPasteTaskUsingCut=docMAC0037_RedoPasteTaskUsingCut.getDocumentElement();
	              
	        Element oXmlEleMAC0037_RedoPasteTaskUsingCut = (Element) eleMAC0037_RedoPasteTaskUsingCut;
	        
	               	
	        this.testData.put("Actions",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForRedoPasteTask",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("segmentForRedoPasteTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AddNewTask",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("addNewTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("TaskName",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("taskName").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("UndoPasteTask",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("undoPasteTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ActionsInTaskPanel",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("actionsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("CutTaskInActionsInTaskPanel",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("cutTaskInActionsInTaskPanel").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("OtherSegmentForRedoPasteTask",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("otherSegmentForRedoPasteTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("PasteTask",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("pasteTask").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("ActionsInSegment",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("actionsInSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("MoveTasksInSegment",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("moveTasksInSegment").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Show",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("AboutPlanSegment",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
			
			this.testData.put("RedoPasteTask",oXmlEleMAC0037_RedoPasteTaskUsingCut.getElementsByTagName("redoPasteTask").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAC0037_RedoPasteTaskUsingCut not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAC0037_RedoPasteTaskUsingCut.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAC0037_RedoPasteTaskUsingCut can not be parsed.");
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