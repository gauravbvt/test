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
import com.mindalliance.configuration.DataController;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.Log4J;
import com.mindalliance.configuration.LogFunctions;
import com.mindalliance.configuration.Reporting;
import com.mindalliance.configuration.UIAutomationException;
import com.mindalliance.pages.DomainPlanPage;
import com.mindalliance.pages.HeaderController;
import com.mindalliance.pages.HomePage;
import com.mindalliance.pages.LoginPage;
import com.mindalliance.pages.PlanPage;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * TestCase ID: MAV0022_viewAllParticipationsForm
 * Summary: Verify that form with heading "Participations" gets loaded on the About Plan window
 * @author afour
*/

public class MAV0022_viewAllParticipationsForm extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0022_viewAllParticipationsForm";
	public String description=null;
	public int stepNo=1;
	public String passed="Pass";
	public String failed="Fail";
	public String blank=""; 
	public String exception="";
	public String browser="";
	public String scriptException;
	
	/**
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
			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser initialized");	
			
		}
		catch(UIAutomationException ue){
			stepNo++;
			description="Unable to initialize the driver";
			Assert.fail("Unable to initialize the driver"+ue.getErrorMessage());
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo, ue.getErrorMessage(), failed, scriptException, blank);
			Log4J.getlogger(this.getClass()).error(testCaseId +"Unable to initialize the driver");	
			
		}
	}
	/**
	 * This method verifies by clicking on 'User As Agents' link under 'Participation' pop up menu 'User As Agent' window opens
	 * @throws UIAutomationException
	 * @throws IOException 
	 */
	@Test
	public void testMAV0022_viewAllParticipationsForm() throws UIAutomationException, IOException{
		try {
			// Enter URL of Channels
			stepNo++;
			description="URL Entered";	
			BrowserController browserController=new BrowserController();
			browserController.enterURL();
			// Write log
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);					    
			Log4J.getlogger(this.getClass()).info(testCaseId +"URL Entered");	
			
			// Login page
			stepNo++;
			description="Login successful";	
		    LoginPage loginPage = new LoginPage();
		    loginPage.Login(GlobalVariables.configuration.getConfigData().get("UserName"),GlobalVariables.configuration.getConfigData().get("PassWord"));
		    // Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);		   
			Log4J.getlogger(this.getClass()).info(testCaseId +"Login successful");	
						
			// Domain Plans
			stepNo++;
			description="Domain Plans";
			DomainPlanPage domainPlanPage= new DomainPlanPage();
			domainPlanPage.clickDomainPlans();	
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);
			Log4J.getlogger(this.getClass()).info(testCaseId +"Domain Plans");	
			
			// Plan Page
			stepNo++;
			description="Navigated to Plan page";
			HomePage homePage=new HomePage();
			homePage.clickDomainPlanEditor();	
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);						
			Log4J.getlogger(this.getClass()).info(testCaseId +"Navigated to Plan page");	
			
			// Click on 'User As Agent' under 'Participations' pop up menu
			stepNo++;
			description="Users As Agents window opened";
			PlanPage planPage= new PlanPage();
			planPage.clickPopupMenu(testData.get("Participations"));
			planPage.clickSubmenu(testData.get("UserAsAgents"));
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);						
			Log4J.getlogger(this.getClass()).info(testCaseId +"Users As Agents window opened");	
			
			// Close 'User As Agent' window
			stepNo++;
			description="Users As Agents window closed";
			planPage.closeUserAsAgentWindow();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);						
			Log4J.getlogger(this.getClass()).info(testCaseId +"Users As Agents window closed");	
			
			// Sign Out from 'Plan' page
			stepNo++;
			description="Logout successful";
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Logout successful");	
			
			Reporting reporting= new Reporting();
		    reporting.generateAutomationReport();
		    
		}catch (UIAutomationException ue) {
			// Write log
			LogFunctions.writeLogs(ue.getErrorMessage());
			LogFunctions.writeResults(testCaseId, stepNo,description,failed, ue.getErrorMessage(), blank);
			Reporting.getScreenShot(testCaseId);
			Log4J.getlogger(this.getClass()).error(testCaseId +ue.getErrorMessage());	
			
			// Sign out from home page
		    stepNo++;
		    description="Logout successful";
			HeaderController headerController=new HeaderController();
			headerController.signOut();
			// Write log			
			LogFunctions.writeLogs(description);
			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);	
			Log4J.getlogger(this.getClass()).info(testCaseId +"Logout successful");	
			
			Reporting reporting= new Reporting();
		    reporting.generateAutomationReport();
		    
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());		
		}
	}
	/**
	 * (non-Javadoc)
	 * This method will perform cleanup actions
	 * @see junit.framework.TestCase#tearDown()
	*/	
	
	@After
	protected void tearDown(){
		if(GlobalVariables.configuration.getWebDriver()!=null){
			GlobalVariables.configuration.getWebDriver().quit();
			Log4J.getlogger(this.getClass()).error(testCaseId +"Browser Quit");	
			
		}
		String endTime=LogFunctions.getDateTime();
		GlobalVariables.configuration.setEndtime(endTime);
	}
	/**
     * Loads Test Data for MAV0022_viewAllParticipationsForm
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException
	{
		try{
			String startTime=LogFunctions.getDateTime();
			GlobalVariables.configuration.setStartTime(startTime);
			
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + File.separator+"TestData"+File.separator;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAV0022_viewAllParticipationsForm=new File(path + "MAV0022_viewAllParticipationsForm.xml");
			
			Document docMAV0022_viewAllParticipationsForm=db.parse(MAV0022_viewAllParticipationsForm);
			Element eleMAV0022_viewAllParticipationsForm=docMAV0022_viewAllParticipationsForm.getDocumentElement();
	              
	        Element oXmlEleMAV0022_viewAllParticipationsForm = (Element) eleMAV0022_viewAllParticipationsForm;
	    	           
	        this.testData.put("Participations", oXmlEleMAV0022_viewAllParticipationsForm.getElementsByTagName("participations").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("UserAsAgents", oXmlEleMAV0022_viewAllParticipationsForm.getElementsByTagName("userAsAgents").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("ChannelsURL", oXmlEleMAV0022_viewAllParticipationsForm.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("Title",oXmlEleMAV0022_viewAllParticipationsForm.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0022_viewAllParticipationsForm.xml can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0022_viewAllParticipationsForm.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0022_viewAllParticipationsForm.xml can not be parsed.");
		}
	}
}
