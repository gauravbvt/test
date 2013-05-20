package com.mindalliance.pages;

import java.util.Hashtable;
import com.mindalliance.configuration.DataController;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.UIActions;
import com.mindalliance.configuration.UIAutomationException;

/**
 * LoginPage.java class contains all the methods for components Login Page.
 * Example "Login Button"
 * @author Afour
 */

public class LoginPage {

	String fileName = "Login.xml";
	public Hashtable<String, String> Login;
	ElementController elementController= new ElementController();
	DataController dataController=new DataController();
	
	/**
	 * 'Login' method enters Username & Password and login to the Analyst
	 * @param  userName, passWord
	 * @return void
	 */
	
	public void Login(String userName,String passWord) throws UIAutomationException{
		
		// Enter Username
		elementController.requireElementSmart(fileName,"UserName",GlobalVariables.configuration.getAttrSearchList(),"Textfield Username ");
		UIActions.enterValueInTextBox(userName,fileName,"UserName",GlobalVariables.configuration.getAttrSearchList(),"Textfield Username ");
		
		// Enter Password
		elementController.requireElementSmart(fileName,"PassWord",GlobalVariables.configuration.getAttrSearchList(),"Textfield Password");
		UIActions.enterValueInTextBox(passWord,fileName,"PassWord",GlobalVariables.configuration.getAttrSearchList(),"Textfield Password");
		
		// Click on 'Login' 
		elementController.requireElementSmart(fileName,"submitButton",GlobalVariables.configuration.getAttrSearchList(), "Login button");
		UIActions.click(fileName,"submitButton",GlobalVariables.configuration.getAttrSearchList(), "Login button");
		
		}
	
	public String getLoginPageTitle() throws UIAutomationException{
		return UIActions.getText(fileName, "LoginPageTitle", GlobalVariables.configuration.getAttrSearchList(), "Login Page Title");
	}
	
	
}
