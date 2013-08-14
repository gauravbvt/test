package com.mindalliance.pages;

import com.mindalliance.configuration.DataController;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.UIActions;
import com.mindalliance.configuration.UIAutomationException;


/**
 * Communities Page class contains all the methods for components related to Communities page.
 * @author Afour
 *
 */
public class CommunitiesPage {
	
	String fileName = "CommunitiesPage.xml";
	ElementController elementController= new ElementController();
	DataController dataController=new DataController();
	
	
	/**
	 * 'clickStartItButton' method clicks on 'Start It' button
	 * @throws UIAutomationException 
	*/
	public void clickStartItButton() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Start It",GlobalVariables.configuration.getAttrSearchList(), "Start It");
		UIActions.click(fileName,"Start It",GlobalVariables.configuration.getAttrSearchList(), "Start It");
				
		// Assertion : Check Title of Page
    	//String title=dataController.getPageDataElements(fileName, "Communities Page Title", "Title");
       	//UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	
	/**
	 * 'clickGoButton' method clicks on 'Go' button
	 * @throws UIAutomationException 
	*/
	public void clickGoButton() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Go",GlobalVariables.configuration.getAttrSearchList(), "Go");
		UIActions.click(fileName,"Go",GlobalVariables.configuration.getAttrSearchList(), "Go");
				
		 //Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Unnamed", "cTitle");
       	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	/**
	 * 'clickPlanParticipationLink' method clicks on 'Go' button
	 * @throws UIAutomationException 
	*/
	public void clickPlanParticipationLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Plan Participation Link",GlobalVariables.configuration.getAttrSearchList(), "Plan Participation Link");
		UIActions.click(fileName,"Plan Participation Link",GlobalVariables.configuration.getAttrSearchList(), "Plan Participation Link");
				
		// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Community participation", "PlanTitle");
       	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
}
