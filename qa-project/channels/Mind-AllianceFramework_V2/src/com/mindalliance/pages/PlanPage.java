package com.mindalliance.pages;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import com.mindalliance.configuration.Configuration;
import com.mindalliance.configuration.DataController;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.UIActions;
import com.mindalliance.configuration.UIAutomationException;

/**
 * PlanPage.java class contains all the methods for components on Information Sharing Page.
 * Example "About plan window", "Collaboration panel"
 * @author Afour
 *
 */
public class PlanPage {
	String fileName = "PlanPage.xml";
	ElementController elementController= new ElementController();
	DataController dataController=new DataController();
	public String xPath=null;
	
	/**
	 * 'closeAboutPlanWindow' method close the About Plan window
	 * @throws UIAutomationException 
	 */
	public void closeAboutPlanWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close About Plan Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Plan Window");
		UIActions.click(fileName,"Close About Plan Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Plan Window");
		
		// Assertion: Verify About plan window is closed
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close About Plan Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("About plan window can not be closed.");
		}
	}
	
	/**
	 * 'closeAboutEventUnnamedWindow' method close the About Plan window
	 * @throws UIAutomationException 
	 */
	public void closeAboutEventUnnamedWindow() throws UIAutomationException{
		
		elementController.requireElementSmart(fileName,"Close About Event: Unnamed Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Event: Unnamed Window");
		UIActions.click(fileName,"Close About Event: Unnamed Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Event: Unnamed Window");
		
		// Assertion: Verify About event window is closed
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close About Event: Unnamed Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("About event window can not be closed.");
		}
	}

	/**
	 * 'closePlanMap' method close the Plan Map window
	 * @throws UIAutomationException 
     */
	public void closePlanMap() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Plan Map Window",GlobalVariables.configuration.getAttrSearchList(), "Close Plan map window");
		UIActions.click(fileName,"Close Plan Map Window",GlobalVariables.configuration.getAttrSearchList(), "Close Plan map window");
		
		// Assertion: Verify plan map window is closed
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close Plan Map Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("Plan map window can not be closed.");
		}
	}
	
	/**
	 * Closes 'Assignment' window
	 * @throws UIAutomationException
	 */
	public void closeAssignmentWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Assignment Window",GlobalVariables.configuration.getAttrSearchList(), "Close Plan map window");
		UIActions.click(fileName,"Close Assignment Window",GlobalVariables.configuration.getAttrSearchList(), "Close Plan map window");
		
		// Assertion: Verify Assignment window is closed
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close Assignment Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("Assignments window can not be closed.");
		}
	}
	
	/**
	 * 'closeSegmentWindow' method close the Segment window
	 * @throws UIAutomationException 
	 */
	public void closeSegmentWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Segment Window",GlobalVariables.configuration.getAttrSearchList(), "Close Segment Window");
		xPath=dataController.getPageDataElements(fileName,"Close Segment Window", "Xpath");
		UIActions.click(fileName,"Close Segment Window",GlobalVariables.configuration.getAttrSearchList(), "Close Segment Window");
	
		// Assertion: Verify segment window is closed
		try{
			Thread.sleep(2000);
		}
		catch(Exception e){}
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("Segment window can not be closed.");
		}
	}
	
	/**
	 * 'closeEventsWindow' method close the Events window
	 * @throws UIAutomationException 
	*/
	public void closeEventsWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Events Window",GlobalVariables.configuration.getAttrSearchList(), "Close Events Window");
		UIActions.click(fileName,"Close Events Window",GlobalVariables.configuration.getAttrSearchList(), "Close Events Window");
		
		// Assertion: Verify Event window is closed
		try{
			Thread.sleep(2000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close Events Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("Events window can not be closed.");
		}
			
	}
	
	/**
	 * 'closedUnamedEventWindow' method closes the unnamed event window
	 * @throws UIAutomationException
	 */
	public void closeUnamedEventsWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Unnamed Events Window",GlobalVariables.configuration.getAttrSearchList(), "Close Unnamed Events Window");
		UIActions.click(fileName,"Close Unnamed Events Window",GlobalVariables.configuration.getAttrSearchList(), "Close Unnamed Events Window");
		
		// Assertion: Verify Event window is closed
	//	xPath=dataController.getPageDataElements(fileName,"Close Unnamed Events Window", "Xpath");
	//	List<WebElement> webElemets=UIActions.getElements(xPath);
	//	if(!webElemets.isEmpty()){
	//		throw new UIAutomationException("Unamed Events window can not be closed.");
	//	}
		
	}
	
	/**
	 * Close requirement window
	 * @throws UIAutomationException
	 */
	public void closeRequirementWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Requirements Window",GlobalVariables.configuration.getAttrSearchList(), "Close Requirements Window");
		UIActions.click(fileName,"Close Requirements Window",GlobalVariables.configuration.getAttrSearchList(), "Close Requirements Window");
		
		// Assertion: Verify Requirement window is closed
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close Requirements Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("Requirement window can not be closed.");
		}
	}
	
	/**
	 * 'closeClassificationWindow' method close the Classification window
	 * @throws UIAutomationException 
	 */
	public void closeClassificationWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Classification Window",GlobalVariables.configuration.getAttrSearchList(), "Close Events Window");
		UIActions.click(fileName,"Close Classification Window",GlobalVariables.configuration.getAttrSearchList(), "Close Events Window");
		
		// Assertion: Verify Classification window is closed
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close Classification Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("Classification window can not be closed.");
		}
	}
	
	/**
	 * 'closeOrganiztionWindow' method close the Organization window
	 * @throws UIAutomationException 
	 */
	public void closeOrganiztionWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Organization Window",GlobalVariables.configuration.getAttrSearchList(), "Close Organization Window");
		UIActions.click(fileName,"Close Organization Window",GlobalVariables.configuration.getAttrSearchList(), "Close Organization Window");
		
		// Assertion: Verify Organization window is closed
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close Organization Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("Organization window can not be closed.");
		}
	}
	
	/**
	 * 'closeSearchingWindow' method close the Searching window
	 * @throws UIAutomationException 
	 */
	public void closeSearchingWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Searching Window",GlobalVariables.configuration.getAttrSearchList(), "Close Searching Window");
		UIActions.click(fileName,"Close Searching Window",GlobalVariables.configuration.getAttrSearchList(), "Close Searching Window");
		
		// Assertion: Verify Searching window is closed
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close Searching Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("Searching window can not be closed.");
		}
	}
	
	/**
	 * 'closAllIssuesWindow' method close the Issues window
	 * @throws UIAutomationException 
	 */
	public void closeAllIssuesWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close All Issues Window",GlobalVariables.configuration.getAttrSearchList(), "Close All Issues Window");
		UIActions.click(fileName,"Close All Issues Window",GlobalVariables.configuration.getAttrSearchList(), "Close All Issues Window");
		
		// Assertion: Verify All issues window is closed
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close All Issues Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("All issues window can not be closed.");
		}
	}
	
	/**
	 * 'closePlanEvaluationWindow' method close the Plan Evaluation window
	 * @throws UIAutomationException 
	 */
	public void closePlanEvaluationWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Template Evaluation Window",GlobalVariables.configuration.getAttrSearchList(), "Close Template Evaluation Window");
		UIActions.click(fileName,"Close Template Evaluation Window",GlobalVariables.configuration.getAttrSearchList(), "Close Template Evaluation Window");
		
		// Assertion: Verify Plan Evaluation window is closed
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close Template Evaluation Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("Template Evaluation window can not be closed.");
		}
	}
	
	/**
	 * 'closeUserAsAgentWindow' method close 'User As Agents' window
	 * @throws UIAutomationException 
	 */
	public void closeUserAsAgentWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close User As Agent Window",GlobalVariables.configuration.getAttrSearchList(), "Close User As Agent Window");
		UIActions.click(fileName,"Close User As Agent Window",GlobalVariables.configuration.getAttrSearchList(), "Close User As Agent Window");
			
		// Assertion: Verify User As Agents window is closed
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close User As Agent Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("User As Agents window can not be closed.");
		}
	}
	
	/**
	 * 'closePlanVersionsWindow' method close 'Plan Versions' window
	 * @throws UIAutomationException 
	 */
	public void closePlanVersionsWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Plan Versions Window",GlobalVariables.configuration.getAttrSearchList(), "Close Plan Versions Window");
		UIActions.click(fileName,"Close Plan Versions Window",GlobalVariables.configuration.getAttrSearchList(), "Close Plan Versions Window");
		
		// Assertion: Verify Plan Versions window is closed
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close Plan Versions Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("Plan Versions window can not be closed.");
		}
	}
	
	/**
	 * 'closeAboutPlanSegmentWindow' method close 'About Plan Segment' window
	 * @throws UIAutomationException 
	 */
	public void closeAboutPlanSegmentWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close About Plan Segment Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Plan Segment Window");
		UIActions.click(fileName,"Close About Plan Segment Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Plan Segment Window");
		
		// Assertion: Verify Plan Versions window is closed
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close About Plan Segment Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("About Plan Segment window can not be closed.");
		}
	}
	
	/**
	 * Close 'Element Of Information' window
	 * @throws UIAutomationException
	 */
	public void closeEOIWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Element Of Information Window",GlobalVariables.configuration.getAttrSearchList(), "Close Element Of Information Window");
		UIActions.click(fileName,"Close Element Of Information Window",GlobalVariables.configuration.getAttrSearchList(), "Close Element Of Information Window");
		
		// Assertion: Verify EOI window is closed
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close Element Of Information Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("EOI window can not be closed.");
		}
	}
	/**
	 * 'closeTaskMoverWindow' method close 'About Plan Segment' window
	 * @throws UIAutomationException 
	 */
	
	public void closeTaskMoverWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Task Mover Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Plan Segment Window");
		UIActions.click(fileName,"Close Task Mover Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Plan Segment Window");			
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close Task Mover Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("Task Mover window can not be closed.");
		}
	}
	
	/**
	 * 'closeAllSurveysWindow' method close 'About Plan Segment' window
	 * @throws UIAutomationException 
	 */
	public void closeAllSurveysWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close All Surveys Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Plan Segment Window");
		UIActions.click(fileName,"Close All Surveys Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Plan Segment Window");
		
		// Assertion: Verify All Surveys window is closed
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close All Surveys Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("All Surveys window can not be closed.");
		}
	}

	/**
	 * 'closeFailureImpactsWindow' method close 'Failure Impacts' window
	 * @throws UIAutomationException
	 */
	public void closeFailureImpactsWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Failure Impacts Window",GlobalVariables.configuration.getAttrSearchList(), "Close Failure Impacts Window");
		UIActions.click(fileName,"Close Failure Impacts Window",GlobalVariables.configuration.getAttrSearchList(), "Close Failure Impacts Window");
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close Failure Impacts Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("Failure Impacts window can not be closed.");
		}
	}
	
	/**
	 * 'closeDisseminationWindow' method close 'Dissemination' window
	 * @throws UIAutomationException
	 */
	public void closeDisseminationWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Dissemination Window",GlobalVariables.configuration.getAttrSearchList(), "Close Dissemination Window");
		UIActions.click(fileName,"Close Dissemination Window",GlobalVariables.configuration.getAttrSearchList(), "Close Dissemination Window");
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		xPath=dataController.getPageDataElements(fileName,"Close Dissemination Window", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("Dissemination window can not be closed.");
		}
	}
	
	/**
	 * 'verifyReceivesPanelIsPresent' method verifies that Receives panel is present on the Plan Editor Page
	 * @param receives
	 * @throws UIAutomationException 
	 */
	public void verifyReceivesPanelIsPresent(String receives) throws UIAutomationException{
		
		// Assertion: Verify 'Receives' is present
		elementController.requireElementSmart(fileName,"Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Receives Panel");
		String receivesPanelText=UIActions.getText(fileName,"Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Receives Panel");
		if(!receives.equals(receivesPanelText)){
		throw new UIAutomationException( "'"+receives +"' not found");
		}
	}
	
	/**
	 * 'verifySendsPanelIsPresent' method verifies that Sends panel is present on the Plan Editor Page
	 * @param sends
	 * @throws UIAutomationException 
	 */
	public void verifySendsPanelIsPresent(String sends) throws UIAutomationException{
		
		// Assertion: Verify 'Sends Panel' is present
		elementController.requireElementSmart(fileName,"Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Sends Panel");
		String sendsPanelText=UIActions.getText(fileName,"Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Sends Panel");
		if(!sends.equals(sendsPanelText)){
		throw new UIAutomationException( "'"+sends +"' not found");
		}
	}
	
	/**
	 * 'verifyChecklistsIconIsPresentInTaskPanel' method verifies that Checklists icon is present on home page
	 * @throws UIAutomationException 
	 */
	public void verifyChecklistsIconIsPresentInTaskPanel() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Checklists Icon",GlobalVariables.configuration.getAttrSearchList(), "Checklists Icon");
				
		// Assertion : Verify Checklists Icon is present on page
		String Xpath=dataController.getPageDataElements(fileName, "Checklists Icon", "Xpath");
		System.out.println(Xpath);

		elementController.waitForElement("Xpath", "Checklists Icon");
		
	}
	
	/**
	 * 'verifyShowPopupMenuIsPresent' method verifies that Show popup menu is present on the Plan Page
	 * @param show
	 * @throws UIAutomationException 
	 */
	public void verifyShowPopupMenuIsPresent(String show) throws UIAutomationException{

		// Assertion: Verify 'show' is present
		elementController.requireElementSmart(fileName,"Show",GlobalVariables.configuration.getAttrSearchList(), "Show");
		String popUpName=UIActions.getText(fileName,"Show",GlobalVariables.configuration.getAttrSearchList(), "Show");
		if(!show.equals(popUpName)){
		throw new UIAutomationException( "'"+show +"' not found");
		}
	}
	
	/**
	 * 'verifyShowPopupMenuIsPresentInTaskPanel' method verifies that Show popup menu is present in Task Panel
	 * @throws UIAutomationException 
	 */
	public void verifyShowPopupMenuIsPresentInTaskPanel(String show) throws UIAutomationException{
		
		// Assertion: Verify 'show' is present
		elementController.requireElementSmart(fileName,"Show In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Task Panel");
		String popUpName=UIActions.getText(fileName,"Show In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Task Panel");
		if(!show.equals(popUpName)){
		throw new UIAutomationException( "'"+show +"' not found");
		}
	}
	
	/**
	 * 'verifyShowPopupMenuIsPresentInReceivesPanel' method verifies that Show popup menu is present in Receives Panel
	 * @throws UIAutomationException 
	 */
	public void verifyShowPopupMenuIsPresentInReceivesPanel(String show) throws UIAutomationException{
		// Assertion: Verify 'show' is present
		elementController.requireElementSmart(fileName,"Show In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Receives Panel");
		String popUpName=UIActions.getText(fileName,"Show In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Receives Panel");
		if(!show.equals(popUpName)){
		throw new UIAutomationException( "'"+show +"' not found");
		}
	}
	
	/**
	 * 'verifyShowPopupMenuIsPresentInSendsPanel' method verifies that Show popup menu is present in Sends Panel
	 * @throws UIAutomationException 
	 */
	public void verifyShowPopupMenuIsPresentInSendsPanel(String show) throws UIAutomationException{
		// Assertion: Verify 'show' is present
		elementController.requireElementSmart(fileName,"Show In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Sends Panel");
		String popUpName=UIActions.getText(fileName,"Show In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Sends Panel");
		if(!show.equals(popUpName)){
		throw new UIAutomationException( "'"+show +"' not found");
		}
	}
	
	/**
	 * 'verifyActionsPopupMenuIsPresent' method verifies that Actions popup menu is present on the Plan Editor Page
	 * @param actions
	 * @throws UIAutomationException 
	 */
	public void verifyActionsPopupMenuIsPresent(String actions) throws UIAutomationException{
		
		// Assertion: Verify 'actions' is present
		elementController.requireElementSmart(fileName,"Actions",GlobalVariables.configuration.getAttrSearchList(), "Actions");
		String popUpName=UIActions.getText(fileName,"Actions",GlobalVariables.configuration.getAttrSearchList(), "Actions");
		if(!actions.equals(popUpName)){
		throw new UIAutomationException( "'"+actions +"' not found");
		}
	}
	
	/**
	 * 'verifyActionsPopupMenuIsPresentInSendsPanel' method verifies that Actions popup menu is present in Sends panel on the Plan Editor Page
	 * @param actions
	 * @throws UIAutomationException 
	 */
	public void verifyActionsPopupMenuIsPresentInSendsPanel(String actions) throws UIAutomationException{
		
		// Assertion: Verify 'actions' is present in Sends panel
		elementController.requireElementSmart(fileName,"Actions In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Sends Panel");
		String popUpName=UIActions.getText(fileName,"Actions In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Sends Panel");
		if(!actions.equals(popUpName)){
		throw new UIAutomationException( "'"+actions +"' not found");
		}
	}
	
	/**
	 * 'verifyActionsPopupMenuIsPresentInReceivesPanel' method verifies that Actions popup menu is present in Receives panel on the Plan Editor Page
	 * @param actions
	 * @throws UIAutomationException 
	 */
	public void verifyActionsPopupMenuIsPresentInReceivesPanel(String actions) throws UIAutomationException{
	
		// Assertion: Verify 'actions' is present in Receives panel
		elementController.requireElementSmart(fileName,"Actions In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Receives Panel");
		String popUpName=UIActions.getText(fileName,"Actions In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Receives Panel");
		if(!actions.equals(popUpName)){
		throw new UIAutomationException( "'"+actions +"' not found");
		}
	}
	
	/**
	 * 'verifyActionsPopupMenuIsPresentInAboutTemplate' method verifies that Actions popup menu is present in About Template
	 * @param actions
	 * @throws UIAutomationException 
	 */
	public void verifyActionsPopupMenuIsPresentInAboutTemplate(String actions) throws UIAutomationException{
	
		// Assertion: Verify 'actions' is present in About Template window
		elementController.requireElementSmart(fileName,"Actions In About Template",GlobalVariables.configuration.getAttrSearchList(), "Actions In About Template");
		String popUpName=UIActions.getText(fileName,"Actions In About Template",GlobalVariables.configuration.getAttrSearchList(), "Actions In About Template");
		if(!actions.equals(popUpName)){
		throw new UIAutomationException( "'"+actions +"' not found");
		}
	}
	
	/**
	 * 'verifyActionsPopupMenuIsPresentInAboutTemplateSegment' method verifies that Actions popup menu is present in About Template Segment window
	 * @throws UIAutomationException 
	 */
	public void verifyActionsPopupMenuIsPresentInAboutTemplateSegment(String actions) throws UIAutomationException{
	
		// Assertion: Verify 'actions' is present in About Template Segment window
		elementController.requireElementSmart(fileName,"Actions In About Template Segment",GlobalVariables.configuration.getAttrSearchList(), "Actions In About Template Segment");
		String popUpName=UIActions.getText(fileName,"Actions In About Template Segment",GlobalVariables.configuration.getAttrSearchList(), "Actions In About Template Segment");
		if(!actions.equals(popUpName)){
		throw new UIAutomationException( "'"+actions +"' not found");
		}
	}
	
	/**
	 * 'verifyActionsPopupMenuIsPresentInAboutFunction' method verifies that Actions popup menu is present in About Function window
	 * @throws UIAutomationException 
	 */
	public void verifyActionsPopupMenuIsPresentInAboutFunction(String actions) throws UIAutomationException{
	
		// Assertion: Verify 'actions' is present in About Function window
		elementController.requireElementSmart(fileName,"Actions In About Function",GlobalVariables.configuration.getAttrSearchList(), "Actions In About Function");
		String popUpName=UIActions.getText(fileName,"Actions In About Function",GlobalVariables.configuration.getAttrSearchList(), "Actions In About Function");
		if(!actions.equals(popUpName)){
		throw new UIAutomationException( "'"+actions +"' not found");
		}
	}
	
	/**
	 * 'verifyActionsPopupMenuIsPresentInAboutLocation
	 * ' method verifies that Actions popup menu is present in About Location window
	 * @throws UIAutomationException 
	 */
	public void verifyActionsPopupMenuIsPresentInAboutLocation(String actions) throws UIAutomationException{
	
		// Assertion: Verify 'actions' is present in About Location window
		elementController.requireElementSmart(fileName,"Actions In About Location",GlobalVariables.configuration.getAttrSearchList(), "Actions In About Location");
		String popUpName=UIActions.getText(fileName,"Actions In About Location",GlobalVariables.configuration.getAttrSearchList(), "Actions In About Location");
		if(!actions.equals(popUpName)){
		throw new UIAutomationException( "'"+actions +"' not found");
		}
	}
	/**
	 * 'verifySearchingPopupMenuIsPresent' method verifies that Searching popup menu is present on the Plan Editor Page
	 * @param actions
	 * @throws UIAutomationException 
	 */
	public void verifySearchingPopupMenuIsPresent(String searching) throws UIAutomationException{
		
		// Assertion: Verify 'searching' is present
		elementController.requireElementSmart(fileName,"Searching",GlobalVariables.configuration.getAttrSearchList(), "Searching");
		String popUpName=UIActions.getText(fileName,"Searching",GlobalVariables.configuration.getAttrSearchList(), "Searching");
		if(!searching.equals(popUpName)){
		throw new UIAutomationException( "'"+searching +"' not found");
		}
	}
	
	/**
	 * 'verifyScopingPopupMenuIsPresent' method verifies that Scoping popup menu is present on the Plan Editor Page
	 * @param scoping
	 * @throws UIAutomationException 
	 */
	public void verifyScopingPopupMenuIsPresent(String scoping) throws UIAutomationException{
		
		// Assertion: Verify 'searching' is present
		elementController.requireElementSmart(fileName,"Scoping",GlobalVariables.configuration.getAttrSearchList(), "Scoping");
		String popUpName=UIActions.getText(fileName,"Scoping",GlobalVariables.configuration.getAttrSearchList(), "Scoping");
		if(!scoping.equals(popUpName)){
		throw new UIAutomationException( "'"+scoping +"' not found");
		}
	}
	
	/**
	 * 'verifyImprovingPopupMenuIsPresent' method verifies that Improving popup menu is present on the Plan Editor Page
	 * @param actions
	 * @throws UIAutomationException 
	 */
	public void verifyImprovingPopupMenuIsPresent(String improving) throws UIAutomationException{
		
		// Assertion: Verify 'improving' is present
		elementController.requireElementSmart(fileName,"Improving",GlobalVariables.configuration.getAttrSearchList(), "Improving");
		String popUpName=UIActions.getText(fileName,"Improving",GlobalVariables.configuration.getAttrSearchList(), "Improving");
		if(!improving.equals(popUpName)){
		throw new UIAutomationException( "'"+improving +"' not found");
		}
	}
	
	/**
	 * 'verifyLearningPopupMenuIsPresent' method verifies that Learning popup menu is present on the Plan Editor Page
	 * @param actions
	 * @throws UIAutomationException 
	 */
	public void verifyLearningPopupMenuIsPresent(String learning) throws UIAutomationException{
		
		// Assertion: Verify 'learning' is present
		elementController.requireElementSmart(fileName,"Learning",GlobalVariables.configuration.getAttrSearchList(), "Learning");
		String popUpName=UIActions.getText(fileName,"Learning",GlobalVariables.configuration.getAttrSearchList(), "Learning");
		if(!learning.equals(popUpName)){
		throw new UIAutomationException( "'"+learning +"' not found");
		}
	}
	
	
	
	/**
	 * 'clickPopupMenu' method click on Pop up menu and Submenu under that Popup menu
	 * @param popUpName Show / Actions / Searching / Scoping / Improving / Participation
	 * @throws UIAutomationException 
	*/
	public void clickPopupMenu(String popUpName) throws UIAutomationException{
		String xPathForPopup="";
		switch (popUpName) {
			case "Show":
				elementController.requireElementSmart(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				xPathForPopup=dataController.getPageDataElements(fileName, popUpName, "Xpath");
				UIActions.click(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
							
				Actions builder = new Actions(GlobalVariables.configuration.getWebDriver());
				builder.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
				break;
			case "Actions":
				elementController.requireElementSmart(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				xPathForPopup=dataController.getPageDataElements(fileName, popUpName, "Xpath");
				UIActions.click(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
							
				Actions builder1 = new Actions(GlobalVariables.configuration.getWebDriver());
				builder1.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
				break;
			case "Searching":
				elementController.requireElementSmart(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				xPathForPopup=dataController.getPageDataElements(fileName, popUpName, "Xpath");
				UIActions.click(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
								
				Actions builder3 = new Actions(GlobalVariables.configuration.getWebDriver());
				builder3.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
				break;
			case "Scoping":
				elementController.requireElementSmart(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				xPathForPopup=dataController.getPageDataElements(fileName, popUpName, "Xpath");
				UIActions.click(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				
				Actions builder4 = new Actions(GlobalVariables.configuration.getWebDriver());
				builder4.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
				break;
			case "Improving":
				elementController.requireElementSmart(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				xPathForPopup=dataController.getPageDataElements(fileName, popUpName, "Xpath");
				UIActions.click(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				
				Actions builder5 = new Actions(GlobalVariables.configuration.getWebDriver());
				builder5.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
				break;
				
			case "Learning":
				elementController.requireElementSmart(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				xPathForPopup=dataController.getPageDataElements(fileName, popUpName, "Xpath");
				UIActions.click(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				
				Actions builder25 = new Actions(GlobalVariables.configuration.getWebDriver());
				builder25.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
				break;
				
			case "Participations":
				elementController.requireElementSmart(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				xPathForPopup=dataController.getPageDataElements(fileName, popUpName, "Xpath");
				UIActions.click(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				
				Actions builder6 = new Actions(GlobalVariables.configuration.getWebDriver());
				builder6.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
				break;
			case "Actions In Task Panel":
				elementController.requireElementSmart(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				xPathForPopup=dataController.getPageDataElements(fileName, popUpName, "Xpath");
				UIActions.click(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				
				Actions builder2 = new Actions(GlobalVariables.configuration.getWebDriver());
				builder2.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
				
				try{
					Thread.sleep(2000);
				}
				catch (Exception e){}
				break;
				
			case "Show In Task Panel":
				elementController.requireElementSmart(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				xPathForPopup=dataController.getPageDataElements(fileName, popUpName, "Xpath");
				UIActions.click(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				
				Actions builder7 = new Actions(GlobalVariables.configuration.getWebDriver());
				builder7.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
				break;
				
			case "Actions In Segment":
				elementController.requireElementSmart(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				xPathForPopup=dataController.getPageDataElements(fileName, popUpName, "Xpath");
				UIActions.click(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				
				Actions builder8 = new Actions(GlobalVariables.configuration.getWebDriver());
				builder8.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
				break;	
				
			case "Show In Receives Panel":
				elementController.requireElementSmart(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				xPathForPopup=dataController.getPageDataElements(fileName, popUpName, "Xpath");
				UIActions.click(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				
				Actions builder9 = new Actions(GlobalVariables.configuration.getWebDriver());
				builder9.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
				break;	
				
			case "Show In Sends Panel":
				elementController.requireElementSmart(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				xPathForPopup=dataController.getPageDataElements(fileName, popUpName, "Xpath");
				UIActions.click(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				
				Actions builder12 = new Actions(GlobalVariables.configuration.getWebDriver());
				builder12.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
				break;	
				
			case "Actions In Receives Panel":
				elementController.requireElementSmart(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				xPathForPopup=dataController.getPageDataElements(fileName, popUpName, "Xpath");
				UIActions.click(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				
				Actions builder10 = new Actions(GlobalVariables.configuration.getWebDriver());
				builder10.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
				break;	
			case "Actions In Sends Panel":
				elementController.requireElementSmart(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				xPathForPopup=dataController.getPageDataElements(fileName, popUpName, "Xpath");
				UIActions.click(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				
				Actions builder11 = new Actions(GlobalVariables.configuration.getWebDriver());
				builder11.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
				break;
			
			case "Menu In Flow Issues In Sends Panel":
				elementController.requireElementSmart(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				xPathForPopup=dataController.getPageDataElements(fileName, popUpName, "Xpath");
				UIActions.scrollDown();
				UIActions.click(fileName,popUpName, GlobalVariables.configuration.getAttrSearchList(), popUpName);
				
				Actions builder26 = new Actions(GlobalVariables.configuration.getWebDriver());
				builder26.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
				break;
			default:
				break;
			}
	}
	
	/**
	 * @param popUpName Add New Segment / About Plan / About Plan Segment / Remove This segment / Cut Task / Paste Task
	 * @throws UIAutomationException
	*/
	public void clickSubmenu(String subMenu) throws UIAutomationException{
		String headingOfWindowInPage=null;
		String headingOfWindowInXML=null;
		String newIssueTextInPage=null;
		String newIssueTextInXML=null;
	switch (subMenu) {	
	case "Add New Segment":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
		
	case "Remove Task":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		try{
			Thread.sleep(3000);
		}
		catch(Exception e){}
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Alert Window Title Of Remove Task", "Title");
		UIActions.assertAlert(headingOfWindowInXML);
		break;
		
	case "Copy Task":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		// Assertion : Verify 'Task copied' message pop up is displayed
		elementController.requireElementSmart(fileName, "Task Copied Message", GlobalVariables.configuration.getAttrSearchList(),"Task Copied Message");
		headingOfWindowInPage=UIActions.getText(fileName, "Task Copied Message", GlobalVariables.configuration.getAttrSearchList(),"Task Copied Message");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Task Copied Actual Text", "Name");
		if(!headingOfWindowInPage.contains(headingOfWindowInXML)){
			throw new UIAutomationException("'"+headingOfWindowInXML+"' not found");
		}
		break;	
	
	case "Duplicate Task":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;	

	case "Disintermediate":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;	
		
	case "Remove This Segment":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Alert Window Title Of Remove Segment", "Title");
		try{
			Thread.sleep(1000);
			}
			catch(Exception e){}
		UIActions.assertAlert(headingOfWindowInXML);
		break;
					
	case "Hide Planners":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
	
		// Verify planner info gets hidden
//		xPath=dataController.getPageDataElements(fileName,"Presence", "Xpath");
//		boolean found=UIActions.findElementBy(xPath);
//		if(found==true){
//			throw new UIAutomationException("Planners info can not be hidden.");
//		}
//		List<WebElement> webElemets1=UIActions.getElements(xPath);
//		if(!webElemets1.isEmpty()){
//			
//		}
		break;
		
	case "About Plan":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		// Assertion :'About Plan window' is present
		elementController.requireElementSmart(fileName, "About Plan Title", GlobalVariables.configuration.getAttrSearchList(),"About Plan Title");
		headingOfWindowInPage=UIActions.getText(fileName, "About Plan Title", GlobalVariables.configuration.getAttrSearchList(),"About Plan Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "About Plan Window Title", "Title");
		if(!headingOfWindowInPage.contains(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;
		
	case "About Plan Segment":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		// Assertion :'About Plan window' is present
		elementController.requireElementSmart(fileName, "About Plan Segment Title", GlobalVariables.configuration.getAttrSearchList(),"About Plan Segment Title");
		headingOfWindowInPage=UIActions.getText(fileName, "About Plan Segment Title", GlobalVariables.configuration.getAttrSearchList(),"About Plan Segment Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "About Plan Segment Window Title", "Title");
		if(!headingOfWindowInPage.contains(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;
		
	case "Plan Map":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
					
		// Assertion :'Plan Map window' is present
		elementController.requireElementSmart(fileName, "Plan Map Title", GlobalVariables.configuration.getAttrSearchList(),"Plan Map Title");
		headingOfWindowInPage=UIActions.getText(fileName, "Plan Map Title", GlobalVariables.configuration.getAttrSearchList(),"Plan Map Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Plan Map Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;
		
	case "Events In Scope":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);				
		
		// Assertion : Verify 'Events In Scope' window is present 
		elementController.requireElementSmart(fileName, "Events In Scope Title", GlobalVariables.configuration.getAttrSearchList(),"Events In Scope Title");
		headingOfWindowInPage=UIActions.getText(fileName, "Events In Scope Title", GlobalVariables.configuration.getAttrSearchList(),"Events In Scope Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Events In Scope Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;
		
	case "Classification Systems":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
				
		// Assertion : Check 'Classification Systems' window is present 
		elementController.requireElementSmart(fileName, "Classification Systems Title", GlobalVariables.configuration.getAttrSearchList(),"Classification Systems Title");
		headingOfWindowInPage=UIActions.getText(fileName, "Classification Systems Title", GlobalVariables.configuration.getAttrSearchList(),"Classification Systems Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Classification Systems Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;
		
	case "Organizations In Scope":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
				
		// Assertion : Verify 'Organization In Scope' window is present 
		elementController.requireElementSmart(fileName, "Organizations In Scope Title", GlobalVariables.configuration.getAttrSearchList(),"Organizations In Scope Title");
		headingOfWindowInPage=UIActions.getText(fileName, "Organizations In Scope Title", GlobalVariables.configuration.getAttrSearchList(),"Organizations In Scope Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Organization In Scope Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;
		
	case "Who's Who":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
			
		// Assertion : Verify 'Who's Who' window is present 
		elementController.requireElementSmart(fileName, "Who's Who Title", GlobalVariables.configuration.getAttrSearchList(),"Who's Who Title");
		headingOfWindowInPage=UIActions.getText(fileName, "Who's Who Title", GlobalVariables.configuration.getAttrSearchList(),"Who's Who Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Who's Who Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;
		
	case "All Issues":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		// Assertion : Verify 'All Issues' window is present 
		elementController.requireElementSmart(fileName, "All Issues Title", GlobalVariables.configuration.getAttrSearchList(),"All Issues Title");
		headingOfWindowInPage=UIActions.getText(fileName, "All Issues Title", GlobalVariables.configuration.getAttrSearchList(),"All Issues Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "All Issues Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;
		
	case "Index":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
				
		// Assertion : Verify 'Index' window is present 
		elementController.requireElementSmart(fileName, "Index Title", GlobalVariables.configuration.getAttrSearchList(),"Index Title");
		headingOfWindowInPage=UIActions.getText(fileName, "Index Title", GlobalVariables.configuration.getAttrSearchList(),"Index Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Index Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;
		
	case "Tags":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		try{
			Thread.sleep(2000);
			}
			catch(Exception e){}		
		// Assertion : Verify 'Tags' window is present 
		elementController.requireElementSmart(fileName, "Tags Title", GlobalVariables.configuration.getAttrSearchList(),"Tags Title");
		headingOfWindowInPage=UIActions.getText(fileName, "Tags Title", GlobalVariables.configuration.getAttrSearchList(),"Tags Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Tags Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;
		
	case "Taxonomies":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		try{
			Thread.sleep(2000);
			}
			catch(Exception e){}		
		// Assertion : Verify 'Taxonomies' window is present 
		elementController.requireElementSmart(fileName, "Taxonomies Title", GlobalVariables.configuration.getAttrSearchList(),"Taxonomies Title");
		headingOfWindowInPage=UIActions.getText(fileName, "Taxonomies Title", GlobalVariables.configuration.getAttrSearchList(),"Taxonomies Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Taxonomies Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;
		
	case "All Attachments":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		try{
			Thread.sleep(2000);
			}
			catch(Exception e){}		
		// Assertion : Verify 'All Attachments' window is present 
		elementController.requireElementSmart(fileName, "All Attachments Title", GlobalVariables.configuration.getAttrSearchList(),"All Attachments Title");
		headingOfWindowInPage=UIActions.getText(fileName, "All Attachments Title", GlobalVariables.configuration.getAttrSearchList(),"All Attachments Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "All Attachments Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;
		
		case "All Events And Phases":
			elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
			UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
			try{
				Thread.sleep(2000);
				}
				catch(Exception e){}		
			// Assertion : Verify 'All events and phases' window is present 
			elementController.requireElementSmart(fileName, "All Events And Phases Title", GlobalVariables.configuration.getAttrSearchList(),"All Events And Phases Title");
			headingOfWindowInPage=UIActions.getText(fileName, "All Events And Phases Title", GlobalVariables.configuration.getAttrSearchList(),"All Events And Phases Title");
			headingOfWindowInXML=dataController.getPageDataElements(fileName, "All Events And Phases Window Title", "Title");
			if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
				throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
			}
			break;

		case "All Involvements":
			elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
			UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
			try{
				Thread.sleep(2000);
				}
				catch(Exception e){}		
			// Assertion : Verify 'All Attachments' window is present 
			elementController.requireElementSmart(fileName, "All Involvements Title", GlobalVariables.configuration.getAttrSearchList(),"All Involvements Title");
			headingOfWindowInPage=UIActions.getText(fileName, "All Involvements Title", GlobalVariables.configuration.getAttrSearchList(),"All Involvements Title");
			headingOfWindowInXML=dataController.getPageDataElements(fileName, "All Involvements Window Title", "Title");
			if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
				throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
			}
			break;
			
		case "All Checklists":
			elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
			UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
					
			// Assertion : Verify 'All Checklists' window is present 
			elementController.requireElementSmart(fileName, "All Checklists Title", GlobalVariables.configuration.getAttrSearchList(),"All Checklists Title");
			headingOfWindowInPage=UIActions.getText(fileName, "All Checklists Title", GlobalVariables.configuration.getAttrSearchList(),"All Checklists Title");
			headingOfWindowInXML=dataController.getPageDataElements(fileName, "All Checklists Window Title", "Title");
			if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
				throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
			}
			break;	
			
		case "Checklists Map":
			elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
			UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
					
			// Assertion : Verify 'Checklists Map' window is present 
			elementController.requireElementSmart(fileName, "Checklists Map Title", GlobalVariables.configuration.getAttrSearchList(),"Checklists Map Title");
			headingOfWindowInPage=UIActions.getText(fileName, "Checklists Map Title", GlobalVariables.configuration.getAttrSearchList(),"Checklists Map Title");
			headingOfWindowInXML=dataController.getPageDataElements(fileName, "Checklists Map Window Title", "Title");
			if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
				throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
			}
			break;	
			
			
	case "Template Evaluation":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
				
		// Assertion : Verify 'Plan Evaluation' window is present 
		elementController.requireElementSmart(fileName, "Template Evaluation Title", GlobalVariables.configuration.getAttrSearchList(),"Template Evaluation Title");
		headingOfWindowInPage=UIActions.getText(fileName, "Template Evaluation Title", GlobalVariables.configuration.getAttrSearchList(),"Template Evaluation Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Template Evaluation Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;	
		
	case "Template Versions":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
				
		// Assertion : Verify 'Template Versions' window is present 
		elementController.requireElementSmart(fileName, "Template Versions Title", GlobalVariables.configuration.getAttrSearchList(),"Template Versions Title");
		headingOfWindowInPage=UIActions.getText(fileName, "Template Versions Title", GlobalVariables.configuration.getAttrSearchList(),"Template Versions Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Template Versions Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;	
		
	case "All Feedback":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
				
		// Assertion : Verify 'All Feedback' window is present 
		elementController.requireElementSmart(fileName, "All Feedback Title", GlobalVariables.configuration.getAttrSearchList(),"All Feedback Title");
		headingOfWindowInPage=UIActions.getText(fileName, "All Feedback Title", GlobalVariables.configuration.getAttrSearchList(),"All Feedback Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "All Feedback Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;	
		
	case "All Surveys":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		// Assertion : Verify 'All Surveys' window is present 
		elementController.requireElementSmart(fileName, "All Surveys Title", GlobalVariables.configuration.getAttrSearchList(),"All Surveys Title");
		headingOfWindowInPage=UIActions.getText(fileName, "All Surveys Title", GlobalVariables.configuration.getAttrSearchList(),"All Surveys Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "All Surveys Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;
		
	case "Task Mover":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		// Assertion : Verify 'Task Mover' window is present 
		elementController.requireElementSmart(fileName, "Task Mover Title", GlobalVariables.configuration.getAttrSearchList(),"Task Mover Title");
		headingOfWindowInPage=UIActions.getText(fileName, "Task Mover Title", GlobalVariables.configuration.getAttrSearchList(),"Task Mover Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Task Mover Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;	
		
	case "New Issue":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		elementController.requireElementSmart(fileName, "New Issue Text", GlobalVariables.configuration.getAttrSearchList(),"New Issue Text");
		newIssueTextInPage=UIActions.getText(fileName, "New Issue Text", GlobalVariables.configuration.getAttrSearchList(),"New Issue Text");
		newIssueTextInXML=dataController.getPageDataElements(fileName, "New Issue Actual Text", "Name");
		if(!newIssueTextInPage.equals(newIssueTextInXML)){
			throw new UIAutomationException("Issue with name '"+newIssueTextInXML+"' not found");
		}
		UIActions.scrollDown();
		break;
		
	case "Undo Add New Issue":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
//		elementController.requireElementSmart(fileName, "New Issue Text", GlobalVariables.configuration.getAttrSearchList(),"New Issue Text");
//		newIssueTextInPage=UIActions.getText();
//		newIssueTextInXML=dataController.getPageDataElements(fileName, "New Issue Actual Text", "Name");
//		if(!newIssueTextInPage.equals(newIssueTextInXML)){
//			throw new UIAutomationException("Issue with name '"+newIssueTextInXML+"' not found");
//		}
		break;	
		
	//'Undo Remove Issue' in Actions menu	
	case "Undo Remove Issue":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		try{
			Thread.sleep(1000);
		}
		catch (Exception e) {}
		
		break;	
		
		//'Undo Remove Issue' in Actions menu	
		case "Redo Remove Issue":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);			UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		try{
			Thread.sleep(1000);
		}
		catch (Exception e) {}
			
		break;	
		
	case "Undo Update Segment":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		try{
			Thread.sleep(1000);
		}
		catch (Exception e) {}
		
		break;	
	case "Undo Duplicate Task":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
	case "Undo Move Tasks":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
	case "Redo Update Segment":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
	case "Redo Remove This Segment":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
	case "Redo Remove Goal":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
	case "Redo Paste Task":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
	case "Redo Add New Task":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;	
	case "Redo Remove Task":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;	
	case "Undo Paste Task":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
	case "Undo Intermediate":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
	case "Redo Add New Issue":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		elementController.requireElementSmart(fileName, "New Issue Text", GlobalVariables.configuration.getAttrSearchList(),"New Issue Text");
		newIssueTextInPage=UIActions.getText(fileName, "New Issue Text", GlobalVariables.configuration.getAttrSearchList(),"New Issue Text");
		newIssueTextInXML=dataController.getPageDataElements(fileName, "New Issue Actual Text", "Name");
		if(!newIssueTextInPage.equals(newIssueTextInXML)){
			throw new UIAutomationException("Issue with name '"+newIssueTextInXML+"' not found");
		}
		break;
		
	case "Add New Task":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		elementController.requireElementSmart(fileName, "Add New Task Header", GlobalVariables.configuration.getAttrSearchList(),"Add New Task Header");
		String newTaskHeaderInPage=UIActions.getText(fileName, "Add New Task Header", GlobalVariables.configuration.getAttrSearchList(),"Add New Task Header");
		String newTaskHeaderInXML=dataController.getPageDataElements(fileName, "New Task Header Actual Text", "Name");
		if(!newTaskHeaderInPage.contains(newTaskHeaderInXML)){
			throw new UIAutomationException("Task panel not contain '"+newTaskHeaderInXML+"'");
		}
		break;	
		
	case "Hide Details":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		// Assertion: Verify by clicking on 'Hide Details' under task panel details gets hidden 
		xPath=dataController.getPageDataElements(fileName,"Actions In Task Panel", "Xpath");
		List<WebElement> webElemets=UIActions.getElements(xPath);
		if(!webElemets.isEmpty()){
			throw new UIAutomationException("Hide details in task panel can not be clicked");
		}
		break;	
		
	case "Details In Task Panel":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
		
	case "Assignments":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		elementController.requireElementSmart(fileName,"Assignments Title", GlobalVariables.configuration.getAttrSearchList(), "Assignments Title");
		headingOfWindowInPage=UIActions.getText(fileName,"Assignments Title", GlobalVariables.configuration.getAttrSearchList(), "Assignments Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Assignments Actual Text", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Assignment window with heading '"+headingOfWindowInXML+"' not found");
		}
		break;	
		
	case "Failure Impacts":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		elementController.requireElementSmart(fileName,"Failure Impacts Title", GlobalVariables.configuration.getAttrSearchList(), "Failure Impacts Title");
		headingOfWindowInPage=UIActions.getText(fileName,"Failure Impacts Title", GlobalVariables.configuration.getAttrSearchList(), "Failure Impacts Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Failure Impacts Actual Text", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Failure Impact window with heading '"+headingOfWindowInXML+"' not found");
		}
		break;	
		
	case "Dissemination":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		elementController.requireElementSmart(fileName,"Dissemination Title", GlobalVariables.configuration.getAttrSearchList(), "Dissemination Title");
		headingOfWindowInPage=UIActions.getText(fileName,"Dissemination Title", GlobalVariables.configuration.getAttrSearchList(), "Dissemination Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Dissemination Actual Text", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Failure Impact window with heading '"+headingOfWindowInXML+"' not found");
		}
		break;	
		
	case "Send Message In Receives Panel":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		// Assertion : Verify control moves to 'Message' window 
		
		elementController.requireElementSmart(fileName, "About In Messages Text", GlobalVariables.configuration.getAttrSearchList(),"About In Messages Text");
		headingOfWindowInPage=UIActions.getText(fileName, "About In Messages Text", GlobalVariables.configuration.getAttrSearchList(),"About In Messages Text");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "About In Messages Actual Text", "Name");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Message Window not found");
		}
		break;	
		
	case "Send Message In Sends Panel":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		// Assertion : Verify control moves to 'Message' window 
		
		elementController.requireElementSmart(fileName, "About In Messages Text", GlobalVariables.configuration.getAttrSearchList(),"About In Messages Text");
		headingOfWindowInPage=UIActions.getText(fileName, "About In Messages Text", GlobalVariables.configuration.getAttrSearchList(),"About In Messages Text");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "About In Messages Actual Text", "Name");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Message Window not found");
		}
		break;		
		
	case "Paste Task":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		
//		// Assertion : Verify 'Task Mover' window is present 
//		elementController.requireElementSmart(fileName, "Task Mover Title", GlobalVariables.configuration.getAttrSearchList(),"Task Mover Title");
//		headingOfWindowInPage=UIActions.getText();
//		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Task Mover Window Title", "Title");
//		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
//			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
//		}
		break;	
		
	case "Move Tasks In Segment":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		try{
			Thread.sleep(2000);
		}
		catch (Exception e) {}
		// Assertion : Verify 'Task Mover' window is present 
		elementController.requireElementSmart(fileName, "Task Mover Title", GlobalVariables.configuration.getAttrSearchList(),"Task Mover Title");
		headingOfWindowInPage=UIActions.getText(fileName, "Task Mover Title", GlobalVariables.configuration.getAttrSearchList(),"Task Mover Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Task Mover Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;	
		
	case "Copy Flow In Receives Panel":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		try{
			Thread.sleep(2000);
		}
		catch (Exception e) {}
		
		// Assertion : Verify 'Flow copied' message pop up is displayed
		elementController.requireElementSmart(fileName, "Flow Copied Message", GlobalVariables.configuration.getAttrSearchList(),"Flow Copied Message");
		headingOfWindowInPage=UIActions.getText(fileName, "Flow Copied Message", GlobalVariables.configuration.getAttrSearchList(),"Flow Copied Message");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Flow Copied Actual Text", "Name");
		if(!headingOfWindowInPage.contains(headingOfWindowInXML)){
			throw new UIAutomationException("'"+headingOfWindowInXML+"' not found");
		}
		break;	
		
	case "Copy Need":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		// Assertion : Verify 'Need copied' message pop up is displayed
		elementController.requireElementSmart(fileName, "Need Copied Message", GlobalVariables.configuration.getAttrSearchList(),"Need Copied Message");
		headingOfWindowInPage=UIActions.getText(fileName, "Need Copied Message", GlobalVariables.configuration.getAttrSearchList(),"Need Copied Message");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Need Copied Actual Text", "Name");
		if(!headingOfWindowInPage.contains(headingOfWindowInXML)){
			throw new UIAutomationException("'"+headingOfWindowInXML+"' not found");
		}
		break;	
		
	case "New Issue In Receives Panel":
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		try{
			Thread.sleep(2000);
		}
		catch (Exception e) {}
		
		// Assertion : Verify new issue is added 
		elementController.requireElementSmart(fileName, "New Issue Text In Receives Panel", GlobalVariables.configuration.getAttrSearchList(),"New Issue Text In Receives Panel");
		headingOfWindowInPage=UIActions.getText(fileName, "New Issue Text In Receives Panel", GlobalVariables.configuration.getAttrSearchList(),"New Issue Text In Receives Panel");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "New Issue Actual Text", "Name");
		if(!headingOfWindowInPage.contains(headingOfWindowInXML)){
			throw new UIAutomationException("'"+headingOfWindowInXML+"' not found");
		}
		break;
		
	case "New Issue In Sends Panel":
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);

		// Assertion : Verify new issue is added 
		elementController.requireElementSmart(fileName, "New Issue Text In Sends Panel", GlobalVariables.configuration.getAttrSearchList(),"New Issue Text In Sends Panel");
		headingOfWindowInPage=UIActions.getText(fileName, "New Issue Text In Sends Panel", GlobalVariables.configuration.getAttrSearchList(),"New Issue Text In Sends Panel");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "New Issue Actual Text", "Name");
		if(!headingOfWindowInPage.contains(headingOfWindowInXML)){
			throw new UIAutomationException("'"+headingOfWindowInXML+"' not found");
		}
		break;	
		
	case "Remove Info Need In Receives Panel":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Alert Window Title Of Remove Segment", "Title");
		
		UIActions.assertAlert(headingOfWindowInXML);
		break;
		
	case "Remove Info Sharing Capability In Sends Panel":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Alert Window Title Of Remove Segment", "Title");

		UIActions.assertAlert(headingOfWindowInXML);
		break;	
		
	case "Duplicate Flow In Receives Panel":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;	
		
	case "Copy Capability In Sends Panel":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		// Verify capability copied
		elementController.requireElementSmart(fileName, "Capability Copied Message", GlobalVariables.configuration.getAttrSearchList(),"Capability Copied Message");
		headingOfWindowInPage=UIActions.getText(fileName, "Capability Copied Message", GlobalVariables.configuration.getAttrSearchList(),"Capability Copied Message");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Capability Copied Actual Text", "Name");
		if(!headingOfWindowInPage.contains(headingOfWindowInXML)){
			throw new UIAutomationException("'"+headingOfWindowInXML+"' not found");
		}
		break;	
		
	case "Issue In Task Panel":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
	/*	// Assertion : Verify new issue is added 
	
		elementController.requireElementSmart(fileName, "New Issue Text In Task Panel", GlobalVariables.configuration.getAttrSearchList(),"New Issue Text In Task Panel");
		headingOfWindowInPage=UIActions.getText(fileName, "New Issue Text In Task Panel", GlobalVariables.configuration.getAttrSearchList(),"New Issue Text In Task Panel");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "New Issue Actual Text", "Name");
		if(!headingOfWindowInPage.contains(headingOfWindowInXML)){
			throw new UIAutomationException("'"+headingOfWindowInXML+"' not found");
		}*/
		break;	
		
	case "Plan Requirements":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		try{
			Thread.sleep(3000);
		}
		catch (Exception e) {}
		// Assertion : Verify plan requirements window is opened 
		elementController.requireElementSmart(fileName, "Plan Requirements Text", GlobalVariables.configuration.getAttrSearchList(),"Plan Requirements Text");
		headingOfWindowInPage=UIActions.getText(fileName, "Plan Requirements Text", GlobalVariables.configuration.getAttrSearchList(),"Plan Requirements Text");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Plan Requirements Actual Text", "Name");
		if(!headingOfWindowInPage.contains(headingOfWindowInXML)){
			throw new UIAutomationException("'"+headingOfWindowInXML+"' not found");
		}
		break;
		
	case "Remove Flow In Receives Panel":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		try{
			Thread.sleep(3000);
		}
		catch (Exception e) {}
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Alert Window Title Of Remove Task", "Title");
		UIActions.assertAlert(headingOfWindowInXML);
		break;
		
	case "Hide Details In Sends Panel":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;	
		
	case "Hide Details In Receives Panel":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;	
	
	case "Redo New Issue":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;	
		
	case "Undo Add New Task":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
		
	case "Undo New Issue":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
		
	case "Undo Add Information Need":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;		
		
	case "Undo Add Information Capability":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;	
		
	case "Undo Remove Flow":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		// Assertion: verify flow is present when
		break;
		
	case "Undo Duplicate Flow":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;		
	
	case "Undo Add New Requirement":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
	
	case "Redo Add New Requirement":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
		
	case "Undo Remove Requirement":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
		
	case "Redo Remove Requirement":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
		
	case "Remove Issue Flow Issues In Sends Panel":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
		
		
	default:
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
		}
	}	
	
	public void verifyDisintermediate(String undoDisintermediate) throws UIAutomationException{
		// Verify Task is disintermediated
		elementController.requireElementSmart(fileName,"Actions", GlobalVariables.configuration.getAttrSearchList(), "Actions");
		String xPathForPopup=dataController.getPageDataElements(fileName, "Actions", "Xpath");
		UIActions.click(fileName,"Actions", GlobalVariables.configuration.getAttrSearchList(), "Actions");
					
		Actions builder1 = new Actions(GlobalVariables.configuration.getWebDriver());
		builder1.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
		
		List<WebElement> tds = GlobalVariables.configuration.getWebElement().findElements(By.tagName("li"));
		for (WebElement li: tds){
			if (li.getText().contains(undoDisintermediate)){
				UIActions.click();
				break;
			}
		}
	}
	
	public void verifyUndoDisintermediate(String redoDisintermediate) throws UIAutomationException{
		// Verify Task is disintermediated
		elementController.requireElementSmart(fileName,"Actions", GlobalVariables.configuration.getAttrSearchList(), "Actions");
		String xPathForPopup=dataController.getPageDataElements(fileName, "Actions", "Xpath");
		UIActions.click(fileName,"Actions", GlobalVariables.configuration.getAttrSearchList(), "Actions");
					
		Actions builder1 = new Actions(GlobalVariables.configuration.getWebDriver());
		builder1.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
		
		List<WebElement> tds = GlobalVariables.configuration.getWebElement().findElements(By.tagName("li"));
		for (WebElement li: tds){
			if (li.getText().contains(redoDisintermediate)){
				UIActions.click();
				break;
			}
		}
	}
	public void verifyRedoDisintermediate(String undoDisintermediate) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Actions", GlobalVariables.configuration.getAttrSearchList(), "Actions");
		String xPathForPopup=dataController.getPageDataElements(fileName, "Actions", "Xpath");
		UIActions.click(fileName,"Actions", GlobalVariables.configuration.getAttrSearchList(), "Actions");
					
		Actions builder1 = new Actions(GlobalVariables.configuration.getWebDriver());
		builder1.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
		
		List<WebElement> tds = GlobalVariables.configuration.getWebElement().findElements(By.tagName("li"));
		for (WebElement li: tds){
			if (li.getText().contains(undoDisintermediate)){
				UIActions.click();
				break;
			}
		}
	}
	/**
	 * Enter segment name in segment name textbox
	 * @param segmentName
	 * @throws UIAutomationException 
	 */
	public void enterSegmentName(String segmentName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Segment Name",GlobalVariables.configuration.getAttrSearchList(), "Segment Name");
		UIActions.click(fileName,"Segment Name",GlobalVariables.configuration.getAttrSearchList(), "Segment Name");
		UIActions.clearTextBox(fileName,"Segment Name",GlobalVariables.configuration.getAttrSearchList(), "Segment Name");
		UIActions.enterValueInTextBox(segmentName,fileName,"Segment Name",GlobalVariables.configuration.getAttrSearchList(), "Segment Name");
		UIActions.enterKey(Keys.TAB);
	}
	/**
	 * Enters location name in task panel
	 * @param locationName
	 * @throws UIAutomationException
	 */
	public void enterLocationInTask(String locationName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Location In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Location In Task Panel");
		UIActions.click(fileName,"Location In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Location In Task Panel");
		UIActions.enterValueInTextBox(locationName,fileName,"Location In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Location In Task Panel");
		UIActions.enterKey(Keys.TAB);
	}
	
	/**
	 * Location link in task panel
	 * @throws UIAutomationException
	 */
	public void clickLocationLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Location Link In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Location Link In Task Panel");
		UIActions.click(fileName,"Location Link In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Location Link In Task Panel");
		
	}
	
	/**
	 * Enters Agent name in task panel
	 * @param agentName
	 * @throws UIAutomationException
	 */
	public void enterAgentInTask(String agentName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Actor In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Actor In Task Panel");
		UIActions.click(fileName,"Actor In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Actor In Task Panel");
		UIActions.enterValueInTextBox(agentName,fileName,"Actor In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Actor In Task Panel");
		UIActions.enterKey(Keys.TAB);
	}
	/**
	 * Enter Role name in task panel 
	 * @param roleName
	 * @throws UIAutomationException
	 */
	public void enterRoleInTask(String roleName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Role In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Role In Task Panel");
		UIActions.click(fileName,"Role In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Role In Task Panel");
		UIActions.enterValueInTextBox(roleName,fileName,"Role In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Role In Task Panel");
		UIActions.enterKey(Keys.TAB);
	}
	/**
	 * Organization name in task panel
	 * @param orgName
	 * @throws UIAutomationException
	 */
	public void enterOrganizationInTask(String orgName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Organization In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Organization In Task Panel");
		UIActions.click(fileName,"Organization In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Organization In Task Panel");
		UIActions.enterValueInTextBox(orgName,fileName,"Organization In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Organization In Task Panel");
		UIActions.enterKey(Keys.TAB);
	}
	/**
	 * Enter classification name in Classification Systems textbox
	 * @param classificationName
	 * @throws UIAutomationException 
	 */
	public void enterClassificationName(String classificationName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Classification Systems Text Field",GlobalVariables.configuration.getAttrSearchList(), "Classification Systems Text Field");
		UIActions.click(fileName,"Classification Systems Text Field",GlobalVariables.configuration.getAttrSearchList(), "Classification Systems Text Field");
		UIActions.clearTextBox(fileName,"Classification Systems Text Field",GlobalVariables.configuration.getAttrSearchList(), "Classification Systems Text Field");
		UIActions.enterValueInTextBox(classificationName,fileName,"Classification Systems Text Field",GlobalVariables.configuration.getAttrSearchList(), "Classification Systems Text Field");
		UIActions.enterKey(Keys.TAB);
	}
	/**
	 * Function name in task panel
	 * @param funcName
	 * @throws UIAutomationException
	 */
	public void enterFunctionInTask(String funcName) throws UIAutomationException{
		
		elementController.requireElementSmart(fileName,"Function In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Function In Task Panel");
		UIActions.click(fileName,"Function In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Function In Task Panel");
		UIActions.clearTextBox(fileName,"Function In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Function In Task Panel");
		UIActions.enterValueInTextBox(funcName,fileName,"Function In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Function In Task Panel");
		UIActions.enterKey(Keys.TAB);
	}
	/**
	 * Function link in task panel
	 * @throws UIAutomationException
	 */
	public void clickFunctionLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Function Link In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Function Link In Task Panel");
		UIActions.click(fileName,"Function Link In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Function Link In Task Panel");
		
	}
	
	/**
	 * Verify Task name is present in Task mover window
	 * @param taskName
	 * @throws UIAutomationException
	 */
	public void verifyTaskNameInTaskMover(String taskName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Task Mover Table",GlobalVariables.configuration.getAttrSearchList(), "Task Mover Table");
		String taskNamesInPage=UIActions.getText(fileName,"Task Mover Table",GlobalVariables.configuration.getAttrSearchList(), "Task Mover Table");
	
		if(!taskNamesInPage.contains(taskName)){
			throw new UIAutomationException("Task '"+taskName+"' is not created.");
		}
	}
	/**
	 * Click on 'Task Name' in task mover window
	 * @param taskName
	 * @throws UIAutomationException
	 */
	public void clickOnTaskNameInTaskMover(String taskName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Task Mover Table",GlobalVariables.configuration.getAttrSearchList(), "Task Mover Table");
		List<WebElement> tds = GlobalVariables.configuration.getWebElement().findElements(By.tagName("tr"));
			for (WebElement li: tds){
				if (li.getText().contains(taskName)){
					UIActions.click();
					break;
				}
			}
			
	}
	/**
	 * Check checkbox of task in task mover
	 * @param taskName
	 * @throws UIAutomationException
	 */
	public void clickOnCheckboxOfTaskNameInTaskMover(String taskName) throws UIAutomationException{
			// get list of task
			int countTasks = 1 ;
			String firstXPath=dataController.getPageDataElements(fileName, "First XPath", "Xpath");
			String secondXPath=dataController.getPageDataElements(fileName, "Second XPath", "Xpath");
			elementController.requireElementSmart(fileName,"Task Mover Table",GlobalVariables.configuration.getAttrSearchList(), "Task Mover Table");
		
			List<WebElement> trs = GlobalVariables.configuration.getWebElement().findElements(By.tagName("tr"));
			List<WebElement> tds;
			for(WebElement tr: trs){
				tds = tr.findElements(By.tagName("td"));
				for(WebElement td: tds){				
					if(td.getText().contains(taskName)){
						GlobalVariables.configuration.getWebDriver().findElement(By.xpath(firstXPath+ (countTasks) + secondXPath)).click();
					}
				}
				countTasks++;
			}
	}
	
	public void selectSegmentFromList(String segmentName) throws UIAutomationException{
		
	
//				/html/body/form/div[29]/div/ul/li[3]/ul/li/a
//				/html/body/form/div[29]/div/ul/li[3]/ul/li[2]/a
				
//				elementController.requireElementSmart(fileName,"Actual Segment Dropdown On Plan Page",GlobalVariables.configuration.getAttrSearchList(), "Actual Segment Dropdown On Plan Page");
//				elementController.requireElementSmart(fileName,"Segments In Dropdown On Plan Page",GlobalVariables.configuration.getAttrSearchList(), "Segments In Dropdown On Plan Page");
//				List<WebElement> tds = GlobalVariables.configuration.getWebElement().findElements(By.tagName("ul"));
//				List<WebElement> tds1 = GlobalVariables.configuration.getWebElement().findElements(By.tagName("li"));
//				for (WebElement li: tds1){
//					if (li.getText().contains(segmentName)){
//						UIActions.click();
//						break;
//					}
//				}
				
				
	}
	
	/**
	 * Verify task is duplicated
	 * @param taskName
	 * @throws UIAutomationException
	 */
	public void verifyTaskIsDuplicated(String taskName) throws UIAutomationException{
		int count=0;
		elementController.requireElementSmart(fileName,"Task Mover Table",GlobalVariables.configuration.getAttrSearchList(), "Task Mover Table");
		
		// Assertion: Verify task is present
			List<WebElement> tds = GlobalVariables.configuration.getWebElement().findElements(By.tagName("tr"));
			for (WebElement li: tds){
				if (li.getText().contains(taskName)){
					count++;
				}
			}
			if(!(count==2)){
				throw new UIAutomationException("Task '"+taskName+"' can not be duplicated.");
			}
	}
	/**
	 * Verify task is not duplicated
	 * @param taskName
	 * @throws UIAutomationException
	 */
	public void verifyTaskIsNotDuplicated(String taskName) throws UIAutomationException{
		int count=0;
		elementController.requireElementSmart(fileName,"Task Mover Table",GlobalVariables.configuration.getAttrSearchList(), "Task Mover Table");
		
		// Assertion: Verify organization is present
			List<WebElement> tds = GlobalVariables.configuration.getWebElement().findElements(By.tagName("tr"));
			for (WebElement li: tds){
				if (li.getText().contains(taskName)){
					count++;
				}
			}
			if(!(count==1)){
				throw new UIAutomationException("Duplicated task can not be undone.");
			}
	}
	/**
	 * Verify task is removed
	 * @param taskName
	 * @throws UIAutomationException
	 */
	public void verifyTaskIsRemoved(String taskName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Task Mover Table",GlobalVariables.configuration.getAttrSearchList(), "Task Mover Table");
		String taskNamesInPage=UIActions.getText(fileName,"Task Mover Table",GlobalVariables.configuration.getAttrSearchList(), "Task Mover Table");
	
		if(taskNamesInPage.contains(taskName)){
			throw new UIAutomationException("Task '"+taskName+"' is not removed.");
		}
	}
	
	/**
	 * Enter description in issue' description textbox
	 * @param description
	 * @throws UIAutomationException 
	 */
	public void enterDescriptionInIssue(String description) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Description Of New Issue In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Description Of New Issue In Task Panel");
		UIActions.click(fileName,"Description Of New Issue In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Description Of New Issue In Task Panel");
		UIActions.clearTextBox(fileName,"Description Of New Issue In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Description Of New Issue In Task Panel");
		UIActions.enterValueInTextBox(description,fileName,"Description Of New Issue In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Description Of New Issue In Task Panel");
		UIActions.enterKey(Keys.TAB);
		
	}
	
	/**
	 * Enter organization name in organization name textbox
	 * @param organizationName
	 * @throws UIAutomationException
	 */
	public void enterOrganizationName(String organizationName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Organization Name Textbox In Organizations In Scope",GlobalVariables.configuration.getAttrSearchList(), "Organization Name Textbox In Organizations In Scope");
		UIActions.click(fileName,"Organization Name Textbox In Organizations In Scope",GlobalVariables.configuration.getAttrSearchList(), "Organization Name Textbox In Organizations In Scope");
		UIActions.enterValueInTextBox(organizationName,fileName,"Organization Name Textbox In Organizations In Scope",GlobalVariables.configuration.getAttrSearchList(), "Organization Name Textbox In Organizations In Scope");
		UIActions.enterKey(Keys.ENTER);
		
		// Verify organization is added
		elementController.requireElementSmart(fileName,"Organization Table In Organizations In Scope",GlobalVariables.configuration.getAttrSearchList(), "Organization Table In Organizations In Scope");
		String organizationNameInPage=UIActions.getText(fileName,"Organization Table In Organizations In Scope",GlobalVariables.configuration.getAttrSearchList(), "Organization Table In Organizations In Scope");
		
		if(!organizationNameInPage.contains(organizationName)){
			throw new UIAutomationException("Organization is not entered.");
		}
		
	}
	/**
	 * Clicks on 'Organization entered
	 * @param orgName
	 * @throws UIAutomationException
	 */
	public void clickOnOrganizationEntered(String orgName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Organization Table In Organizations In Scope",GlobalVariables.configuration.getAttrSearchList(), "Organization Table In Organizations In Scope");

		List<WebElement> trs = GlobalVariables.configuration.getWebElement().findElements(By.tagName("tr"));
		List<WebElement> tds;
		for(WebElement tr: trs){
			tds = tr.findElements(By.tagName("td"));
			for(WebElement td: tds){				
				if(td.getText().contains(orgName)){
					GlobalVariables.configuration.getWebDriver().findElement(By.linkText(td.getText())).click();
					break;
				}
			}
		}
	}
	
	/**
	 * Click on Network tab of Organizations
	 * @throws UIAutomationException
	 */
	public void clickOrganizationsNetworkTab() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Network Tab",GlobalVariables.configuration.getAttrSearchList(), "Network Tab");
		UIActions.click(fileName,"Network Tab",GlobalVariables.configuration.getAttrSearchList(), "Network Tab");
		
		// Assertion: Verify if the Network Tab is clicked
		elementController.requireElementSmart(fileName,"All flows invoving Org",GlobalVariables.configuration.getAttrSearchList(), "All flows invoving Org");
		String networkTab=UIActions.getText(fileName,"All flows invoving Org",GlobalVariables.configuration.getAttrSearchList(), "All flows invoving Org");
		
		if(!networkTab.contains(networkTab)){
			throw new UIAutomationException("Network's Tab not selected");
		}
	}
	
	/**
	 * Click on Structure Tab under Organizations
	 * @throws UIAutomationException
	 */	
	public void clickOrganizationsStructureTab()throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Structure Tab",GlobalVariables.configuration.getAttrSearchList(), "Structure Tab");
		UIActions.click(fileName,"Structure Tab",GlobalVariables.configuration.getAttrSearchList(), "Structure Tab");
		
		// Assertion: Verify if the Structure Tab is clicked
		elementController.requireElementSmart(fileName,"Jobs",GlobalVariables.configuration.getAttrSearchList(), "Jobs");
		String networkTab=UIActions.getText(fileName,"Jobs",GlobalVariables.configuration.getAttrSearchList(), "Jobs");
		
		if(!networkTab.contains(networkTab)){
			throw new UIAutomationException("Structure's Tab not selected");
		}
	}
	
	/**
	 * Click on Agreement Tab under Organizations
	 * @throws UIAutomationException
	 */
	public void clickOrganizationsAgreementTab() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Participation Tab",GlobalVariables.configuration.getAttrSearchList(), "Participation Tab");
		UIActions.click(fileName,"Participation Tab",GlobalVariables.configuration.getAttrSearchList(), "Participation Tab");
		
		// Assertion: Verify if the Agreements Tab is clicked
		//elementController.requireElementSmart(fileName,"Information sharing agreements",GlobalVariables.configuration.getAttrSearchList(), "Information sharing agreements");
		//String networkTab=UIActions.getText(fileName,"Information sharing agreements",GlobalVariables.configuration.getAttrSearchList(), "Information sharing agreements");
		
		//if(!networkTab.contains(networkTab)){
	//		throw new UIAutomationException("Agreements Tab not selected");
//		}
	}
	
	/**
	 * Click on Analytics Tab under Organizations
	 * @throws UIAutomationException
	 */
	public void clickOrganizationsAnalyticsTab() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Analytics Tab",GlobalVariables.configuration.getAttrSearchList(), "Analytics Tab");
		UIActions.click(fileName,"Analytics Tab",GlobalVariables.configuration.getAttrSearchList(), "Analytics Tab");
		
		// Assertion: Verify if the Analytics Tab is clicked
		elementController.requireElementSmart(fileName,"Task assignments",GlobalVariables.configuration.getAttrSearchList(), "Task assignments");
		String networkTab=UIActions.getText(fileName,"Task assignments",GlobalVariables.configuration.getAttrSearchList(), "Task assignments");
		
		if(!networkTab.contains(networkTab)){
			throw new UIAutomationException("Analytics Tab not selected");
		}
	}
	
	
	/**
	 * Click on Participation Tab under Organizations
	 * @throws UIAutomationException
	 */
	public void clickParticipationTab() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Participation Tab in Organization",GlobalVariables.configuration.getAttrSearchList(), "Participation Tab in Organization");
		UIActions.click(fileName,"Participation Tab in Organization",GlobalVariables.configuration.getAttrSearchList(), "Participation Tab in Organization");
		
	/*	// Assertion: Verify if the Participation Tab is clicked
		elementController.requireElementSmart(fileName,"This organization is a placeholder",GlobalVariables.configuration.getAttrSearchList(), "This organization is a placeholder");
		String participationTab=UIActions.getText(fileName,"This organization is a placeholder",GlobalVariables.configuration.getAttrSearchList(), "This organization is a placeholder");
		
		if(!participationTab.contains(participationTab)){
			throw new UIAutomationException("Participation Tab not selected");
		}*/
	}
	
	/**
	 * Check checkbox of Organization in Participation tab in Organizations in scope
	 * @throws UIAutomationException
	 */
	public void checkCheckboxOfOrganizationInOrganizationInScope() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Checkbox of Organization in Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Checkbox of Organization in Organization In Scope");
		UIActions.click(fileName,"Checkbox of Organization in Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Checkbox of Organization in Organization In Scope");
	}
	
	
	/**
	 * Enter Agent name in the text field in the Participation tab
	 * @param agentName
	 * @throws UIAutomationException 
	*/
	public void enterAgentName(String agentName) throws UIAutomationException	{
		elementController.requireElementSmart(fileName,"Agent Textfield in Participation",GlobalVariables.configuration.getAttrSearchList(), "Agent Name");
		UIActions.click(fileName,"Agent Textfield in Participation",GlobalVariables.configuration.getAttrSearchList(), "Agent Name");
		UIActions.clearTextBox(fileName,"Agent Textfield in Participation",GlobalVariables.configuration.getAttrSearchList(), "Agent Name");
		UIActions.enterValueInTextBox(agentName,fileName,"Agent Textfield in Participation",GlobalVariables.configuration.getAttrSearchList(), "Agent Name");
		UIActions.enterKey(Keys.TAB);
	}
	
	/**
	 * Enter Issues Tab under Organizations
	 * @throws UIAutomationException 
	*/
	public void clickOrganizationsIssuesTab() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Issues Tab",GlobalVariables.configuration.getAttrSearchList(), "Issues Tab");
		UIActions.click(fileName,"Issues Tab",GlobalVariables.configuration.getAttrSearchList(), "Issues Tab");
		
		// Assertion: Verify if the Issues Tab is clicked
		elementController.requireElementSmart(fileName,"Issues Tab",GlobalVariables.configuration.getAttrSearchList(), "Issues Tab");
		String issuesTab=UIActions.getText(fileName,"Issues Tab",GlobalVariables.configuration.getAttrSearchList(), "Issues Tab");
		
		if(!issuesTab.contains(issuesTab)){
			throw new UIAutomationException("Issues Tab not selected");
		}
	}
	/**
	 * Enter agent name in Organization in scope
	 * @param agentName
	 * @throws UIAutomationException
	 */
	public void enterAgentInOrganizationInScope(String agentName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Agent In Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Agent In Organization In Scope");
		UIActions.click(fileName,"Agent In Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Agent In Organization In Scope");
		UIActions.enterValueInTextBox(agentName,fileName,"Agent In Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Agent In Organization In Scope");
		UIActions.enterKey(Keys.TAB);
	}
	/**
	 * Enter title name in Organization in scope
	 * @param titleName
	 * @throws UIAutomationException
	 */
	public void enterTitleInOrganizationInScope(String titleName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Title In Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Title In Organization In Scope");
		UIActions.click(fileName,"Title In Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Title In Organization In Scope");
		UIActions.enterValueInTextBox(titleName,fileName,"Title In Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Title In Organization In Scope");
		UIActions.enterKey(Keys.TAB);
	}
	/**
	 * Enter role in Organization in scope
	 * @param roleName
	 * @throws UIAutomationException
	 */
	public void enterRoleInOrganizationInScope(String roleName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Role In Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Role In Organization In Scope");
		UIActions.click(fileName,"Role In Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Role In Organization In Scope");
		UIActions.enterValueInTextBox(roleName,fileName,"Role In Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Role In Organization In Scope");
		UIActions.enterKey(Keys.TAB);
	}
	/**
	 * Enter jurisdiction in Organization in scope
	 * @param jurisdictionName
	 * @throws UIAutomationException
	 */
	public void enterJurisdictionInOrganizationInScope(String jurisdictionName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Jurisdiction In Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Jurisdiction In Organization In Scope");
		UIActions.click(fileName,"Jurisdiction In Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Jurisdiction In Organization In Scope");
		UIActions.enterValueInTextBox(jurisdictionName,fileName,"Jurisdiction In Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Jurisdiction In Organization In Scope");
		UIActions.enterKey(Keys.TAB);
	}
	/**
	 * Enter supervisor in Organization in scope
	 * @param supervisorName
	 * @throws UIAutomationException
	 */
	public void enterSupervisorInOrganizationInScope(String supervisorName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Supervisor In Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Supervisor In Organization In Scope");
		UIActions.click(fileName,"Supervisor In Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Supervisor In Organization In Scope");
		UIActions.enterValueInTextBox(supervisorName,fileName,"Supervisor In Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Supervisor In Organization In Scope");
		UIActions.enterKey(Keys.TAB);
	}
	
	/**
	 * Check checkbox of Agent in organization in scope
	 * @throws UIAutomationException
	 */
	public void checkCheckboxOfAgentInOrganizationInScope() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Checkbox Of Agent Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Checkbox Of Agent Organization In Scope");
		UIActions.click(fileName,"Checkbox Of Agent Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Checkbox Of Agent Organization In Scope");
	}
	
	/**
	 * Check checkbox of Agent in organization in scope
	 * @throws UIAutomationException
	 */
	public void clickAddButtonOfAgentInOrganizationInScope(String Agent) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Agent Add Button",GlobalVariables.configuration.getAttrSearchList(), "Agent Add Button");
		UIActions.click(fileName,"Agent Add Button",GlobalVariables.configuration.getAttrSearchList(), "Agent Add Button");
		
		// Assertion: Verify if the Agent Tab is added
		elementController.requireElementSmart(fileName,"Agent Added",GlobalVariables.configuration.getAttrSearchList(), "Agent Added");
		String agentName=UIActions.getText(fileName,"Agent Added",GlobalVariables.configuration.getAttrSearchList(), "Agent Added");
		
		if(!agentName.contains(Agent)){
			throw new UIAutomationException("Agent Not Added");
		}

	}
	
	/**
	 * Click on Agent in Organizations in scope
	 * @throws UIAutomationException
	 */
	public void clickAgentInOrganizationInScope() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Agent name in Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Agent name in Organization In Scope");
		UIActions.click(fileName,"Agent name in Organization In Scope",GlobalVariables.configuration.getAttrSearchList(), "Agent name in Organization In Scope");
	}
	
	/**
	 * Select from "Contact" dropdownlist in receives panel
	 * @param contact
	 * @throws UIAutomationException
	 */
	public void selectFromContact(String contact) throws UIAutomationException{
        
		elementController.requireElementSmart(fileName,"Contact Info Drop Down For Agent",GlobalVariables.configuration.getAttrSearchList(),"Contact Info Drop Down For Agent");
		UIActions.click(fileName,"Contact Info Drop Down For Agent",GlobalVariables.configuration.getAttrSearchList(),"Contact Info Drop Down For Agent");
		
		Select contactDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(contactDropDownList);
		UIActions.selectByTextAndClick(contact);
	}
	
	
	/**
	 * Enter Task name in name textbox
	 * @param taskName
	 * @throws UIAutomationException 
	*/
	public void enterTaskName(String taskName) throws UIAutomationException	{
		elementController.requireElementSmart(fileName,"Textbox of Task",GlobalVariables.configuration.getAttrSearchList(), "Task Name");
		UIActions.click(fileName,"Textbox of Task",GlobalVariables.configuration.getAttrSearchList(), "Task Name");
		UIActions.clearTextBox(fileName,"Textbox of Task",GlobalVariables.configuration.getAttrSearchList(), "Task Name");
		UIActions.enterValueInTextBox(taskName,fileName,"Textbox of Task",GlobalVariables.configuration.getAttrSearchList(), "Task Name");
		UIActions.enterKey(Keys.TAB);
		
		// Assertion: Verify if the Task is added
		elementController.requireElementSmart(fileName,"Textbox of Task",GlobalVariables.configuration.getAttrSearchList(), "Textbox of Task");
		taskName=UIActions.getText(fileName,"Textbox of Task",GlobalVariables.configuration.getAttrSearchList(), "Textbox of Task");
		
		if(!taskName.contains(taskName)){
			throw new UIAutomationException("Textbox of Task not selected");
		}
		
	}
	/**
	 * Enters information in receives panel
	 * @param informationName
	 * @throws UIAutomationException
	 */
	public void enterInformationNameInReceivesPanel(String informationName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Information In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Information In Receives Panel");
		UIActions.click(fileName,"Information In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Information In Receives Panel");
		UIActions.enterValueInTextBox(informationName,fileName,"Information In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Information In Receives Panel");
		UIActions.enterKey(Keys.TAB);
		
//		// Assertion: verify information is added
//		elementController.requireElementSmart(fileName,"Add New Receives Header",GlobalVariables.configuration.getAttrSearchList(), "Add New Receives Header");
//		String informationNameInSendsPanel=UIActions.getText(fileName,"Add New Receives Header",GlobalVariables.configuration.getAttrSearchList(), "Add New Receives Header");
//		if(!informationNameInSendsPanel.contains(informationName)){
//			throw new UIAutomationException("Information not entered in receives panel");
//		}
	}
	
	/**
	 * Enters information in Sends panel
	 * @param informationName
	 * @throws UIAutomationException
	 */
	public void enterInformationNameInSendsPanel(String informationName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Information In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Information In Sends Panel");
		UIActions.click(fileName,"Information In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Information In Sends Panel");
		UIActions.enterValueInTextBox(informationName,fileName,"Information In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Information In Sends Panel");
		elementController.requireElementSmart(fileName,"Information In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Information In Sends Panel");
		UIActions.enterKey(Keys.TAB);
		
//		// Assertion: verify information is added
//		elementController.requireElementSmart(fileName,"Add New Sends Header",GlobalVariables.configuration.getAttrSearchList(), "Add New Sends Header");
//		String informationNameInSendsPanel=UIActions.getText(fileName,"Add New Sends Header",GlobalVariables.configuration.getAttrSearchList(), "Add New Sends Header");
//		if(!informationNameInSendsPanel.contains(informationName)){
//			throw new UIAutomationException("Information not entered in sends panel");
//		}
	}
	
	/**
	 * Select from dropdownlist in receives panel
	 * @param other
	 * @throws UIAutomationException
	 */
	public void selectFrom(String Other) throws UIAutomationException{
		
		elementController.requireElementSmart(fileName,"From Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"From Dropdown In Receives Panel");
		UIActions.click(fileName,"From Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"From Dropdown In Receives Panel");
		
		Select fromDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(fromDropDownList);
		UIActions.selectByTextAndClick(Other);
		UIActions.enterKey(Keys.TAB);
	}
	
	/*public void enterOrganizationName(String organizationName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Organization Name Textbox In Organizations In Scope",GlobalVariables.configuration.getAttrSearchList(), "Organization Name Textbox In Organizations In Scope");
		UIActions.click(fileName,"Organization Name Textbox In Organizations In Scope",GlobalVariables.configuration.getAttrSearchList(), "Organization Name Textbox In Organizations In Scope");
		UIActions.enterValueInTextBox(organizationName,fileName,"Organization Name Textbox In Organizations In Scope",GlobalVariables.configuration.getAttrSearchList(), "Organization Name Textbox In Organizations In Scope");
		UIActions.enterKey(Keys.ENTER);
		
		// Verify organization is added
		elementController.requireElementSmart(fileName,"Organization Table In Organizations In Scope",GlobalVariables.configuration.getAttrSearchList(), "Organization Table In Organizations In Scope");
		String organizationNameInPage=UIActions.getText(fileName,"Organization Table In Organizations In Scope",GlobalVariables.configuration.getAttrSearchList(), "Organization Table In Organizations In Scope");
		
		if(!organizationNameInPage.contains(organizationName)){
			throw new UIAutomationException("Organization is not entered.");
		}
		
	}*/
	/**
	 * Enters contact information for Agent
	 * @param information
	 * @throws UIAutomationException
	 */
	public void enterContactInformationForAgent(String information) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Contact Info For Agent Text Field",GlobalVariables.configuration.getAttrSearchList(), "Contact Info For Agent Text Field");
		UIActions.click(fileName,"Contact Info For Agent Text Field",GlobalVariables.configuration.getAttrSearchList(), "Contact Info For Agent Text Field");
		UIActions.enterValueInTextBox(information,fileName,"Contact Info For Agent Text Field",GlobalVariables.configuration.getAttrSearchList(), "Contact Info For Agent Text Field");
		UIActions.enterKey(Keys.ENTER);
		
//		// Assertion: verify information is added
//		elementController.requireElementSmart(fileName,"Add New Receives Header",GlobalVariables.configuration.getAttrSearchList(), "Add New Receives Header");
//		String informationNameInSendsPanel=UIActions.getText(fileName,"Add New Receives Header",GlobalVariables.configuration.getAttrSearchList(), "Add New Receives Header");
//		if(!informationNameInSendsPanel.contains(informationName)){
//			throw new UIAutomationException("Information not entered in receives panel");
//		}
	}
	
	/**
	 * Select from dropdownlist in Sends panel
	 * @param other
	 * @throws UIAutomationException
	 */
	public void selectFromInSends(String Other) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"From Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"From Dropdown In Sends Panel");
		UIActions.click(fileName,"From Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"From Dropdown In Sends Panel");
		UIActions.enterKey(Keys.TAB);
		Select organizationDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(organizationDropDownList);
		UIActions.selectByText(Other);
		UIActions.enterKey(Keys.TAB);
	}
	/**
	 * Select segment from dropdown list in task mover
	 * @param segmentName
	 * @throws UIAutomationException
	 */
	public void selectSegmentInTaskMover(String segmentName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Move to Segment Dropdown In Task Mover",GlobalVariables.configuration.getAttrSearchList(),"Move to Segment Dropdown In Task Mover");
		UIActions.click(fileName,"Move to Segment Dropdown In Task Mover",GlobalVariables.configuration.getAttrSearchList(),"Move to Segment Dropdown In Task Mover");
		
		Select segmentDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(segmentDropDownList);
		UIActions.selectByTextAndClick(segmentName);
	}
	/**
	 * Select option from Phase dropdown in the About Plan Segment Window
	 * @param Responding
	 * @throws UIAutomationException
	 */
	public void selectFromPhaseDropdown(String Responding) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Phase Dropdown In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Context Dropdown In About Plan Segment");
		UIActions.click(fileName,"Phase Dropdown In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Phase Dropdown In About Plan Segment");
		
		Select phaseDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(phaseDropDownList);
		UIActions.selectByTextAndClick(Responding);
	}
	/**
	 * Select option from first "Rated" dropdown
	 * @param Low
	 * @throws UIAutomationException
	 */
	public void selectFromFirstRatedDropdown(String Low) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"First Rated Dropdown In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"First Rated Dropdown In About Plan Segment");
		UIActions.click(fileName,"First Rated Dropdown In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"First Rated Dropdown In About Plan Segment");
		
		Select firstRatedDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(firstRatedDropDownList);
		UIActions.selectByText(Low);
		
}
	/**
	 * Select option from second "Rated" dropdown
	 * @param High
	 * @throws UIAutomationException
	 */
	public void selectFromSecondRatedDropdown(String High) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Second Rated Dropdown In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Second Rated Dropdown In About Plan Segment");
		UIActions.click(fileName,"Second Rated Dropdown In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Second Rated Dropdown In About Plan Segment");
		
		Select secondRatedDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(secondRatedDropDownList);
		UIActions.selectByText(High);
	}
	
	/**
	 * Select option from Occurring dropdown
	 * @param occurring
	 * @throws UIAutomationException
	 */
	public void selectFromOccurringDropdown(String After) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Occurring Dropdown In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Occurring Dropdown In About Plan Segment");
		UIActions.click(fileName,"Occurring Dropdown In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Occurring Dropdown In About Plan Segment");
		
		Select occurringDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(occurringDropDownList);
		UIActions.selectByTextAndClick(After);
	}
	
	/**
	 * Select Receives from task dropdown
	 * @param option
	 * @throws UIAutomationException
	 */
	public void selectReceivesFromTaskDropDown(String option) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"From DropDown Receives Info",GlobalVariables.configuration.getAttrSearchList(),"From DropDown Receives Info");
		UIActions.click(fileName,"From DropDown Receives Info",GlobalVariables.configuration.getAttrSearchList(),"From DropDown Receives Info");
		
		Select contextDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(contextDropDownList);
		UIActions.selectByTextAndClick(option);
	}
	
	/**
	 * Enter value in 'From' textbox in sends panel
	 * @param fromTaskNameInSendsPanel
	 * @throws UIAutomationException
	 */
	public void enterFromTaskName(String fromTaskNameInSendsPanel) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"From Task Name In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "From Task Name In Sends Panel");
		UIActions.click(fileName,"From Task Name In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "From Task Name In Sends Panel");
	    UIActions.enterValueInTextBox(fromTaskNameInSendsPanel,fileName,"From Task Name In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "From Task Name In Sends");
		UIActions.enterKey(Keys.ENTER);
	}
	
	/**
	 * Enter value in 'From' textbox in receives panel
	 * @param FromTaskNameInRecevesPanel
	 * @throws UIAutomationException
	 */
	public void enterFromTaskNameInReceives(String FromTaskNameInRecevesPanel) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"From Task Name In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "From Task Name In Receives Panel");
		UIActions.click(fileName,"From Task Name In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "From Task Name In Receives Panel");
		
		for (int i = 0; i <= 17; i++){
			UIActions.enterKey(Keys.BACK_SPACE);
		}
		UIActions.enterValueInTextBox(FromTaskNameInRecevesPanel,fileName,"From Task Name In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "From Task Name In Receves Panel");
		UIActions.enterKey(Keys.TAB);
	}
	
	/**
	 * Click Default event UNNAMED
	 * @throws UIAutomationException
	 */
	public void clickDefaultEvent() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Default Event",GlobalVariables.configuration.getAttrSearchList(), "Default Event");
		UIActions.click(fileName,"Default Event",GlobalVariables.configuration.getAttrSearchList(), "Default Event");
		
		// Assertion : Verify 'About Event' window is present		
		elementController.requireElementSmart(fileName, "About Event Unnamed Title", GlobalVariables.configuration.getAttrSearchList(),"About Event Unnamed Title");
		String headingOfWindowInPage=UIActions.getText(fileName, "About Event Unnamed Title", GlobalVariables.configuration.getAttrSearchList(),"About Event Unnamed Title");
		String headingOfWindowInXML=dataController.getPageDataElements(fileName, "About Event Unnamed Title Actual Text", "Title");
		if(!headingOfWindowInPage.contains(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
	}
	
	/**
	 * Click Analytics Tab of Event
	 * @throws UIAutomationException
	 */
	public void clickAnalyticsTab() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Analytics Tab",GlobalVariables.configuration.getAttrSearchList(), "Analytics Tab");
		UIActions.click(fileName,"Analytics Tab",GlobalVariables.configuration.getAttrSearchList(), "Analytics Tab");
	
		//	 Assertion: Verify if the Default Event is clicked
		elementController.requireElementSmart(fileName,"Where this event is referenced",GlobalVariables.configuration.getAttrSearchList(), "Where this event is referenced");
		String analyticsTab=UIActions.getText(fileName,"Where this event is referenced",GlobalVariables.configuration.getAttrSearchList(), "Where this event is referenced");
		
		if(!analyticsTab.contains(analyticsTab)){
			throw new UIAutomationException("Analytics Tab not selected");
		}	
	}
	
	/**
	 * Click Issues Tab of Event
	 * @throws UIAutomationException
	 */
	public void clickIssuesTabForEvent() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Issues Tab For Event",GlobalVariables.configuration.getAttrSearchList(), "Issues Tab For Event");
		UIActions.click(fileName,"Issues Tab For Event",GlobalVariables.configuration.getAttrSearchList(), "Issues Tab For Event");
	//	 Assertion: Verify if the Default Event is clicked
		elementController.requireElementSmart(fileName,"Issue Column In Issues Tab",GlobalVariables.configuration.getAttrSearchList(), "Issue Column In Issues Tab");
		String issuesTab=UIActions.getText(fileName,"Issue Column In Issues Tab",GlobalVariables.configuration.getAttrSearchList(), "Issue Column In Issues Tab");
		
		if(!issuesTab.contains(issuesTab)){
			throw new UIAutomationException("Issues Tab not selected");
		}	
	}
	
	/**
	 * Click Event Phases Tab of Event
	 * @throws UIAutomationException
	 */
	public void clickEventPhasesTab() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Event Phases Tab",GlobalVariables.configuration.getAttrSearchList(), "Event Phases Tab");
		UIActions.click(fileName,"Event Phases Tab",GlobalVariables.configuration.getAttrSearchList(), "Event Phases Tab");
	}
	/**
	 * Click Scenario Tab in the About Plan Segment window
	 * @throws UIAutomationException
	 */
	public void clickScenarioTab() throws UIAutomationException{
		elementController.requireElementSmart(fileName, "Scenario Tab", GlobalVariables.configuration.getAttrSearchList(),"Scenario Tab");
        UIActions.click(fileName, "Scenario Tab", GlobalVariables.configuration.getAttrSearchList(), "Scenario Tab");   	
	
        //Assertion: Verify that the Scenario Tab is clicked
        elementController.requireElementSmart(fileName, "This plan segment covers", GlobalVariables.configuration.getAttrSearchList(), "This plan segment covers");
	    String scenarioTab=UIActions.getText(fileName, "This plan segment covers", GlobalVariables.configuration.getAttrSearchList(), "This plan segment covers");
	
	    if(!scenarioTab.contains(scenarioTab))
	    {
	    	throw new UIAutomationException("Scenario Tab not selected");
	    }
	}
	
	/**
	 * Verify Owners Tab is present in the About Plan Segment window
	 * @throws UIAutomationException
	 */
	public void verifyOwnersTabIsPresent() throws UIAutomationException{
		elementController.requireElementSmart(fileName, "Owners Tab", GlobalVariables.configuration.getAttrSearchList(),"Owners Tab");
       
        //Assertion: Verify that the Owners tab is present
        elementController.requireElementSmart(fileName, "Owners Tab", GlobalVariables.configuration.getAttrSearchList(), "Owners Tab");
	    String ownersTab=UIActions.getText(fileName, "Owners Tab", GlobalVariables.configuration.getAttrSearchList(), "Owners Tab");
	
	    if(!ownersTab.contains(ownersTab))
	    {
	    	throw new UIAutomationException("Owners Tab not present");
	    }
	}
	
	/**
	 * Click Owners Tab present in the About Plan Segment window
	 * @throws UIAutomationException
	 */
	public void clickOwnersTab() throws UIAutomationException{
		elementController.requireElementSmart(fileName, "Owners Tab", GlobalVariables.configuration.getAttrSearchList(),"Owners Tab");
		  UIActions.click(fileName, "Owners Tab", GlobalVariables.configuration.getAttrSearchList(), "Owners Tab");   	
			
        //Assertion: Verify the title on  Owners tab
        elementController.requireElementSmart(fileName, "Who Can Modify This Segment", GlobalVariables.configuration.getAttrSearchList(), "Who Can Modify This Segment");
	    String ownersTab1=UIActions.getText(fileName, "Who Can Modify This Segment ", GlobalVariables.configuration.getAttrSearchList(), "Who Can Modify This Segment");
	    String ownersTabText=dataController.getPageDataElements(fileName, "Who Can Modify This Segment Text", "Name");
		if(!ownersTab1.contains(ownersTabText)){
			throw new UIAutomationException("Tab with Text '"+ownersTabText+"' not found");
		}
	    System.out.println(ownersTabText);
	}
	
	/**
	 * Click Owners Tab present in the About Plan Segment window
	 * @throws UIAutomationException
	 */
	public void clickHelpIconInAboutTemplateOwnersTab() throws UIAutomationException{
		
		elementController.requireElementSmart(fileName, "Help Icon In About Template Segment", GlobalVariables.configuration.getAttrSearchList(), "Help Icon In About Template Segment");
		UIActions.click(fileName, "Help Icon In About Template Segment", GlobalVariables.configuration.getAttrSearchList(), "Help Icon In About Template Segment");   	
			 
        //Assertion: Verify the title on  Owners tab
        elementController.requireElementSmart(fileName, "Help For Segment Owners", GlobalVariables.configuration.getAttrSearchList(), "Help For Segment Owners");
	    String helpTab=UIActions.getText(fileName, "Help For Segment Owners", GlobalVariables.configuration.getAttrSearchList(), "Help For Segment Owners");
	    String helpTabText=dataController.getPageDataElements(fileName, "Help For Segment Owners Text", "Name");
		if(!helpTab.contains(helpTabText)){
			throw new UIAutomationException("Tab with Text '"+helpTabText+"' not found");
		}
	    System.out.println(helpTabText);
	}
	/**
	 * Admin Clicks Owners Tab present in the About Plan Segment window
	 * @throws UIAutomationException
	 */
	public void clickOwnersTabAsAdmin() throws UIAutomationException{
		elementController.requireElementSmart(fileName, "Owners Tab", GlobalVariables.configuration.getAttrSearchList(),"Owners Tab");
		  UIActions.click(fileName, "Owners Tab", GlobalVariables.configuration.getAttrSearchList(), "Owners Tab");   	
			
        //Assertion: Verify the title on  Owners tab
        elementController.requireElementSmart(fileName, "Message On Owners Tab For Admin", GlobalVariables.configuration.getAttrSearchList(), "Message On Owners Tab For Admin");
	    String ownersTab=UIActions.getText(fileName, "Message On Owners Tab For Admin", GlobalVariables.configuration.getAttrSearchList(), "Message On Owners Tab For Admin");
	    String ownersTabText=dataController.getPageDataElements(fileName, "Message On Owners Tab For Admin Text", "Name");
		if(!ownersTab.contains(ownersTabText)){
			throw new UIAutomationException("Tab with Text '"+ownersTabText+"' not found");
		}
	    System.out.println(ownersTabText);
	    
	}
	
	/**
	 * Developer Clicks Owners Tab present in the About Plan Segment window
	 * @throws UIAutomationException
	 */
	public void clickOwnersTabAsDeveloper() throws UIAutomationException{
		elementController.requireElementSmart(fileName, "Owners Tab", GlobalVariables.configuration.getAttrSearchList(),"Owners Tab");
		  UIActions.click(fileName, "Owners Tab", GlobalVariables.configuration.getAttrSearchList(), "Owners Tab");   	
			
        //Assertion: Verify the message on  Owners tab
        elementController.requireElementSmart(fileName, "Message On Owners Tab For Developer", GlobalVariables.configuration.getAttrSearchList(), "Message On Owners Tab For Developer");
	    String developerTab=UIActions.getText(fileName, "Message On Owners Tab For Developer", GlobalVariables.configuration.getAttrSearchList(), "Message On Owners Tab For Developer");
	    String developerTabText=dataController.getPageDataElements(fileName, "Message On Owners Tab For Developer Text", "Name");
		if(!developerTab.contains(developerTabText)){
			throw new UIAutomationException("Tab with Text '"+developerTabText+"' not found");
		}
	    System.out.println(developerTabText);
	    
	}
	
	/**
	 * Developer who is not an owner Clicks Owners Tab present in the About Plan Segment window
	 * @throws UIAutomationException
	 */
	public void clickOwnersTabAsDeveloperWhoIsNotAnOwner() throws UIAutomationException{
		elementController.requireElementSmart(fileName, "Owners Tab", GlobalVariables.configuration.getAttrSearchList(),"Owners Tab");
		  UIActions.click(fileName, "Owners Tab", GlobalVariables.configuration.getAttrSearchList(), "Owners Tab");   	
			
        //Assertion: Verify the message on  Owners tab
        elementController.requireElementSmart(fileName, "Message On Owners Tab For Developer Who Is Not Owner", GlobalVariables.configuration.getAttrSearchList(), "Message On Owners Tab For Developer Who Is Not Owner");
	    String developerTab=UIActions.getText(fileName, "Message On Owners Tab For Developer Who Is Not Owner", GlobalVariables.configuration.getAttrSearchList(), "Message On Owners Tab For Developer Who Is Not Owner");
	    String developerTabText=dataController.getPageDataElements(fileName, "Message On Owners Tab For Developer Who Is Not Owner Text", "Name");
		if(!developerTab.contains(developerTabText)){
			throw new UIAutomationException("Tab with Text '"+developerTabText+"' not found");
		}
	    System.out.println(developerTabText);
	    
	}
	
	/**
	 * Guest user clicks Owners Tab present in the About Plan Segment window
	 * @throws UIAutomationException
	 */
	public void clickOwnersTabAsGuest() throws UIAutomationException{
		elementController.requireElementSmart(fileName, "Owners Tab", GlobalVariables.configuration.getAttrSearchList(),"Owners Tab");
		  UIActions.click(fileName, "Owners Tab", GlobalVariables.configuration.getAttrSearchList(), "Owners Tab");   	
			
        //Assertion: Verify the message on  Owners tab when Guest User Logs in
        elementController.requireElementSmart(fileName, "Message On Owners Tab For Guest User", GlobalVariables.configuration.getAttrSearchList(), "Message On Owners Tab For Guest User");
	    String guestUserTab=UIActions.getText(fileName, "Message On Owners Tab For Guest User", GlobalVariables.configuration.getAttrSearchList(), "Message On Owners Tab For Guest User");
	    String guestUserTabText=dataController.getPageDataElements(fileName, "Message On Owners Tab For Guest User Text", "Name");
		if(!guestUserTab.contains(guestUserTabText)){
			throw new UIAutomationException("Tab with Text '"+guestUserTabText+"' not found");
		}
	    System.out.println(guestUserTabText);
	    
	}
	/**
	 * Admin Clicks Owners Tab present in the About Plan Segment window
	 * @throws UIAutomationException
	 */
	public void checkIsOwnerCheckbox() throws UIAutomationException{
		elementController.requireElementSmart(fileName, "Is Owner Check Box", GlobalVariables.configuration.getAttrSearchList(),"Is Owner Check Box");
		UIActions.click(fileName, "Is Owner Check Box", GlobalVariables.configuration.getAttrSearchList(), "Is Owner Check Box");   	
	
		//Assertion: Check the text present on Surveys Page
        elementController.requireElementSmart(fileName,"Can Modify Column",GlobalVariables.configuration.getAttrSearchList(), "Can Modify Column");
      	String canModifyColumn=UIActions.getText(fileName,"Can Modify Column",GlobalVariables.configuration.getAttrSearchList(), "Can Modify Column");
      	String canModifyColumnText=dataController.getPageDataElements(fileName,"Can Modify Column Text" , "Name");
      	if(!canModifyColumn.contains(canModifyColumnText)){
      		throw new UIAutomationException( "'"+canModifyColumnText +"' not found");}
	}
	
	
	/**
	 * Click Create Survey Button In Task Panel
	 * @throws UIAutomationException
	 */
	public void clickCreateSurveyButtonInTaskPanel() throws UIAutomationException{
		elementController.requireElementSmart(fileName, "Create Survey In Task Panel", GlobalVariables.configuration.getAttrSearchList(),"Create Survey In Task Panel");
        UIActions.click(fileName, "Create Survey In Task Panel", GlobalVariables.configuration.getAttrSearchList(), "Create Survey In Task Panel");   	
	
        //Assertion: Check the text present on Surveys Page
        elementController.requireElementSmart(fileName,"All Surveys Title",GlobalVariables.configuration.getAttrSearchList(), "All Surveys Title");
      	String surveysPageTitle=UIActions.getText(fileName,"All Surveys Title",GlobalVariables.configuration.getAttrSearchList(), "All Surveys Title");
      	String textInTitle=dataController.getPageDataElements(fileName,"All Surveys Text" , "Name");
      	if(!surveysPageTitle.contains(textInTitle)){
      		throw new UIAutomationException( "'"+textInTitle +"' not found");}
	}
	
	/**
	 * Enter event in about plan window
	 * @param eventName
	 * @throws UIAutomationException
	 */
	public void enterEventInAboutPlanSegment(String eventName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Event In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(), "Event In About Plan Segment");
		UIActions.click(fileName,"Event In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(), "Event In About Plan Segment");
		UIActions.clearTextBox(fileName,"Event In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(), "Event In About Plan Segment");
		UIActions.enterValueInTextBox(eventName,fileName,"Event In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(), "Event In About Plan Segment");
		UIActions.enterKey(Keys.TAB,fileName,"Event In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(), "Event In About Plan Segment");
	}
	
/**
	 * Enter phase in All Events and Phases window
	 * @param phaseName
	 * @throws UIAutomationException
	 */
	public void enterPhaseInAllEventsAndPhases(String phaseName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Phase In All Events And Phases",GlobalVariables.configuration.getAttrSearchList(), "Phase In All Events And Phases");
		UIActions.click(fileName,"Phase In All Events And Phases",GlobalVariables.configuration.getAttrSearchList(), "Phase In All Events And Phases");
	//	UIActions.clearTextBox(fileName,"Event In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(), "Event In About Plan Segment");
		UIActions.enterValueInTextBox(phaseName,fileName,"Phase In All Events And Phases",GlobalVariables.configuration.getAttrSearchList(), "Phase In All Events And Phases");
		UIActions.enterKey(Keys.TAB,fileName,"Phase In All Events And Phases",GlobalVariables.configuration.getAttrSearchList(), "Phase In All Events And Phases");
	}
	
/**
     * Enter second event in About plan window
	 * @param eventName
	 * @throws UIAutomationException
	 */
	public void enterSecondEventInAboutPlanSegment(String eventName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Second Event In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(), "Second Event In About Plan Segment");
		UIActions.click(fileName,"Second Event In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(), "Second Event In About Plan Segment");
		UIActions.enterValueInTextBox(eventName,fileName,"Second Event In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(), "Second Event In About Plan Segment");
		UIActions.enterKey(Keys.TAB,fileName,"Second Event In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(), "Second Event In About Plan Segment");
	}
	/**
	 * Enter value of Tag in 'Tags' textbox
	 * @param tagName
	 * @throws UIAutomationException
	 */
	public void enterValueInTagsInTask(String tagName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Tags In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Tags In Task Panel");
		UIActions.click(fileName,"Tags In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Tags In Task Panel");
		UIActions.enterValueInTextBox(tagName,fileName,"Tags In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Tags In Task Panel");
		UIActions.enterKey(Keys.TAB);
	}
	
	/**
	 * Enter value in 'Causes Event' textbox
	 * @param eventName
	 * @throws UIAutomationException
	 */
	public void enterValueInCausesEventInTask(String eventName) throws UIAutomationException{
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"Causes Event In Task",GlobalVariables.configuration.getAttrSearchList(), "Causes Event In Task");
		UIActions.click(fileName,"Causes Event In Task",GlobalVariables.configuration.getAttrSearchList(), "Causes Event In Task");
		UIActions.enterValueInTextBox(eventName,fileName,"Causes Event In Task",GlobalVariables.configuration.getAttrSearchList(), "Causes Event In Task");
		UIActions.enterKey(Keys.TAB);
		
	}
	/**
	 * Enter value in phase
	 * @param phaseName
	 * @throws UIAutomationException
	 */
	public void enterValueInPhaseInEvent(String phaseName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Phase In About Plan",GlobalVariables.configuration.getAttrSearchList(), "Phase In About Plan");
		UIActions.click(fileName,"Phase In About Plan",GlobalVariables.configuration.getAttrSearchList(), "Phase In About Plan");
		UIActions.enterValueInTextBox(phaseName,fileName,"Phase In About Plan",GlobalVariables.configuration.getAttrSearchList(), "Phase In About Plan");
		UIActions.enterKey(Keys.TAB);
		
	}
	/**
	 * Click on phase added
	 * @param phaseName
	 * @throws UIAutomationException
	 */
	
	public void clickOnPhaseInAboutPlan() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Actual Phase In About Plan",GlobalVariables.configuration.getAttrSearchList(), "Actual Phase In About Plan");
		UIActions.click(fileName,"Actual Phase In About Plan",GlobalVariables.configuration.getAttrSearchList(), "Actual Phase In About Plan");
	}
	
	/**
	 * Click on Move in Task Mover
	 * @throws UIAutomationException
	 */
	public void clickOnMoveInTaskMover() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Move Button In Task Mover",GlobalVariables.configuration.getAttrSearchList(), "Move Button In Task Mover");
		UIActions.click(fileName,"Move Button In Task Mover",GlobalVariables.configuration.getAttrSearchList(), "Move Button In Task Mover");
	}
	/**
	 * Enter value in description of phase
	 * @param phaseDescription
	 * @throws UIAutomationException
	 */
	public void enterValueInPhaseDescriptionInAboutPlan(String phaseDescription) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Phase Description",GlobalVariables.configuration.getAttrSearchList(), "Phase Description");
		UIActions.click(fileName,"Phase Description",GlobalVariables.configuration.getAttrSearchList(), "Phase Description");
		UIActions.enterValueInTextBox(phaseDescription,fileName,"Phase Description",GlobalVariables.configuration.getAttrSearchList(), "Phase Description");
		UIActions.enterKey(Keys.TAB);
		
	}
	
	/**
	 * Delete Phase in About Plan
	 * delete phase
	 * @throws UIAutomationException
	 */
	public void deletePhase() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Delete Phase In About Plan",GlobalVariables.configuration.getAttrSearchList(), "Delete Phase In About Plan");
		UIActions.click(fileName,"Delete Phase In About Plan",GlobalVariables.configuration.getAttrSearchList(), "Delete Phase In About Plan");
	
		String questionInXML=dataController.getPageDataElements(fileName, "Delete Phase Window Title", "Title");
		try{
			Thread.sleep(1000);
			}
			catch(Exception e){}
		UIActions.assertAlert(questionInXML);
	}
	 
	/**
	 * Enter value of event in event textbox
	 * @param eventName
	 * @throws UIAutomationException
	 */
	public void enterValueInEventInEventInScope(String eventName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Event In Event In Scope Text",GlobalVariables.configuration.getAttrSearchList(), "Event In Event In Scope Text");
		UIActions.click(fileName,"Event In Event In Scope Text",GlobalVariables.configuration.getAttrSearchList(), "Event In Event In Scope Text");
		UIActions.enterValueInTextBox(eventName,fileName,"Event In Event In Scope Text",GlobalVariables.configuration.getAttrSearchList(), "Event In Event In Scope Text");
		UIActions.enterKey(Keys.TAB);
		
		// Verify event is added
		elementController.requireElementSmart(fileName,"Table Of Events In Event In Scope",GlobalVariables.configuration.getAttrSearchList(), "Table Of Events In Event In Scope");
		String eventsInPage=UIActions.getText(fileName,"Table Of Events In Event In Scope",GlobalVariables.configuration.getAttrSearchList(), "Table Of Events In Event In Scope");
		if(!eventsInPage.contains(eventName)){
			throw new UIAutomationException("Event "+eventName+" is not added.");
		}
	}
	
	/**
	 * Delete Tag value in Tags text box
	 * @throws UIAutomationException
	 */
	public void removeValueInTagsInTask() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Tags In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Tags In Task Panel");
		UIActions.click(fileName,"Tags In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Tags In Task Panel");
		
		for (int i = 0; i <= 3; i++){
			UIActions.enterKey(Keys.BACK_SPACE);
		}
		UIActions.enterKey(Keys.TAB);
	}
	
	/**
	 * Verifies 'Task Is' dropdown is present
	 * @param unspecified
	 * @throws UIAutomationException
	 */
	
	public void verifyTaskIsDropdownPresent(String unspecified) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Task Is Dropdown In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Task Is Dropdown In Task Panel");
		UIActions.click(fileName,"Task Is Dropdown In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Task Is Dropdown In Task Panel");
		
		Select taskIsDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(taskIsDropDownList);
		UIActions.selectByTextAndClick(unspecified);
	}
	/**
	 * Clicks on 'Goals' tab in 'About Plan Segment' window
	 * @throws UIAutomationException
	 */
	public void clickGoalTab() throws UIAutomationException{
		String nameOfTabInPage=null;
		String nameOfTabInXML=null;
		elementController.requireElementSmart(fileName,"Goals Tab",GlobalVariables.configuration.getAttrSearchList(), "Goals Tab");
		UIActions.click(fileName,"Goals Tab",GlobalVariables.configuration.getAttrSearchList(), "Goals Tab");
				
		// Assertion : Verify 'Goals' window is present 
		elementController.requireElementSmart(fileName, "Goals Tab", GlobalVariables.configuration.getAttrSearchList(),"Goals Tab");
		nameOfTabInPage=UIActions.getText(fileName, "Goals Tab", GlobalVariables.configuration.getAttrSearchList(),"Goals Tab");
		nameOfTabInXML=dataController.getPageDataElements(fileName, "Goals Window Title", "Title");
		if(!nameOfTabInPage.equals(nameOfTabInXML)){
			throw new UIAutomationException("Tab with name '"+nameOfTabInXML+"' not found");
		}
	}
	
	/** 
	 * Clicks on 'Tags' link in task panel
	 * @throws UIAutomationException
	 */
	public void clickTagsLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Tags Link In Task Panel", GlobalVariables.configuration.getAttrSearchList(), "Tags Link In Task Panel");
		UIActions.click(fileName,"Tags Link In Task Panel", GlobalVariables.configuration.getAttrSearchList(), "Tags Link In Task Panel");
				
		// Assertion : Verify 'Tags' window is present 
		elementController.requireElementSmart(fileName, "Tags Title", GlobalVariables.configuration.getAttrSearchList(),"Tags Title");
		String headingOfWindowInPage=UIActions.getText(fileName, "Tags Title", GlobalVariables.configuration.getAttrSearchList(),"Tags Title");
		String headingOfWindowInXML=dataController.getPageDataElements(fileName, "Tags Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
	}
	
	
	/**
	 * Clicks on 'Unnamed' link in 'can end event unnamed'
	 * @throws UIAutomationException
	 */
	public void clickUnnamedLinkIntask() throws UIAutomationException{
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"Can End Event Unnamed Link In Task", GlobalVariables.configuration.getAttrSearchList(), "Can End Event Unnamed Link In Task");
		UIActions.click(fileName,"Can End Event Unnamed Link In Task", GlobalVariables.configuration.getAttrSearchList(), "Can End Event Unnamed Link In Task");
				
		// Assertion : Verify 'About Event' window is present		
		elementController.requireElementSmart(fileName, "About Event Unnamed Title", GlobalVariables.configuration.getAttrSearchList(),"About Event Unnamed Title");
		String headingOfWindowInPage=UIActions.getText(fileName, "About Event Unnamed Title", GlobalVariables.configuration.getAttrSearchList(),"About Event Unnamed Title");
		String headingOfWindowInXML=dataController.getPageDataElements(fileName, "About Event Unnamed Title Actual Text", "Title");
		if(!headingOfWindowInPage.contains(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
	}
	
	/**
	 * Clicks on 'Organizations' tab in 'About Plan Segment' window
	 * @throws UIAutomationException
	 */
	public void clickOrganizationsTab() throws UIAutomationException{
		String nameOfTabInPage=null;
		String nameOfTabInXML=null;
		elementController.requireElementSmart(fileName,"Organizations Tab",GlobalVariables.configuration.getAttrSearchList(), "Organizations Tab");
		UIActions.click(fileName,"Organizations Tab",GlobalVariables.configuration.getAttrSearchList(), "Organizations Tab");
		
		// Assertion : Verify 'Organizations' window is present 
		elementController.requireElementSmart(fileName, "Organizations Tab", GlobalVariables.configuration.getAttrSearchList(),"Organizations Tab");
		nameOfTabInPage=UIActions.getText(fileName, "Organizations Tab", GlobalVariables.configuration.getAttrSearchList(),"Organizations Tab");
		
		nameOfTabInXML=dataController.getPageDataElements(fileName, "Organization Tab Window Title", "Title");
		if(!nameOfTabInPage.equals(nameOfTabInXML)){
			throw new UIAutomationException("Tab with name '"+nameOfTabInXML+"' not found");
		}
		
	}
	
	/**
	 * Clicks on organization name
	 * @param organizationName
	 * @throws UIAutomationException
	 */
	public void clickOrganizationName(String organizationName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Organization Table In Organizations In Scope",GlobalVariables.configuration.getAttrSearchList(), "Organization Table In Organizations In Scope");
			
		List<WebElement> tds = GlobalVariables.configuration.getWebElement().findElements(By.tagName("li"));
		for (WebElement li: tds){
			if (li.getText().equals(organizationName)){
				li.findElement(By.linkText(organizationName)).click();
				break;
			}
		}
		
	}
	
	/**
	 * Close Actual Organization window
	 * @throws UIAutomationException
	 */
	public void closeActualOrganizationWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Actual Organization Window",GlobalVariables.configuration.getAttrSearchList(), "Close Actual Organization Window");
		UIActions.click(fileName,"Close Actual Organization Window",GlobalVariables.configuration.getAttrSearchList(), "Close Actual Organization Window");
		
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		
	}
	/**
	 * Close actual phase window
	 * @throws UIAutomationException
	 */
	public void closeActualPhaseWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Actual Phase Window",GlobalVariables.configuration.getAttrSearchList(), "Close Actual Phase Window");
		UIActions.click(fileName,"Close Actual Phase Window",GlobalVariables.configuration.getAttrSearchList(), "Close Actual Phase Window");
		
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		
	}
	/**
	 * Close Actual Agent window
	 * @throws UIAutomationException
	 */
	public void closeActualAgentWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Actual Agent Window",GlobalVariables.configuration.getAttrSearchList(), "Close Actual Agent Window");
		UIActions.click(fileName,"Close Actual Agent Window",GlobalVariables.configuration.getAttrSearchList(), "Close Actual Agent Window");
		
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		
	}
	
	/**
	 * Close About Function window
	 * @throws UIAutomationException
	 */
	public void closeAboutFunctionWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close About Function Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Function Window");
		UIActions.click(fileName,"Close About Function Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Function Window");
		
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		
	}
	
	/** 
	 * Clicks on 'Tags' link in task panel
	 * @throws UIAutomationException
	 */
	public void clickChecklistsIcon() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Checklists Icon", GlobalVariables.configuration.getAttrSearchList(), "Checklists Icon");
		UIActions.click(fileName,"Checklists Icon", GlobalVariables.configuration.getAttrSearchList(), "Checklists Icon");
				
		// Assertion : Verify 'Checklist' window is present 
		elementController.requireElementSmart(fileName, "Checklists Title", GlobalVariables.configuration.getAttrSearchList(),"Tags Title");
		String headingOfWindowInPage=UIActions.getText(fileName, "Checklists Title", GlobalVariables.configuration.getAttrSearchList(),"Tags Title");
		String headingOfWindowInXML=dataController.getPageDataElements(fileName, "Checklists Window Title", "Name");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
	}
	
	
	/**
	 * Clicks on 'Strench Up' form icon
	 * @throws UIAutomationException
	 */
	public void clickStrenchUpForm() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Strench Up Forms", GlobalVariables.configuration.getAttrSearchList(), "Strench Up Forms");
		UIActions.click(fileName,"Strench Up Forms", GlobalVariables.configuration.getAttrSearchList(), "Strench Up Forms");
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Clicks on 'Actions' pop up menu in 'About Plan Segment' window
	 * @throws UIAutomationException
	 */
	public void clickActionsInAboutPlanSegment() throws UIAutomationException{
		// Assertion: Check 'Actions' pop up is present 
		elementController.requireElementSmart(fileName,"Actions In Segment",GlobalVariables.configuration.getAttrSearchList(), "Actions In Segment");
		String popUpNameInPage=UIActions.getText(fileName,"Actions In Segment",GlobalVariables.configuration.getAttrSearchList(), "Actions In Segment");
		String popUpNameInXml=dataController.getPageDataElements(fileName, "Actions In About Plan Segment", "Name");
		if(!popUpNameInPage.equals(popUpNameInXml)){
			throw new UIAutomationException( "'"+popUpNameInXml +"' not found");
		}
	}
	public void clickOnIntermediateTask(String intermediateTaskName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Intermediate Actual Task",GlobalVariables.configuration.getAttrSearchList(), "Intermediate Actual Task");
		UIActions.click(fileName,"Intermediate Actual Task",GlobalVariables.configuration.getAttrSearchList(), "Intermediate Actual Task");
	}
	
	/**
	 * Click on Remove expectation
	 * @param organizationName
	 * @throws UIAutomationException
	 */
	public void clickRemoveExpectation(String organizationName) throws UIAutomationException{
		
		elementController.requireElementSmart(fileName,"Organization Table In Organizations In Scope",GlobalVariables.configuration.getAttrSearchList(), "Organization Table In Organizations In Scope");
		// Assertion: Verify organization is present
		List<WebElement> tds = GlobalVariables.configuration.getWebElement().findElements(By.tagName("li"));
		for (WebElement li: tds){
			if (li.getText().equals(organizationName)){
				break;
			}
		}
		
		elementController.requireElementSmart(fileName,"Remove Expectation",GlobalVariables.configuration.getAttrSearchList(), "Remove Expectation");
		UIActions.click(fileName,"Remove Expectation",GlobalVariables.configuration.getAttrSearchList(), "Remove Expectation");
		
		try{
			Thread.sleep(2000);
		}
		catch(Exception e){}
		// Assertion: verify organization is removed
		elementController.requireElementSmart(fileName,"Organization Table In Organizations In Scope",GlobalVariables.configuration.getAttrSearchList(), "Organization Table In Organizations In Scope");
		List<WebElement> tds1 = GlobalVariables.configuration.getWebElement().findElements(By.tagName("li"));
		for (WebElement li: tds1){
			if (li.getText().equals(organizationName)){
				throw new UIAutomationException("Organization not removed.");
			}
		}
		
	}
	/**
	 * Verify event is deleted
	 * @param eventName
	 * @throws UIAutomationException
	 */
	public void verifyEventIsDeleted(String eventName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Segment Frame In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(), "Segment Frame In About Plan Segment");
		String eventNamesInPage=UIActions.getText(fileName,"Segment Frame In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(), "Segment Frame In About Plan Segment");
		if(eventNamesInPage.equals(eventName)){
			throw new UIAutomationException( "'"+eventName +"' not deleted.");
		}
	}
	
	/**
	 * Click on 'Definitions' tab in plan requirements
	 * @param tabName
	 * @throws UIAutomationException
	 */
	public void clickDefinitionsTabInPlanRequirement(String tabName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Definitions Tab In Plan Requirements",GlobalVariables.configuration.getAttrSearchList(), "Definitions Tab In Plan Requirements");
		UIActions.click(fileName,"Definitions Tab In Plan Requirements",GlobalVariables.configuration.getAttrSearchList(), "Definitions Tab In Plan Requirements");
		
		String tabNameInPage=UIActions.getText(fileName,"Definitions Tab In Plan Requirements",GlobalVariables.configuration.getAttrSearchList(), "Definitions Tab In Plan Requirements");
		if(!tabNameInPage.equals(tabName)){
			throw new UIAutomationException( "'"+tabName +"' not found");
		}
	}
	/**
	 * Click on 'New' button in plan requirements 
	 * @param name
	 * @throws UIAutomationException
	 */
	public void clickNewButtonInPlanRequirement(String name) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"New Button In Definitions Tab In Plan Requirements",GlobalVariables.configuration.getAttrSearchList(), "New Button In Definitions Tab In Plan Requirements");
		UIActions.click(fileName,"New Button In Definitions Tab In Plan Requirements",GlobalVariables.configuration.getAttrSearchList(), "New Button In Definitions Tab In Plan Requirements");
		
		String nameInPage=UIActions.getText(fileName,"Requirement Definitions In Plan Requirements",GlobalVariables.configuration.getAttrSearchList(), "Requirement Definitions In Plan Requirements");
		if(!nameInPage.contains(name)){
			throw new UIAutomationException( "'"+name +"' not found");
		}
	}
	/**
	 * Click on 'Remove requirement'
	 * @throws UIAutomationException
	 */
	public void clickRemoveRequirementInPlanRequirement() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Remove Requirement In Plan Requirements",GlobalVariables.configuration.getAttrSearchList(), "Remove Requirement In Plan Requirements");
		UIActions.click(fileName,"Remove Requirement In Plan Requirements",GlobalVariables.configuration.getAttrSearchList(), "Remove Requirement In Plan Requirements");
		try{
			Thread.sleep(2000);
		}
		catch(Exception e){}
		String alertquestion =dataController.getPageDataElements(fileName, "Alert Window Title Of Remove Requirement", "Title");
		UIActions.assertAlert(alertquestion);
		
		
	}
	/**
	 * Click on 'Satisfaction' tab in plan requirements
	 * @param tabName
	 * @throws UIAutomationException
	 */
	public void clickSatisfactionsTabInPlanRequirement(String tabName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Satisfactions Tab In Plan Requirements",GlobalVariables.configuration.getAttrSearchList(), "Satisfactions Tab In Plan Requirements");
		UIActions.click(fileName,"Satisfactions Tab In Plan Requirements",GlobalVariables.configuration.getAttrSearchList(), "Definitions Tab In Plan Requirements");
		
		String tabNameInPage=UIActions.getText(fileName,"Satisfactions Tab In Plan Requirements",GlobalVariables.configuration.getAttrSearchList(), "Satisfactions Tab In Plan Requirements");
		if(!tabNameInPage.equals(tabName)){
			throw new UIAutomationException( "'"+tabName +"' not found");
		}
	}
	
	/**
	 * Select option from situation dropdown list
	 * @param optionName
	 * @throws UIAutomationException
	 */
	public void selectOptionFromSituationDropdownInPlanRequirement(String optionName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Situation Dropdown In Plan Requirement",GlobalVariables.configuration.getAttrSearchList(), "Situation Dropdown In Plan Requirement");
		UIActions.click(fileName,"Situation Dropdown In Plan Requirement",GlobalVariables.configuration.getAttrSearchList(), "Situation Dropdown In Plan Requirement");
		
		Select situationDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(situationDropDownList);
		UIActions.selectByText(optionName);
		
		// Assertion: verify optionname is selected from dropdown
		elementController.requireElementSmart(fileName,"Situation Dropdown In Plan Requirement",GlobalVariables.configuration.getAttrSearchList(), "Situation Dropdown In Plan Requirement");
		String optionInDropdown=UIActions.getText(fileName,"Situation Dropdown In Plan Requirement",GlobalVariables.configuration.getAttrSearchList(), "Situation Dropdown In Plan Requirement");
		if(!optionInDropdown.contains(optionName)){
			throw new UIAutomationException("Option not selected.");
		}
		
	}
	
	/**
	 * Select option from Any Event dropdown list
	 * @param optionName
	 * @throws UIAutomationException
	 */
	public void selectOptionFromAnyEventDropdownInPlanRequirement(String optionName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Any Event Dropdown In Plan Requirement",GlobalVariables.configuration.getAttrSearchList(), "Any Event Dropdown In Plan Requirement");
		UIActions.click(fileName,"Any Event Dropdown In Plan Requirement",GlobalVariables.configuration.getAttrSearchList(), "Any Event Dropdown In Plan Requirement");
		
		Select anyEventDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(anyEventDropDownList);
		UIActions.selectByText(optionName);
		
		// Assertion: verify optionname is selected from dropdown
		elementController.requireElementSmart(fileName,"Any Event Dropdown In Plan Requirement",GlobalVariables.configuration.getAttrSearchList(), "Any Event Dropdown In Plan Requirement");
		String optionInDropdown=UIActions.getText(fileName,"Any Event Dropdown In Plan Requirement",GlobalVariables.configuration.getAttrSearchList(), "Any Event Dropdown In Plan Requirement");
		if(!optionInDropdown.contains(optionName)){
			throw new UIAutomationException("Option not selected.");
		}
		
	}
	
	/**
	 * Clicks on 'CLOSE' button in plan requirement
	 * @throws UIAutomationException
	 */
	public void clickCloseInPlanRequirement() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"CLOSE EDIT In Plan Requirement",GlobalVariables.configuration.getAttrSearchList(), "CLOSE EDIT In Plan Requirement");
		UIActions.click(fileName,"CLOSE EDIT In Plan Requirement",GlobalVariables.configuration.getAttrSearchList(), "CLOSE EDIT In Plan Requirement");
	}
	/**
	 * Clicks on 'EDIT' button in plan requirement
	 * @param name
	 * @throws UIAutomationException
	 */
	public void clickEditInPlanRequirement(String name) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"CLOSE EDIT In Plan Requirement",GlobalVariables.configuration.getAttrSearchList(), "CLOSE EDIT In Plan Requirement");
		UIActions.click(fileName,"CLOSE EDIT In Plan Requirement",GlobalVariables.configuration.getAttrSearchList(), "CLOSE EDIT In Plan Requirement");
		
		// Assertion: verify requirement definition is displayed
		String nameInPage=UIActions.getText(fileName,"Requirement Definitions In Plan Requirements",GlobalVariables.configuration.getAttrSearchList(), "Requirement Definitions In Plan Requirements");
		if(!nameInPage.contains(name)){
			throw new UIAutomationException( "'"+name +"' not found");
		}
	}
	/**
	 * Clicks on 'Actions' in receives panel
	 * @param actions
	 * @throws UIAutomationException
	 */
	public void clickActionsInReceivesPanel(String actions) throws UIAutomationException{
		// Assertion: Check 'Actions' pop up is present 
		elementController.requireElementSmart(fileName,"Actions In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Receives Panel");
		UIActions.click(fileName,"Actions In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Receives Panel");
		String popUpName=UIActions.getText(fileName,"Actions In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Receives Panel");
		if(!popUpName.equals(actions)){
			throw new UIAutomationException( "'"+actions +"' not found");
		}
	}
	/**
	 * Clicks on 'Show' pop up menu and verify subMenu is present
	 * @param subMenuNameInTask
	 * @throws UIAutomationException
	 */
	public void clickShowInTaskPanel(String subMenuNameInTask) throws UIAutomationException{
		String subMenuName=null;
		elementController.requireElementSmart(fileName,"Show In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Task Panel");
		UIActions.click(fileName,"Show In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Task Panel");
		
		elementController.requireElementSmart(fileName,"Show In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Task Panel");
		String xPathForPopup=dataController.getPageDataElements(fileName, "Show In Task Panel", "Xpath");
		UIActions.click(fileName,"Show In Task Panel", GlobalVariables.configuration.getAttrSearchList(), "Show In Task Panel");
		
		Actions builder7 = new Actions(GlobalVariables.configuration.getWebDriver());
		builder7.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xPathForPopup))).build().perform();
		
		// Assertion: Verify SubMenu is present
		elementController.requireElementSmart(fileName,"Hide Details In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Segment");
		subMenuName=UIActions.getText(fileName,"Hide Details In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Segment");
		if(!subMenuName.equals(subMenuNameInTask)){
			throw new UIAutomationException( "'"+subMenuNameInTask +"' not found");
		}
	}
		
	/**
	 * Clics on 'Show' in receives panel and verify 'Show' is present
	 * @param show
	 * @throws UIAutomationException
	 */
	public void clickShowInReceivesPanel(String show) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Show In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Receives Panel");
		UIActions.click(fileName,"Show In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Receives Panel");
	
		// Assertion: Verify 'show' is present
		elementController.requireElementSmart(fileName,"Show In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Receives Panel");
		String popUpName=UIActions.getText(fileName,"Show In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Receives Panel");
		if(!show.equals(popUpName)){
			throw new UIAutomationException( "'"+show +"' not found");
		}
	}
	/**
	 * Clicks on 'Show' in sends panel and verify 'Show' is present
	 * @param show
	 * @throws UIAutomationException
	 */
	public void clickShowInSendsPanel(String show) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Show In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Sends Panel");
		UIActions.click(fileName,"Show In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Sends Panel");
				
		// Assertion: Verify 'show' is present
		elementController.requireElementSmart(fileName,"Show In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Sends Panel");
		String popUpName=UIActions.getText(fileName,"Show In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Sends Panel");
		if(!show.equals(popUpName)){
			throw new UIAutomationException( "'"+show +"' not found");
		}
	}
	/**
	 * Clicks on 'Actions in sends panel and verify 'Actions' is present
	 * @param actions
	 * @throws UIAutomationException
	 */
	public void clickActionsInSendsPanel(String actions) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Actions In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Sends Panel");
		UIActions.click(fileName,"Actions In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Sends Panel");
				
		// Assertion: Verify 'actions' is present
		elementController.requireElementSmart(fileName,"Actions In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Sends Panel");
		String popUpName=UIActions.getText(fileName,"Actions In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Sends Panel");
		if(!actions.equals(popUpName)){
			throw new UIAutomationException( "'"+actions +"' not found");
		}
	}
	/**
	 * Check checkbox of 'Usually completes after' and verify textbox and dropdown gets enabled
	 * @throws UIAutomationException
	 */
	public void clickUsuallycompletesAfter() throws UIAutomationException{
		
		boolean textboxIsEnabled=false;
		boolean dropdownIsEnabled=false;
		UIActions.scrollDown();
		
		elementController.requireElementSmart(fileName,"Usually Completes After In Task",GlobalVariables.configuration.getAttrSearchList(), "Usually Completes After In Task");
		UIActions.click(fileName,"Usually Completes After In Task",GlobalVariables.configuration.getAttrSearchList(), "Usually Completes After In Task");
				
		
		// assertion: Verify by clicking on 'Usually Completes after' checkbox textbox and dropdown gets enabled
		elementController.requireElementSmart(fileName,"Usually Completes After Textbox",GlobalVariables.configuration.getAttrSearchList(), "Usually Completes After Textbox");
		textboxIsEnabled=UIActions.checkEnable(fileName,"Usually Completes After Dropdown",GlobalVariables.configuration.getAttrSearchList(), "Usually Completes After Dropdown");
		
		elementController.requireElementSmart(fileName,"Usually Completes After Dropdown",GlobalVariables.configuration.getAttrSearchList(), "Usually Completes After Dropdown");
		textboxIsEnabled=UIActions.checkEnable(fileName,"Usually Completes After Dropdown",GlobalVariables.configuration.getAttrSearchList(), "Usually Completes After Dropdown");
		
		if(!(textboxIsEnabled==true) &&(dropdownIsEnabled==true)){
			throw new UIAutomationException("Usually completes after is not gets clicked.");
			
		}
	}
	/**
	 * Check checkbox of 'Repeats Every' and verify textbox and dropdown gets enabled
	 * @throws UIAutomationException
	 */
	
	public void clickRepeatsEvery() throws UIAutomationException{
		boolean textboxIsEnabled=false;
		boolean dropdownIsEnabled=false;
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"Repeats Every In Task",GlobalVariables.configuration.getAttrSearchList(), "Repeats Every In Task");
		UIActions.click(fileName,"Repeats Every In Task",GlobalVariables.configuration.getAttrSearchList(), "Repeats Every In Task");
				
		// assertion: Verify by clicking on 'Repeats every' checkbox textbox and dropdown gets enabled
		elementController.requireElementSmart(fileName,"Repeats Every Textbox",GlobalVariables.configuration.getAttrSearchList(), "Repeats Every Textbox");
		textboxIsEnabled=UIActions.checkEnable(fileName,"Repeats Every Textbox",GlobalVariables.configuration.getAttrSearchList(), "Repeats Every Textbox");
		
		elementController.requireElementSmart(fileName,"Repeats Every Dropdown",GlobalVariables.configuration.getAttrSearchList(), "Repeats Every Dropdown");
		textboxIsEnabled=UIActions.checkEnable(fileName,"Repeats Every Dropdown",GlobalVariables.configuration.getAttrSearchList(), "Repeats Every Dropdown");
		
		if(!(textboxIsEnabled==true) &&(dropdownIsEnabled==true)){
			throw new UIAutomationException("Repeats Every is not gets clicked.");
		}
	}
	
	/**
	 * Check checkbox of Is Ongoing in task panel
	 * @throws UIAutomationException
	 */
	public void clickIsOngoing() throws UIAutomationException{
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"Is ongoing In Task",GlobalVariables.configuration.getAttrSearchList(), "Is ongoing In Task");
		UIActions.click(fileName,"Is ongoing In Task",GlobalVariables.configuration.getAttrSearchList(), "Is ongoing In Task");
			
		clickStrenchUpForm();
		clickdoingUnnamedTask();
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		
		
		// Assertion: Verify by clicking on 'Is ongoing' ,text 'is ongoing' is displayed in header in task panel  
		elementController.requireElementSmart(fileName,"Header In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Header In Task Panel");
		String textInPage=UIActions.getText(fileName,"Header In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Header In Task Panel");
		String textInXml=dataController.getPageDataElements(fileName, "Ongoing Text", "Name");
		if(!textInPage.contains(textInXml)){
			throw new UIAutomationException("Ongoing is not displayed in flow map");
		}
		
	}
	/**
	 * Check checkbox of 'can end event' in task panel
	 * @throws UIAutomationException
	 */
	public void clickCanEndEvent() throws UIAutomationException{
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"Can End Event In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Can End Event In Task Panel");
		UIActions.click(fileName,"Can End Event In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Can End Event In Task Panel");
			
		clickStrenchUpForm();
		clickdoingUnnamedTask();
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		
		
		// Assertion: Verify by clicking on 'Can end event' text 'Can end event' is displayed in header  
		elementController.requireElementSmart(fileName,"Header In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Header In Task Panel");
		String textInPage=UIActions.getText(fileName,"Header In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Header In Task Panel");
		String textInXml=dataController.getPageDataElements(fileName, "Can End Event Text", "Name");
		if(!textInPage.contains(textInXml)){
			throw new UIAutomationException("Can end event is not displayed in flow map");
		}
		
	}
	/**
	 * Click 'tags' in sends panel
	 * @throws UIAutomationException
	 */
	public void clickTagsInSends() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Tags In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Tags In Sends Panel");
		UIActions.click(fileName,"Tags In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Tags In Sends Panel");
	}
	
	/**
	 * Click 'tags' in receives panel
	 * @throws UIAutomationException
	 */
	public void clickTagsInRceives() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Tags In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Tags In Receives Panel");
		UIActions.click(fileName,"Tags In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Tags In Receives Panel");
	}
	/**
	 * Clicks in 'Goals' link in task panel
	 * @throws UIAutomationException
	 */
	public void clickGoalsLinkInTask() throws UIAutomationException{
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"Goals Link In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Goals Link In Task Panel");
		UIActions.click(fileName,"Goals Link In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Goals Link In Task Panel");
		clickStrenchUpForm();
		
		// Assertion : Verify 'Goals' tab is present 
		String nameOfTabInPage=UIActions.getText(fileName, "Goals Tab", GlobalVariables.configuration.getAttrSearchList(),"Goals Tab");
		String nameOfTabInXML=dataController.getPageDataElements(fileName, "Goals Window Title", "Title");
		if(!nameOfTabInPage.equals(nameOfTabInXML)){
			throw new UIAutomationException("Tab with name '"+nameOfTabInXML+"' not found");
		}
	}
	/**
	 * Click on 'doing unnamed task'
	 * @throws UIAutomationException
	 */
	public void clickdoingUnnamedTask() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Doing Unnamed Task",GlobalVariables.configuration.getAttrSearchList(), "Doing Unnamed Task");
		UIActions.click(fileName,"Doing Unnamed Task",GlobalVariables.configuration.getAttrSearchList(), "Doing Unnamed Task");
	}
	
	public void clickBack() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Click Back Link",GlobalVariables.configuration.getAttrSearchList(), "Click Back Link");
		UIActions.click(fileName,"Click Back Link",GlobalVariables.configuration.getAttrSearchList(), "Click Back Link");
	}
	/**
	 * Clicks on 'Show Advanced Form' in Task panel
	 * @throws UIAutomationException
	 */
	public void clickShowAdvancedFormInTaskPanel(String showlinkTextInXML,String advancedLinkTextInXML,String flag) throws UIAutomationException{
		String linkTextInPage=null;
		elementController.requireElementSmart(fileName,"Show Advanced Simple Form In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Task Panel");
		UIActions.click(fileName,"Show Advanced Simple Form In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Task Panel");
		try{
			Thread.sleep(3000);
		}
		catch(Exception e){}
		
		if(flag.equals("True")){
			// Assertion: Verify by clicking on 'Show Advanced Form' link changes to 'Show Simple form'
			elementController.requireElementSmart(fileName,"Show Advanced Simple Form In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Task Panel");
			linkTextInPage=UIActions.getText(fileName,"Show Advanced Simple Form In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Task Panel");
			if(!linkTextInPage.equals(showlinkTextInXML)){
				throw new UIAutomationException( "'"+showlinkTextInXML +"' not found");
			}
		}
		else {
			// Assertion: Verify by clicking on 'Show Simple Form' link changes to 'Show Advanced form'
			elementController.requireElementSmart(fileName,"Show Advanced Simple Form In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Task Panel");
			UIActions.click(fileName,"Show Advanced Simple Form In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Task Panel");
			elementController.requireElementSmart(fileName,"Show Advanced Simple Form In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Task Panel");
			linkTextInPage=UIActions.getText(fileName,"Show Advanced Simple Form In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Task Panel");
			if(!linkTextInPage.equals(advancedLinkTextInXML)){
				throw new UIAutomationException( "'"+advancedLinkTextInXML +"' not found");
			}
			
		}
	}
	/**
	 * Clicks on 'Show Advanced Form' in Receives panel
	 * @throws UIAutomationException
	 */
	public void clickShowAdvancedFormInReceivesPanel(String showlinkTextInXML,String advancedLinkTextInXML,String flag) throws UIAutomationException{
		String linkTextInPage=null;
		elementController.requireElementSmart(fileName,"Show Advanced Simple Form In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Receives Panel");
		UIActions.click(fileName,"Show Advanced Simple Form In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Receives Panel");
//		try{
//			Thread.sleep(2000);
//		}
//		catch(Exception e){}
		if(flag.equals("False")){
			// Assertion: Verify by clicking on 'Show Advanced Form' link chabges to 'Show Simple form'
			elementController.requireElementSmart(fileName,"Show Advanced Simple Form In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Receives Panel");
			linkTextInPage=UIActions.getText(fileName,"Show Advanced Simple Form In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Receives Panel");
			if(!linkTextInPage.equals(showlinkTextInXML)){
				throw new UIAutomationException( "'"+showlinkTextInXML +"' not found");
			}
		}
		else {
			// Assertion: Verify by clicking on 'Show Simple Form' link chabges to 'Show Advanced form'
			elementController.requireElementSmart(fileName,"Show Advanced Simple Form In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Receives Panel");
			UIActions.click(fileName,"Show Advanced Simple Form In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Receives Panel");
			elementController.requireElementSmart(fileName,"Show Advanced Simple Form In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Receives Panel");
			linkTextInPage=UIActions.getText(fileName,"Show Advanced Simple Form In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Receives Panel");
			if(!linkTextInPage.equals(advancedLinkTextInXML)){
				throw new UIAutomationException( "'"+advancedLinkTextInXML +"' not found");
			}
			
		}
	}
	/**
	 * Clicks on 'Show Advanced Form' in Receives panel
	 * @throws UIAutomationException
	 */
	public void clickShowAdvancedFormInSendsPanel(String showlinkTextInXML,String advancedLinkTextInXML,String flag) throws UIAutomationException{
		String linkTextInPage=null;
		elementController.requireElementSmart(fileName,"Show Advanced Simple Form In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Sends Panel");
		UIActions.click(fileName,"Show Advanced Simple Form In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Sends Panel");
		try{
			Thread.sleep(2000);
		}
		catch(Exception e){}
		if(flag.equals("False")){
			// Assertion: Verify by clicking on 'Show Advanced Form' link chabges to 'Show Simple form'
			elementController.requireElementSmart(fileName,"Show Advanced Simple Form In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Sends Panel");
			linkTextInPage=UIActions.getText(fileName,"Show Advanced Simple Form In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Sends Panel");
			if(!linkTextInPage.equals(showlinkTextInXML)){
				throw new UIAutomationException( "'"+showlinkTextInXML +"' not found");
			}
		}
		else {
			// Assertion: Verify by clicking on 'Show Simple Form' link changes to 'Show Advanced form'
			elementController.requireElementSmart(fileName,"Show Advanced Simple Form In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Sends Panel");
			UIActions.click(fileName,"Show Advanced Simple Form In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Sends Panel");
			elementController.requireElementSmart(fileName,"Show Advanced Simple Form In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Sends Panel");
			linkTextInPage=UIActions.getText(fileName,"Show Advanced Simple Form In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Sends Panel");
			if(!linkTextInPage.equals(advancedLinkTextInXML)){
				throw new UIAutomationException( "'"+advancedLinkTextInXML +"' not found");
			}
			
		}
	}
	/**
	 * Clicks on 'Actions' pop up menu in 'Task' panel
	 * @throws UIAutomationException
	 */
	public void clickActionsInTask() throws UIAutomationException{
		// Assertion: Check 'Actions' pop up is present 
		elementController.requireElementSmart(fileName,"Actions In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Task Panel");
		String popUpNameInPage=UIActions.getText(fileName,"Actions In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Task Panel");
		String popUpNameInXml=dataController.getPageDataElements(fileName, "Actions Menu In Task Panel", "Name");
		if(!popUpNameInPage.equals(popUpNameInXml)){
			throw new UIAutomationException( "'"+popUpNameInXml +"' not found");
		}
	}
	
	/**
	 * Clicks on 'Add' in 'Receives' panel
	 * @throws UIAutomationException
	 */
	public void clickAddInfoReceivesPanel() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Add Info Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Add Info Receives Panel");
		UIActions.click(fileName,"Add Info Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Add Info Receives Panel");
	}
	
	/**
	 * Clicks on 'Add' in 'Sends' panel
	 * @throws UIAutomationException
	 */
	public void clickAddInfoSendsPanel() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Add Info Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Add Info Sends Panel");
		UIActions.click(fileName,"Add Info Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Add Info Sends Panel");
	}
	
	/**
	 * Clicks on 'Informaation' in 'receives' panel
	 * @throws UIAutomationException
	 */
	public void clickInformationInReceivesPanel() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Information In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Information In Receives Panel");
		UIActions.click(fileName,"Information In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Information In Receives Panel");
	}
	
	/**
	 * Clicks On Presence Tab
	 * @throws UIAutomationException
	 */
	public void clickPresenceTab() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Presence",GlobalVariables.configuration.getAttrSearchList(), "Presence");
		UIActions.click(fileName,"Presence",GlobalVariables.configuration.getAttrSearchList(), "Presence");
		String tabTextInPage=UIActions.getText(fileName,"Presence",GlobalVariables.configuration.getAttrSearchList(), "Presence");
		String tabTextInXML=dataController.getPageDataElements(fileName,"Presence Name" , "Name");
		if(!tabTextInPage.contains(tabTextInXML)){
			throw new UIAutomationException( "'"+tabTextInXML +"' not found");
		}
	}
	
	/**
	 * Clicks On 'Show all Users' link and 'Hide Inactive User' link
	 * @throws UIAutomationException
	 */
	public void clickShowAllUsers(String flag) throws UIAutomationException{
	
		elementController.requireElementSmart(fileName,"Show All Users",GlobalVariables.configuration.getAttrSearchList(), "Show All Users");
		if(flag.equals("True")){
			UIActions.click(fileName,"Show All Users",GlobalVariables.configuration.getAttrSearchList(), "Show All Users");
			
			// Check by clicking on 'show all users' it changes to 'hide inactive users'
			elementController.requireElementSmart(fileName,"Hide Inactive Users",GlobalVariables.configuration.getAttrSearchList(), "Hide Inactive Users");
			String linkTextInPage=UIActions.getText(fileName,"Hide Inactive Users",GlobalVariables.configuration.getAttrSearchList(), "Hide Inactive Users");
			String linkTextInXML=dataController.getPageDataElements(fileName,"Hide Inactive Users Name" , "Name");
			if(!linkTextInPage.contains(linkTextInXML)){
				throw new UIAutomationException( "'"+linkTextInXML +"' not found");
			}
			
			// Click on 'Hide Inactive Users'
			elementController.requireElementSmart(fileName,"Hide Inactive Users",GlobalVariables.configuration.getAttrSearchList(), "Hide Inactive Users");
			UIActions.click(fileName,"Hide Inactive Users",GlobalVariables.configuration.getAttrSearchList(), "Hide Inactive Users");
			try{
				Thread.sleep(1000);
			}
			catch(Exception e ){}
			// Check by Clicking on 'Hide Inactive User' it changes to 'show all users'
			elementController.requireElementSmart(fileName,"Show All Users",GlobalVariables.configuration.getAttrSearchList(), "Show All Users");
			String showLinkTextInPage=UIActions.getText(fileName,"Show All Users",GlobalVariables.configuration.getAttrSearchList(), "Show All Users");
			String showLinkTextInXML=dataController.getPageDataElements(fileName,"Show All Users Name" , "Name");
			if(!showLinkTextInPage.contains(showLinkTextInXML)){
				throw new UIAutomationException( "'"+showLinkTextInXML +"' not found");
			}
		}
		else {
			String linkTextInPage=UIActions.getText(fileName,"Show All Users",GlobalVariables.configuration.getAttrSearchList(), "Show All Users");
			String linkTextInXML=dataController.getPageDataElements(fileName,"Show All Users Name" , "Name");
			if(!linkTextInPage.contains(linkTextInXML)){
				throw new UIAutomationException( "'"+linkTextInXML +"' not found");
			}
		}
	}
	
	/**
	 * Clicks on 'Activities' tab
	 * @throws UIAutomationException
	 */
	public void clickActivitiesTab() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Activities",GlobalVariables.configuration.getAttrSearchList(), "Activities");
		UIActions.click(fileName,"Activities",GlobalVariables.configuration.getAttrSearchList(), "Activities");
		elementController.requireElementSmart(fileName,"Activities",GlobalVariables.configuration.getAttrSearchList(), "Activities");
		String tabTextInPage=UIActions.getText(fileName,"Activities",GlobalVariables.configuration.getAttrSearchList(), "Activities");
		String tabTextInXML=dataController.getPageDataElements(fileName,"Activities Name" , "Name");
		if(!tabTextInPage.contains(tabTextInXML)){
			throw new UIAutomationException( "'"+tabTextInXML +"' not found");
		}
	}
	
	/**
	 * Clicks on Messages Tab in Collaboration Panel
	 * @throws UIAutomationException
	 */
	public void clickMessagesTabInCollaborationPanel() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Messages Tab in Collaboration Panel",GlobalVariables.configuration.getAttrSearchList(), "Messages Tab in Collaboration Panel");
		UIActions.click(fileName,"Messages Tab in Collaboration Panel",GlobalVariables.configuration.getAttrSearchList(), "Messages Tab in Collaboration Panel");
		elementController.requireElementSmart(fileName,"Messages Tab in Collaboration Panel",GlobalVariables.configuration.getAttrSearchList(), "Messages Tab in Collaboration Panel");
		String tabTextInPage=UIActions.getText(fileName,"Messages Tab in Collaboration Panel",GlobalVariables.configuration.getAttrSearchList(), "Messages Tab in Collaboration Panel");
		String tabTextInXML=dataController.getPageDataElements(fileName,"Messages Tab in Collaboration Panel Name" , "Name");
		if(!tabTextInPage.contains(tabTextInXML)){
			throw new UIAutomationException( "'"+tabTextInXML +"' not found");
		}
	}
	
	/**
	 * Select from to  dropdownlist in Collaboration panel
	 * @param to
	 * @throws UIAutomationException
	 */
	public void clickToDropDownInCollaborationPanel(String to) throws UIAutomationException{
        
		elementController.requireElementSmart(fileName,"To Drop Down In Messages Tab in Collaboration Panel",GlobalVariables.configuration.getAttrSearchList(),"To Drop Down In Messages Tab in Collaboration Panel");
		UIActions.click(fileName,"To Drop Down In Messages Tab in Collaboration Panel",GlobalVariables.configuration.getAttrSearchList(),"To Drop Down In Messages Tab in Collaboration Panel");
		
		Select toDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(toDropDownList);
		UIActions.selectByTextAndClick(to);
		
	}
	
	/**
	 * Click Send button in Collaboration Panel
	 * @throws UIAutomationException
	 */
	public String clickSendButtonInCollaborationPanel() throws UIAutomationException{
        
		elementController.requireElementSmart(fileName,"Send Button In Collaboration Panel",GlobalVariables.configuration.getAttrSearchList(),"Send Button In Collaboration Panel");
		UIActions.click(fileName,"Send Button In Collaboration Panel",GlobalVariables.configuration.getAttrSearchList(),"Send Button In Collaboration Panel");
		try{
			Thread.sleep(5000);
		}
		catch(Exception e){}
		return UIActions.getText(fileName,"Message Sent Notification",GlobalVariables.configuration.getAttrSearchList(), "Message Sent Notification");
	
	}
	/**
	 * Enter message in Message text area in Collaboration Panel
	 * @param message
	 * @throws UIAutomationException
	 */
	public void enterMessage(String message) throws UIAutomationException{
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"Message Text Area In Collaboration Panel",GlobalVariables.configuration.getAttrSearchList(),"Message Text Area In Collaboration Panel");
		UIActions.click(fileName,"Message Text Area In Collaboration Panel",GlobalVariables.configuration.getAttrSearchList(),"Message Text Area In Collaboration Panel");
		
		UIActions.clearTextBox(fileName,"Message Text Area In Collaboration Panel",GlobalVariables.configuration.getAttrSearchList(),"Message Text Area In Collaboration Panel");
		UIActions.enterValueInTextBox(message,fileName,"Message Text Area In Collaboration Panel",GlobalVariables.configuration.getAttrSearchList(),"Message Text Area In Collaboration Panel");
		
	}
	
	/**
	 * Check if 'Receives' panel is present
	 * @throws UIAutomationException
	 */
	public void checkReceivesPanel(String textInXML) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Receives Panel");
		String textInPage=UIActions.getText(fileName,"Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Receives Panel");
		if(!textInPage.contains(textInXML)){
			throw new UIAutomationException( "'"+textInXML +"' not found");
		}
	}
	/**
	 * Check if 'Sends' panel is present
	 * @throws UIAutomationException
	 */
	public void checkSendsPanel(String textInXML) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Sends Panel");
		String textInPage=UIActions.getText(fileName,"Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Sends Panel");
		if(!textInPage.contains(textInXML)){
			throw new UIAutomationException( "'"+textInXML +"' not found");
		}
	}
	/**
	 * Clicks on 'hide my activites', 'show all activites' 
	 * @param flag
	 * @throws UIAutomationException
	 */
	public void clickHideActivities(String flag) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Hide My Activities",GlobalVariables.configuration.getAttrSearchList(), "Hide My Activities");
	
		if(flag.equals("True")){
			elementController.requireElementSmart(fileName,"Hide My Activities",GlobalVariables.configuration.getAttrSearchList(), "Hide My Activities");
			UIActions.click(fileName,"Hide My Activities",GlobalVariables.configuration.getAttrSearchList(), "Hide My Activities");
			
			// Check by clicking on 'hide my activities' it changes to 'show all activites'
			elementController.requireElementSmart(fileName,"Show All Activities",GlobalVariables.configuration.getAttrSearchList(), "Show All Activities");
			String linkTextInPage=UIActions.getText(fileName,"Show All Activities",GlobalVariables.configuration.getAttrSearchList(), "Show All Activities");
			String linkTextInXML=dataController.getPageDataElements(fileName,"Show All Activities Name" , "Name");
			if(!linkTextInPage.contains(linkTextInXML)){
				throw new UIAutomationException( "'"+linkTextInXML +"' not found");
			}
			
			// Click on 'show All Activites'
			elementController.requireElementSmart(fileName,"Show All Activities",GlobalVariables.configuration.getAttrSearchList(), "Show All Activities");
			UIActions.click(fileName,"Show All Activities",GlobalVariables.configuration.getAttrSearchList(), "Show All Activities");
			try{
				Thread.sleep(2000);
			}
			catch(Exception e ){}
			
			// Check by clicking on 'show all activites' it changes to 'hide my activites'
			elementController.requireElementSmart(fileName,"Hide My Activities",GlobalVariables.configuration.getAttrSearchList(), "Hide My Activities");
			String showLinkTextInPage=UIActions.getText(fileName,"Hide My Activities",GlobalVariables.configuration.getAttrSearchList(), "Hide My Activities");
			String showLinkTextInXML=dataController.getPageDataElements(fileName,"Hide My Activities Name" , "Name");
			if(!showLinkTextInPage.contains(showLinkTextInXML)){
				throw new UIAutomationException( "'"+showLinkTextInXML +"' not found");
			}
		}
		else {
			elementController.requireElementSmart(fileName,"Hide My Activities",GlobalVariables.configuration.getAttrSearchList(), "Hide My Activities");
			try{
				Thread.sleep(1000);
			}
			catch(Exception e ){}
			String linkTextInPage=UIActions.getText(fileName,"Hide My Activities",GlobalVariables.configuration.getAttrSearchList(), "Hide My Activities");
			String linkTextInXML=dataController.getPageDataElements(fileName,"Hide My Activities Name" , "Name");
			if(!linkTextInPage.contains(linkTextInXML)){
				throw new UIAutomationException( "'"+linkTextInXML +"' not found");
			}
		}
	}
	
	/**
	 * Clicks on 'Message' tab
	 * @throws UIAutomationException
	 */
	public void clickMessagesTab() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Messages",GlobalVariables.configuration.getAttrSearchList(), "Messages");
		UIActions.click(fileName,"Messages",GlobalVariables.configuration.getAttrSearchList(), "Messages");
		elementController.requireElementSmart(fileName,"Messages",GlobalVariables.configuration.getAttrSearchList(), "Messages");
		String tabTextInPage=UIActions.getText(fileName,"Messages",GlobalVariables.configuration.getAttrSearchList(), "Messages");
		String tabTextInXML=dataController.getPageDataElements(fileName,"Messages Name" , "Name");
		if(!tabTextInPage.contains(tabTextInXML)){
			throw new UIAutomationException( "'"+tabTextInXML +"' not found");
		}
		
		// Check if 'To:' is present in 'Messages'panel
		elementController.requireElementSmart(fileName,"To in Messages",GlobalVariables.configuration.getAttrSearchList(), "To in Messages");
		String textInPage=UIActions.getText(fileName,"To in Messages",GlobalVariables.configuration.getAttrSearchList(), "To in Messages");
		String textInXML=dataController.getPageDataElements(fileName,"To In Message Panel" , "Name");
		if(!textInPage.contains(textInXML)){
			throw new UIAutomationException( "'"+textInXML +"' not found");
		}
		
	}
	/**
	 * Clicks on 'Questionnaire' tab
	 * @throws UIAutomationException
	 */
	public void clickQuestionnaireTab() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"QUESTIONNAIRES",GlobalVariables.configuration.getAttrSearchList(), "QUESTIONNAIRES");
		UIActions.click(fileName,"QUESTIONNAIRES",GlobalVariables.configuration.getAttrSearchList(), "QUESTIONNAIRES");
		elementController.requireElementSmart(fileName,"QUESTIONNAIRES",GlobalVariables.configuration.getAttrSearchList(), "QUESTIONNAIRES");
		String tabTextInPage=UIActions.getText(fileName,"QUESTIONNAIRES",GlobalVariables.configuration.getAttrSearchList(), "QUESTIONNAIRES");
		String tabTextInXML=dataController.getPageDataElements(fileName,"Questionnaire Name" , "Name");
		if(!tabTextInPage.contains(tabTextInXML)){
			throw new UIAutomationException( "'"+tabTextInXML +"' not found");
		}
	}
	
	/**
	 * Click on New button in questionnaire
	 * @throws UIAutomationException
	 */
	public void clickAddNewQuestionnaire() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Add New Questionnaire",GlobalVariables.configuration.getAttrSearchList(), "Add New Questionnaire");
		UIActions.click(fileName,"Add New Questionnaire",GlobalVariables.configuration.getAttrSearchList(), "Add New Questionnaire");
	}
	/**
	 * Update questionnaire name
	 * @param questionnaireName
	 * @throws UIAutomationException
	 */
	public void enterQuestionnaireName(String questionnaireName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Questionnaire Name Textbox",GlobalVariables.configuration.getAttrSearchList(), "Questionnaire Name Textbox");
		UIActions.click(fileName,"Questionnaire Name Textbox",GlobalVariables.configuration.getAttrSearchList(), "Questionnaire Name Textbox");
		for(int i=0; i<10; i++)
			UIActions.enterKey(Keys.CLEAR);
//		UIActions.clearTextBox(fileName, "Questionnaire Name Textbox", GlobalVariables.configuration.getAttrSearchList(),"Questionnaire Name Textbox");
		UIActions.clearTextBox(fileName, "Questionnaire Name Textbox", GlobalVariables.configuration.getAttrSearchList(),"Questionnaire Name Textbox");
		UIActions.enterValueInTextBox(questionnaireName,fileName,"Questionnaire Name Textbox",GlobalVariables.configuration.getAttrSearchList(), "Questionnaire Name Textbox");
		UIActions.enterKey(Keys.TAB);
		
		
	}
	
	/**
	 * Activate Questionnaire
	 * @throws UIAutomationException
	 */
	public void activateQuestionnaire() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Activate Questionnaire",GlobalVariables.configuration.getAttrSearchList(), "Activate Questionnaire");
		UIActions.click(fileName,"Activate Questionnaire",GlobalVariables.configuration.getAttrSearchList(), "Activate Questionnaire");

		String headingOfWindowInXML=dataController.getPageDataElements(fileName, "Alert Window Title Of Activate Questionnaire", "Title");
		UIActions.assertAlert(headingOfWindowInXML);
		
		//verifyQuestionnaireIsActivated();
	}

	public void verifyQuestionnaireIsActivated() throws UIAutomationException{
	
		elementController.requireElementSmart(fileName,"Status Column In Questionnaires",GlobalVariables.configuration.getAttrSearchList(), "Status Column In Questionnaires");
		String textInQuestionnairesTab=UIActions.getText(fileName,"Status Column In Questionnaires",GlobalVariables.configuration.getAttrSearchList(), "Status Column In Questionnaires");
		String active=dataController.getPageDataElements(fileName,"Status Column In Questionnaires Text" , "Name");
		if(!textInQuestionnairesTab.contains(active)){
			throw new UIAutomationException( "'"+active +"' not found");
		}
	}

	/**
	 * Delete Questionnaire
	 * @throws UIAutomationException
	 */
	public void deleteQuestionnaire() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Delete Querstionnaire",GlobalVariables.configuration.getAttrSearchList(), "Delete Querstionnaire");
		UIActions.click(fileName,"Delete Querstionnaire",GlobalVariables.configuration.getAttrSearchList(), "Delete Querstionnaire");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String headingOfWindowInXML=dataController.getPageDataElements(fileName, "Alert Window Title Of Delete Questionnaire", "Title");
		UIActions.assertAlert(headingOfWindowInXML);
	}
	/**
	 * Verify questionnaire is added
	 * @param questionnaireName
	 * @throws UIAutomationException
	 */
	public void verifyQuestionnaireIsAdded(String questionnaireName) throws UIAutomationException{
		boolean present=false;
		elementController.requireElementSmart(fileName,"Questionnaire Table",GlobalVariables.configuration.getAttrSearchList(), "Questionnaire Table");
		
		// Assertion: Verify questionnaire is present
			List<WebElement> tds = GlobalVariables.configuration.getWebElement().findElements(By.tagName("tr"));
			for (WebElement li: tds){
				if (li.getText().contains(questionnaireName)){
					present=true;
					break;
				}
			}
			if(present==false){
				throw new UIAutomationException("Questionnaire '"+questionnaireName+"' is not present.");
			}
			
	}
	/**
	 * Clicks on 'hide broadcasts' and 'show all messages'
	 * @param flag
	 * @throws UIAutomationException
	 */
	public void clickHideBroadcasts(String flag) throws UIAutomationException{
		
		elementController.requireElementSmart(fileName,"Hide Broadcasts",GlobalVariables.configuration.getAttrSearchList(), "Hide Broadcasts");
		if(flag.equals("True")){
			UIActions.click(fileName,"Hide Broadcasts",GlobalVariables.configuration.getAttrSearchList(), "Hide Broadcasts");
			
			// Check by clicking on 'hide broadcasts' it changes to 'show all messages'
			elementController.requireElementSmart(fileName,"Show All Messages",GlobalVariables.configuration.getAttrSearchList(), "Show All Messages");
			String linkTextInPage=UIActions.getText(fileName,"Show All Messages",GlobalVariables.configuration.getAttrSearchList(), "Show All Messages");
			String linkTextInXML=dataController.getPageDataElements(fileName,"Show All Messages Name" , "Name");
			if(!linkTextInPage.contains(linkTextInXML)){
				throw new UIAutomationException( "'"+linkTextInXML +"' not found");
			}
			
			
			// Click on 'show All messages'
			elementController.requireElementSmart(fileName,"Show All Messages",GlobalVariables.configuration.getAttrSearchList(), "Show All Messages");
			UIActions.click(fileName,"Show All Messages",GlobalVariables.configuration.getAttrSearchList(), "Show All Messages");
			try{
				Thread.sleep(1000);
			}
			catch(Exception e ){}
			
			// Check by clicking on 'show all messages' it changes to 'hide broadcasts'
			elementController.requireElementSmart(fileName,"Hide Broadcasts",GlobalVariables.configuration.getAttrSearchList(), "Hide Broadcasts");
			String showLinkTextInPage=UIActions.getText(fileName,"Hide Broadcasts",GlobalVariables.configuration.getAttrSearchList(), "Hide Broadcasts");
			String showLinkTextInXML=dataController.getPageDataElements(fileName,"Hide Broadcasts Name" , "Name");
			if(!showLinkTextInPage.contains(showLinkTextInXML)){
				throw new UIAutomationException( "'"+showLinkTextInXML +"' not found");
			}
		}
		else {
			String linkTextInPage=UIActions.getText(fileName,"Hide Broadcasts",GlobalVariables.configuration.getAttrSearchList(), "Hide Broadcasts");
			String linkTextInXML=dataController.getPageDataElements(fileName,"Hide Broadcasts Name" , "Name");
			if(!linkTextInPage.contains(linkTextInXML)){
				throw new UIAutomationException( "'"+linkTextInXML +"' not found");
			}
		}
	}
	/**
	 * Clicks on 'show sent' and 'show received'
	 * @param flag
	 * @throws UIAutomationException
	 */
	public void clickShowSent(String flag) throws UIAutomationException{		
		elementController.requireElementSmart(fileName,"Show Sent",GlobalVariables.configuration.getAttrSearchList(), "Show Sent");
		if(flag.equals("True")){
			UIActions.click(fileName,"Show Sent",GlobalVariables.configuration.getAttrSearchList(), "Show Sent");
			
			// Check by clicking on 'show sent' it changes to 'show received'
			elementController.requireElementSmart(fileName,"Show Received",GlobalVariables.configuration.getAttrSearchList(), "Show Received");
			String linkTextInPage=UIActions.getText(fileName,"Show Received",GlobalVariables.configuration.getAttrSearchList(), "Show Received");
			String linkTextInXML=dataController.getPageDataElements(fileName,"Show Received Name" , "Name");
			if(!linkTextInPage.contains(linkTextInXML)){
				throw new UIAutomationException( "'"+linkTextInXML +"' not found");
			}
				
			// Click on 'show received'
			elementController.requireElementSmart(fileName,"Show Received",GlobalVariables.configuration.getAttrSearchList(), "Show Received");
			UIActions.click(fileName,"Show Received",GlobalVariables.configuration.getAttrSearchList(), "Show Received");
			try{
				Thread.sleep(1000);
			}
			catch(Exception e ){}
			
			// Check by clicking on 'show received' it changes to 'show sent'
			elementController.requireElementSmart(fileName,"Show Sent",GlobalVariables.configuration.getAttrSearchList(), "Show Sent");
			String showLinkTextInPage=UIActions.getText(fileName,"Show Sent",GlobalVariables.configuration.getAttrSearchList(), "Show Sent");
			String showLinkTextInXML=dataController.getPageDataElements(fileName,"Show Sent Name" , "Name");
			if(!showLinkTextInPage.contains(showLinkTextInXML)){
				throw new UIAutomationException( "'"+showLinkTextInXML +"' not found");
			}
		}
		else {
			String linkTextInPage=UIActions.getText(fileName,"Show Sent",GlobalVariables.configuration.getAttrSearchList(), "Show Sent");
			String linkTextInXML=dataController.getPageDataElements(fileName,"Show Sent Name" , "Name");
			if(!linkTextInPage.contains(linkTextInXML)){
				throw new UIAutomationException( "'"+linkTextInXML +"' not found");
			}
		}
	}

	
	
	/**
	 * Adds goal to Segment
	 * @param slectGoal 
	 * @param type
	 * @param organization
	 * @throws UIAutomationException
	 */
	public void addGoal(String category,String type,String organization) throws UIAutomationException
	{
		// Select goal from dropdown
		elementController.requireElementSmart(fileName,"Category in goal",GlobalVariables.configuration.getAttrSearchList(),"Category in goal");
		UIActions.click(fileName,"Category in goal",GlobalVariables.configuration.getAttrSearchList(),"Category in goal");	
		UIActions.enterKey(Keys.TAB);
		Select fromDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(fromDropDownList);
		UIActions.selectByText(category);
		UIActions.enterKey(Keys.TAB);
		
		// Select Organization (Type/Actual)
		elementController.requireElementSmart(fileName,"Type Or Actual",GlobalVariables.configuration.getAttrSearchList(),"Type Or Actual");
		UIActions.click(fileName,"Type Or Actual",GlobalVariables.configuration.getAttrSearchList(),"Type Or Actual");
		UIActions.enterKey(Keys.TAB);
		Select organizationDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(organizationDropDownList);
		UIActions.selectByText(type);
		UIActions.enterKey(Keys.TAB);
		
		elementController.requireElementSmart(fileName, "Goal Name", GlobalVariables.configuration.getAttrSearchList(), "Goal Name");
		UIActions.enterValueInTextBox(organization,fileName, "Goal Name", GlobalVariables.configuration.getAttrSearchList(), "Goal Name");
		UIActions.enterKey(Keys.TAB);
		
		elementController.requireElementSmart(fileName,"Acheived at end",GlobalVariables.configuration.getAttrSearchList(),"Acheived at end");
		UIActions.click(fileName,"Acheived at end",GlobalVariables.configuration.getAttrSearchList(),"Acheived at end");
		
	}
	
	
	
	/**
	 * Deletes goal from Segment
	 * @throws UIAutomationException
	 */
	public void deleteGoal() throws UIAutomationException{
		
		elementController.requireElementSmart(fileName,"Delete goal",GlobalVariables.configuration.getAttrSearchList(), "Delete goal");
		UIActions.click(fileName,"Delete goal",GlobalVariables.configuration.getAttrSearchList(), "Delete goal");
	
		String headingOfWindowInXML=dataController.getPageDataElements(fileName, "Alert Window Title Of Goal", "Title");
		try{
			Thread.sleep(1000);
			}
			catch(Exception e){}
		UIActions.assertAlert(headingOfWindowInXML);
	}
	
	/**
	 * Select goal from dropdown list in task panel
	 * @param selectGoal
	 * @throws UIAutomationException
	 */
	public void selectgoalfromDropDownInTask(String selectGoal) throws UIAutomationException{
		
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"Goals Dropdown In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Goals Dropdown In Task Panel");
		UIActions.click(fileName,"Goals Dropdown In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Goals Dropdown In Task Panel");
			
		Select goalsDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(goalsDropDownList);
		UIActions.selectByText(selectGoal);
		try{
			Thread.sleep(5000);
		}
		catch(Exception e){}		
		// Assertion: Verify Goal is selected 
		elementController.requireElementSmart(fileName,"Verify Goal Added In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Verify Goal Added In Task Panel");
		String goalNameInPage=UIActions.getText(fileName,"Verify Goal Added In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Verify Goal Added In Task Panel");
		if(!goalNameInPage.contains(selectGoal)){
			throw new UIAutomationException("Goal is not selected from dropdown list.");
		}
	}
	
	/**
	 * Uncheck checkbox of goal in task panel
	 * @param selectGoal
	 * @throws UIAutomationException
	 */
	public void uncheckGoalInTask(String selectGoal,String chooseOne) throws UIAutomationException{
		try{
			Thread.sleep(5000);
		}
		catch(Exception e){}
		elementController.requireElementSmart(fileName,"Uncheck Checkbox Of Goal In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Uncheck Checkbox Of Goal In Task Panel");
		UIActions.click(fileName,"Uncheck Checkbox Of Goal In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Uncheck Checkbox Of Goal In Task Panel");
							
		// Assertion: Verify Goal is deselected 
		elementController.requireElementSmart(fileName,"Goals Dropdown In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Goals Dropdown In Task Panel");
		UIActions.click(fileName,"Goals Dropdown In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Goals Dropdown In Task Panel");
		String goalsIndropdown=UIActions.getText(fileName,"Goals Dropdown In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Goals Dropdown In Task Panel");
		
		if(!goalsIndropdown.contains(chooseOne)){
			throw new UIAutomationException("Goal can not be unchecked.");
		}
	
	}
	
	/**
	 * Verify Attachment link is present in task panel
	 * @throws UIAutomationException 
	 */
	public void clickAttachmentLinkInTask() throws UIAutomationException{
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"Task Attachment Link",GlobalVariables.configuration.getAttrSearchList(), "Task Attachment Link");
		UIActions.click(fileName,"Task Attachment Link",GlobalVariables.configuration.getAttrSearchList(), "Task Attachment Link");
	
