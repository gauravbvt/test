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
		elementController.requireElementSmart(fileName, "Sign Out", GlobalVariables.configuration.getAttrSearchList(), "Sign Out Button");
		UIActions.click(fileName, "Sign Out", GlobalVariables.configuration.getAttrSearchList(), "Sign Out Button");
		
		// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Login Page Title", "Title");
       	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	/**
	 * 'sendfeedback' method clicks on 'Send Feedback' button
	 * @throws UIAutomationException 
	 */
	public void sendFeedback() throws UIAutomationException, InterruptedException{
	    elementController.requireElementSmart(fileName, "Send Feedback", GlobalVariables.configuration.getAttrSearchList(), "Send feedback Button");
		UIActions.click(fileName, "Send Feedback", GlobalVariables.configuration.getAttrSearchList(), "Send feedback Button");
	}
	
	/**
	 * 'sendFeedbackOnHelp' method clicks on 'Send Feedback' button
	 * @throws UIAutomationException 
	 */
	public void sendFeedbackOnHelp() throws UIAutomationException{
	    elementController.requireElementSmart(fileName, "Send Feedback On Help", GlobalVariables.configuration.getAttrSearchList(), "Send feedback Button");
		UIActions.click(fileName, "Send Feedback On Help", GlobalVariables.configuration.getAttrSearchList(), "Send feedback Button");
		
		String titleInXML=dataController.getPageDataElements(fileName, "Send Feedback On Help Title", "Title");
		elementController.requireElementSmart(fileName, "Send Feedback On Help", GlobalVariables.configuration.getAttrSearchList(), "Send feedback Button");
		
		String titleInPage=UIActions.getText(fileName, "Send Feedback On Help", GlobalVariables.configuration.getAttrSearchList(), "Send feedback Button");
		
		if(!titleInPage.equals(titleInXML)){
			throw new UIAutomationException("Window with Title '"+titleInXML+"' not found");
		}
	}
	
	/**
	 * 'Sign out Admin' method clicks on 'Sign out' button on 'Plan' page
	 * @throws UIAutomationException 
    */
	public void signOutPlan() throws UIAutomationException{
		try{
			Thread.sleep(6000);	
			}
		catch(Exception e){}
		
		elementController.requireElementSmart(fileName, "Sign Out Plan", GlobalVariables.configuration.getAttrSearchList(), "Sign out plan Button");
		UIActions.click(fileName, "Sign Out Plan", GlobalVariables.configuration.getAttrSearchList(), "Sign out plan Button");
		// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Login Page Title", "Title");
    	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	/**
	 * 'Sign out Admin' method clicks on 'Sign out' button on 'Communities' page
	 * @throws UIAutomationException 
    */
	public void signOutCommunities() throws UIAutomationException{
		try{
			Thread.sleep(6000);	
			}
		catch(Exception e){}
		
		elementController.requireElementSmart(fileName, "Sign Out Communities", GlobalVariables.configuration.getAttrSearchList(), "Sign Out Communities");
		UIActions.click(fileName, "Sign Out Communities", GlobalVariables.configuration.getAttrSearchList(), "Sign Out Communities");
		// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Login Page Title", "Title");
    	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	/**
	 * 'Sign out Admin' method clicks on 'Sign out' button on 'Admin' page
	 * @throws UIAutomationException 
	 */
	public void signOutAdmin() throws UIAutomationException{
		elementController.requireElementSmart(fileName, "Sign Out Admin", GlobalVariables.configuration.getAttrSearchList(), "Sign out admin Button");
	    UIActions.click(fileName, "Sign Out Admin", GlobalVariables.configuration.getAttrSearchList(), "Sign out admin Button");
	    
	    // Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Login Page Title", "Title");
    	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	/**
	 * clicks on 'Help' button in home page
	 * @throws UIAutomationException 
	*/
	public void clickHelpOnHomePage() throws UIAutomationException{
	    elementController.requireElementSmart(fileName, "Help", GlobalVariables.configuration.getAttrSearchList(), "Help Button");
	    UIActions.click(fileName, "Help", GlobalVariables.configuration.getAttrSearchList(), "Help Button");
	}
	/**
	 * clicks on 'Help' button in plan page
	 * @throws UIAutomationException 
	*/
	public void clickHelpOnPlanPage() throws UIAutomationException{
	 
		elementController.requireElementSmart(fileName, "Help On Plan Page", GlobalVariables.configuration.getAttrSearchList(), "Help On Plan Page");
	    UIActions.click(fileName, "Help On Plan Page", GlobalVariables.configuration.getAttrSearchList(), "Help On Plan Page");
	    
	    //Switch to 'Help' Window
	    for (String handle : UIActions.getHandles()) {
	    	UIActions.switchToNewwindow(handle);
	    }
	    
	    // Assertion : Check Title of Page
	    String titleOfHelpPage=dataController.getPageDataElements(fileName, "Help Page Title", "Title");
    	UIActions.waitForTitle(titleOfHelpPage,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
  	}	
	/**
	 * Switch to plan page from any other page
	 * @throws UIAutomationException
	 */
	public void switchToPlanPage() throws UIAutomationException{		
	String titleOfPlanPageInXML=dataController.getPageDataElements(fileName, "Plan Page Title", "Title");
	   String titleOfPlanPageInPage=null;
    	//Switch to 'Main' Window
		for (String handle : UIActions.getHandles()) {
			titleOfPlanPageInPage=UIActions.getTitle();
			if(titleOfPlanPageInPage.contains(titleOfPlanPageInXML)){
				break;
			}
			UIActions.switchToNewwindow(handle);
		}
	}
	
	/**
	 * 'clickHomeImageLink' method clicks on 'Home Image' Link
	 * @throws UIAutomationException 
	 */
	public void clickHomeImageLink(String title) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Home Image Link",GlobalVariables.configuration.getAttrSearchList(), "Home Image Link");
		UIActions.click(fileName,"Home Image Link",GlobalVariables.configuration.getAttrSearchList(), "Home Image Link");
		
		// Assertion : Check Heading of Page
		UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}	
}
