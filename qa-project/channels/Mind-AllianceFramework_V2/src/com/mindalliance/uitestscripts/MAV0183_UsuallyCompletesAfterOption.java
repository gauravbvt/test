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
 * TestCase Id: MAV0183_UsuallyCompletesAfterOption 
 * Summary: Verify option gets selected when clicked on 'Usually completes after'
 * @author afour 
 *
 */
public class MAV0183_UsuallyCompletesAfterOption extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0183_UsuallyCompletesAfterOption";
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
			// Loads the Test Data
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
	 * This method verifies Usually completes after
	 * @throws UIAutomationException
	 * @throws IOException 
	 */
	@Test
	public void testMAV0183_UsuallyCompletesAfterOption() throws UIAutomationException, IOException {
		try{
			 // Enter URL of Channels
			stepNo++;
			description="URL Entered";	
			BrowserController browserController=new BrowserController();
			browserController.enterURL();
			 // Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);    
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser initialized");
			   
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
			
			// Click on 'stretch Up forms' icon 
 			stepNo++;
			description="Stretch up form";	
			PlanPage planPage = new PlanPage();
			planPage.clickStrenchUpForm();
			 // Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);    
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Stretch up form");
			
			// Click on 'Details' under 'Show' pop up under 'Task' panel
			stepNo++;
			description="Details in task panel";
			planPage.clickPopupMenu(testData.get("ShowInTask"));
			planPage.clickSubmenu(testData.get("Details"));	
			 // Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);    
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Details in task panel");
			
			// Click on 'Show Advanced Form' link
			stepNo++;
			description="Advance form of task panel";
			planPage.clickShowAdvancedFormInTaskPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
			 // Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);    
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Advance form of task panel");
				
			// Click on 'Usually Completes After'in Task panel
			stepNo++;
			description="Click usually completes after";
			planPage.clickUsuallycompletesAfter();
			 // Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);    
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Click usually completes after");
			
			//Sign Out from 'Plan' page
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
			Log4J.getlogger(this.getClass()).info(testCaseId +"Browser Quit");
			}
	}
	/**
     * Loads Test Data for MAV0183_UsuallyCompletesAfterOption.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + File.separator+"TestData"+File.separator;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAV0183_UsuallyCompletesAfterOption=new File(path + "MAV0183_UsuallyCompletesAfterOption.xml");
			
			Document docMAV0183_UsuallyCompletesAfterOption=db.parse(MAV0183_UsuallyCompletesAfterOption);
			Element eleMAV0183_UsuallyCompletesAfterOption=docMAV0183_UsuallyCompletesAfterOption.getDocumentElement();
	              
	        Element oXmlEleMAV0183_UsuallyCompletesAfterOption = (Element) eleMAV0183_UsuallyCompletesAfterOption;
	        
	        this.testData.put("ChannelsURL",oXmlEleMAV0183_UsuallyCompletesAfterOption.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowInTask",oXmlEleMAV0183_UsuallyCompletesAfterOption.getElementsByTagName("showInTask").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Details",oXmlEleMAV0183_UsuallyCompletesAfterOption.getElementsByTagName("details").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAV0183_UsuallyCompletesAfterOption.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowSimpleFormText",oXmlEleMAV0183_UsuallyCompletesAfterOption.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0183_UsuallyCompletesAfterOption.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0183_UsuallyCompletesAfterOption.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Tags",oXmlEleMAV0183_UsuallyCompletesAfterOption.getElementsByTagName("tags").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0183_UsuallyCompletesAfterOption not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0183_UsuallyCompletesAfterOption.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0183_UsuallyCompletesAfterOption can not be parsed.");
		}
	}
	
}