//		elementController.requireElementSmart(fileName,"Verify Goal Added In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Verify Goal Added In Task Panel");
//		String goalNameInPage=UIActions.getText(fileName,"Verify Goal Added In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Verify Goal Added In Task Panel");
//		if(!goalNameInPage.contains("")){
//			throw new UIAutomationException("Goal is not selected from dropdown list.");
//		}
	}
	
	/**
	 * Verify attach dropdown in task panel contains reference, policy, mandating policy, prohibiting policy
	 * @param reference
	 * @param policy
	 * @param mandatingPolicy
	 * @param prohibitingPolicy
	 * @throws UIAutomationException
	 */
	public void verifyAttachDropdownInTask(String reference,String policy,String mandatingPolicy,String prohibitingPolicy) throws UIAutomationException{
	
		
		elementController.requireElementSmart(fileName,"Attach Dropdown In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In Task Panel");
		UIActions.click(fileName,"Attach Dropdown In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In Task Panel");
							
		// Assertion: Verify reference,policy,mandatin policy,prohibiting policy is present
		elementController.requireElementSmart(fileName,"Attach Dropdown In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In Task Panel");
		String attachDropdown=UIActions.getText(fileName,"Attach Dropdown In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Attach Dropdown In Task Panel");
		
		if(!(attachDropdown.contains(reference)) &&(attachDropdown.contains(policy) && (attachDropdown.contains(mandatingPolicy))&&(attachDropdown.contains(prohibitingPolicy)))){
			throw new UIAutomationException("Attach dropdown is not present");
		}
		elementController.requireElementSmart(fileName,"Attach Dropdown In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In Task Panel");
		UIActions.click(fileName,"Attach Dropdown In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In Task Panel");
		
	}
	
	/*public void attachFiles() throws UIAutomationException
	{
		// assuming driver is a healthy WebDriver instance
					WebElement fileInput = driver.findElement(By.name("uploadfile"));
					fileInput.sendKeys("C:/path/to/file.jpg");
	}*/
	
	
	/**
	 * Verify segment is added
	 * @param segmentName
	 * @throws UIAutomationException
	 */
	public void verifySegmentInDropdown(String segmentName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Segment Dropdown On Plan Page",GlobalVariables.configuration.getAttrSearchList(),"Segment Dropdown On Plan Page");
		
		// Assertion: Verify segment is added
		elementController.requireElementSmart(fileName,"Segment Dropdown On Plan Page",GlobalVariables.configuration.getAttrSearchList(),"Segment Dropdown On Plan Page");
		String segmentDropdown=UIActions.getText(fileName,"Segment Dropdown On Plan Page",GlobalVariables.configuration.getAttrSearchList(), "Segment Dropdown On Plan Page");
		
		if(!(segmentDropdown.contains(segmentName))){
			throw new UIAutomationException("Segment '"+segmentName+"' not added.");
		}
	}
	/**
	 * Verify changes made to the segment are redone 
	 * @param segmentName
	 * @throws UIAutomationException
	 */
	public void verifyChangesInSegmentRedone(String segmentName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Segment Dropdown On Plan Page",GlobalVariables.configuration.getAttrSearchList(),"Segment Dropdown On Plan Page");
		
		// Assertion: Verify changes done to segment is redone
		elementController.requireElementSmart(fileName,"Segment Dropdown On Plan Page",GlobalVariables.configuration.getAttrSearchList(),"Segment Dropdown On Plan Page");
		String segmentDropdown=UIActions.getText(fileName,"Segment Dropdown On Plan Page",GlobalVariables.configuration.getAttrSearchList(), "Segment Dropdown On Plan Page");
		
		if(!(segmentDropdown.contains(segmentName))){
			throw new UIAutomationException("Segment '"+segmentName+"' not added.");
		}
	}
	
	/**
	 * Click Phase tab under Events in scope
	 * @throws UIAutomationException
	 */
	public void clickPhaseTab()throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Event Phases",GlobalVariables.configuration.getAttrSearchList(), "Event Phases");
		UIActions.click(fileName,"Event Phases",GlobalVariables.configuration.getAttrSearchList(), "Event Phases");
		// Assertion: Verify if the Events Phase Tab is clicked
		elementController.requireElementSmart(fileName,"All event phases in the collaboration template",GlobalVariables.configuration.getAttrSearchList(), "All event phases in the collaboration template");
