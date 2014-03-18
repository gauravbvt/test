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
 * TestCase Id: MAV0167_SimpleFormOfReceiveInfo 
 * Summary: Verify by clicking on 'Show Simple Form' link in receives panel,
 * link text changes to 'Show Advanced Form' link text 
 * @author afour
 *
 */
public class MAV0167_SimpleFormOfReceiveInfo extends TestCase{
	public Hashtable<String, String> testData;
	public String testCaseId="MAV0167_SimpleFormOfReceiveInfo";
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
	 * This method displays simple form of Receive info
	 * @throws UIAutomationException
	 * @throws IOException 
	 */
	@Test
	public void testMAV0167_SimpleFormOfReceiveInfo() throws UIAutomationException, IOException {
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
		    LoginPage loginPage = new LoginPage();
		    stepNo++;
			description="Login successful";	
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
			
			// Click Actions pop up menu and Add New Segment
			stepNo++;
			description="Add new segment";
			PlanPage planPage = new PlanPage();
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("AddNewSegment"));
			// Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);    
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add new segment");	
			
			// Enter Segment Name
			stepNo++;
			description="Segment name entered";
			planPage.enterSegmentName(testData.get("SegmentForSimpleFormOfReceiveInfo"));
			// Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);    
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Segment name entered");	
			
			// Close Segment window
			stepNo++;
			description="Segment window closed";
			planPage.closeSegmentWindow();
			// Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);    
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Segment window closed");	
			
			// Click on 'Stretch Up' form icon
			stepNo++;
			description="Strench up form";
			planPage.clickStrenchUpForm();
			// Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);    
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Stretch up form");	
			
			// Click on 'Add' in 'Receives' panel
			stepNo++;
			description="Add in receives panel";
			planPage.clickAddInfoReceivesPanel();
			// Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);    
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Add in receives panel");	
			
			// Click on 'Show Advanced Form' link
			stepNo++;
			description="Show advanced form in receives panel";
			planPage.clickShowAdvancedFormInReceivesPanel(testData.get("ShowSimpleFormText"),testData.get("ShowAdvancedFormText"),testData.get("Flag"));
			// Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);    
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Show advanced form in receives panel");	
			
			// Click on Remove this segment
			stepNo++;
			description="Remove segment";
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
			// Write log			
 			LogFunctions.writeLogs(description);
 			LogFunctions.writeResults(testCaseId,stepNo, description,passed,blank,blank);    
 			Log4J.getlogger(this.getClass()).info(testCaseId +"Remove segment");	
			
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
			Log4J.getlogger(this.getClass()).info(testCaseId +ue.getErrorMessage());	
			
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
     * Loads Test Data for MAV0167_SimpleFormOfReceiveInfo.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException
	{		
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + File.separator+"TestData"+File.separator;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAV0167_SimpleFormOfReceiveInfo=new File(path + "MAV0167_SimpleFormOfReceiveInfo.xml");
			
			Document docMAV0167_SimpleFormOfReceiveInfo=db.parse(MAV0167_SimpleFormOfReceiveInfo);
			Element eleMAV0167_SimpleFormOfReceiveInfo=docMAV0167_SimpleFormOfReceiveInfo.getDocumentElement();
	              
	        Element oXmlEleMAV0167_SimpleFormOfReceiveInfo = (Element) eleMAV0167_SimpleFormOfReceiveInfo;
	       	
	        this.testData.put("Actions",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
	       	this.testData.put("SegmentForSimpleFormOfReceiveInfo",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("segmentForSimpleFormOfReceiveInfo").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ShowSimpleFormText",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("showSimpleFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("ShowAdvancedFormText",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("showAdvancedFormText").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Flag",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("flag").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("Title",oXmlEleMAV0167_SimpleFormOfReceiveInfo.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
			
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAV0167_SimpleFormOfReceiveInfo can not be parsed.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAV0167_SimpleFormOfReceiveInfo.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAV0167_SimpleFormOfReceiveInfo can not be parsed.");
		}
			
	}
}