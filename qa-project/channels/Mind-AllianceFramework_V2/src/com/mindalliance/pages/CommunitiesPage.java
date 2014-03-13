package com.mindalliance.pages;

import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.Select;

import com.mindalliance.configuration.Configuration;
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
	public void clickStartItButton(String planName) throws UIAutomationException{
		dropDownNewCommunity(planName);
		elementController.requireElementSmart(fileName,"Start It",GlobalVariables.configuration.getAttrSearchList(), "Start It");
		UIActions.click(fileName,"Start It",GlobalVariables.configuration.getAttrSearchList(), "Start It");
				
		// Assertion : Check Title of Page
    	//String title=dataController.getPageDataElements(fileName, "Communities Page Title", "Title");
       	//UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	
	public void dropDownNewCommunity(String planName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Drop Down Start It",GlobalVariables.configuration.getAttrSearchList(), "Drop Down Start It");
		UIActions.click(fileName,"Drop Down Start It",GlobalVariables.configuration.getAttrSearchList(), "Drop Down Start It");
		UIActions.enterKey(Keys.TAB);

		Select fromDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(fromDropDownList);
		UIActions.selectByText(planName);
		UIActions.enterKey(Keys.TAB);
		
	}
	
	/**
	 * 'clickGoButton' method clicks on 'Go' button
	 * @throws UIAutomationException 
	*/
	public void clickGoButton() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Go",GlobalVariables.configuration.getAttrSearchList(), "Go");
		UIActions.click(fileName,"Go",GlobalVariables.configuration.getAttrSearchList(), "Go");
				
       	// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Communities Page Title", "Title");
       	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	/**
	 * 'clickPlanParticipationLink' method clicks on 'Plan Participation' link
	 * @throws UIAutomationException 
	*/
	public void clickPlanParticipationLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Plan Participation Link",GlobalVariables.configuration.getAttrSearchList(), "Plan Participation Link");
		UIActions.click(fileName,"Plan Participation Link",GlobalVariables.configuration.getAttrSearchList(), "Plan Participation Link");
				
		// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Community participation", "PlanTitle");
       	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	/**
	 * 'clickCollaborationRequirementsLink' method clicks on 'Collaboration requirements' link
	 * @throws UIAutomationException 
	*/
	public void clickCollaborationRequirementsLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Collaboration Requirements Link",GlobalVariables.configuration.getAttrSearchList(), "Collaboration Requirements Link");
		UIActions.click(fileName,"Collaboration Requirements Link",GlobalVariables.configuration.getAttrSearchList(), "Collaboration Requirements Link");
				
//		// Assertion : Check Title of Page
//    	String title=dataController.getPageDataElements(fileName, "Requirements Page Title", "PlanTitle");
//       	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	/**
	 * 'clickNewRequirementButton' method clicks on 'New' button on the 'Collaboration Requirements' page
	 * @throws UIAutomationException 
	*/
	public void clickNewRequirementButton() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"New Requirement Button",GlobalVariables.configuration.getAttrSearchList(), "New Requirement Button");
		UIActions.click(fileName,"New Requirement Button",GlobalVariables.configuration.getAttrSearchList(), "New Requirement Button");
				
	//Assertion : Check Title of Page
    //	String title=dataController.getPageDataElements(fileName, "Unnamed", "cTitle");
    //   	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	/**
	 * 'clickNewRequirementButton' method clicks on 'New' button on the 'Collaboration Requirements' page
	 * @throws UIAutomationException 
	*/
	public void clickRemoveRequirementButton() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Remove Requirement Button",GlobalVariables.configuration.getAttrSearchList(), "Remove Requirement Button");
		UIActions.click(fileName,"Remove Requirement Button",GlobalVariables.configuration.getAttrSearchList(), "Remove Requirement Button");
				
	//Assertion : Check Title of Page
    //	String title=dataController.getPageDataElements(fileName, "Unnamed", "cTitle");
    //   	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
}
