package com.mindalliance.pages;

import java.util.Hashtable;

import com.mindalliance.configuration.DataController;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.UIActions;
import com.mindalliance.configuration.UIAutomationException;

public class ProjectsPage {
	String fileName = "ProjectsPage.xml";
	public Hashtable<String, String> ProjectsPage;
	ElementController elementController= new ElementController();
	DataController dataController=new DataController();
	
	/**
	 * 'clickAddProjectButton' method clicks on 'Add project' button
	 * @throws UIAutomationException 
	*/
	
public void clickAddProjectButton() throws UIAutomationException{
		
	elementController.requireElementSmart(fileName,"AddProjectButton",GlobalVariables.configuration.getAttrSearchList(), "Add Project button");
	UIActions.click(fileName,"AddProjectButton",GlobalVariables.configuration.getAttrSearchList(), "Add Project button");
	}	
	
// Enter Project name and description
	public void EnterProjectNameDescription(String projectName,String description) throws UIAutomationException{
	
		// Enter Project name
		elementController.requireElementSmart(fileName,"ProjectName",GlobalVariables.configuration.getAttrSearchList(),"Textfield ProjectName");
		UIActions.enterValueInTextBox(projectName,fileName,"ProjectName",GlobalVariables.configuration.getAttrSearchList(),"Textfield ProjectName ");
				
		// Enter description
		elementController.requireElementSmart(fileName,"Description",GlobalVariables.configuration.getAttrSearchList(),"Textfield Description");
		UIActions.enterValueInTextBox(description,fileName,"Description",GlobalVariables.configuration.getAttrSearchList(),"Textfield Description");
						

	}

public void clickSaveButton() throws UIAutomationException{
		
	elementController.requireElementSmart(fileName,"saveButton",GlobalVariables.configuration.getAttrSearchList(), "Save button");
	UIActions.click(fileName,"saveButton",GlobalVariables.configuration.getAttrSearchList(), "Save button");
	}	

public void clickDocumentsButton() throws UIAutomationException{
	
	elementController.requireElementSmart(fileName,"documentsButton",GlobalVariables.configuration.getAttrSearchList(), "Documents button");
	UIActions.click(fileName,"documentsButton",GlobalVariables.configuration.getAttrSearchList(), "Documents button");
	}	
	
	}