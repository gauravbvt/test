package com.mindalliance.driverscripts;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindalliance.automationbvt.*;
import com.mindalliance.configuration.UIAutomationException;

public class AutomationBVTHome {
	public static Test suite() throws UIAutomationException{
		TestSuite suite = new TestSuite("Mind-Alliance Automation Framework");	
		
		suite.addTestSuite(MABVT0001_VerifyBrowsersVersion.class);
		suite.addTestSuite(MABVT0002_VerifyOSInstalled.class);
		suite.addTestSuite(MABVT0003_VerifyJavaVersion.class);
		suite.addTestSuite(MABVT0004_VerifyXMLFiles.class);
		suite.addTestSuite(MABVT0005_VerifyBrowsersInstalled.class);
		suite.addTestSuite(MABVT0006_ApplicationRunning.class);
		return suite;
	
	}
}