//		String eventPhaseTab=UIActions.getText(fileName,"All event phases in the collaboration template",GlobalVariables.configuration.getAttrSearchList(), "All event phases in the collaboration template");
//		System.out.println(eventPhaseTab);
//		if(!eventPhaseTab.contains(eventPhaseTab)){
//			throw new UIAutomationException("Events Phases Tab not selected");
//		}
	}
	/**
	 * Verify goal is removed
	 * @param goalName
	 * @throws UIAutomationException
	 */
	public void verifyGoalRemoved(String goalName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Frame in goal",GlobalVariables.configuration.getAttrSearchList(),"Frame in goal");
		boolean categryEnabled=UIActions.checkEnable(fileName,"Frame in goal",GlobalVariables.configuration.getAttrSearchList(),"Frame in goal");

		if(!categryEnabled){
			throw new UIAutomationException("Goal '"+goalName+"' is not removed.");
		}
	}
	/**
	 * Verify event is added in about plan segment
	 * @param eventName
	 * @throws UIAutomationException
	 */
	public void verifyEventAddedInAboutPlanSegment(String eventName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Event Table In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Event Table In About Plan Segment");
		
		// Assertion: Verify event is added
		elementController.requireElementSmart(fileName,"Event Table In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Event Table In About Plan Segment");
		String allEventNames=UIActions.getText(fileName,"Event Table In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(), "Event Table In About Plan Segment");
		
		if(!(allEventNames.contains(eventName))){
			throw new UIAutomationException("Event '"+eventName+"' not added.");
		}
	}
	/**
	 * Delete event from segment
	 * @param eventName
	 * @throws UIAutomationException
	 */
	public void deleteEvent(String eventName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Delete Event In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(), "Delete Event In About Plan Segment");
		UIActions.click(fileName,"Delete Event In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(), "Delete Event In About Plan Segment");
		String headingOfWindowInXML=dataController.getPageDataElements(fileName, "Alert Window Title Of Delete Event", "Title");
		UIActions.assertAlert(headingOfWindowInXML);
	}
	/**
	 * Gets list of events
	 * @param eventName
	 * @throws UIAutomationException
	 */
	public void getEvent(String eventName) throws UIAutomationException{
		// get list of events
		int countEvent = 1 ;
		String firstXPath=dataController.getPageDataElements(fileName, "First Xpath For Delete Event", "Xpath");
		String secondXPath=dataController.getPageDataElements(fileName, "Second Xpath For Delete Event", "Xpath");
		elementController.requireElementSmart(fileName,"Event Table In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(), "Event Table In About Plan Segment");
	
		List<WebElement> trs = GlobalVariables.configuration.getWebElement().findElements(By.tagName("tr"));
		List<WebElement> tds;
		for(WebElement tr: trs){
			tds = tr.findElements(By.tagName("td"));
			for(WebElement td: tds){				
				if(td.getText().equals(eventName)){
					GlobalVariables.configuration.getWebDriver().findElement(By.xpath(firstXPath+ (countEvent) + secondXPath)).click();
				}
			}
			countEvent++;
		}
	}
	public void verifySegmentIsDeleted(String segmentName) throws UIAutomationException{
//		elementController.requireElementSmart(fileName,"Segment Dropdown On Plan Page",GlobalVariables.configuration.getAttrSearchList(),"Segment Dropdown On Plan Page");
//	    String xpath=dataController.getPageDataElements(fileName, "Segment Dropdown On Plan Page", "Xpath");
//	
//	    Actions builder1 = new Actions(GlobalVariables.configuration.getWebDriver());
//		builder1.moveToElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xpath))).build().perform();
//	    
//		// Assertion: verify segment is removed
//		int count=elementController.getXPathCount(xpath);
//		String firstXPath=dataController.getPageDataElements(fileName, "Actual Segment Dropdown On Plan Page", "Xpath");
//		String secondXPath=dataController.getPageDataElements(fileName, "Actual Segment Dropdown On Plan Page Second Xpath", "Xpath");
//		int countUsers=0;
//		elementController.requireElementSmart(fileName,"Actual Segment Dropdown On Plan Page",GlobalVariables.configuration.getAttrSearchList(),"Actual Segment Dropdown On Plan Page");
//		List<WebElement> trs = GlobalVariables.configuration.getWebElement().findElements(By.tagName("li"));
//		List<WebElement> tds;
//		for(WebElement tr: trs){
//			tds = tr.findElements(By.tagName("ul"));
//			for(WebElement td: tds){				
//				if(td.getText().contains(segmentName)){
//					GlobalVariables.configuration.getWebDriver().findElement(By.xpath(firstXPath + secondXPath +"["+countUsers+"]")).click();
//				}
//			}
//			countUsers++;
//		}
		
//		// Assertion: Verify reference,policy,mandatin policy,prohibiting policy is present
//		elementController.requireElementSmart(fileName,"Segment Dropdown On Plan Page",GlobalVariables.configuration.getAttrSearchList(),"Segment Dropdown On Plan Page");
//		String segmentDropdown=UIActions.getText(fileName,"Segment Dropdown On Plan Page",GlobalVariables.configuration.getAttrSearchList(), "Segment Dropdown On Plan Page");
//		
//		if(!(segmentDropdown.contains(segmentName))){
//			throw new UIAutomationException("Segment '"+segmentName+"' not added.");
//		}
	}
	/**
	 * Verify attach dropdown in sends panel
	 * @param reference
	 * @param policy
	 * @param picture
	 * @param mandatingPolicy
	 * @param prohibitingPolicy
	 * @throws UIAutomationException
	 */
	public void verifyAttachDropdownInSendsPanel(String reference,String policy,String picture,String mandatingPolicy,String prohibitingPolicy) throws UIAutomationException{
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"Attach Dropdown In Send Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In Send Panel");
		UIActions.click(fileName,"Attach Dropdown In Send Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In Send Panel");
							
		// Assertion: Verify reference,policy,mandatin policy,prohibiting policy is present
		elementController.requireElementSmart(fileName,"Attach Dropdown In Send Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In Send Panel");
		String attachDropdown=UIActions.getText(fileName,"Attach Dropdown In Send Panel",GlobalVariables.configuration.getAttrSearchList(), "Attach Dropdown In Send Panel");
		
		if(!(attachDropdown.contains(reference)) &&(attachDropdown.contains(policy) && (attachDropdown.contains(picture))&&(attachDropdown.contains(mandatingPolicy))&&(attachDropdown.contains(prohibitingPolicy)))){
			throw new UIAutomationException("Attach dropdown is not present");
		}
		
		elementController.requireElementSmart(fileName,"Attach Dropdown In Send Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In Send Panel");
		UIActions.click(fileName,"Attach Dropdown In Send Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In Send Panel");
	}
	/**
	 * Verify attach dropdown in receives panel
	 * @param reference
	 * @param policy
	 * @param picture
	 * @param mandatingPolicy
	 * @param prohibitingPolicy
	 * @throws UIAutomationException
	 */
	public void verifyAttachDropdownInReceivesPanel(String reference,String policy,String picture,String mandatingPolicy,String prohibitingPolicy) throws UIAutomationException{
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"Attach Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In Receives Panel");
		UIActions.click(fileName,"Attach Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In Receives Panel");
							
		// Assertion: Verify reference,policy,mandatin policy,prohibiting policy is present
		elementController.requireElementSmart(fileName,"Attach Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In Receives Panel");
		String attachDropdown=UIActions.getText(fileName,"Attach Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Attach Dropdown In Receives Panel");
		
		if(!(attachDropdown.contains(reference)) &&(attachDropdown.contains(policy) && (attachDropdown.contains(picture))&& (attachDropdown.contains(mandatingPolicy))&&(attachDropdown.contains(prohibitingPolicy)))){
			throw new UIAutomationException("Attach dropdown is not present");
		}
		elementController.requireElementSmart(fileName,"Attach Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In Receives Panel");
		UIActions.click(fileName,"Attach Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In Receives Panel");
		
	}

	/**
	 * Verify type dropdown of issue in task panel is present
	 * @param validity
	 * @param completeness
	 * @param robustness
	 * @throws UIAutomationException
	 */
	public void verifyTypeDropdownInIssueInTask(String validity,String completeness,String robustness) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Type Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Type Dropdown In New Issue Text In Task Panel");
		UIActions.click(fileName,"Type Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Type Dropdown In New Issue Text In Task Panel");
							
		// Assertion: Verify validity,completeness,robustness is present
		elementController.requireElementSmart(fileName,"Type Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Type Dropdown In New Issue Text In Task Panel");
		String typeDropdown=UIActions.getText(fileName,"Type Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Type Dropdown In New Issue Text In Task Panel");
		
		if(!(typeDropdown.contains(validity)) &&(typeDropdown.contains(completeness) && (typeDropdown.contains(robustness)))){
			throw new UIAutomationException("Type dropdown in issue is not present");
		}	
		elementController.requireElementSmart(fileName,"Type Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Type Dropdown In New Issue Text In Task Panel");
		UIActions.click(fileName,"Type Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Type Dropdown In New Issue Text In Task Panel");
	}
	/**
	 * Verify severity dropdown in 'Issues' in 'Task' panel
	 * @param minor
	 * @param major
	 * @param severe
	 * @param extreme
	 * @throws UIAutomationException
	 */
	public void verifySeverityDropdownInIssueInTask(String minor,String major,String severe,String extreme) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Severity Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue Text In Task Panel");
		UIActions.click(fileName,"Severity Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue Text In Task Panel");
							
		// Assertion: Verify validity,completeness,robustness is present
		elementController.requireElementSmart(fileName,"Severity Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue Text In Task Panel");
		String severityDropdown=UIActions.getText(fileName,"Severity Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Severity Dropdown In New Issue Text In Task Panel");
		
		if(!(severityDropdown.contains(minor)) &&(severityDropdown.contains(major) && (severityDropdown.contains(severe))&& (severityDropdown.contains(extreme)))){
			throw new UIAutomationException("Severity dropdown in issue is not present");
		}
		elementController.requireElementSmart(fileName,"Severity Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue Text In Task Panel");
		UIActions.click(fileName,"Severity Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue Text In Task Panel");
	}
	/**
	 * Verify severity dropdown in 'Issues' in 'Sends' panel 
	 * @param minor
	 * @param major
	 * @param severe
	 * @param extreme
	 * @throws UIAutomationException
	 */
	public void verifySeverityDropdownInIssueInSends(String minor,String major,String severe,String extreme) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Severity Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue In Sends Panel");
		UIActions.click(fileName,"Severity Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue In Sends Panel");
							
		// Assertion: Verify minor,major,severe,extreme are present in 'Severity' dropdown
		elementController.requireElementSmart(fileName,"Severity Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue In Sends Panel");
		String severityDropdown=UIActions.getText(fileName,"Severity Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Severity Dropdown In New Issue In Sends Panel");
		
		if(!(severityDropdown.contains(minor)) &&(severityDropdown.contains(major) && (severityDropdown.contains(severe))&& (severityDropdown.contains(extreme)))){
			throw new UIAutomationException("Severity dropdown in issue is not present");
		}
		elementController.requireElementSmart(fileName,"Severity Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue In Sends Panel");
		UIActions.click(fileName,"Severity Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue In Sends Panel");
	}
	/**
	 * Verify severity dropdown in 'Issues' in 'receives' panel
	 * @param minor
	 * @param major
	 * @param severe
	 * @param extreme
	 * @throws UIAutomationException
	 */
	public void verifySeverityDropdownInIssueInReceives(String minor,String major,String severe,String extreme) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Severity Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue In Receives Panel");
		UIActions.click(fileName,"Severity Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue In Receives Panel");
							
		// Assertion: Verify validity,completeness,robustness is present
		elementController.requireElementSmart(fileName,"Severity Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue In Receives Panel");
		String severityDropdown=UIActions.getText(fileName,"Severity Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Severity Dropdown In New Issue In Receives Panel");
		
		if(!(severityDropdown.contains(minor)) &&(severityDropdown.contains(major) && (severityDropdown.contains(severe))&& (severityDropdown.contains(extreme)))){
			throw new UIAutomationException("Severity dropdown in issue is not present");
		}
		elementController.requireElementSmart(fileName,"Severity Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue In Receives Panel");
		UIActions.click(fileName,"Severity Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue In Receives Panel");
		
	}
	
	/**
	 * Verify attach dropdown in issue in task panel contains reference, policy
	 * @param reference
	 * @param policy
	 * @throws UIAutomationException
	 */
	public void verifyAttachDropdownInIssueInTask(String reference,String policy) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Attach Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In New Issue Text In Task Panel");
		UIActions.click(fileName,"Attach Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In New Issue Text In Task Panel");
							
		// Assertion: Verify reference, policy are present
		elementController.requireElementSmart(fileName,"Attach Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In New Issue Text In Task Panel");
		String severityDropdown=UIActions.getText(fileName,"Attach Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Attach Dropdown In New Issue Text In Task Panel");
		
		if(!(severityDropdown.contains(reference)) &&(severityDropdown.contains(policy))){
			throw new UIAutomationException("Attach dropdown in issue is not present");
		}
		elementController.requireElementSmart(fileName,"Attach Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In New Issue Text In Task Panel");
		UIActions.click(fileName,"Attach Dropdown In New Issue Text In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In New Issue Text In Task Panel");
	}
	/**
	 * Verify attach dropdown in issues in sends panel
	 * @param reference
	 * @param policy
	 * @throws UIAutomationException
	 */
	public void verifyAttachDropdownInIssueInSends(String reference,String policy) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Attach Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In New Issue In Sends Panel");
		UIActions.click(fileName,"Attach Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In New Issue In Sends Panel");
							
		// Assertion: Verify reference, policy are present
		elementController.requireElementSmart(fileName,"Attach Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In New Issue In Sends Panel");
		String severityDropdown=UIActions.getText(fileName,"Attach Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Attach Dropdown In New Issue In Sends Panel");
		
		if(!(severityDropdown.contains(reference)) &&(severityDropdown.contains(policy))){
			throw new UIAutomationException("Attach dropdown in issue is not present");
		}
		elementController.requireElementSmart(fileName,"Attach Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In New Issue In Sends Panel");
		UIActions.click(fileName,"Attach Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In New Issue In Sends Panel");
		
	}
	
	/**
	 * Click Attachment Panel In the Sends panel
	 * @throws UIAutomationException
	 */
	public void clickAttachmentPanelInSends() throws UIAutomationException{
		
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"Attachment Panel In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Attachment Panel In Sends Panel");
		UIActions.click(fileName,"Attachment Panel In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Attachment Panel In Sends Panel");
	
	}
	
	
	/**
	 * Click Attachment Panel In New Issues in the Sends panel
	 * @throws UIAutomationException
	 */
	public void clickAttachmentPanelInNewIssuesSends() throws UIAutomationException{
		
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"Attachment Panel In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Attachment Panel In New Issue In Sends Panel");
		UIActions.click(fileName,"Attachment Panel In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Attachment Panel In New Issue In Sends Panel");
	
	}
		/**
	 * Verify attach dropdown in issues in receives panel
	 * @param reference
	 * @param policy
	 * @throws UIAutomationException
	 */
	public void verifyAttachDropdownInIssueInReceives(String reference,String policy) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Attach Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In New Issue In Receives Panel");
		UIActions.click(fileName,"Attach Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In New Issue In Receives Panel");
							
		// Assertion: Verify reference, policy are present
		elementController.requireElementSmart(fileName,"Attach Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In New Issue In Receives Panel");
		String severityDropdown=UIActions.getText(fileName,"Attach Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Attach Dropdown In New Issue In Receives Panel");
		
		if(!(severityDropdown.contains(reference)) &&(severityDropdown.contains(policy))){
			throw new UIAutomationException("Attach dropdown in issue is not present");
		}
		elementController.requireElementSmart(fileName,"Attach Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In New Issue In Receives Panel");
		UIActions.click(fileName,"Attach Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Attach Dropdown In New Issue In Receives Panel");
	}
	
	/**
	 * Click Attachment Panel In the Sends panel
	 * @throws UIAutomationException
	 */
	public void clickAttachmentPanelInReceives() throws UIAutomationException{
		
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"Attachment Panel In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Attachment Panel In Receives Panel");
		UIActions.click(fileName,"Attachment Panel In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Attachment Panel In Receives Panel");
	
	}
	
	/**
	 * Click hide button to hide the Collaboration Panel
	 * @param reference
	 * @param policy
	 * @throws UIAutomationException
	 */
	public void clickHideCollaborationPanelButton() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Hide Collaboration Panel Button",GlobalVariables.configuration.getAttrSearchList(),"Hide Collaboration Panel Button");
		UIActions.click(fileName,"Hide Collaboration Panel Button",GlobalVariables.configuration.getAttrSearchList(),"Hide Collaboration Panel Button");
		try{
			Thread.sleep(5000);
		}
		catch(Exception e){}
		//UIActions.getText(fileName, "HideCollaborationPanelNotification", GlobalVariables.configuration.getAttrSearchList(), "Hide Collaboration Panel Notification");
	}
	
	/**
	 * Verify 'Type' dropdown in issues in sends panel
	 * @param validity
	 * @param completeness
	 * @param robustness
	 * @throws UIAutomationException
	 */
	public void verifyTypeDropdownInIssueInSends(String validity,String completeness,String robustness) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Type Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Type Dropdown In New Issue In Sends Panel");
		UIActions.click(fileName,"Type Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Type Dropdown In New Issue In Sends Panel");
							
		// Assertion: Verify validity,completeness,robustness is present
		elementController.requireElementSmart(fileName,"Type Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Type Dropdown In New Issue Text In Task Panel");
		String typeDropdown=UIActions.getText(fileName,"Type Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Type Dropdown In New Issue In Sends Panel");
		
		if(!(typeDropdown.contains(validity)) &&(typeDropdown.contains(completeness) && (typeDropdown.contains(robustness)))){
			throw new UIAutomationException("Type dropdown in issue is not present");
		}	
		
		elementController.requireElementSmart(fileName,"Type Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Type Dropdown In New Issue In Sends Panel");
		UIActions.click(fileName,"Type Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Type Dropdown In New Issue In Sends Panel");
		
	}
	/**
	 * Verify 'Type' dropdown in issues in receives panel 
	 * @param validity
	 * @param completeness
	 * @param robustness
	 * @throws UIAutomationException
	 */
	public void verifyTypeDropdownInIssueInReceives(String validity,String completeness,String robustness) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Type Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Type Dropdown In New Issue In Receives Panel");
		UIActions.click(fileName,"Type Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Type Dropdown In New Issue In Receives Panel");
							
		// Assertion: Verify validity,completeness,robustness is present
		elementController.requireElementSmart(fileName,"Type Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Type Dropdown In New Issue In Receives Panel");
		String typeDropdown=UIActions.getText(fileName,"Type Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Type Dropdown In New Issue In Receives Panel");
		
		if(!(typeDropdown.contains(validity)) &&(typeDropdown.contains(completeness) && (typeDropdown.contains(robustness)))){
			throw new UIAutomationException("Type dropdown in issue is not present");
		}	
		elementController.requireElementSmart(fileName,"Type Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Type Dropdown In New Issue In Receives Panel");
		UIActions.click(fileName,"Type Dropdown In New Issue In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Type Dropdown In New Issue In Receives Panel");
		
	}
	/**
	 * Verify Intent dropdown in sends panel contains unspecified, alarm, announcement, command, feedback, report
	 * @param unspecified
	 * @param alarm
	 * @param announcement
	 * @param command
	 * @param feedback
	 * @param report
	 * @throws UIAutomationException
	 */
	public void verifyIntentDropdownInSendPanel(String unspecified,String alarm,String announcement,String command,String feedback,String report) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Intent Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Intent Dropdown In Sends Panel");
		UIActions.click(fileName,"Intent Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Intent Dropdown In Sends Panel");
							
		// Assertion: Verify unspecified,alarm,announcement,command,feedback,report is present
		elementController.requireElementSmart(fileName,"Intent Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Intent Dropdown In Sends Panel");
		String intentDropdown=UIActions.getText(fileName,"Intent Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Intent Dropdown In Sends Panel");
		
		if(!(intentDropdown.contains(unspecified)) &&(intentDropdown.contains(alarm) && (intentDropdown.contains(announcement))&&(intentDropdown.contains(command))&&(intentDropdown.contains(feedback))&&(intentDropdown.contains(report)))){
			throw new UIAutomationException("Intent dropdown is not present");
		}
	}
	
	/**
	 * Verify Intent dropdown in receives panel contains unspecified, alarm, announcement, command, feedback, report
	 * @param unspecified
	 * @param alarm
	 * @param announcement
	 * @param command
	 * @param feedback
	 * @param report
	 * @throws UIAutomationException
	 */
	public void verifyIntentDropdownInReceivesPanel(String unspecified,String alarm,String announcement,String command,String feedback,String report) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Intent Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Intent Dropdown In Receives Panel");
		UIActions.click(fileName,"Intent Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Intent Dropdown In Receives Panel");
							
		// Assertion: Verify reference,policy,mandatin policy,prohibiting policy is present
		elementController.requireElementSmart(fileName,"Intent Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Intent Dropdown In Receives Panel");
		String intentDropdown=UIActions.getText(fileName,"Intent Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Intent Dropdown In Receives Panel");
		
		if(!(intentDropdown.contains(unspecified)) &&(intentDropdown.contains(alarm) && (intentDropdown.contains(announcement))&&(intentDropdown.contains(command))&&(intentDropdown.contains(feedback))&&(intentDropdown.contains(report)))){
			throw new UIAutomationException("Attach dropdown is not present");
		}
		elementController.requireElementSmart(fileName,"Intent Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Intent Dropdown In Receives Panel");
		UIActions.click(fileName,"Intent Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Intent Dropdown In Receives Panel");	
	}
	
	/**
	 * Click on 'Element' in receives panel
	 * @param heading
	 * @throws UIAutomationException
	 */
	public void clickElementInReceivesPanel(String heading) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Element Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Element Receives Panel");
		UIActions.click(fileName,"Element Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Element Receives Panel");
			
		// Assertion: Verify Element of information window opens
		elementController.requireElementSmart(fileName,"Element Of Information Window Heading",GlobalVariables.configuration.getAttrSearchList(),"Element Of Information Window Heading");
		String headingInPage=UIActions.getText(fileName,"Element Of Information Window Heading",GlobalVariables.configuration.getAttrSearchList(),"Element Of Information Window Heading");
		
		if(!headingInPage.contains(heading)){
			throw new UIAutomationException("Element Of Information' window not found");
		}
		
	
	}
	
	/**
	 * Clicks on 'Notification' radio button in 'Sends' panel
	 * @throws UIAutomationException
	 */
	public void clickNotificationInSendsPanel() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Notification In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Notification In Sends Panel");
		UIActions.click(fileName,"Notification In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Notification In Sends Panel");
			
		// Assertion: Verify instruction textbox should be disabled
		elementController.requireElementSmart(fileName,"Instructions In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Instructions In Sends Panel");
		boolean textboxIsEnabled=UIActions.checkEnable(fileName,"Instructions In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Instructions In Sends Panel");
		if(!textboxIsEnabled){
			throw new UIAutomationException("Notification radio button can not be clicked.");
		}
	}
	
	/**
	 * Clicks on 'Notification' radio button in 'Receives' panel
	 * @throws UIAutomationException
	 */
	public void clickNotificationInReceivesPanel() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Notification In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Notification In Receives Panel");
		UIActions.click(fileName,"Notification In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Notification In Receives Panel");
			
		// Assertion: verify instruction textbox should be disabled
		elementController.requireElementSmart(fileName,"Instructions In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Instructions In Receives Panel");
		boolean textboxIsEnabled=UIActions.checkEnable(fileName,"Instructions In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Instructions In Receives Panel");
		if(textboxIsEnabled){
			throw new UIAutomationException("Notification radio button can not be clicked.");
		}
	}
	
	/**
	 * Click on checkbox of 'but only if' in sends panel
	 * @throws UIAutomationException
	 */
	public void clickCheckboxOfbutOnlyIfInSendsPanel(String unspecified,String	inDifferentLocations,
			String inDifferentOrganizations, String inDifferentOverallOrganizations, String inTheSameLocation,
			String inTheSameOrganization, String inTheSameOrganizationAndLocation, String inTheSameOverallOrganization,
			String toASupervisor,String toSelf,String toSomeoneElse) throws UIAutomationException{
		
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"But Only If In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"But Only If In Sends Panel");
		UIActions.click(fileName,"But Only If In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"But Only If In Sends Panel");
	
		elementController.requireElementSmart(fileName,"Change Button In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Change Button In Sends Panel");
		UIActions.click(fileName,"Change Button In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Change Button In Sends Panel");
		
		// Verify dropdown is enabled
		elementController.requireElementSmart(fileName,"But Only If Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "But Only If Dropdown In Sends Panel");
		boolean textboxIsEnabled=UIActions.checkEnable(fileName,"But Only If Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "But Only If Dropdown In Sends Panel");
		if(!textboxIsEnabled){
			throw new UIAutomationException("But only if checkbox in sends panel can not be checked.");
		}
		
		// Verify dropdown contains 
		elementController.requireElementSmart(fileName,"But Only If Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "But Only If Dropdown In Sends Panel");
		String butOnlyIfDropdown=UIActions.getText(fileName,"But Only If Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "But Only If Dropdown In Sends Panel");
		
		if(!(butOnlyIfDropdown.contains(unspecified))&& (butOnlyIfDropdown.contains(inDifferentLocations))&& 
				(butOnlyIfDropdown.contains(inDifferentOrganizations))&&(butOnlyIfDropdown.contains(inDifferentOverallOrganizations))&&
				(butOnlyIfDropdown.contains(inTheSameLocation))&&(butOnlyIfDropdown.contains(inTheSameOrganization))&&
				(butOnlyIfDropdown.contains(inTheSameOrganizationAndLocation))&&(butOnlyIfDropdown.contains(inTheSameOverallOrganization))&&
				(butOnlyIfDropdown.contains(toASupervisor))&&(butOnlyIfDropdown.contains(toSelf))&&(butOnlyIfDropdown.contains(toSomeoneElse))){
			throw new UIAutomationException("But only if dropdown in sends panel is not present.");
		}
		
		elementController.requireElementSmart(fileName,"But Only If Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"But Only If Dropdown In Sends Panel");
		UIActions.click(fileName,"But Only If Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"But Only If Dropdown In Sends Panel");
		
	}
	
	/**
	 * Select option from but only if dropdown
	 * @param option
	 * @throws UIAutomationException
	 */
	public void selectOptionFromButOnlyIfDrodownInSendsPanel(String option) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"But Only If Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"But Only If Dropdown In Sends Panel");
		UIActions.click(fileName,"But Only If Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"But Only If Dropdown In Sends Panel");
		
		Select butOnlyDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(butOnlyDropDownList);
		UIActions.selectByText(option);
	}
	/**
	 * Select option from survey about dropdown list
	 * @param option
	 * @throws UIAutomationException
	 */
	public void selectOptionFromQuestionnaireFromSurveysAbout(String option) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Dropdown Of Surveys About Questionnaire",GlobalVariables.configuration.getAttrSearchList(),"Dropdown Of Surveys About Questionnaire");
		UIActions.click(fileName,"Dropdown Of Surveys About Questionnaire",GlobalVariables.configuration.getAttrSearchList(),"Dropdown Of Surveys About Questionnaire");
		elementController.requireElementSmart(fileName,"Dropdown Of Surveys About Questionnaire",GlobalVariables.configuration.getAttrSearchList(),"Dropdown Of Surveys About Questionnaire");
		Select surveysAboutDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(surveysAboutDropDownList);
		UIActions.selectByText(option);
		UIActions.enterKey(Keys.TAB);
		// Verify option is selected
		String surveyAboutDropdown=UIActions.getText(fileName,"Dropdown Of Surveys About Questionnaire",GlobalVariables.configuration.getAttrSearchList(), "Dropdown Of Surveys About Questionnaire");
		if(!surveyAboutDropdown.contains(option)){
			throw new UIAutomationException("Option '"+option+"' can not be selected.");
		}
	}
	
	public void addQuestionnaire() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Add Questionnaire",GlobalVariables.configuration.getAttrSearchList(),"Add Questionnaire");
		UIActions.click(fileName,"Add Questionnaire",GlobalVariables.configuration.getAttrSearchList(),"Add Questionnaire");
		
	}
	/**
	 * Select option from Channels dropdown in sends panel
	 * @param option
	 * @throws UIAutomationException
	 */
	public void selectOptionFromChannelsInSend(String option) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Channels Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Channels Dropdown In Sends Panel");
		UIActions.click(fileName,"Channels Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Channels Dropdown In Sends Panel");
	
		Select channelsDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(channelsDropDownList);
		UIActions.selectByText(option);
	}
	
	/**
	 * Select option from Channels dropdown in receives panel
	 * @param option
	 * @throws UIAutomationException
	 */
	public void selectOptionFromChannelsInReceives(String option) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Channels Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Channels Dropdown In Receives Panel");
		UIActions.click(fileName,"Channels Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Channels Dropdown In Receives Panel");
		
		Select channelsDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(channelsDropDownList);
		UIActions.selectByText(option);
	}
	/**
	 * Select option from but only if dropdown
	 * @param option
	 * @throws UIAutomationException
	 */
	public void selectOptionFromButOnlyIfDrodownInReceivesPanel(String option) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"But Only If Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"But Only If Dropdown In Receives Panel");
		UIActions.click(fileName,"But Only If Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"But Only If Dropdown In Receives Panel");
		
		Select butOnlyDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(butOnlyDropDownList);
		UIActions.selectByText(option);
	}
	/**
	 * Enter value in 'within' field in sends panel
	 * @param number
	 * @throws UIAutomationException
	 */
	public void enterWithinInSends(String number) throws UIAutomationException{
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"Within Count In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Count In Sends Panel");
		UIActions.click(fileName,"Within Count In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Count In Sends Panel");
		
		UIActions.clearTextBox(fileName,"Within Count In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Count In Sends Panel");
		UIActions.enterValueInTextBox(number,fileName,"Within Count In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Count In Sends Panel");
		
	}
	
	/**
	 * Click Attachment panel in Segment
	 * @throws UIAutomationException
	 */
	public void clickAttachmentPanelInSegment() throws UIAutomationException{
		
		elementController.requireElementSmart(fileName,"Attachment Panel In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Attachment Panel In About Plan Segment");
		UIActions.click(fileName,"Attachment Panel In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Attachment Panel In About Plan Segment");
	    UIActions.scrollDown();
		
	}
	/**
	 * Enter File name to be attached
	 * @param fileName1
	 * @throws UIAutomationException
	 */
	public void enterAttachFileNameInSegment(String fileName1) throws UIAutomationException{

		elementController.requireElementSmart(fileName,"Attach File Name Textbox In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Attach File Name Textbox In About Plan Segment");
		UIActions.click(fileName,"Attach File Name Textbox In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Attach File Name Textbox In About Plan Segment");
		UIActions.enterValueInTextBox(fileName1,fileName,"Attach File Name Textbox In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Attach File Name Textbox In About Plan Segment");
		
	}
	/**
	 * Enters value in description textbox in actual organization
	 * @param description
	 * @throws UIAutomationException
	 */
	public void enterDescriptionInActualOrganization(String description) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Description In Actual Organization",GlobalVariables.configuration.getAttrSearchList(),"Description In Actual Organization");
		UIActions.click(fileName,"Description In Actual Organization",GlobalVariables.configuration.getAttrSearchList(),"Description In Actual Organization");
		
		UIActions.enterValueInTextBox(description,fileName,"Description In Actual Organization",GlobalVariables.configuration.getAttrSearchList(),"Description In Actual Organization");
		UIActions.enterKey(Keys.TAB);
		
	}
	/**
	 * Enters value in Categorization textbox in actual organization
	 * @param category
	 * @throws UIAutomationException
	 */
	public void enterCategoryInActualOrganization(String category) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Category In Actual Organization",GlobalVariables.configuration.getAttrSearchList(),"Category In Actual Organization");
		UIActions.click(fileName,"Category In Actual Organization",GlobalVariables.configuration.getAttrSearchList(),"Category In Actual Organization");
		UIActions.enterValueInTextBox(category,fileName,"Category In Actual Organization",GlobalVariables.configuration.getAttrSearchList(),"Description In Actual Organization");
		UIActions.enterKey(Keys.TAB);
		
	}
	
	/**
	 * Select option from 'within' dropdown in Sends panel
	 * @param option
	 * @throws UIAutomationException
	 */
	public void selectOptionFromWithinInSends(String option) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Within Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Dropdown In Sends Panel");
		UIActions.click(fileName,"Within Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Dropdown In Sends Panel");
		
		Select withinDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(withinDropDownList);
		UIActions.selectByText(option);
		
	}
	/**
	 * Select option from 'within' dropdown in Receives panel
	 * @param option
	 * @throws UIAutomationException
	 */
	public void selectOptionFromWithinInReceives(String option) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Within Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Dropdown In Receives Panel");
		UIActions.click(fileName,"Within Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Dropdown In Receives Panel");
		
		Select withinDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(withinDropDownList);
		UIActions.selectByText(option);
	}
	/**
	 * Enter value in within field in receives panel
	 * @param number
	 * @throws UIAutomationException
	 */
	public void enterWithinInReceives(String number) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Within Count In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Count In Receives Panel");
		UIActions.click(fileName,"Within Count In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Count In Receives Panel");
		UIActions.clearTextBox(fileName,"Within Count In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Count In Receives Panel");
		UIActions.enterValueInTextBox(number,fileName,"Within Count In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Count In Receives Panel");
		
	}
	/**
	 * Click on checkbox of 'but only if' in receives panel
	 * @throws UIAutomationException
	 */
	public void clickCheckboxOfbutOnlyIfInReceivesPanel(String unspecified,String	inDifferentLocations,
			String inDifferentOrganizations, String inDifferentOverallOrganizations, String inTheSameLocation,
			String inTheSameOrganization, String inTheSameOrganizationAndLocation, String inTheSameOverallOrganization,
			String toASupervisor,String toSelf,String toSomeoneElse) throws UIAutomationException{
		
		UIActions.scrollDown();
		elementController.requireElementSmart(fileName,"But Only If In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"But Only If In Receives Panel");
		UIActions.click(fileName,"But Only If In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"But Only If In Receives Panel");
		
		elementController.requireElementSmart(fileName,"Change Button In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Change Button In Receives Panel");
		UIActions.click(fileName,"Change Button In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Change Button In Receives Panel");
		
		// Verify dropdown is enabled
		elementController.requireElementSmart(fileName,"But Only If Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "But Only If Dropdown In Receives Panel");
		boolean textboxIsEnabled=UIActions.checkEnable(fileName,"But Only If Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "But Only If Dropdown In Receives Panel");
		if(!textboxIsEnabled){
			throw new UIAutomationException("But only if checkbox in receives panel can not be checked.");
		}
		
		// Verify dropdown contain		
		elementController.requireElementSmart(fileName,"But Only If Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "But Only If Dropdown In Receives Panel");
		String butOnlyIfDropdown=UIActions.getText(fileName,"But Only If Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "But Only If Dropdown In Receives Panel");
		
		if(!(butOnlyIfDropdown.contains(unspecified))&& (butOnlyIfDropdown.contains(inDifferentLocations))&& 
				(butOnlyIfDropdown.contains(inDifferentOrganizations))&&(butOnlyIfDropdown.contains(inDifferentOverallOrganizations))&&
				(butOnlyIfDropdown.contains(inTheSameLocation))&&(butOnlyIfDropdown.contains(inTheSameOrganization))&&
				(butOnlyIfDropdown.contains(inTheSameOrganizationAndLocation))&&(butOnlyIfDropdown.contains(inTheSameOverallOrganization))&&
				(butOnlyIfDropdown.contains(toASupervisor))&&(butOnlyIfDropdown.contains(toSelf))&&(butOnlyIfDropdown.contains(toSomeoneElse))){
			throw new UIAutomationException("But only if dropdown in receives panel is not present.");
		}
		
	}
	
	/**
	 * Clicks on 'Channels' dropdown in sends panel
	 * @param cell
	 * @param conferenceCall
	 * @param courier
	 * @param email
	 * @param faceToFace
	 * @param fax
	 * @param IM
	 * @param landline
	 * @param mail
	 * @param meeting
	 * @param notificationSystem
	 * @param onlineChat
	 * @param pager
	 * @param PASystem
	 * @param phone
	 * @param radio
	 * @param television
	 * @param twoWayRadio
	 * @param newMedium
	 * @throws UIAutomationException
	 */
	public void clickChannelsDropdownInSendsPanel(String cell, String conferenceCall, String courier, String email,
			String faceToFace, String fax, String IM, String landline, String mail, String meeting, String notificationSystem,
			String onlineChat, String pager, String PASystem, String phone, String radio, String television, String twoWayRadio,
			String newMedium) throws UIAutomationException{
		
		elementController.requireElementSmart(fileName,"Channels Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Channels Dropdown In Sends Panel");
		UIActions.click(fileName,"Channels Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Channels Dropdown In Sends Panel");
		
		
		// Verify dropdown contains 
		elementController.requireElementSmart(fileName,"Channels Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Channels Dropdown In Sends Panel");
		String channelsDropdown=UIActions.getText(fileName,"Channels Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Channels Dropdown In Sends Panel");
		
		if(!(channelsDropdown.contains(cell))&& (channelsDropdown.contains(conferenceCall))&& 
				(channelsDropdown.contains(courier))&&(channelsDropdown.contains(email))&&
				(channelsDropdown.contains(faceToFace))&&(channelsDropdown.contains(fax))&&
				(channelsDropdown.contains(IM))&&(channelsDropdown.contains(landline))&&
				(channelsDropdown.contains(mail))&&(channelsDropdown.contains(meeting))&&
				(channelsDropdown.contains(notificationSystem))&&(channelsDropdown.contains(onlineChat))&&
				(channelsDropdown.contains(pager))&&(channelsDropdown.contains(PASystem))&&(channelsDropdown.contains(phone))&&(channelsDropdown.contains(radio))&&
				(channelsDropdown.contains(television))&&(channelsDropdown.contains(twoWayRadio))&&(channelsDropdown.contains(newMedium))){
			throw new UIAutomationException("Channels dropdown in sends panel is not present.");
		}
		
		UIActions.enterKey(Keys.TAB);
		
	}
	/**
	 * Clicks on channels dropdown in 'Receives' panel
	 * @param cell
	 * @param conferenceCall
	 * @param courier
	 * @param email
	 * @param faceToFace
	 * @param fax
	 * @param IM
	 * @param landline
	 * @param mail
	 * @param meeting
	 * @param notificationSystem
	 * @param onlineChat
	 * @param pager
	 * @param PASystem
	 * @param phone
	 * @param radio
	 * @param television
	 * @param twoWayRadio
	 * @param newMedium
	 * @throws UIAutomationException
	 */
	public void clickChannelsDropdownInReceivesPanel(String cell, String conferenceCall, String courier, String email,
			String faceToFace, String fax, String IM, String landline, String mail, String meeting, String notificationSystem,
			String onlineChat, String pager, String PASystem, String phone, String radio, String television, String twoWayRadio,
			String newMedium) throws UIAutomationException{		
		
		elementController.requireElementSmart(fileName,"Channels Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Channels Dropdown In Receives Panel");
		UIActions.click(fileName,"Channels Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Channels Dropdown In Receives Panel");
		
		// Verify dropdown contains 
		elementController.requireElementSmart(fileName,"Channels Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Channels Dropdown In Receives Panel");
		String channelsDropdown=UIActions.getText(fileName,"Channels Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Channels Dropdown In Receives Panel");
		
		if(!(channelsDropdown.contains(cell))&& (channelsDropdown.contains(conferenceCall))&& 
				(channelsDropdown.contains(courier))&&(channelsDropdown.contains(email))&&
				(channelsDropdown.contains(faceToFace))&&(channelsDropdown.contains(fax))&&
				(channelsDropdown.contains(IM))&&(channelsDropdown.contains(landline))&&
				(channelsDropdown.contains(mail))&&(channelsDropdown.contains(meeting))&&
				(channelsDropdown.contains(notificationSystem))&&(channelsDropdown.contains(onlineChat))&&
				(channelsDropdown.contains(pager))&&(channelsDropdown.contains(PASystem))&&(channelsDropdown.contains(phone))&&(channelsDropdown.contains(radio))&&
				(channelsDropdown.contains(television))&&(channelsDropdown.contains(twoWayRadio))&&(channelsDropdown.contains(newMedium))){
			throw new UIAutomationException("Channels dropdown in receives panel is not present.");
		}
		
		UIActions.enterKey(Keys.TAB);
		
	}
	
	/**
	 * Click on 'Reply That' in sends panel
	 * @throws UIAutomationException
	 */
	public void clickReplyThatInSendsPanel() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Reply That In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Reply That In Sends Panel");
		UIActions.click(fileName,"Reply That In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Reply That In Sends Panel");
			
		// Assertion: verify instruction textbox should be disabled
		elementController.requireElementSmart(fileName,"Instructions In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Instructions In Sends Panel");
		boolean textboxIsEnabled=UIActions.checkEnable(fileName,"Instructions In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Instructions In Sends Panel");
		if(textboxIsEnabled){
			throw new UIAutomationException("Reply That radio button can not be clicked.");
		}
	}
	/**
	 * Click on 'Reply That' in receives panel
	 * @throws UIAutomationException
	 */
	public void clickReplyThatInReceivesPanel() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Reply That In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Reply That In Receives Panel");
		UIActions.click(fileName,"Reply That In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Reply That In Receives Panel");

		//		elementController.requireElementSmart(fileName, elementName, findBys, notFound)
//		String radioButtonxPath = dataController.getPageDataElements(fileName, "Reply That In Receives Panel", "Xpath");
//		if(radioButtonxPath.startsWith("[")){
//			radioButtonxPath = radioButtonxPath.substring(1, radioButtonxPath.length()-1);
//		}
//		
//		else
//		{
//			throw new UIAutomationException("Reply That Radio Buttons not found");
//		}
	
		// Assertion: verify instruction textbox should be disabled
		elementController.requireElementSmart(fileName,"Instructions In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Instructions In Receives Panel");
		boolean textboxIsEnabled=UIActions.checkEnable(fileName,"Instructions In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Instructions In Receives Panel");
		if(!textboxIsEnabled){
			throw new UIAutomationException("Reply That radio button can not be clicked.");
		}
	
	}
	/**
	 * Click on browse button
	 * @throws UIAutomationException
	 */
	public void clickBrowseInAttachFileInAboutPlanSegment() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Attach File Upload Button In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Attach File Upload Button In About Plan Segment");
		UIActions.click(fileName,"Attach File Upload Button In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Attach File Upload Button In About Plan Segment");
		GlobalVariables.configuration.getWebElement().sendKeys("D:\\Channels\\Mind-AllianceFramework_V2\\TestData\\CAP.txt");		
	}
	
public void enterFileNameInAttachFileInAboutPlanSegment(String fileName) throws UIAutomationException{
		String path=getPath(fileName);
	GlobalVariables.configuration.getWebElement().sendKeys(path);
	}
	/**
	 * Gets file path
	 * @param fileName
	 * @return
	 * @throws UIAutomationException
	 */
	public String getPath(String fileName) throws UIAutomationException{
		try{
			File currentDir=new File(".");
			String path= currentDir.getCanonicalPath().toString() + "\\TestData\\";
			path=path+fileName;
			return path;
		}
		catch (IOException ie) {
			throw new UIAutomationException("File MAP0018_attachFile.xml not found.");
		}
	}
	/**
	 * Click on 'Submit' in attachment panel in About Plan Segment 
	 * @throws UIAutomationException
	 */
	public void clickSubmitInAttachFileInAboutPlanSegment() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Attach File Submit Button In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Attach File Submit Button In About Plan Segment");
		UIActions.click(fileName,"Attach File Submit Button In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Attach File Submit Button In About Plan Segment");
		
	}
	
	/**
	 * Click on 'Delete' in attach File in About Plan Segment
	 * @throws UIAutomationException
	 */
	public void clickDeleteInAttachFileInAboutPlanSegment() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Attach File Delete Button In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Attach File Delete Button In About Plan Segment");
		UIActions.click(fileName,"Attach File Delete Button In About Plan Segment",GlobalVariables.configuration.getAttrSearchList(),"Attach File Delete Button In About Plan Segment");
		
	}
	
	/**
	 * Verify by clicking on 'Elements' link 'EOI' window opens
	 * @throws UIAutomationException
	 */
	public void clickElementInSendsPanel(String heading) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Element Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Element Sends Panel");
		UIActions.click(fileName,"Element Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Element Sends Panel");
		
		// Assertion: Verify Element of information window opens
		elementController.requireElementSmart(fileName,"Element Of Information Window Heading",GlobalVariables.configuration.getAttrSearchList(),"Element Of Information Window Heading");
		String headingInPage=UIActions.getText(fileName,"Element Of Information Window Heading",GlobalVariables.configuration.getAttrSearchList(),"Element Of Information Window Heading");
		
		if(!headingInPage.contains(heading)){
			throw new UIAutomationException("Element Of Information' window not found");
		}
	}
	
	/**
	 * Stack Trace method
	 */
	
	public void getStackTraceOnPlanPage() throws UIAutomationException{
		String pageTitle=GlobalVariables.configuration.getWebDriver().getTitle();
		pageTitle="Internal Error";
		String stackTrace=GlobalVariables.configuration.getWebDriver().getPageSource();
		if(stackTrace.contains(pageTitle)){
			System.out.println(stackTrace);
		}else{
			System.out.println("No Error Found");
		}
  	}	
}	
