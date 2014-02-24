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
	
	/**
	 * Clicks on 'About Me' tab
	 * @throws UIAutomationException
	 */
	public void clickAboutMeTabInSocialPanel() throws UIAutomationException{	
		elementController.requireElementSmart(fileName,"About Me In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "About Me In Social Panel");
		UIActions.click(fileName,"About Me In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "About Me In Social Panel");
		
		elementController.requireElementSmart(fileName,"About Me In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "About Me In Social Panel");
		String tabTextInPage=UIActions.getText(fileName,"About Me In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "About Me In Social Panel");
		String tabTextInXML=dataController.getPageDataElements(fileName,"About Me Tab Text" , "Name");
		if(!tabTextInPage.contains(tabTextInXML)){
			throw new UIAutomationException( "'"+tabTextInXML +"' not found");
		}
	}
	/**
	 * Clicks on 'Contact info' drop down
	 * @throws UIAutomationException
	 */
	public void selectContactInfo(String contact) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Contact Info Dropdown In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Contact Info Dropdown In Social Panel");
		UIActions.click(fileName,"Contact Info Dropdown In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Contact Info Dropdown In Social Panel");
	
		Select fromDropDownList = new Select(GlobalVariables.configuration.getWebElement());
		Configuration.getConfigurationObject().setSelect(fromDropDownList);
		UIActions.selectByTextAndClick(contact);
		UIActions.enterKey(Keys.TAB);
	
	}
	
	/**
	 * Enters contact details in the Contact text field
	 * @param contactInfo
	 * @throws UIAutomationException
	 */
	public void enterContactInfo(String contactInfo) throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Contact Info Textfield In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Contact Info Textfield In Social Panel");
		UIActions.click(fileName,"Contact Info Textfield In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Contact Info Textfield In Social Panel");

		UIActions.enterValueInTextBox(contactInfo,fileName,"Contact Info Textfield In Social Panel",GlobalVariables.configuration.getAttrSearchList(), "Contact Info Textfield In Social Panel");
		UIActions.enterKey(Keys.ENTER);
			
			try{
				Thread.sleep(2000);
			}
			catch(Exception e){}
	}


	/**
	 * Click on Collaboration Template Editor link
	 * @throws UIAutomationException
	 */
	public void clickCollaborationPlanEditorLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Collaboration Template Editor Link",GlobalVariables.configuration.getAttrSearchList(), "Collaboration Template Editor Link");
		UIActions.click(fileName,"Collaboration Template Editor Link",GlobalVariables.configuration.getAttrSearchList(), "Collaboration Template Editor Link");

		// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Collaboration Plan Editor Page Title", "Title");
       	UIActions.waitForTitle(title,Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds")));
		UIActions.getText(fileName, "CollaborationPlanEditorPageTitle", GlobalVariables.configuration.getAttrSearchList(), "Collaboration Plan Editor Page Title");
	}
	/**
	 * Click on Template Issues link
	 * @throws UIAutomationException
	 */
	public void clickTemplateIssuesLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Template Issues Link",GlobalVariables.configuration.getAttrSearchList(), "Template Issues Link");
		UIActions.click(fileName,"Template Issues Link",GlobalVariables.configuration.getAttrSearchList(), "Template Issues Link");

		// Assertion : Check the text present on Issues Summary Report page
		elementController.requireElementSmart(fileName,"Issue Summary Report Page Title",GlobalVariables.configuration.getAttrSearchList(), "Issue Summary Report Page Title");
		String title1=UIActions.getText(fileName,"Issue Summary Report Page Title",GlobalVariables.configuration.getAttrSearchList(), "Issue Summary Report Page Title");
		String textInTitle=dataController.getPageDataElements(fileName,"Issue Summary Report Page Title Name" , "Name");
		if(!title1.contains(textInTitle)){
			throw new UIAutomationException( "'"+textInTitle +"' not found");
	}}
	/**
	 * Click on Template link on Issue Summary Report Page
	 * @throws UIAutomationException
	 */
	public String clickTemplateLinkOnIssueSummaryReportPage() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Template Link On Issue Summary Report Page",GlobalVariables.configuration.getAttrSearchList(), "Template Link On Issue Summary Report Page");
		UIActions.click(fileName,"Template Link On Issue Summary Report Page",GlobalVariables.configuration.getAttrSearchList(), "Template Link On Issue Summary Report Page");

       	return UIActions.getText(fileName, "CollaborationTemplatePageTitle", GlobalVariables.configuration.getAttrSearchList(), "Collaboration Template Page Title");
	}
	
	/**
	 * Click on Surveys link
	 * @throws UIAutomationException
	 */
	public void clickSurveysLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Surveys Link",GlobalVariables.configuration.getAttrSearchList(), "Surveys Link");
		UIActions.click(fileName,"Surveys Link",GlobalVariables.configuration.getAttrSearchList(), "Surveys Link");
        
		//Assertion: Check the text present on Surveys Page
		elementController.requireElementSmart(fileName,"Surveys Page Title",GlobalVariables.configuration.getAttrSearchList(), "Surveys Page Title");
		String surveysPageTitle=UIActions.getText(fileName,"Surveys Page Title",GlobalVariables.configuration.getAttrSearchList(), "Surveys Page Title");
		String textInTitle=dataController.getPageDataElements(fileName,"Surveys Page Title Text" , "Name");
		if(!surveysPageTitle.contains(textInTitle)){
			throw new UIAutomationException( "'"+textInTitle +"' not found");}
	
	}

	/**
	 * Click on Feedback And Replies link
	 * @throws UIAutomationException
	 */
	public void clickFeedbackAndRepliesLink() throws UIAutomationException{
		elementController.requireElementSmart(fileName,"Feedback And Replies Link",GlobalVariables.configuration.getAttrSearchList(), "Feedback And Replies Link");
		UIActions.click(fileName,"Feedback And Replies Link",GlobalVariables.configuration.getAttrSearchList(), "Feedback And Replies Link");

		//Assertion: Check the text present on Surveys Page
		elementController.requireElementSmart(fileName,"Feedback And Replies Page Title",GlobalVariables.configuration.getAttrSearchList(), "Feedback And Replies Page Title");
		String feedbackPageTitle=UIActions.getText(fileName,"Feedback And Replies Page Title",GlobalVariables.configuration.getAttrSearchList(), "Feedback And Replies Page Title");
		String textInTitle=dataController.getPageDataElements(fileName,"Feedback And Replies Page Title Text" , "Name");
		if(!feedbackPageTitle.contains(textInTitle)){
		throw new UIAutomationException( "'"+textInTitle +"' not found");}
			
	}

	
	
}
