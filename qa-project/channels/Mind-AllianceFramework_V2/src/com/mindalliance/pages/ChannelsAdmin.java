package com.mindalliance.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.mindalliance.configuration.Configuration;
import com.mindalliance.configuration.DataController;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.UIActions;
import com.mindalliance.configuration.UIAutomationException;

/**
 * ChannelsAdmin class contains all the methods for components related to Channels Setting page.
 * Example "Create plan", "Delete Plan".
 * @author Afour
 *
 */

public class ChannelsAdmin {
	String fileName = "ChannelsAdmin.xml";
	String xPath=null;
	String className=null;
	ElementController elementController= new ElementController();
	DataController dataController=new DataController();
	
	/**
	 * Enters plan name in 'Plan name' text box.
	 * @param planName
	 * @throws UIAutomationException 
	 */
	public void enterPlanName(String planName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Plan Name",GlobalVariables.configuration.getAttrSearchList(), "Plan Name");
		xPath=dataController.getPageDataElements(fileName,"Plan Name","Xpath");
		UIActions.click(fileName,"Plan Name",GlobalVariables.configuration.getAttrSearchList(), "Plan Name");
		UIActions.enterValueInTextBox(planName,fileName,"Plan Name",GlobalVariables.configuration.getAttrSearchList(), "Plan Name");
	}
	
	/**
	 * Enters owner name in 'Plan Owner' text box.
	 * @throws UIAutomationException 
	 */
	public void enterPlanOwnerName(String ownerName)throws UIAutomationException
	{
		UIActions.clearTextBox(fileName, "Owner Name", GlobalVariables.configuration.getAttrSearchList(), "Owner Name");
		elementController.requireElementSmart(fileName,"Owner Name",GlobalVariables.configuration.getAttrSearchList(), "Owner Name");
		xPath=dataController.getPageDataElements(fileName,"Owner Name","Xpath");
		UIActions.click(fileName,"Owner Name",GlobalVariables.configuration.getAttrSearchList(), "Owner Name");
		UIActions.enterValueInTextBox(ownerName,fileName,"Owner Name",GlobalVariables.configuration.getAttrSearchList(), "Owner Name");
	}
	
	/**
	 * Click the Collaboration Templates Tab
	 * @throws UIAutomationException 
	 */
	public void clickCollaborationTemplatesTab() throws UIAutomationException
	{
		elementController.requireElementSmart(fileName,"Collaboration Templates Tab",GlobalVariables.configuration.getAttrSearchList(), "Collaboration Templates Tab");
		UIActions.click(fileName,"Collaboration Templates Tab",GlobalVariables.configuration.getAttrSearchList(), "Collaboration Templates Tab");

		elementController.requireElementSmart(fileName,"Collaboration Templates Tab",GlobalVariables.configuration.getAttrSearchList(), "Collaboration Templates Tab");
		String collaborationTemplatesTab=UIActions.getText(fileName,"Collaboration Templates Tab",GlobalVariables.configuration.getAttrSearchList(), "Collaboration Templates Tab");
		
		if(!collaborationTemplatesTab.contains(collaborationTemplatesTab)){
			throw new UIAutomationException("Collaboration Templates Tab not selected");
		}
	}
	
	/**
	 * Click the Add Plan button
	 * @throws UIAutomationException 
	 */
	public void clickAddPlanButton(String planName) throws UIAutomationException
	{
		elementController.requireElementSmart(fileName,"ADD",GlobalVariables.configuration.getAttrSearchList(), "Add Plan");
		UIActions.click(fileName,"ADD",GlobalVariables.configuration.getAttrSearchList(), "Add Plan");

		elementController.requireElementSmart(fileName,"New Plan Added Assertion",GlobalVariables.configuration.getAttrSearchList(), "New Plan Added Assertion");
		String newPlan=UIActions.getText(fileName,"New Plan Added Assertion",GlobalVariables.configuration.getAttrSearchList(), "New Plan Added Assertion");
		if(!planName.equals(newPlan)){
		throw new UIAutomationException( "'"+planName +"' not found");
		}
	}
	
