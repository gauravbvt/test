package com.mindalliance.pages;
import org.openqa.selenium.support.ui.Select;

import com.mindalliance.configuration.Configuration;
import com.mindalliance.configuration.DataController;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.UIActions;
import com.mindalliance.configuration.UIAutomationException;

/**
 * HomePage.java class contains all the methods for components on Home Page.
 * Example "Click on Channels Setting"
 * @author Afour
 *
 */

public class HomePage {
	String fileName = "HomePage.xml";
	ElementController elementController= new ElementController();
	DataController dataController=new DataController();
	
	/**
	 * 'clickDomainPlans' method clicks on Domain Plans Link
	 * @throws UIAutomationException 
	 */
	public void clickDomainPlanEditor() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Domain Plan Editor",GlobalVariables.configuration.getAttrSearchList(), "Domain Plan Editor link");
		UIActions.click(fileName,"Domain Plan Editor",GlobalVariables.configuration.getAttrSearchList(), "Domain Plan Editor link");
				
		// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Domain Plan Editor Page Title", "Title");
       	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	/**
	 * 'clickCollaborationPlanLink' method clicks on 'Collaboration Plan' Link
	 * @throws UIAutomationException 
	*/
	public void clickCollaborationPlanLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Collaboration Plan",GlobalVariables.configuration.getAttrSearchList(), "Collaboration Plan link");
		UIActions.click(fileName,"Collaboration Plan",GlobalVariables.configuration.getAttrSearchList(), "Collaboration Plan link");
				
