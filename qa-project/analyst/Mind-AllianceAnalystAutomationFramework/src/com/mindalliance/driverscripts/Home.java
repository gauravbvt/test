package com.mindalliance.driverscripts;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindalliance.configuration.Configuration;
import com.mindalliance.configuration.ElementController;
import com.mindalliance.configuration.GlobalVariables;
import com.mindalliance.configuration.UIAutomationException;

/**
 * Class Home page creates a test suite  
 * @author afour
 *
 */
public class Home {
	public static Test suite() throws UIAutomationException{	  
		GlobalVariables.configuration= Configuration.getConfigurationObject();
		new ElementController();
		TestSuite suite = new TestSuite("Mind-Alliance Automation Framework");	
		
//		suite.addTestSuite(MAV0001_viewLoginPage.class);
				return suite;
	}
}

