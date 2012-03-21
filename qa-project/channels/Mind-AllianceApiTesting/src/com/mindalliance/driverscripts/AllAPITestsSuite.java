package com.mindalliance.driverscripts;

import com.mindalliance.testscripts.REST001_ISPPlans;
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
		return suite;
	}
}
