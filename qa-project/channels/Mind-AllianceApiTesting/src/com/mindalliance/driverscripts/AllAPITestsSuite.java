package com.mindalliance.driverscripts;

import com.mindalliance.testscripts.REST001_ISPPlans;
import com.mindalliance.testscripts.REST002_ISPProcedures;
import com.mindalliance.testscripts.REST003_ISPAgents;
import com.mindalliance.testscripts.REST004_ISPIssues;
import com.mindalliance.testscripts.REST005_ISPScope;
import com.mindalliance.testscripts.REST006_ISPRelease;
import com.mindalliance.testscripts.REST007_ISPVersion;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * A TestSuite is a Composite of Tests. It runs a collection of test cases.
 * This class contains test suite in which test scripts can be added. The test scripts are executed as per the test suite.
 * @author AFourTech
 */
public class AllAPITestsSuite extends TestCase{
	public static Test suite() {
		// Create a test suite which includes all the API tests
		TestSuite suite = new TestSuite("AllAPITests");
		suite.addTest(new TestSuite(REST001_ISPPlans.class));
		suite.addTest(new TestSuite(REST002_ISPProcedures.class));
//		suite.addTest(new TestSuite(REST003_ISPAgents.class));
//		suite.addTest(new TestSuite(REST004_ISPIssues.class));
//		suite.addTest(new TestSuite(REST005_ISPScope.class));
		suite.addTest(new TestSuite(REST006_ISPRelease.class));
		suite.addTest(new TestSuite(REST007_ISPVersion.class));
		return suite;
	}
}
