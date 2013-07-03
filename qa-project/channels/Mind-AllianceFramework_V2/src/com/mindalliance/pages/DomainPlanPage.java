package com.mindalliance.pages;

import com.mindalliance.configuration.DataController;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.UIActions;
import com.mindalliance.configuration.UIAutomationException;

/**
 * DomainPlanPage.java class contains all the methods for components on Domain Plan Page.
 * Example ""
 * @author Afour
 *
 */
public class DomainPlanPage {

	String fileName = "DomainPlanPage.xml";
	ElementController elementController= new ElementController();
	DataController dataController=new DataController();
	
	/**
	 * 'clickCommunitiesLink' method clicks on 'Collaboration Plan' Link
	 * @throws UIAutomationException 
	*/
	public void clickCommunitiesLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Communities Link",GlobalVariables.configuration.getAttrSearchList(), "Communities Link");
		UIActions.click(fileName,"Communities Link",GlobalVariables.configuration.getAttrSearchList(), "Communities Linkk");
				
		// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Communities Page Title", "Title");
       	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	/**
	 * 'clickDomainPlans' method clicks on Domain Plans Link
	 * @throws UIAutomationException 
	 */
	public void clickDomainPlans() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Domain Plans",GlobalVariables.configuration.getAttrSearchList(), "Domain Plans link");
		UIActions.click(fileName,"Domain Plans",GlobalVariables.configuration.getAttrSearchList(), "Domain Plans link");
				
		// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Domain Plan Editor Page Title", "Title");
       	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	
}
