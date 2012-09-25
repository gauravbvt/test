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
 * Test Case ID: MAP0018_attachFile
 * Summary: Verify that File can be added to the of the Segment
 * @author afour
 *
 */
public class MAP0018_attachFile extends TestCase{

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
	public void testMAP0018_attachFile() throws UIAutomationException {
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
			planPage.enterSegmentName(testData.get("SegmentForAttachFile"));
			
			// Close Segment window
			planPage.closeSegmentWindow();
				
			// Click on 'About Plan Segment' under 'Show' pop up menu
			planPage.clickPopupMenu(testData.get("Show"));
			planPage.clickSubmenu(testData.get("AboutPlanSegment"));
			
			// Attach file to segment
			
			// Enter file name
			planPage.enterAttachFileNameInSegment(testData.get("FileName1"));
			
			// Click on 'Browse' button
			planPage.clickBrowseInAttachFileInAboutPlanSegment();
			
//			// Enter value in open dialog box
//			planPage.enterFileNameInAttachFileInAboutPlanSegment(testData.get("FileName"));
			
			// Click on 'Submit' button
			planPage.clickSubmitInAttachFileInAboutPlanSegment();
			
			
			// Close Segment window
			planPage.closeSegmentWindow();
			
			//Click on Remove this segment
			planPage.clickPopupMenu(testData.get("Actions"));
			planPage.clickSubmenu(testData.get("RemoveThisSegment"));
								
			//Sign Out from 'Plan' page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
		}
		catch (UIAutomationException ue) {
			Reporting.getScreenShot("MAP0018_attachFile");
					
			// Sign out from plan page
			HeaderController headerController=new HeaderController();
			headerController.signOutPlan();
			
			// Quits the Browser
			GlobalVariables.configuration.getWebDriver().quit();
			Assert.fail(ue.getErrorMessage());
		}
	}
	
	/**
     * Loads Test Data for MAP0018_attachFile.
     * @throws UIAutomationException
     */
	public void loadTestData() throws UIAutomationException{
		try{
			testData=new Hashtable<String,String>();
			File currentDir=new File(".");
			
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			File MAP0018_attachFile=new File(path + "MAP0018_attachFile.xml");
			
			Document docMAP0018_attachFile=db.parse(MAP0018_attachFile);
			Element eleMAP0018_attachFile=docMAP0018_attachFile.getDocumentElement();
	              
	        Element oXmlEleMAP0018_attachFile = (Element) eleMAP0018_attachFile;
	        
	               	
	        this.testData.put("Actions",oXmlEleMAP0018_attachFile.getElementsByTagName("actions").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AddNewSegment",oXmlEleMAP0018_attachFile.getElementsByTagName("addNewSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("SegmentForAttachFile",oXmlEleMAP0018_attachFile.getElementsByTagName("segmentForAttachFile").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("RemoveThisSegment",oXmlEleMAP0018_attachFile.getElementsByTagName("removeThisSegment").item(0).getChildNodes().item(0).getNodeValue());
			this.testData.put("ChannelsURL",oXmlEleMAP0018_attachFile.getElementsByTagName("channelsURL").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Title",oXmlEleMAP0018_attachFile.getElementsByTagName("title").item(0).getChildNodes().item(0).getNodeValue());
		 	this.testData.put("Show",oXmlEleMAP0018_attachFile.getElementsByTagName("show").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("AboutPlanSegment",oXmlEleMAP0018_attachFile.getElementsByTagName("aboutPlanSegment").item(0).getChildNodes().item(0).getNodeValue());
	        this.testData.put("FileName1",oXmlEleMAP0018_attachFile.getElementsByTagName("fileName1").item(0).getChildNodes().item(0).getNodeValue());
		}
		catch(SAXException se){
			throw new UIAutomationException("File MAP0018_attachFile not found.");
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAP0018_attachFile.xml not found.");
		}
		catch (ParserConfigurationException pe) {
			throw new UIAutomationException("File MAP0018_attachFile can not be parsed.");
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

//	public MAP0018_attachFile() {
//		try {
//			GlobalVariables.sTestCaseId = "MAP0018_attachFile";
//			GlobalVariables.sDescription = "Testcase: " + GlobalVariables.sTestCaseId + " execution started";
//			LogFunctions.writeLogs(GlobalVariables.sDescription);
//			System.out.println(GlobalVariables.sDescription);
//			// Call login()
//			GlobalVariables.bIsSuccess = ApplicationFunctionLibrary.login();
//			if (GlobalVariables.bIsSuccess) {
//					
//				// Select  the plan from 'Switch to Plan' drop down, located on the top right corner
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "Switch to plan";
//				GlobalVariables.oDropDown = new Select(GlobalVariables.oDriver.findElement(By.name("switch-plan:plan-sel")));
//				List <WebElement> options = GlobalVariables.oDropDown.getOptions();
//			    for(WebElement option : options) {
//			    	if(GlobalVariables.testData.get("Automation Test Plan v.1 (dev)").equals(option.getText())){
//			    			option.setSelected();
//			    			break;
//			    	}
//			    }
//			    // Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);	
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
//				Thread.sleep(1000);
//			    
//				// Click on 'Add new segment' option under 'Actions' pop up menu
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription="Segment Added";
//				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("addNewSegment"));
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);
//				
//				// Details of Segment
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription="Details of Segment enetered";
//				GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name")).click();
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:name"));
//				for (int i = 0; i <= 8; i++)
//					GlobalVariables.oElement.sendKeys(Keys.BACK_SPACE);
//				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("Segment For Attach File"));
//				GlobalVariables.oDriver.findElement(By.className("close")).click();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);
//				
//				// Select the Segment from 'Select Plan Segment' drop down, located on the top right corner
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "Select Plan Segment";
//				GlobalVariables.oDropDown =new Select(GlobalVariables.oDriver.findElement(By.name("select-segment:sg-sel")));
//				options = GlobalVariables.oDropDown.getOptions();
//			    for(WebElement option : options) {
//			    	if(GlobalVariables.testData.get("Segment For Attach File").equals(option.getText())){
//			    			option.setSelected();
//			    			break;
//			    	}
//			    }
//			    // Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);
//				
//				// Add 'About plan segment' under Show pop-up menu
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription="About plan segment";
//				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathShowPopUpMenu"),GlobalVariables.viewElements.get("aboutPlanSegment"));
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(3000);
//				
//				// Attach file to segment
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription="Attach File";
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:attachments:container:controls:name"));
//				GlobalVariables.oElement.sendKeys(GlobalVariables.testData.get("AttachmentFileName"));
//				GlobalVariables .oElement=GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:attachments:container:controls:upload"));
//				GlobalVariables.oElement.sendKeys(GlobalVariables.sTestDataDirectoryPath + "CAP.txt");
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(3000);
//				GlobalVariables.oDriver.findElement(By.name("sg-editor:content:mo:aspect:attachments:container:controls:submit")).click();
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(3000);
//				// Assertion: verify that file is attached
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.xpath("//a[contains(@href,'uploads/CAP.txt')]"));
//				List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("li"));
//				for (WebElement li: tds){
//					if (li.getText().equals(GlobalVariables.testData.get("AttachmentFileName"))){
//						// Write Results
//						LogFunctions.writeLogs(GlobalVariables.sDescription);
//						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//								GlobalVariables.sBlank, GlobalVariables.sBlank);
//						break;
//					}
//					else{
//						GlobalVariables.sVerifyError ="Verification Failed "+"Expected 'This is File 1' "+" Actual "+li.getText();
//				    	// Write Results
//						LogFunctions.writeLogs(GlobalVariables.sDescription + "" + GlobalVariables.sFailed);
//						LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//								GlobalVariables.sBlank, GlobalVariables.sVerifyError);
//						break;
//				    }
//				}
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(3000);
//				
//				// Click on done
//				GlobalVariables.iStepNo++;
//				GlobalVariables.sDescription="File is Attahced";
//				GlobalVariables.oDriver.findElement(By.className("close")).click();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);				
//				// Click on 'Remove this segment' under 'Actions' pop up menu
//				ApplicationFunctionLibrary.MouseOverAndClick(GlobalVariables.plan.get("sXpathActionsPopUpMenu"),GlobalVariables.viewElements.get("removeThisSegment"));
//				// Get a handle to the open alert, prompt or confirmation
//				Alert alert = GlobalVariables.oDriver.switchTo().alert();
//				alert.accept();
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);
//				
//				// Call logout()
//				GlobalVariables.iStepNo++ ;
//				GlobalVariables.sDescription = "Logout is successful";
//				ApplicationFunctionLibrary.logout();
//				// Write Results
//				LogFunctions.writeLogs(GlobalVariables.sDescription);
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sPassed, 
//						GlobalVariables.sBlank, GlobalVariables.sBlank);
//				// WebElement Synchronization
//				Thread.currentThread();
//				Thread.sleep(1000);
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
//				System.out.println("Unable to Attach File" + ReportFunctions.getScreenShot("Attach File failed"));
//				GlobalVariables.oDriver.quit();
//			}
//		} 
//		catch (Exception e) {
//			if (GlobalVariables.oDriver.getTitle().equals(GlobalVariables.sInternalErrorPageTitle)) {
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						e.getMessage(),GlobalVariables.sErrorLogSubDirectoryPath + "\\" + GlobalVariables.sTestCaseId + ".logs");
//				GlobalVariables.oElement = GlobalVariables.oDriver.findElement(By.id("stackTrace"));
//				LogFunctions.writeErrorLogs(GlobalVariables.oElement.getText());
//				System.out.println("Unable to Attach File"+ReportFunctions.getScreenShot("Attach File failed"));
//				ApplicationFunctionLibrary.logout();
//			}
//			else {
//				LogFunctions.writeResults(GlobalVariables.sTestCaseId, GlobalVariables.iStepNo, GlobalVariables.sDescription, GlobalVariables.sFailed, 
//						e.getMessage(),GlobalVariables.sBlank);
//				System.out.println("Unable to Attach File"+ReportFunctions.getScreenShot("Attach File failed"));
//				ApplicationFunctionLibrary.logout();	
//			}
//			System.out.println("Testcase: " + GlobalVariables.sTestCaseId + " execution failed");
//		}
//	}
//    public static void main(String args[]) {
//		try {
//			GenericFunctionLibrary.initializeTestData();
//			GenericFunctionLibrary.loadObjectRepository();
//			new MAP0018_attachFile();
//			GenericFunctionLibrary.tearDownTestData();
//			ReportFunctions.generateAutomationReport();
//		} 
//		catch (Exception oException) {
//			// TODO Auto-generated catch block
//			oException.printStackTrace();
//			System.out.println("Unable to Attach File"+ReportFunctions.getScreenShot("Attach File failed"));
//		}
//	}
}