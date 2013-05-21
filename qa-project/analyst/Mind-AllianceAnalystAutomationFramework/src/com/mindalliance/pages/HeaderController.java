package com.mindalliance.pages;

import com.mindalliance.configuration.DataController;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.UIActions;
import com.mindalliance.configuration.UIAutomationException;

/**
 * HeaderController.java class contains all the methods for components with common header.
 * Example "Send Feedback button","Help button", "Sign out button"
 * @author afour
 *
 */

public class HeaderController {
	String fileName = "HeaderControls.xml";
	ElementController elementController= new ElementController();
	DataController dataController=new DataController();
	
	/**
	 * 'signOut' method clicks on 'Sign out' button
	 * @throws UIAutomationException 
     */
	
	public void signOut() throws UIAutomationException{
		elementController.requireElementSmart(fileName, "LogOut", GlobalVariables.configuration.getAttrSearchList(), "Log out Button");
		UIActions.click(fileName, "LogOut", GlobalVariables.configuration.getAttrSearchList(), "Logout Button");
		
		// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Login Page Title", "Title");
       	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));

	}

	public String getLoginPageTitle() throws UIAutomationException{
		return UIActions.getText(fileName, "LoginPageTitle", GlobalVariables.configuration.getAttrSearchList(), "Login Page Title");
	}
}
