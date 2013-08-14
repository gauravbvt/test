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
	 * @throws UIAutomationException 
	 */
	
	public void enterPlanName(String planName,String ownerName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Plan Name",GlobalVariables.configuration.getAttrSearchList(), "Plan Name");
		xPath=dataController.getPageDataElements(fileName,"Plan Name","Xpath");
		UIActions.click(fileName,"Plan Name",GlobalVariables.configuration.getAttrSearchList(), "Plan Name");
		UIActions.enterValueInTextBox(planName,fileName,"Plan Name",GlobalVariables.configuration.getAttrSearchList(), "Plan Name");
	
		elementController.requireElementSmart(fileName,"ADD",GlobalVariables.configuration.getAttrSearchList(), "ADD");
		UIActions.click(fileName,"ADD",GlobalVariables.configuration.getAttrSearchList(), "ADD");
		
		UIActions.clearTextBox(fileName, "Owner Name", GlobalVariables.configuration.getAttrSearchList(), "Owner Name");
		elementController.requireElementSmart(fileName,"Owner Name",GlobalVariables.configuration.getAttrSearchList(), "Owner Name");
		xPath=dataController.getPageDataElements(fileName,"Owner Name","Xpath");
		UIActions.click(fileName,"Owner Name",GlobalVariables.configuration.getAttrSearchList(), "Owner Name");
		UIActions.enterValueInTextBox(ownerName,fileName,"Owner Name",GlobalVariables.configuration.getAttrSearchList(), "Owner Name");
	}
	
	/**
	 * Enters plan name in 'Plan Owner' text box.
	 * @throws UIAutomationException 
	 */
	//public void enterPlanOwnerName(String ownerName)throws UIAutomationException
	//{
		//UIActions.clearTextBox(fileName, "Owner Name", GlobalVariables.configuration.getAttrSearchList(), "Owner Name");
		//elementController.requireElementSmart(fileName,"Owner Name",GlobalVariables.configuration.getAttrSearchList(), "Owner Name");
		//xPath=dataController.getPageDataElements(fileName,"Owner Name","Xpath");
		//UIActions.click(fileName,"Owner Name",GlobalVariables.configuration.getAttrSearchList(), "Owner Name");
		//UIActions.enterValueInTextBox(ownerName,fileName,"Owner Name",GlobalVariables.configuration.getAttrSearchList(), "Owner Name");
	//}
	
	
	/**
	 * Click the Save Plan button
	 * @throws UIAutomationException 
	 */
	public void clickSavePlanButton() throws UIAutomationException
	{
		elementController.requireElementSmart(fileName,"Save Plan",GlobalVariables.configuration.getAttrSearchList(), "Save Plan");
		UIActions.click(fileName,"Save Plan",GlobalVariables.configuration.getAttrSearchList(), "Save Plan");
	}
	
	/**
	 * Click the Productize Plan button
	 * @throws UIAutomationException 
	 */
	public void clickProductizePlanButton() throws UIAutomationException
	{
		String headingOfWindowInXML=null;
		elementController.requireElementSmart(fileName,"Productize Plan",GlobalVariables.configuration.getAttrSearchList(), "Productize Plan");
		UIActions.click(fileName,"Productize Plan",GlobalVariables.configuration.getAttrSearchList(), "Productize Plan");
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
	 * @throws UIAutomationException
	 */
	public void selectPlan(String planName) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Select Plan",GlobalVariables.configuration.getAttrSearchList(),"Select Plan");
		xPath=dataController.getPageDataElements(fileName,"Select Plan","Xpath");
		Select categoryDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(categoryDropDownList);
		UIActions.selectByText(planName);
	}
	

	/**
	 * Delete the plan
	 * @throws UIAutomationException
	 */
	public void deletePlan(String planName) throws UIAutomationException{
		String headingOfWindowInXML=null;
		selectPlan(planName);
		
		elementController.requireElementSmart(fileName,"Delete Plan",GlobalVariables.configuration.getAttrSearchList(), "Delete Plan");
		UIActions.click(fileName,"Delete Plan",GlobalVariables.configuration.getAttrSearchList(), "Delete Plan");
		headingOfWindowInXML=dataController.getPageDataElements(fileName, "Alert Window Title Of Delete Plan", "Title");
		UIActions.assertAlert(headingOfWindowInXML);
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
	 * Gets username from list of usernname
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