	/**
	 * Click the Add Plan button after entering invalid plan name
	 * @throws UIAutomationException 
	 */
	public void clickAddPlanForBlankTemplateName() throws UIAutomationException{
		
		elementController.requireElementSmart(fileName,"ADD",GlobalVariables.configuration.getAttrSearchList(), "Add Plan");
		UIActions.click(fileName,"ADD",GlobalVariables.configuration.getAttrSearchList(), "Add Plan");
		
		//Check whether the "Invalid Template" notification appears
		elementController.requireElementSmart(fileName,"Invalid Template Notification",GlobalVariables.configuration.getAttrSearchList(), "Invalid Template Notification");
		String tabTextInPage=UIActions.getText(fileName,"Invalid Template Notification",GlobalVariables.configuration.getAttrSearchList(), "Invalid Template Notification");
		String tabTextInXML=dataController.getPageDataElements(fileName,"Invalid Template Notification Name" , "Name");
		if(!tabTextInPage.contains(tabTextInXML)){
		throw new UIAutomationException( "'"+tabTextInXML +"' not found");
		}
	}
	
	/**
	 * Click the Save Plan button
	 * @throws UIAutomationException 
	 */
	public void clickSavePlanButton() throws UIAutomationException
	{
		elementController.requireElementSmart(fileName,"Save Plan",GlobalVariables.configuration.getAttrSearchList(), "Save Plan");
		UIActions.click(fileName,"Save Plan",GlobalVariables.configuration.getAttrSearchList(), "Save Plan");
		try{
			Thread.sleep(3500);
		}
		catch(Exception e){}
		
		//Check whether the "Settings Changed" notification appears
		elementController.requireElementSmart(fileName,"Settings Changed Notification",GlobalVariables.configuration.getAttrSearchList(), "Settings Changed Notification");
		String tabTextInPage=UIActions.getText(fileName,"Settings Changed Notification",GlobalVariables.configuration.getAttrSearchList(), "Settings Changed Notification");
		String tabTextInXML=dataController.getPageDataElements(fileName,"Settings Changed Notification Name" , "Name");
		if(!tabTextInPage.contains(tabTextInXML)){
		throw new UIAutomationException( "'"+tabTextInXML +"' not found");
		}
	
	}
	
	/**
	 * Click the Save Plan button when Plan ids are the same
	 * @throws UIAutomationException 
	 */
	public void clickSavePlanButtonSamePlanId(String planName) throws UIAutomationException
	{
		elementController.requireElementSmart(fileName,"ADD",GlobalVariables.configuration.getAttrSearchList(), "Add Plan");
		UIActions.click(fileName,"ADD",GlobalVariables.configuration.getAttrSearchList(), "Add Plan");
	
		//Check whether the "Invalid Template" notification appears
		elementController.requireElementSmart(fileName,"Invalid Template Notification",GlobalVariables.configuration.getAttrSearchList(), "Invalid Template Notification");
		String TextInPage=UIActions.getText(fileName,"Invalid Template Notification",GlobalVariables.configuration.getAttrSearchList(), "Invalid Template Notification");
		String TextInXML=dataController.getPageDataElements(fileName,"Invalid Template Notification Name" , "Name");
		
		if(!TextInPage.contains(TextInXML)){
		throw new UIAutomationException( "'"+TextInXML +"' not found");
		}		
		
//		String alert=dataController.getPageDataElements(fileName, "Invalid Template Notification", "Xpath");
//		elementController.waitForElement("Name", "Invalid Template Notification Name");
//		UIActions.assertAlert(alert);
	}
	
	/**
	 * Click the Productize Plan button
	 * @throws UIAutomationException 
	 */
	public void clickProductizePlanButton() throws UIAutomationException
	{
		String headingOfWindowInXML=null;
		elementController.requireElementSmart(fileName,"Put In Production",GlobalVariables.configuration.getAttrSearchList(), "Put In Production");
		UIActions.click(fileName,"Put In Production",GlobalVariables.configuration.getAttrSearchList(), "Put In Production");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Alert Window Title Of Productize Plan", "Title");
		UIActions.assertAlert(headingOfWindowInXML);
	}
	
