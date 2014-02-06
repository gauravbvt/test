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
 * HomePage.java class contains all the methods for components on Home Page.
 * Example "Click on Channels Settings"
 * @author Afour
 *
 */

public class HomePage {
	String fileName = "HomePage.xml";
	ElementController elementController= new ElementController();
	DataController dataController=new DataController();
	
	
	/**
	 * Welcome message verification
	 * @throws UIAutomationException 
	 */
	
	public void verifyHomePage() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Welcome To Channels Title",GlobalVariables.configuration.getAttrSearchList(), "Welcome To Channels Title");
		String headingInPage=UIActions.getText(fileName,"Welcome To Channels Title",GlobalVariables.configuration.getAttrSearchList(), "Welcome To Channels Title");
    	
		if(!headingInPage.contains("Welcome To Channels")){
    		throw new UIAutomationException("Welcome To Channels page not found.");
    	}
	}
	

	/**
	 * 'verifyCollaborationTemplateLink' method verifies that link is present on home page
	 * @throws UIAutomationException 
	 */
	public void verifyCollaborationTemplateLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Domain Plan Editor",GlobalVariables.configuration.getAttrSearchList(), "Domain Plan Editor link");
				
		// Assertion : Check Link is present on page
		String linkText=dataController.getPageDataElements(fileName, "Collaboration Templates Link", "LinkText");
		UIActions.waitForLinkText(linkText,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
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
		elementController.requireElementSmart(fileName,"Channels Admin",GlobalVariables.configuration.getAttrSearchList(), "Channels Admin");
		UIActions.click(fileName,"Channels Admin",GlobalVariables.configuration.getAttrSearchList(), "Channels Admin");
		
		// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Admin Page Title", "Title");
    	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}	
	
	/**
	 * 'verifyCollaborationTemplateLink' method verifies that link is present on home page
	 * @throws UIAutomationException 
	 */
	public void verifyChannelsAdminLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Channels Admin",GlobalVariables.configuration.getAttrSearchList(), "Channels Admin Link");
				
		// Assertion : Check Link is present on page
		String linkText=dataController.getPageDataElements(fileName, "Channels Admin Link", "LinkText");
		System.out.println(linkText);
		UIActions.waitForLinkText(linkText,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	/**
	 * 'clickCommunitiesLink' method clicks on Communities Link
	 * @throws UIAutomationException 
	 */
	public void clickCommunitiesLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Communities or Collaboration Plans",GlobalVariables.configuration.getAttrSearchList(), "Communities or Collaboration Plans");
		UIActions.click(fileName,"Communities or Collaboration Plans",GlobalVariables.configuration.getAttrSearchList(), "Communities or Collaboration Plans");
				
		// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Collaboration Plans Page Title", "Title");
       	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	/**
	 * 'verifyCommunitiesLink' method verifies that link is present on home page
	 * @throws UIAutomationException 
	 */
	public void verifyCommunitiesLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Collaboration Plans",GlobalVariables.configuration.getAttrSearchList(), "Collaboration Plans");
				
		// Assertion : Check Link is present on page
		String linkText=dataController.getPageDataElements(fileName, "Collaboration Plans Link", "LinkText");
       	UIActions.waitForLinkText(linkText,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
	}
	
	/**
	 * 'verifyApplyButtonInAboutMe' method verifies that Apply button is present on home page
	 * @throws UIAutomationException 
	 */
	public void verifyApplyButtonInAboutMe() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Apply Button In About Me",GlobalVariables.configuration.getAttrSearchList(), "Apply Button In About Me");
				
		// Assertion : Check Button is present on page
		String linkText=dataController.getPageDataElements(fileName, "Apply Button Name In About Me", "LinkText");
		System.out.println(linkText);
//       	UIActions.waitForLinkText(linkText,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
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
  
		if(!headingInPage.contains(heading)){
    		throw new UIAutomationException("Issues Summary Report page not found.");
    	}
	}	
	
	/**
	 * Enters Email address in About Me tab
	 * @throws UIAutomationException
	 */
	public void enterEmailInAboutMe(String email) throws UIAutomationException{
	
		elementController.requireElementSmart(fileName,"Email Text Field In About Me",GlobalVariables.configuration.getAttrSearchList(), "Email Address");
		UIActions.click(fileName,"Email Text Field In About Me",GlobalVariables.configuration.getAttrSearchList(), "Email Address");
		UIActions.clearTextBox(fileName,"Email Text Field In About Me",GlobalVariables.configuration.getAttrSearchList(), "Email Address");
		UIActions.enterValueInTextBox(email,fileName,"Email Text Field In About Me",GlobalVariables.configuration.getAttrSearchList(), "Email Address");
		UIActions.enterKey(Keys.TAB);
	
	}
	
	/**
	 * Enters Name in Name text field in the About Me tab
	 * @throws UIAutomationException
	 */
	public void enterNameInAboutMe(String name) throws UIAutomationException{
	
		elementController.requireElementSmart(fileName,"Name Text Field In About Me",GlobalVariables.configuration.getAttrSearchList(), "User Name");
		UIActions.click(fileName,"Name Text Field In About Me",GlobalVariables.configuration.getAttrSearchList(), "User Name");
		UIActions.clearTextBox(fileName,"Name Text Field In About Me",GlobalVariables.configuration.getAttrSearchList(), "User Name");
		UIActions.enterValueInTextBox(name,fileName,"Name Text Field In About Me",GlobalVariables.configuration.getAttrSearchList(), "User Name");
		UIActions.enterKey(Keys.TAB);
	
	}
	
	/**
	 * Clicks on 'Apply' button in About Me tab
	 * @throws UIAutomationException
	 */
	public void clickApplyButtonInAboutMe() throws UIAutomationException{	
		elementController.requireElementSmart(fileName,"Apply Button In About Me",GlobalVariables.configuration.getAttrSearchList(), "Apply Button In About Me");
		UIActions.click(fileName,"Apply Button In About Me",GlobalVariables.configuration.getAttrSearchList(), "Apply Button In About Me");
		try
		{
			Thread.sleep(3500);
		}
		catch(Exception e){}
		elementController.requireElementSmart(fileName,"No Changes Applied Notification",GlobalVariables.configuration.getAttrSearchList(), "No Changes Applied Notification");
		String tabTextInPage=UIActions.getText(fileName,"No Changes Applied Notification",GlobalVariables.configuration.getAttrSearchList(), "No Changes Applied Notification");
		String tabTextInXML=dataController.getPageDataElements(fileName,"No Changes Notification Name" , "Name");
		if(!tabTextInPage.contains(tabTextInXML)){
			throw new UIAutomationException( "'"+tabTextInXML +"' not found");
		}

		
		}
		
	/**
	 * Enters password in Current Password text field in the My Password tab
	 * @param currentPassword
	 * @throws UIAutomationException
	 */
	public void enterPasswordInCurrentPassword(String currentPassword) throws UIAutomationException{
		
		elementController.requireElementSmart(fileName,"Current Password Text Field",GlobalVariables.configuration.getAttrSearchList(), "Current Password");
		UIActions.click(fileName,"Current Password Text Field",GlobalVariables.configuration.getAttrSearchList(), "Current Password");
		UIActions.clearTextBox(fileName,"Current Password Text Field",GlobalVariables.configuration.getAttrSearchList(), "Current Password");
		UIActions.enterValueInTextBox(currentPassword,fileName,"Current Password Text Field",GlobalVariables.configuration.getAttrSearchList(), "Current Password");
		UIActions.enterKey(Keys.TAB);
	
	}
	
	/**
	 * Enters password in New Password text field in the My Password tab
	 * @param newPassword
	 * @throws UIAutomationException
	 */
	public void enterPasswordInNewPassword(String newPassword) throws UIAutomationException{
	
		elementController.requireElementSmart(fileName,"New Password Text Field",GlobalVariables.configuration.getAttrSearchList(), "New Password");
		UIActions.click(fileName,"New Password Text Field",GlobalVariables.configuration.getAttrSearchList(), "New Password");
		UIActions.clearTextBox(fileName,"New Password Text Field",GlobalVariables.configuration.getAttrSearchList(), "New Password");
		UIActions.enterValueInTextBox(newPassword,fileName,"New Password Text Field",GlobalVariables.configuration.getAttrSearchList(), "New Password");
		UIActions.enterKey(Keys.TAB);
	
	}
	
	/**
	 * Enters password in Confirm Password text field in the My Password tab
	 * @param confirmPassword
	 * @throws UIAutomationException
	 */
	public void enterPasswordInConfirmPassword(String confirmPassword) throws UIAutomationException{
	
		elementController.requireElementSmart(fileName,"Confirm Password Text Field",GlobalVariables.configuration.getAttrSearchList(), "Confirm Password");
		UIActions.click(fileName,"Confirm Password Text Field",GlobalVariables.configuration.getAttrSearchList(), "Confirm Password");
		UIActions.clearTextBox(fileName,"Confirm Password Text Field",GlobalVariables.configuration.getAttrSearchList(), "Confirm Password");
		UIActions.enterValueInTextBox(confirmPassword,fileName,"Confirm Password Text Field",GlobalVariables.configuration.getAttrSearchList(), "Confirm Password");
		UIActions.enterKey(Keys.TAB);
	
	}
	
	/**
	 * Clicks on 'Apply' button in My Password tab
	 * @throws UIAutomationException
	 */
	public void clickApplyButtonInMyPassword() throws UIAutomationException{	
		elementController.requireElementSmart(fileName,"Apply Button In My Password",GlobalVariables.configuration.getAttrSearchList(), "Apply Button In My Password");
		UIActions.click(fileName,"Apply Button In My Password",GlobalVariables.configuration.getAttrSearchList(), "Apply Button In My Password");
		UIActions.getText(fileName, "Password Changed Notification",GlobalVariables.configuration.getAttrSearchList() , "Password Changed Notification");
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
	 * Clicks on 'Messages' tab
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
		UIActions.getText(fileName, "Messages Tab Text", GlobalVariables.configuration.getAttrSearchList(), "Messages Tab Text");
	
	}
	
	/**
	 * Enters message in text area in the Messages tab 
	 * @param message
	 * @throws UIAutomationException
	 */
	public void enterMessageInMessagesTextArea(String message) throws UIAutomationException{
	
		elementController.requireElementSmart(fileName,"Message Text Area In Messages Tab",GlobalVariables.configuration.getAttrSearchList(), "Message Text Area");
		UIActions.click(fileName,"Message Text Area In Messages Tab",GlobalVariables.configuration.getAttrSearchList(), "Message Text Area");
		UIActions.clearTextBox(fileName,"Message Text Area In Messages Tab",GlobalVariables.configuration.getAttrSearchList(), "Message Text Area");
		UIActions.enterValueInTextBox(message,fileName,"Message Text Area In Messages Tab",GlobalVariables.configuration.getAttrSearchList(), "Message Text Area");
		UIActions.enterKey(Keys.TAB);
		
		if(message.equals(""))
		{
			UIActions.getText(message, "Blank Message Notification", GlobalVariables.configuration.getAttrSearchList(),"Blank Message Notification");
		}
	
	}
	/**
	 * Clicks on 'Send' button in Messages tab
	 * @param message
	 * @throws UIAutomationException
	 */
	public void clickSendButtonInMessagesTab(String message) throws UIAutomationException{	
		elementController.requireElementSmart(fileName,"Send Button In Messages Tab",GlobalVariables.configuration.getAttrSearchList(), "Send Button In Messages Tab");
		UIActions.click(fileName,"Send Button In Messages Tab",GlobalVariables.configuration.getAttrSearchList(), "Send Button In Messages Tab");
		try
		{
			Thread.sleep(3500);
		}
		catch(Exception e){}
		
		//If the message is blank , check whether "Message not sent" notification appears
		if(message.equals(" "))
		{
		// Check by clicking on 'Send' notification appears
		elementController.requireElementSmart(fileName,"Message Not Sent Notification",GlobalVariables.configuration.getAttrSearchList(), "Message Not Sent Notification");
		String linkTextInPage=UIActions.getText(fileName,"Message Not Sent Notification",GlobalVariables.configuration.getAttrSearchList(), "Message Not Sent Notification");
		String linkTextInXML=dataController.getPageDataElements(fileName,"Message Not Sent Notification Name" , "Name");
		if(!linkTextInPage.contains(linkTextInXML)){
		throw new UIAutomationException( "'"+linkTextInXML +"' not found");
		}			
			
		}
		else
		{
		// Check by clicking on 'Send' notification appears
		elementController.requireElementSmart(fileName,"Message Sent Notification",GlobalVariables.configuration.getAttrSearchList(), "Message Sent Notification");
		String linkTextInPage=UIActions.getText(fileName,"Message Sent Notification",GlobalVariables.configuration.getAttrSearchList(), "Message Sent Notification");
		String linkTextInXML=dataController.getPageDataElements(fileName,"Message Sent Notification Name" , "Name");
		if(!linkTextInPage.contains(linkTextInXML)){
		throw new UIAutomationException( "'"+linkTextInXML +"' not found");
			}
		}
	
		}
	
	/**
	 * Clicks on 'Reset' button in Messages tab
	 * @throws UIAutomationException
	 */
	public void clickResetButtonInMessagesTab() throws UIAutomationException{	
		elementController.requireElementSmart(fileName,"Reset Button In Messages Tab",GlobalVariables.configuration.getAttrSearchList(), "Reset Button In Messages Tab");
		UIActions.click(fileName,"Reset Button In Messages Tab",GlobalVariables.configuration.getAttrSearchList(), "Reset Button In Messages Tab");
		}
	
	
	/**
	 * 'selectFromToDropDown' method clicks on To drop down
	 * @param To
	 * @throws UIAutomationException 
	 */
	public void selectFromToDropDown(String To) throws UIAutomationException{
		// Select an option from to dropdown
		elementController.requireElementSmart(fileName,"To Drop Down In Messages Tab",GlobalVariables.configuration.getAttrSearchList(),"To Drop Down In Messages Tab");
		UIActions.click(fileName,"To Drop Down In Messages Tab",GlobalVariables.configuration.getAttrSearchList(),"To Drop Down In Messages Tab");
				
		Select planDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(planDropDownList);
		
		UIActions.selectByTextAndClick(To);
		
	}
	
	
	/**
	 * Clicks on 'Password' tab
	 * @throws UIAutomationException
	 */
	public void clickMyPasswordTab() throws UIAutomationException{
	
		elementController.requireElementSmart(fileName,"My Password In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "My Password In Social Panel");
		UIActions.click(fileName,"My Password In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "My Password In Social Panel");
		
		UIActions.getText(fileName, "My Password Tab Text", GlobalVariables.configuration.getAttrSearchList(), "My Password Tab Text");
	
	}
	
	/**
	 * Clicks on 'show sent' and 'show received'
	 * @throws UIAutomationException
	 */
	public void clickShowSent() throws UIAutomationException{		
		elementController.requireElementSmart(fileName,"Show Sent In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Sent In Social Panel");
		UIActions.click(fileName,"Show Sent In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Sent In Social Panel");
			
		/*	// Check by clicking on 'show sent' it changes to 'show received'
			elementController.requireElementSmart(fileName,"Show Sent In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Sent In Social Panel");
			String linkTextInPage=UIActions.getText(fileName,"Show Sent In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Sent In Social Panel");
			String linkTextInXML=dataController.getPageDataElements(fileName,"Show Received Text" , "Name");
			if(!linkTextInPage.contains(linkTextInXML)){
				throw new UIAutomationException( "'"+linkTextInXML +"' not found");
			}*/
	}
	
	/**
	 * Clicks on 'show received'
	 * @throws UIAutomationException
	 */
	public void clickShowReceived() throws UIAutomationException{		
		elementController.requireElementSmart(fileName,"Show Received In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Received In Social Panel");
		UIActions.click(fileName,"Show Received In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Received In Social Panel");
			
		/*	// Check by clicking on 'show sent' it changes to 'show received'
			elementController.requireElementSmart(fileName,"Show Sent In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Sent In Social Panel");
			String linkTextInPage=UIActions.getText(fileName,"Show Sent In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show Sent In Social Panel");
			String linkTextInXML=dataController.getPageDataElements(fileName,"Show Received Text" , "Name");
			if(!linkTextInPage.contains(linkTextInXML)){
				throw new UIAutomationException( "'"+linkTextInXML +"' not found");
			}*/
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
		String linkTextInXML=dataController.getPageDataElements(fileName,"Show All Messages Name" , "Title");
		if(!linkTextInPage.contains(linkTextInXML)){
			throw new UIAutomationException( "'"+linkTextInXML +"' not found");
		}
		
		// Click on 'show All messages'
		elementController.requireElementSmart(fileName,"Show All Messages In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show All Messages In Social Panel");
		UIActions.click(fileName,"Show All Messages In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Show All Messages In Social Panel");
		try{
			Thread.sleep(5000);
		}
		catch(Exception e ){}
		
		// Check by clicking on 'show all messages' it changes to 'hide broadcasts'
		elementController.requireElementSmart(fileName,"Hide Broadcasts In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Hide Broadcasts In Social Panel");
		String showLinkTextInPage=UIActions.getText(fileName,"Hide Broadcasts In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Hide Broadcasts In Social Panel");
		String showLinkTextInXML=dataController.getPageDataElements(fileName,"Hide Broadcasts Name" , "Title");
		if(!showLinkTextInPage.contains(showLinkTextInXML)){
			throw new UIAutomationException( "'"+showLinkTextInXML +"' not found");
		}
	}
	
}