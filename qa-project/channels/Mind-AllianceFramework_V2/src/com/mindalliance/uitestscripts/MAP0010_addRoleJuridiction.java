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
 * Test Case ID: MAP0010_addRoleJuridiction
 * Summary: Verify that Role, Title, Jurisdiction and supervisor can be assign to participating agent within an organization
 * @author AfourTech
 *
 */
public class MAP0010_addRoleJuridiction extends TestCase{


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
		catch(UIAutomationException ue)	{
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
		}
	}
	
	@Test
	public void testMAP0010_addRoleJuridiction() throws UIAutomationException{
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
			
			// Click on 'Add New Segment' under 'Actions' pop up menu
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
						
			// Enter Segment Name
			planPage.enterSegmentName(testData.get("SegmentForAddRoleJurisdiction"));
			
			// Close segment window
			planPage.closeSegmentWindow();
			
			// Click on 'Organizations In Scope' under 'Scoping' pop up menu
			planPage.clickPopupMenu(testData.get("Scoping"));
			planPage.clickSubmenu(testData.get("OrganizationsInScope"));
			
			// Enter organization name
			planPage.enterOrganizationName(testData.get("Organization"));
				
			// Click on 'Organization'
			planPage.clickOnOrganizationEntered(testData.get("Organization"));
			
			// Enter Agent
			planPage.enterAgentInOrganizationInScope(testData.get("Agent"));
			
			// Enter Title
			planPage.enterTitleInOrganizationInScope(testData.get("TitleInOrg"));
			
			// Enter Role
			planPage.enterRoleInOrganizationInScope(testData.get("Role"));
			
			// Enter Jurisdiction 
			planPage.enterJurisdictionInOrganizationInScope(testData.get("Jurisdiction"));
			
			// Enter Supervisor
			planPage.enterSupervisorInOrganizationInScope(testData.get("Supervisor"));
			
			// Check checkbox of agent 1
			planPage.checkCheckboxOfAgentInOrganizationInScope();
			
			// Close Actual organization window
			planPage.closeActualOrganizationWindow();
			
			// Click on 'Organization'
			planPage.clickOnOrganizationEntered(testData.get("Organization"));
			
			planPage.clickOnOrganizationEntered(testData.get("Organization"));
			
			// Check checkbox of agent 1
			planPage.checkCheckboxOfAgentInOrganizationInScope();
			
			// Close Actual organization window
			planPage.closeActualOrganizationWindow();
			
			planPage.clickOnOrganizationEntered(testData.get("Organization"));
			// Close Actual organization window
			planPage.closeActualOrganizationWindow();
			
			// Remove expectation
			planPage.clickRemoveExpectation(testData.get("Organization"));
			
			// Close Organization Window
			planPage.closeOrganiztionWindow();
			
			//Click on Remove this segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
								
			// Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();

		} catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAP0010_addRoleJuridiction");
		
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	

	
	/**
     * Loads Test Data for MAP0010_addRoleJuridiction.
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
			File MAP0010_addRoleJuridiction=new File(path + "MAP0010_addRoleJuridiction.xml");
			
			Document docMAP0010_addRoleJuridiction=db.parse(MAP0010_addRoleJuridiction);
			Element eleMAP0010_addRoleJuridiction=docMAP0010_addRoleJuridiction.getDocumentElement();
	              
	        Element oXmlEleMAP0010_addRoleJuridiction = (Element) eleMAP0010_addRoleJuridiction;
	    	           
	        this.testData.put("Scoping", oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("scoping").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("OrganizationsInScope", oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("organizationsInScope").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL", oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Organization",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("organization").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Actions",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForAddRoleJurisdiction",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("segmentForAddRoleJurisdiction").item(0).getChildNodes().item(0).getNodeValue());
	        
			this.testData.put("Agent",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("agent").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Role",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("role").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("TitleInOrg",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("titleInOrg").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Jurisdiction",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("jurisdiction").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Supervisor",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("supervisor").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Supervisor",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("supervisor").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAP0010_addRoleJuridiction.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());  
	    }
		catch(SAXException se){
			throw new UIAutomationException("File MAP0010_addRoleJuridiction.xml can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAP0010_addRoleJuridiction.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAP0010_addRoleJuridiction.xml can not be parsed.");
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