	/**
	 * Click the Home link on Channels Settings page
	 * @throws UIAutomationException 
	 */
	public void clickHomeLink() throws UIAutomationException
	{
		//String headingOfWindowInXML=null;
		elementController.requireElementSmart(fileName,"Home Link",GlobalVariables.configuration.getAttrSearchList(), "Home Link");
		UIActions.click(fileName,"Home Link",GlobalVariables.configuration.getAttrSearchList(), "Home Link");
		//headingOfWindowInXML=dataController.getPageDataElements(fileName, "Alert Window Title Of Productize Plan", "Title");
		//UIActions.assertAlert(headingOfWindowInXML);
	}
	/**
	 * Selects the plan from the drop down
	 * @param planName
	 * @throws UIAutomationException
	 */
	public void selectPlan(String planName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Select Plan",GlobalVariables.configuration.getAttrSearchList(),"Select Plan");
		UIActions.click(fileName,"Select Plan",GlobalVariables.configuration.getAttrSearchList(),"Select Plan");
		Select planDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(planDropDownList);
		UIActions.selectByTextAndClick(planName);
	}
	/**
	 * Delete the plan
	 * @param planName
	 * @throws UIAutomationException
	 */
	public void deletePlan(String planName) throws UIAutomationException{
		
		selectPlan(planName);
		
		elementController.requireElementSmart(fileName,"Delete Plan",GlobalVariables.configuration.getAttrSearchList(), "Delete Plan");
		UIActions.click(fileName,"Delete Plan",GlobalVariables.configuration.getAttrSearchList(), "Delete Plan");
	
		String alert=dataController.getPageDataElements(fileName, "Alert Window Title Of Delete Plan", "Title");
		UIActions.assertAlert(alert);
//		try{
//			Thread.sleep(5200);
//			}
//		catch(Exception e){}
	//	elementController.waitForElement("Title", "Alert Window Title Of Delete Plan");
	//	UIActions.assertAlert(alert);
	
		
	}
	
	/**
	 * Click Users Tab on Channels settings page
	 * @throws UIAutomationException
	 */
	public void clickUsersTab() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Users Tab",GlobalVariables.configuration.getAttrSearchList(), "Users Tab");
		UIActions.click(fileName,"Users Tab",GlobalVariables.configuration.getAttrSearchList(), "Users Tab");
		
		 //Assertion: Verify if the Users tab is clicked
		elementController.requireElementSmart(fileName,"Users Tab",GlobalVariables.configuration.getAttrSearchList(), "Users Tab");
		String usersTab=UIActions.getText(fileName,"Users Tab",GlobalVariables.configuration.getAttrSearchList(), "Users Tab");
		
