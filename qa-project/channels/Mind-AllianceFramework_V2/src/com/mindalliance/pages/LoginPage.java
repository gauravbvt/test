package com.mindalliance.pages;

import java.util.Hashtable;

import com.mindalliance.configuration.DataController;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.UIActions;
import com.mindalliance.configuration.UIAutomationException;
/**
 * LoginPage.java class contains all the methods for components Login Page.
 * Example "Login Button", "Forgot Password link"
 * @author Afour
 *
 */
public class LoginPage {
	String fileName = "LoginPage.xml";
	public Hashtable<String, String> LoginPage;
	ElementController elementController= new ElementController();
	DataController dataController=new DataController();
	
	/**
	 * 'Login' method enters Username & Paasword and login to the Channels
	 * @param  userName, passWord
	 * @return void
	 */
	public void Login(String userName,String passWord) throws UIAutomationException{
		int timeout=0;
	
		// Enter Username
		elementController.requireElementSmart(fileName,"username",GlobalVariables.configuration.getAttrSearchList(),"Textfield Username ");
		UIActions.enterValueInTextBox(userName,fileName,"username",GlobalVariables.configuration.getAttrSearchList(),"Textfield Username ");
		
		// Enter Password
		elementController.requireElementSmart(fileName,"password",GlobalVariables.configuration.getAttrSearchList(),"Textfield Password");
		UIActions.enterValueInTextBox(passWord,fileName,"password",GlobalVariables.configuration.getAttrSearchList(),"Textfield Password");
		
		// Click on 'Sign In' 
		elementController.requireElementSmart(fileName,"submitButton",GlobalVariables.configuration.getAttrSearchList(), "Login button");
		UIActions.click(fileName,"submitButton",GlobalVariables.configuration.getAttrSearchList(), "Login button");
			
		// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Home Page Title", "Title");
    	timeout=Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds"));
    	UIActions.waitForTitle(title,timeout);
	}
	
	/**
	 *  Can't Access Your Account link method navigates to Password reset page 
	 * @throws UIAutomationException
	 */
	public void clickCantAccessYourAccount() throws UIAutomationException{
		int timeout=0;
		elementController.requireElementSmart(fileName,"Cant' Access Your Account Link",GlobalVariables.configuration.getAttrSearchList(), "Cant' Access Your Account Link");
		UIActions.click(fileName,"Cant' Access Your Account Link",GlobalVariables.configuration.getAttrSearchList(), "Cant' Access Your Account Link");
		
		// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Channels Login Page", "Title");
    	timeout=Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds"));
    	UIActions.waitForTitle(title,timeout);
	}
	
	
	public void clickBackToLoginLink() throws UIAutomationException{
		int timeout=0;
		elementController.requireElementSmart(fileName,"Back to Login Link",GlobalVariables.configuration.getAttrSearchList(), "Back to Login Link");
		UIActions.click(fileName,"Back to Login Link",GlobalVariables.configuration.getAttrSearchList(), "Back to Login Link");
		
		// Assertion : Check Title of Page
    	String title=dataController.getPageDataElements(fileName, "Channels Login Page", "Title");
    	timeout=Integer.parseInt(GlobalVariables.configuration.getConfigData().get("TimeOutForFindingElementSeconds"));
    	UIActions.waitForTitle(title,timeout);
	}
	
		
	
	}