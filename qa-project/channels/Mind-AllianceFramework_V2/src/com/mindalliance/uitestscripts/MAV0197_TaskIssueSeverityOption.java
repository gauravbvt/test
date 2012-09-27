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

public class MAV0197_TaskIssueSeverityOption extends TestCase{


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
	public void testMAV0197_TaskIssueSeverityOption() throws UIAutomationException {
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
			
			// Add New Segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			
			// Close segment window
			planPage.closeSegmentWindow();
			
			// Click on 'strench Up forms' icon 
			planPage.clickStrenchUpForm();
		
			// Click on 'Hide details' under 'Show' pop up under 'Task' panel
			planPage.clickPopupMenu(testData.get("ShowInTask"));
			planPage.clickSubmenu(testData.get("Details"));	
			
			// Click on 'Show Advanced Form' link
			planPage.clickShowAdvancedFormInTaskPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
				
			// Click on 'New Issue' under 'Actions' pop up menu in task panel
			planPage.clickPopupMenu(testData.get("ActionsInTask"));
			planPage.clickSubmenu(testData.get("NewIssueInTask"));	
			
			// Enter details in description textbox
			planPage.enterDescriptionInIssue(testData.get("Description"));
			
			// Verify dropdown of severity in issue
			planPage.verifySeverityDropdownInIssueInTask(testData.get("Minor"), testData.get("Major"),testData.get("Severe"), testData.get("Extreme"));
			
			// Remove This segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
		
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
		}
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAV0197_TaskIssueSeverityOption");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAV0197_TaskIssueSeverityOption.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAV0197_TaskIssueSeverityOption=new File(path + "MAV0197_TaskIssueSeverityOption.xml");
			
			Document docMAV0197_TaskIssueSeverityOption=db.parse(MAV0197_TaskIssueSeverityOption);
			Element eleMAV0197_TaskIssueSeverityOption=docMAV0197_TaskIssueSeverityOption.getDocumentElement();
	              
	        Element oXmlEleMAV0197_TaskIssueSeverityOption = (Element) eleMAV0197_TaskIssueSeverityOption;
	        
	        this.testData.put("ChannelsURL",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowInTask",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("showInTask").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Details",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("details").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowSimpleFormText",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Unspecified",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("unspecified").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ActionsInTask",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("actionsInTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("NewIssueInTask",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("newIssueInTask").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Actions",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("AddNewSegment",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Description",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("description").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Minor",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("minor").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Major",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("major").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Severe",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("severe").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Extreme",oXmlEleMAV0197_TaskIssueSeverityOption.getElementsByTagName("extreme").item(0).getChildNodes().item(0).getNodeValue());
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0197_TaskIssueSeverityOption not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0197_TaskIssueSeverityOption.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0197_TaskIssueSeverityOption can not be parsed.");
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