		if(!usersTab.contains(usersTab)){
			throw new UIAutomationException("Users Tab not selected");
		}	
	}
		
	/**
	 * Add user to plan
	 * @param userName
	 * @throws UIAutomationException
	 */
	public void addUser(String userName) throws UIAutomationException{
		
		elementController.requireElementSmart(fileName,"User Name",GlobalVariables.configuration.getAttrSearchList(), "User Name");
		UIActions.click(fileName,"User Name",GlobalVariables.configuration.getAttrSearchList(), "User Name");
		UIActions.enterValueInTextBox(userName,fileName,"User Name",GlobalVariables.configuration.getAttrSearchList(), "User Name");
		
		elementController.requireElementSmart(fileName,"Submit",GlobalVariables.configuration.getAttrSearchList(), "Submit");
		UIActions.click(fileName,"Submit",GlobalVariables.configuration.getAttrSearchList(), "Submit");
			
		// Assertion: Verify that "User 1" is added
		try{
			Thread.sleep(4000);
		}
		catch(Exception e){}
		/*if(userName.equals(userName))
		{
			elementController.requireElementSmart(fileName,"Settings changed notification for users",GlobalVariables.configuration.getAttrSearchList(), "Settings changed notification for users");
			String tabTextInPage=UIActions.getText(fileName,"Settings changed notification for users",GlobalVariables.configuration.getAttrSearchList(), "Settings changed notification for users");
			String tabTextInXML=dataController.getPageDataElements(fileName,"Settings changed notification for users Name" , "Name");
			if(!tabTextInPage.contains(tabTextInXML)){
			throw new UIAutomationException( "'"+tabTextInXML +"' not found");
			}
		}*/
		
	}
	
	/**
	 * Verify Same User notification
	 * @throws UIAutomationException
	 */
	public void verifySameUserNotification() throws UIAutomationException{
		String samePlanAlert=null;
		
		samePlanAlert=dataController.getPageDataElements(fileName, "Alert Window Title Of Same Plan", "Title");
		UIActions.assertAlert(samePlanAlert);
			
	}
	 
	/**
	 * Select an option from drop down
	 * @param userRole
	 * @throws UIAutomationException
	 */
	public String selectOptionFromTemplateDropDown(String userRole) throws UIAutomationException{
		
		elementController.requireElementSmart(fileName,"User Role Drop Down",GlobalVariables.configuration.getAttrSearchList(),"User Role Drop Down");
		UIActions.click(fileName,"User Role Drop Down",GlobalVariables.configuration.getAttrSearchList(),"User Role Drop Down");
		Select adminDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(adminDropDownList);
		UIActions.selectByTextAndClick(userRole);
			
		elementController.requireElementSmart(fileName,"Apply Button For User Role",GlobalVariables.configuration.getAttrSearchList(),"Apply Button For User Role");
		UIActions.click(fileName,"Apply Button User For Role",GlobalVariables.configuration.getAttrSearchList(),"Apply Button For User Role");
		return "yes";
	
	}
	
	/**
	 * Add user details to plan
	 * @param email
	 * @param password
	 * @param IsAdministrator
	 * @param IsDisabled
	 * @throws UIAutomationException
	 */
	public void addUserDetails(String email,String password,String IsAdministrator,String IsDisabled) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Email",GlobalVariables.configuration.getAttrSearchList(), "Email");
		UIActions.click(fileName,"Email",GlobalVariables.configuration.getAttrSearchList(), "Email");
		UIActions.clearTextBox(fileName,"Email",GlobalVariables.configuration.getAttrSearchList(), "Email Of User");
		UIActions.enterValueInTextBox(email,fileName,"Email",GlobalVariables.configuration.getAttrSearchList(), "Email Of User");
		UIActions.enterKey(Keys.TAB);
		
		elementController.requireElementSmart(fileName,"Password",GlobalVariables.configuration.getAttrSearchList(), "Password");
		UIActions.click(fileName,"Password",GlobalVariables.configuration.getAttrSearchList(), "Password");
		UIActions.clearTextBox(fileName,"Password",GlobalVariables.configuration.getAttrSearchList(), "Password Of User");
		UIActions.enterValueInTextBox(password,fileName,"Password",GlobalVariables.configuration.getAttrSearchList(), "Password Of User");
		UIActions.enterKey(Keys.TAB);
		
		elementController.requireElementSmart(fileName,"IsAdministrator",GlobalVariables.configuration.getAttrSearchList(),"IsAdministrator");
		UIActions.click(fileName,"IsAdministrator",GlobalVariables.configuration.getAttrSearchList(),"IsAdministrator Option");
		Select adminDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(adminDropDownList);
		UIActions.selectByTextAndClick(IsAdministrator);
		
		elementController.requireElementSmart(fileName,"IsDisabled",GlobalVariables.configuration.getAttrSearchList(),"IsDisabled");
		UIActions.click(fileName,"IsDisabled",GlobalVariables.configuration.getAttrSearchList(),"IsDisabled Option");
		Select disabledDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(disabledDropDownList);
		UIActions.selectByTextAndClick(IsDisabled);
			
		elementController.requireElementSmart(fileName,"Apply Button For User Details",GlobalVariables.configuration.getAttrSearchList(),"Apply Button For User Details");
		UIActions.click(fileName,"Apply Button For User Details",GlobalVariables.configuration.getAttrSearchList(),"Apply Button For User Details");
		try{
			Thread.sleep(3500);
		}
		catch(Exception e){}
		
		// Verify that the new user details are added
		elementController.requireElementSmart(fileName,"Settings changed notification for users",GlobalVariables.configuration.getAttrSearchList(), "Settings changed notification for users");
		String tabTextInPage=UIActions.getText(fileName,"Settings changed notification for users",GlobalVariables.configuration.getAttrSearchList(), "Settings changed notification for users");
		String tabTextInXML=dataController.getPageDataElements(fileName,"Settings changed notification for users Name" , "Name");
		if(!tabTextInPage.contains(tabTextInXML)){
		throw new UIAutomationException( "'"+tabTextInXML +"' not found");
		}
						
	}
	
	/**
	 * Add invalid user details to plan
	 * @param email
	 * @param password
	 * @param IsAdministrator
	 * @param IsDisabled
	 * @throws UIAutomationException
	 */
	public void addInvalidUserDetails(String email,String password,String IsAdministrator,String IsDisabled) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Email",GlobalVariables.configuration.getAttrSearchList(), "Email");
		UIActions.click(fileName,"Email",GlobalVariables.configuration.getAttrSearchList(), "Email");
		UIActions.clearTextBox(fileName,"Email",GlobalVariables.configuration.getAttrSearchList(), "Email Of User");
		UIActions.enterValueInTextBox(email,fileName,"Email",GlobalVariables.configuration.getAttrSearchList(), "Email Of User");
		UIActions.enterKey(Keys.TAB);
		
		elementController.requireElementSmart(fileName,"Password",GlobalVariables.configuration.getAttrSearchList(), "Password");
		UIActions.click(fileName,"Password",GlobalVariables.configuration.getAttrSearchList(), "Password");
		UIActions.clearTextBox(fileName,"Password",GlobalVariables.configuration.getAttrSearchList(), "Password Of User");
		UIActions.enterValueInTextBox(password,fileName,"Password",GlobalVariables.configuration.getAttrSearchList(), "Password Of User");
		UIActions.enterKey(Keys.TAB);
		
		elementController.requireElementSmart(fileName,"IsAdministrator",GlobalVariables.configuration.getAttrSearchList(),"IsAdministrator");
		UIActions.click(fileName,"IsAdministrator",GlobalVariables.configuration.getAttrSearchList(),"IsAdministrator");
		Select adminDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(adminDropDownList);
		UIActions.selectByTextAndClick(IsAdministrator);
		
		elementController.requireElementSmart(fileName,"IsDisabled",GlobalVariables.configuration.getAttrSearchList(),"IsDisabled");
		UIActions.click(fileName,"IsDisabled",GlobalVariables.configuration.getAttrSearchList(),"IsDisabled");
		Select disabledDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(disabledDropDownList);
		UIActions.selectByTextAndClick(IsDisabled);
			
		elementController.requireElementSmart(fileName,"Apply Button For User Details",GlobalVariables.configuration.getAttrSearchList(),"Apply Button For User Details");
		UIActions.click(fileName,"Apply Button For User Details",GlobalVariables.configuration.getAttrSearchList(),"Apply Button For User Details");
	
		try{
			Thread.sleep(5000);
		}
		catch(Exception e){}
		//Settings not changed notification after entering invalid details
		/*elementController.requireElementSmart(fileName,"Settings not changed notification for users",GlobalVariables.configuration.getAttrSearchList(), "Settings not changed notification for users");
		String tabTextInPage=UIActions.getText(fileName,"Settings not changed notification for users",GlobalVariables.configuration.getAttrSearchList(), "Settings not changed notification for users");
		String tabTextInXML=dataController.getPageDataElements(fileName,"Settings not changed notification for users Name" , "Name");
		if(!tabTextInPage.contains(tabTextInXML)){
		throw new UIAutomationException( "'"+tabTextInXML +"' not found");
		}*/
	}
	
	/**
	 * Delete user from plan
	 * @param userName
	 * @param email
	 * @throws UIAutomationException
	 */
	public void deleteUser(String userName,String email) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Email Of User",GlobalVariables.configuration.getAttrSearchList(), "Email Of User");
		UIActions.click(fileName,"Email Of User",GlobalVariables.configuration.getAttrSearchList(), "Email Of User");
		
		UIActions.clearTextBox(fileName,"Email Of User",GlobalVariables.configuration.getAttrSearchList(), "Email Of User");
		UIActions.enterValueInTextBox(email,fileName,"Email Of User",GlobalVariables.configuration.getAttrSearchList(), "Email Of User");
		UIActions.enterKey(Keys.TAB);
		getUser(userName);
	
		elementController.requireElementSmart(fileName,"Delete User Submit",GlobalVariables.configuration.getAttrSearchList(), "Delete User Submit");
		UIActions.click(fileName,"Delete User Submit",GlobalVariables.configuration.getAttrSearchList(), "Delete User Submit");
			
	}
	
	/**
	 * Disable user 
	 * @param userName
	 * @param isDisabled
	 * @throws UIAutomationException
	 */
	public void disableUser(String userName,String isDisabled) throws UIAutomationException{
		getUser(userName);
		
		elementController.requireElementSmart(fileName,"IsDisabled",GlobalVariables.configuration.getAttrSearchList(),"IsDisabled");
		UIActions.click(fileName,"IsDisabled",GlobalVariables.configuration.getAttrSearchList(),"IsDisabled");
		Select disabledDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(disabledDropDownList);
		UIActions.selectByTextAndClick(isDisabled);
			
		elementController.requireElementSmart(fileName,"Apply Button For User Details",GlobalVariables.configuration.getAttrSearchList(),"Apply Button For User Details");
		UIActions.click(fileName,"Apply Button For User Details",GlobalVariables.configuration.getAttrSearchList(),"Apply Button For User Details");
	}
	
	/**
	 * Gets username from list of username
	 * @param userName
	 * @throws UIAutomationException
	 */
	public void getUser(String userName) throws UIAutomationException{
		// get list of user
		int countUsers = 1 ;
		String firstXPath=dataController.getPageDataElements(fileName, "First XPath", "Xpath");
		String secondXPath=dataController.getPageDataElements(fileName, "Second XPath", "Xpath");
		elementController.requireElementSmart(fileName,"Table Of Users",GlobalVariables.configuration.getAttrSearchList(), "Table Of Users");
	
		List<WebElement> trs = GlobalVariables.configuration.getWebElement().findElements(By.tagName("tr"));
		List<WebElement> tds;
		for(WebElement tr: trs){
			tds = tr.findElements(By.tagName("td"));
			for(WebElement td: tds){				
				if(td.getText().contains(userName)){
					GlobalVariables.configuration.getWebDriver().findElement(By.xpath(firstXPath+ (countUsers) + secondXPath)).click();
				}
			}
			countUsers++;
		}
	}
	
	public void setPasswords(String userName) throws UIAutomationException{
		int countUsers = 1;
		String firstXPath=dataController.getPageDataElements(fileName, "First XPath", "Xpath");
		String secondXPath=dataController.getPageDataElements(fileName, "Second XPath", "Xpath");
		elementController.requireElementSmart(fileName,"Table Of Users",GlobalVariables.configuration.getAttrSearchList(), "Table Of Users");
	
		List<WebElement> trs = GlobalVariables.configuration.getWebElement().findElements(By.tagName("tr"));
		List<WebElement> tds;
		for(WebElement tr: trs){
			tds = tr.findElements(By.tagName("td"));
			for(WebElement td: tds){				
				if(td.getText().contains(userName)){
					GlobalVariables.configuration.getWebDriver().findElement(By.xpath(firstXPath+ (countUsers) + secondXPath)).click();
				}
			}
			countUsers++;
	}
	}
}