		/*// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Collaboration Plans Page Title", "Title");
       	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));*/
		UIActions.getText(fileName, "CollaborationPlansPageTitle", GlobalVariables.configuration.getAttrSearchList(), "Collaboration Plans Page Title");
	}
	
	
	/**
	 * 'clickChannelsAdminLink' method clicks on 'Channels Settings' Link
	 * @throws UIAutomationException 
	 */
	public void clickChannelsAdminLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Channels Admin Link",GlobalVariables.configuration.getAttrSearchList(), "Channels Admin Link");
		UIActions.click(fileName,"Channels Admin Link",GlobalVariables.configuration.getAttrSearchList(), "Channels Admin Link");
		
		// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Admin Page Title", "Title");
    	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}	
	
	/**
	 * 'clickCommunitiesLink' method clicks on Communities Link
	 * @throws UIAutomationException 
	 */
	public void clickCommunitiesLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Communities",GlobalVariables.configuration.getAttrSearchList(), "Communities");
		UIActions.click(fileName,"Communities",GlobalVariables.configuration.getAttrSearchList(), "Communities");
				
		
		// Assertion : Check Title of Page
    	//String title=dataController.getPageDataElements(fileName, "Communities Page Title", "Title");
       	//UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	/**
	 * 'selectPlanFromDropDown' method clicks on Plan drop down
	 * @throws UIAutomationException 
	 */
	public void selectPlanFromDropDown(String plan) throws UIAutomationException{
		// Select Organization
		
		elementController.requireElementSmart(fileName,"Plan dropdown",GlobalVariables.configuration.getAttrSearchList(),"Plan dropdown");
		UIActions.click(fileName,"Plan dropdown",GlobalVariables.configuration.getAttrSearchList(),"Plan dropdown");
				
		Select planDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(planDropDownList);
		
		UIActions.selectByTextAndClick(plan);
		
		// Assertion : Check Title of Page
    	//String title=dataController.getPageDataElements(fileName, "Communities Page Title", "Title");
       	//UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	/**
	 * 'clickIssuesReportLink' method clicks on 'Issues Report' Link
	 * @throws UIAutomationException 
	 */
	public void clickIssuesReportLink(String heading) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Issues Report Link",GlobalVariables.configuration.getAttrSearchList(), "Issues Report Link");
		UIActions.click(fileName,"Issues Report Link",GlobalVariables.configuration.getAttrSearchList(), "Issues Report Link");
		// Assertion : Check Heading of Page
		elementController.requireElementSmart(fileName,"Issues Report Heading",GlobalVariables.configuration.getAttrSearchList(), "Issues Report Heading");
		String headingInPage=UIActions.getText(fileName,"Issues Report Heading",GlobalVariables.configuration.getAttrSearchList(), "Issues Report Heading");
    	System.out.println(heading);
    	System.out.println(headingInPage);
		if(!headingInPage.contains(heading)){
    		throw new UIAutomationException("Issues Summary Report page not found.");
    	}
	}	
	
	
	/**
	 * Clicks on 'Message' tab
	 * @throws UIAutomationException
	 */
	public void clickMessagesTabInSocialPanel() throws UIAutomationException{
	
		elementController.requireElementSmart(fileName,"Messages In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Messages In Social Panel");
		UIActions.click(fileName,"Messages In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Messages In Social Panel");
		elementController.requireElementSmart(fileName,"Messages In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Messages In Social Panel");
		String tabTextInPage=UIActions.getText(fileName,"Messages In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Messages In Social Panel");
		String tabTextInXML=dataController.getPageDataElements(fileName,"Messages Tab Text" , "Name");
		if(!tabTextInPage.contains(tabTextInXML)){
			throw new UIAutomationException( "'"+tabTextInXML +"' not found");
		}
	
	}
	
	/**
	 * Clicks on 'Calendar' tab
	 * @throws UIAutomationException
	 */
	public void clickCalendarTabInSocialPanel() throws UIAutomationException{	
		elementController.requireElementSmart(fileName,"Calender In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Calender In Social Panel");
		UIActions.click(fileName,"Calender In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Calender In Social Panel");
		
		elementController.requireElementSmart(fileName,"Calender In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Calender In Social Panel");
		String tabTextInPage=UIActions.getText(fileName,"Calender In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Calender In Social Panel");
		String tabTextInXML=dataController.getPageDataElements(fileName,"Calendar Tab Text" , "Name");
		if(!tabTextInPage.contains(tabTextInXML)){
			throw new UIAutomationException( "'"+tabTextInXML +"' not found");
		}
	
	}
	
	/**
	 * Clicks on 'show sent' and 'show received'
	 * @throws UIAutomationException
	 */
	public void clickShowSent() throws UIAutomationException{		
		elementController.requireElementSmart(fileName,"Show Sent In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Sent In Social Panel");
		UIActions.click(fileName,"Show Sent In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Sent In Social Panel");
			
			// Check by clicking on 'show sent' it changes to 'show received'
			elementController.requireElementSmart(fileName,"Show Sent In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Sent In Social Panel");
			String linkTextInPage=UIActions.getText(fileName,"Show Sent In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Sent In Social Panel");
			String linkTextInXML=dataController.getPageDataElements(fileName,"Show Received Text" , "Name");
			if(!linkTextInPage.contains(linkTextInXML)){
				throw new UIAutomationException( "'"+linkTextInXML +"' not found");
			}
	}
	
	/**
	 * Clicks on 'hide broadcast' under 'Social Panel'
	 * @throws UIAutomationException
	 */
	public void clickHideBroadcastsInSocialPanel() throws UIAutomationException{		
		elementController.requireElementSmart(fileName,"Hide Broadcasts In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Hide Broadcasts In Social Panel");
		UIActions.click(fileName,"Hide Broadcasts In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Hide Broadcasts In Social Panel");
		
		// Check by clicking on 'hide broadcasts' it changes to 'show all messages'
		elementController.requireElementSmart(fileName,"Show All Messages In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show All Messages In Social Panel");
		String linkTextInPage=UIActions.getText(fileName,"Show All Messages In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show All Messages In Social Panel");
		String linkTextInXML=dataController.getPageDataElements(fileName,"Show All Messages Name" , "Name");
		if(!linkTextInPage.contains(linkTextInXML)){
			throw new UIAutomationException( "'"+linkTextInXML +"' not found");
		}
		
		
		// Click on 'show All messages'
		elementController.requireElementSmart(fileName,"Show All Messages In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show All Messages In Social Panel");
		UIActions.click(fileName,"Show All Messages In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show All Messages In Social Panel");
		try{
			Thread.sleep(1000);
		}
		catch(Exception e ){}
		
		// Check by clicking on 'show all messages' it changes to 'hide broadcasts'
		elementController.requireElementSmart(fileName,"Hide Broadcasts In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Hide Broadcasts In Social Panel");
		String showLinkTextInPage=UIActions.getText(fileName,"Hide Broadcasts In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Hide Broadcasts In Social Panel");
		String showLinkTextInXML=dataController.getPageDataElements(fileName,"Hide Broadcasts Name" , "Name");
		if(!showLinkTextInPage.contains(showLinkTextInXML)){
			throw new UIAutomationException( "'"+showLinkTextInXML +"' not found");
		}
	}
	
}