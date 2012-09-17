package com.mindalliance.pages;

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
	 * @param   void
	 * @return  Void
	 * @throws UIAutomationException 
	 */
	
	public void closeAboutPlanWindow() throws UIAutomationException{
			elementController.requireElementSmart(fileName,"Close About Plan Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Plan Window");
			UIActions.click(fileName,"Close About Plan Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Plan Window");
	}
	/**
	 * 'closeAboutEventUnnamedWindow' method close the About Plan window
	 * @param   void
	 * @return  Void
	 * @throws UIAutomationException 
	 */
	
	public void closeAboutEventUnnamedWindow() throws UIAutomationException{
			elementController.requireElementSmart(fileName,"Close About Event: Unnamed Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Event: Unnamed Window");
			UIActions.click(fileName,"Close About Event: Unnamed Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Event: Unnamed Window");
	}
	/**
	 * 'closePlanMap' method close the Plan Map window
	 * @param   void
	 * @return  Void
	 * @throws UIAutomationException 
     */
	
	public void closePlanMap() throws UIAutomationException{
			elementController.requireElementSmart(fileName,"Close Plan Map Window",GlobalVariables.configuration.getAttrSearchList(), "Close Plan map window");
			UIActions.click(fileName,"Close Plan Map Window",GlobalVariables.configuration.getAttrSearchList(), "Close Plan map window");
			
	}
	
	/**
	 * Closes 'Assignment' window
	 * @throws UIAutomationException
	 */
	public void closeAssignmentWindow() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Close Assignment Window",GlobalVariables.configuration.getAttrSearchList(), "Close Plan map window");
		UIActions.click(fileName,"Close Assignment Window",GlobalVariables.configuration.getAttrSearchList(), "Close Plan map window");
		
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
	 * @param   void
	 * @return  Void
	 * @throws UIAutomationException 
	 */
	
	public void closeSegmentWindow() throws UIAutomationException{
			elementController.requireElementSmart(fileName,"Close Segment Window",GlobalVariables.configuration.getAttrSearchList(), "Close Segment Window");
			xPath=dataController.getPageDataElements(fileName,"Close Segment Window", "Xpath");
			UIActions.click(fileName,"Close Segment Window",GlobalVariables.configuration.getAttrSearchList(), "Close Segment Window");
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
	 * @param   void
	 * @return  Void
	 * @throws UIAutomationException 
	*/
	
	public void closeEventsWindow() throws UIAutomationException{
			elementController.requireElementSmart(fileName,"Close Events Window",GlobalVariables.configuration.getAttrSearchList(), "Close Events Window");
			UIActions.click(fileName,"Close Events Window",GlobalVariables.configuration.getAttrSearchList(), "Close Events Window");
	}
	
	/**
	 * 'closeClassificationWindow' method close the Classification window
	 * @param   void
	 * @return  Void
	 * @throws UIAutomationException 
	 */
	
	public void closeClassificationWindow() throws UIAutomationException{
			elementController.requireElementSmart(fileName,"Close Classification Window",GlobalVariables.configuration.getAttrSearchList(), "Close Events Window");
			UIActions.click(fileName,"Close Classification Window",GlobalVariables.configuration.getAttrSearchList(), "Close Events Window");
	}
	
	/**
	 * 'closeOrganiztionWindow' method close the Organization window
	 * @param   void
	 * @return  Void
	 * @throws UIAutomationException 
	 */
	
	public void closeOrganiztionWindow() throws UIAutomationException{
			elementController.requireElementSmart(fileName,"Close Organization Window",GlobalVariables.configuration.getAttrSearchList(), "Close Organization Window");
			UIActions.click(fileName,"Close Organization Window",GlobalVariables.configuration.getAttrSearchList(), "Close Organization Window");
	}
	
	/**
	 * 'closeSearchingWindow' method close the Searching window
	 * @param   void
	 * @return  Void
	 * @throws UIAutomationException 
	 */
	
	public void closeSearchingWindow() throws UIAutomationException{
			elementController.requireElementSmart(fileName,"Close Searching Window",GlobalVariables.configuration.getAttrSearchList(), "Close Searching Window");
			UIActions.click(fileName,"Close Searching Window",GlobalVariables.configuration.getAttrSearchList(), "Close Searching Window");
	}
	
	/**
	 * 'closAllIssuesWindow' method close the Issues window
	 * @param   void
	 * @return  void
	 * @throws UIAutomationException 
	 */
	
	public void closeAllIssuesWindow() throws UIAutomationException{
			elementController.requireElementSmart(fileName,"Close All Issues Window",GlobalVariables.configuration.getAttrSearchList(), "Close All Issues Window");
			UIActions.click(fileName,"Close All Issues Window",GlobalVariables.configuration.getAttrSearchList(), "Close All Issues Window");
	}
	
	/**
	 * 'closePlanEvaluationWindow' method close the Plan Evaluation window
	 * @param   void
	 * @return  void
	 * @throws UIAutomationException 
	 */
	
	public void closePlanEvaluationWindow() throws UIAutomationException{
			elementController.requireElementSmart(fileName,"Close Plan Evaluation Window",GlobalVariables.configuration.getAttrSearchList(), "Close Plan Evaluation Window");
			UIActions.click(fileName,"Close Plan Evaluation Window",GlobalVariables.configuration.getAttrSearchList(), "Close Plan Evaluation Window");
	}
	

	/**
	 * 'closeUserAsAgentWindow' method close 'User As Agents' window
	 * @param   void
	 * @return  void
	 * @throws UIAutomationException 
	 */
	
	public void closeUserAsAgentWindow() throws UIAutomationException{
			elementController.requireElementSmart(fileName,"Close User As Agent Window",GlobalVariables.configuration.getAttrSearchList(), "Close User As Agent Window");
			UIActions.click(fileName,"Close User As Agent Window",GlobalVariables.configuration.getAttrSearchList(), "Close User As Agent Window");
	}
	
	/**
	 * 'closePlanVersionsWindow' method close 'Plan Versions' window
	 * @param   void
	 * @return  void
	 * @throws UIAutomationException 
	 */
	
	public void closePlanVersionsWindow() throws UIAutomationException{
			elementController.requireElementSmart(fileName,"Close Plan Versions Window",GlobalVariables.configuration.getAttrSearchList(), "Close Plan Versions Window");
			UIActions.click(fileName,"Close Plan Versions Window",GlobalVariables.configuration.getAttrSearchList(), "Close Plan Versions Window");
	}
	/**
	 * 'closeAboutPlanSegmentWindow' method close 'About Plan Segment' window
	 * @param   void
	 * @return  void
	 * @throws UIAutomationException 
	 */
	
	public void closeAboutPlanSegmentWindow() throws UIAutomationException{
			elementController.requireElementSmart(fileName,"Close About Plan Segment Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Plan Segment Window");
			UIActions.click(fileName,"Close About Plan Segment Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Plan Segment Window");
	}
	
	/**
	 * Close 'Element Of Information' window
	 * @throws UIAutomationException
	 */
	public void closeEOIWindow() throws UIAutomationException{
			elementController.requireElementSmart(fileName,"Close Element Of Information Window",GlobalVariables.configuration.getAttrSearchList(), "Close Element Of Information Window");
			UIActions.click(fileName,"Close Element Of Information Window",GlobalVariables.configuration.getAttrSearchList(), "Close Element Of Information Window");
	}
	/**
	 * 'closeTaskMoverWindow' method close 'About Plan Segment' window
	 * @param   void
	 * @return  void
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
	 * @param   void
	 * @return  void
	 * @throws UIAutomationException 
	 */
	
	public void closeAllSurveysWindow() throws UIAutomationException{
			elementController.requireElementSmart(fileName,"Close All Surveys Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Plan Segment Window");
			UIActions.click(fileName,"Close All Surveys Window",GlobalVariables.configuration.getAttrSearchList(), "Close About Plan Segment Window");
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
			default:
				break;
			}
	}
	/**
	 * 'clickSubmenu' method click on Sub Menu under Popup menu
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
		
	case "Cut Task":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		
		try{
			Thread.sleep(3000);
		}
		catch(Exception e){}
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Alert Window Title Of Cut Task", "Title");
		UIActions.assertAlert(headingOfWindowInXML);
					
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
				
		// Assertion : Verify 'Tags' window is present 
		elementController.requireElementSmart(fileName, "Tags Title", GlobalVariables.configuration.getAttrSearchList(),"Tags Title");
		headingOfWindowInPage=UIActions.getText(fileName, "Tags Title", GlobalVariables.configuration.getAttrSearchList(),"Tags Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Tags Window Title", "Title");
		if(!headingOfWindowInPage.equals(headingOfWindowInXML)){
			throw new UIAutomationException("Window with Title '"+headingOfWindowInXML+"' not found");
		}
		break;
		
	case "Plan Evaluation":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
				
		// Assertion : Verify 'Plan Evaluation' window is present 
		elementController.requireElementSmart(fileName, "Plan Evaluation Title", GlobalVariables.configuration.getAttrSearchList(),"Plan Evaluation Title");
		headingOfWindowInPage=UIActions.getText(fileName, "Plan Evaluation Title", GlobalVariables.configuration.getAttrSearchList(),"Plan Evaluation Title");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Plan Evaluation Window Title", "Title");
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
	case "Undo Update Segment":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;	
	case "Undo Paste Task":
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
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		try{
			Thread.sleep(2000);
		}
		catch (Exception e) {}
		
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
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		UIActions.assertAlert(headingOfWindowInXML);
		break;
	case "Remove Info Sharing Capability In Sends Panel":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Alert Window Title Of Remove Segment", "Title");
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		UIActions.assertAlert(headingOfWindowInXML);
		break;	
	case "Issue In Task Panel":
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		try{
			Thread.sleep(2000);
		}
		catch (Exception e) {}
		
		// Assertion : Verify new issue is added 
		elementController.requireElementSmart(fileName, "New Issue Text In Task Panel", GlobalVariables.configuration.getAttrSearchList(),"New Issue Text In Task Panel");
		headingOfWindowInPage=UIActions.getText(fileName, "New Issue Text In Task Panel", GlobalVariables.configuration.getAttrSearchList(),"New Issue Text In Task Panel");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "New Issue Actual Text", "Name");
		if(!headingOfWindowInPage.contains(headingOfWindowInXML)){
			throw new UIAutomationException("'"+headingOfWindowInXML+"' not found");
		}
		break;	
	default:
		elementController.requireElementSmart(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		UIActions.click(fileName,subMenu, GlobalVariables.configuration.getAttrSearchList(), subMenu);
		break;
		}
	}	
	
	/**
	 * Enter segment name in segment name textbox
	 * @param segmentName
	 * @return  Void
	 * @throws UIAutomationException 
	 */
	public void enterSegmentName(String segmentName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Segment Name",GlobalVariables.configuration.getAttrSearchList(), "Segment Name");
		UIActions.click(fileName,"Segment Name",GlobalVariables.configuration.getAttrSearchList(), "Segment Name");
		for (int i = 0; i <= 8; i++){
			UIActions.enterKey(Keys.BACK_SPACE);
		}
		UIActions.enterValueInTextBox(segmentName);
		UIActions.enterKey(Keys.TAB);
	}
	/**
	 * Enter description in issue' description textbox
	 * @param description
	 * @return  Void
	 * @throws UIAutomationException 
	 */
	public void enterDescriptionInIssue(String description) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Description Of New Issue In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Description Of New Issue In Task Panel");
		UIActions.click(fileName,"Description Of New Issue In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Description Of New Issue In Task Panel");
		UIActions.clearTextBox(fileName,"Description Of New Issue In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Description Of New Issue In Task Panel");
		UIActions.enterValueInTextBox(description);
		UIActions.enterKey(Keys.TAB);
		
		// Verify 
	}
	
	
	/**
	 * Enter Task name in segment name textbox
	 * @param taskName
	 * @return  Void
	 * @throws UIAutomationException 
	*/
	public void enterTaskName(String taskName) throws UIAutomationException	{
		elementController.requireElementSmart(fileName,"Textbox of Task",GlobalVariables.configuration.getAttrSearchList(), "Task Name");
		UIActions.click(fileName,"Textbox of Task",GlobalVariables.configuration.getAttrSearchList(), "Task Name");
		for (int i = 0; i <= 18; i++){
			UIActions.enterKey(Keys.BACK_SPACE);
		}
		UIActions.enterValueInTextBox(taskName);
		UIActions.enterKey(Keys.TAB);
	}
	/**
	 * Enters information in receives panel
	 * @param informationName
	 * @throws UIAutomationException
	 */
	public void enterInformationNameInReceivesPanel(String informationName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Information In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Information In Receives Panel");
		UIActions.click(fileName,"Information In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Information In Receives Panel");
		UIActions.enterValueInTextBox(informationName);
		UIActions.enterKey(Keys.TAB);
		
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		
		// Assertion: verify information is added
		elementController.requireElementSmart(fileName,"Add New Receives Header",GlobalVariables.configuration.getAttrSearchList(), "Add New Receives Header");
		String informationNameInSendsPanel=UIActions.getText(fileName,"Add New Receives Header",GlobalVariables.configuration.getAttrSearchList(), "Add New Receives Header");
		if(!informationNameInSendsPanel.contains(informationName)){
			throw new UIAutomationException("Information not entered in receives panel");
		}
	}
	
	/**
	 * Enters information in sends panel
	 * @param informationName
	 * @throws UIAutomationException
	 */
	public void enterInformationNameInSendsPanel(String informationName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Information In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Information In Sends Panel");
		UIActions.click(fileName,"Information In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Information In Sends Panel");
		UIActions.enterValueInTextBox(informationName);
		UIActions.enterKey(Keys.TAB);
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		
		// Assertion: verify information is added
		elementController.requireElementSmart(fileName,"Add New Sends Header",GlobalVariables.configuration.getAttrSearchList(), "Add New Sends Header");
		String informationNameInSendsPanel=UIActions.getText(fileName,"Add New Sends Header",GlobalVariables.configuration.getAttrSearchList(), "Add New Sends Header");
		if(!informationNameInSendsPanel.contains(informationName)){
			throw new UIAutomationException("Information not entered in sends panel");
		}
	}
	
	/**
	 * Select from dropdownlist in receives panel
	 * @param other
	 * @throws UIAutomationException
	 */
	public void selectFrom(String other) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"From Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"From Dropdown In Receives Panel");
		UIActions.click(fileName,"From Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"From Dropdown In Receives Panel");
		
		Select fromDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(fromDropDownList);
		UIActions.selectByText(other);
	}
	
	/**
	 * Enter value in 'From' textbox in receives panel
	 * @param fromTaskName
	 * @throws UIAutomationException
	 */
	public void enterFromTaskName(String fromTaskName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"From Task Name In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "From Task Name In Receives Panel");
		UIActions.click(fileName,"From Task Name In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "From Task Name In Receives Panel");
		for (int i = 0; i <= 17; i++){
			UIActions.enterKey(Keys.BACK_SPACE);
		}
		
		UIActions.enterValueInTextBox(fromTaskName);
		UIActions.enterKey(Keys.TAB);
	}
	
	/**
	 * Enter value of Tag in 'Tags' textbox
	 * @param tagName
	 * @throws UIAutomationException
	 */
	public void enterValueInTagsInTask(String tagName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Tags In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Tags In Task Panel");
		UIActions.click(fileName,"Tags In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Tags In Task Panel");
		UIActions.enterValueInTextBox(tagName);
		UIActions.enterKey(Keys.TAB);
	}
	
	/**
	 * Enter value in 'Causes Event' textbox
	 * @param eventName
	 * @throws UIAutomationException
	 */
	public void enterValueInCausesEventInTask(String eventName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Causes Event In Task",GlobalVariables.configuration.getAttrSearchList(), "Causes Event In Task");
		UIActions.click(fileName,"Causes Event In Task",GlobalVariables.configuration.getAttrSearchList(), "Causes Event In Task");
		UIActions.enterValueInTextBox(eventName);
		UIActions.enterKey(Keys.TAB);
		
		// Verify event is added
		
	}
	
	/**
	 * Delete Tag value in textbox
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
	 * Clicks on 'Strench Up' form icon
	 * @throws UIAutomationException
	 */
	public void clickStrenchUpForm() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Strench Up Forms", GlobalVariables.configuration.getAttrSearchList(), "Strench Up Forms");
		UIActions.click(fileName,"Strench Up Forms", GlobalVariables.configuration.getAttrSearchList(), "Strench Up Forms");
		
		
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
	
	/**
	 * Clicks on 'Show' pop up menu and verify subMenu is present
	 * @param subMenuNameInTask
	 * @throws UIAutomationException
	 */
	public void clickShowInTaskPanel(String subMenuNameInTask) throws UIAutomationException{
		String subMenuName=null;
		elementController.requireElementSmart(fileName,"Show In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Task Panel");
		UIActions.click(fileName,"Show In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show In Task Panel");
		
		// Assertion: Verify SubMenu is present
		elementController.requireElementSmart(fileName,"Hide Details In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Segment");
		subMenuName=UIActions.getText(fileName,"Hide Details In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Actions In Segment");
		if(!subMenuName.equals(subMenuNameInTask)){
			throw new UIAutomationException( "'"+subMenuNameInTask +"' not found");
		}
	}
		
	/**
	 * Check checkbox of 'Usually completes after' and verify textbox and dropdown gets enabled
	 * @throws UIAutomationException
	 */
	public void clickUsuallycompletesAfter() throws UIAutomationException{
		boolean textboxIsEnabled=false;
		boolean dropdownIsEnabled=false;
		
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
		elementController.requireElementSmart(fileName,"Is ongoing In Task",GlobalVariables.configuration.getAttrSearchList(), "Is ongoing In Task");
		UIActions.click(fileName,"Is ongoing In Task",GlobalVariables.configuration.getAttrSearchList(), "Is ongoing In Task");
			
		clickStrenchUpForm();
		clickdoingUnnamedTask();
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		
		
		// Assertion: Verify by clicking on 'Is ongoing'  
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
		elementController.requireElementSmart(fileName,"Can End Event In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Can End Event In Task Panel");
		UIActions.click(fileName,"Can End Event In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Can End Event In Task Panel");
			
		clickStrenchUpForm();
		clickdoingUnnamedTask();
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		
		
		// Assertion: Verify by clicking on 'Can end event'  
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
		elementController.requireElementSmart(fileName,"Goals Link In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Goals Link In Task Panel");
		UIActions.click(fileName,"Goals Link In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Goals Link In Task Panel");
		clickStrenchUpForm();
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){}
		
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
	
	/**
	 * Clicks on 'Show Advanced Form' in Task panel
	 * @throws UIAutomationException
	 */
	public void clickShowAdvancedFormInTaskPanel(String showlinkTextInXML,String advancedLinkTextInXML,String flag) throws UIAutomationException{
		String linkTextInPage=null;
		elementController.requireElementSmart(fileName,"Show Advanced Simple Form In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Task Panel");
		UIActions.click(fileName,"Show Advanced Simple Form In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Task Panel");
		try{
			Thread.sleep(2000);
		}
		catch(Exception e){}
		if(flag.equals("False")){
			// Assertion: Verify by clicking on 'Show Advanced Form' link chabges to 'Show Simple form'
			elementController.requireElementSmart(fileName,"Show Advanced Simple Form In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Task Panel");
			linkTextInPage=UIActions.getText(fileName,"Show Advanced Simple Form In Task Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Advanced Simple Form In Task Panel");
			if(!linkTextInPage.equals(showlinkTextInXML)){
				throw new UIAutomationException( "'"+showlinkTextInXML +"' not found");
			}
		}
		else {
			// Assertion: Verify by clicking on 'Show Simple Form' link chabges to 'Show Advanced form'
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
		try{
			Thread.sleep(2000);
		}
		catch(Exception e){}
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
			// Assertion: Verify by clicking on 'Show Simple Form' link chabges to 'Show Advanced form'
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
	
	public void clickAddInReceivesPanel() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Add In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Add In Receives Panel");
		UIActions.click(fileName,"Add In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Add In Receives Panel");
	}
	
	public void clickAddInSendsPanel() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Add In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Add In Sends Panel");
		UIActions.click(fileName,"Add In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Add In Sends Panel");
	}
	
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
	 * @param goalName 
	 * @throws UIAutomationException
	 */
	public void addGoal(String goalName,String selectGoal,String type) throws UIAutomationException
	{
		// Select goal from dropdown
		elementController.requireElementSmart(fileName,"Category in goal",GlobalVariables.configuration.getAttrSearchList(),"Category in goal");
		UIActions.click(fileName,"Category in goal",GlobalVariables.configuration.getAttrSearchList(),"Category in goal");
			
		Select fromDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(fromDropDownList);
		UIActions.selectByTextAndClick(selectGoal);
		
		// Select Organization
		elementController.requireElementSmart(fileName,"Organization in goal dropdown",GlobalVariables.configuration.getAttrSearchList(),"Organization in goal dropdown");
		UIActions.click(fileName,"Organization in goal dropdown",GlobalVariables.configuration.getAttrSearchList(),"Organization in goal dropdown");
		
		Select organizationDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(organizationDropDownList);
		UIActions.selectByTextAndClick(type);
	
		// Enter name in Goal text box
		elementController.requireElementSmart(fileName, "Goal Name", GlobalVariables.configuration.getAttrSearchList(), "Goal Name");
		UIActions.click(fileName, "Goal Name", GlobalVariables.configuration.getAttrSearchList(), "Goal Name");
		elementController.requireElementSmart(fileName, "Goal Name", GlobalVariables.configuration.getAttrSearchList(), "Goal Name");
		UIActions.enterValueInTextBox(goalName);
		
		// Achive at end
		elementController.requireElementSmart(fileName,"Acheived at end", GlobalVariables.configuration.getAttrSearchList(), "Acheived at end");
		UIActions.click(fileName,"Acheived at end", GlobalVariables.configuration.getAttrSearchList(), "Acheived at end");
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
		elementController.requireElementSmart(fileName,"Goals Dropdown In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Goals Dropdown In Task Panel");
		UIActions.click(fileName,"Goals Dropdown In Task Panel",GlobalVariables.configuration.getAttrSearchList(),"Goals Dropdown In Task Panel");
			
		Select goalsDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(goalsDropDownList);
		UIActions.selectByText(selectGoal);
				
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
	
	public void verifyAttachDropdownInSendsPanel(String reference,String policy,String picture,String mandatingPolicy,String prohibitingPolicy) throws UIAutomationException{
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

	public void verifyAttachDropdownInReceivesPanel(String reference,String policy,String picture,String mandatingPolicy,String prohibitingPolicy) throws UIAutomationException{
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
	public void verifySeverityDropdownInIssueInSends(String minor,String major,String severe,String extreme) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Severity Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue In Sends Panel");
		UIActions.click(fileName,"Severity Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue In Sends Panel");
							
		// Assertion: Verify minor,major,severe,extreme
		elementController.requireElementSmart(fileName,"Severity Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue In Sends Panel");
		String severityDropdown=UIActions.getText(fileName,"Severity Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(), "Severity Dropdown In New Issue In Sends Panel");
		
		if(!(severityDropdown.contains(minor)) &&(severityDropdown.contains(major) && (severityDropdown.contains(severe))&& (severityDropdown.contains(extreme)))){
			throw new UIAutomationException("Severity dropdown in issue is not present");
		}
		elementController.requireElementSmart(fileName,"Severity Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue In Sends Panel");
		UIActions.click(fileName,"Severity Dropdown In New Issue In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Severity Dropdown In New Issue In Sends Panel");
			
	}
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
	 * @param reference
	 * @param policy
	 * @param mandatingPolicy
	 * @param prohibitingPolicy
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
		
	}
	
	/**
	 * Click on 'Element' in receives panel
	 * @throws UIAutomationException
	 */
	public void clickElementInReceivesPanel() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Element Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Element Receives Panel");
		UIActions.click(fileName,"Element Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Element Receives Panel");
							
//		// Assertion: Verify reference,policy,mandatin policy,prohibiting policy is present
//		elementController.requireElementSmart(fileName,"Intent Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Intent Dropdown In Receives Panel");
//		String intentDropdown=UIActions.getText(fileName,"Intent Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Intent Dropdown In Receives Panel");
//		
//		if(!(intentDropdown.contains(unspecified)) &&(intentDropdown.contains(alarm) && (intentDropdown.contains(announcement))&&(intentDropdown.contains(command))&&(intentDropdown.contains(feedback))&&(intentDropdown.contains(report)))){
//			throw new UIAutomationException("Attach dropdown is not present");
//		}
	
	}
	
	/**
	 * Clicks on 'Notification' radio button in 'Sends' panel
	 * @throws UIAutomationException
	 */
	public void clickNotificationInSendsPanel() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Notification In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Notification In Sends Panel");
		UIActions.click(fileName,"Notification In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Notification In Sends Panel");
			
		// Assertion: verify instruction textbox should be disabled
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
		
		elementController.requireElementSmart(fileName,"But Only If In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"But Only If In Sends Panel");
		UIActions.click(fileName,"But Only If In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"But Only If In Sends Panel");
		
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
	
	public void enterWithinInSends(String number) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Within Count In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Count In Sends Panel");
		UIActions.click(fileName,"Within Count In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Count In Sends Panel");
		
		UIActions.clearTextBox(fileName,"Within Count In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Count In Sends Panel");
		UIActions.enterValueInTextBox(number);
		
	}
	
	public void selectOptionFromWithinInSends(String option) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Within Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Dropdown In Sends Panel");
		UIActions.click(fileName,"Within Dropdown In Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Dropdown In Sends Panel");
		
		Select withinDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(withinDropDownList);
		UIActions.selectByText(option);
		
	}
	
	public void selectOptionFromWithinInReceives(String option) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Within Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Dropdown In Receives Panel");
		UIActions.click(fileName,"Within Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Dropdown In Receives Panel");
		
		Select withinDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(withinDropDownList);
		UIActions.selectByText(option);
	}
	public void enterWithinInReceives(String number) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Within Count In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Count In Receives Panel");
		UIActions.click(fileName,"Within Count In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Count In Receives Panel");
		UIActions.clearTextBox(fileName,"Within Count In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Within Count In Receives Panel");
		UIActions.enterValueInTextBox(number);
		
	}
	/**
	 * Click on checkbox of 'but only if' in receives panel
	 * @throws UIAutomationException
	 */
	public void clickCheckboxOfbutOnlyIfInReceivesPanel(String unspecified,String	inDifferentLocations,
			String inDifferentOrganizations, String inDifferentOverallOrganizations, String inTheSameLocation,
			String inTheSameOrganization, String inTheSameOrganizationAndLocation, String inTheSameOverallOrganization,
			String toASupervisor,String toSelf,String toSomeoneElse) throws UIAutomationException{
		
		elementController.requireElementSmart(fileName,"But Only If In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"But Only If In Receives Panel");
		UIActions.click(fileName,"But Only If In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"But Only If In Receives Panel");
		
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
			
		// Assertion: verify instruction textbox should be disabled
		elementController.requireElementSmart(fileName,"Instructions In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Instructions In Receives Panel");
		boolean textboxIsEnabled=UIActions.checkEnable(fileName,"Instructions In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Instructions In Receives Panel");
		if(!textboxIsEnabled){
			throw new UIAutomationException("Reply That radio button can not be clicked.");
		}
	
	}
	/**
	 * Verify by clicking on 'Elements' link 'EOI' window opens
	 * @throws UIAutomationException
	 */
	public void clickElementInSendsPanel() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Element Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Element Sends Panel");
		UIActions.click(fileName,"Element Sends Panel",GlobalVariables.configuration.getAttrSearchList(),"Element Sends Panel");
							
		// Assertion: Verify element window is opened
//		elementController.requireElementSmart(fileName,"Element Of Information Window",GlobalVariables.configuration.getAttrSearchList(),"Element Of Information Window");
//		String headingOfwindowInPage=UIActions.getText(fileName,"Element Of Information Window",GlobalVariables.configuration.getAttrSearchList(), "Element Of Information Window");
		
//		elementController.requireElementSmart(fileName,"Intent Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(),"Intent Dropdown In Receives Panel");
//		String intentDropdown=UIActions.getText(fileName,"Intent Dropdown In Receives Panel",GlobalVariables.configuration.getAttrSearchList(), "Intent Dropdown In Receives Panel");
//		
//		if(!(intentDropdown.contains(unspecified)) &&(intentDropdown.contains(alarm) && (intentDropdown.contains(announcement))&&(intentDropdown.contains(command))&&(intentDropdown.contains(feedback))&&(intentDropdown.contains(report)))){
//			throw new UIAutomationException("Attach dropdown is not present");
//		}
	
	}
	public void goalRemoved()
	{
		
		
		//Shrilekha
//		boolean goalRemoved=false;
//		String xpathOrganizationNameEntered=dataController.getPageDataElements(fileName, "Organization text in goal","Xpath");
//		GlobalVariables.configuration.setWebElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath(xpathOrganizationNameEntered)));
//		if(GlobalVariables.configuration.getWebElement().getText().isEmpty()){
//			goalRemoved=true;
//			System.out.println("Goal is removed");
//		}
//		else {
//			goalRemoved=false;
//			System.out.println("Goal is not removed");
//		}
//		return goalRemoved;
		
		// Priyanka
//		
//		GlobalVariables.configuration.getWebDriver().findElement(By.xpath("/html/body/form/div[5]/div/div[3]/div/div/div/table/tbody/tr"));
//		GlobalVariables.configuration.setWebElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath("/html/body/form/div[5]/div/div[3]/div/div/div/table/tbody/tr")));
//		List<WebElement> tds = GlobalVariables.oElement.findElements(By.tagName("li"));
		
//		GlobalVariables.configuration.setWebElement(GlobalVariables.configuration.getWebDriver().findElement(By.tagName("tr")));
//		List<WebElement> tds=GlobalVariables.configuration.setWebElement(By.tagName("tr"));
	}
	
	public static void getOptions(){
		GlobalVariables.configuration.setWebElement(GlobalVariables.configuration.getWebDriver().findElement(By.xpath("//span[@class='menubar']/span[2]/ul")));
		List <WebElement> list=GlobalVariables.configuration.getWebElement().findElements(By.tagName("li"));
		for (WebElement li: list){			
			if (li.getText().equals("Cut Task")){
						
				break;
			}
			else{
		    }
		}
	}
}	
