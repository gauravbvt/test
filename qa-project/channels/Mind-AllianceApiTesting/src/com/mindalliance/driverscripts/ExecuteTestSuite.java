package com.mindalliance.driverscripts;

import junit.swingui.TestRunner;
/**
 * This class generated an UI of JUNIT for all test scripts and run each of the testMethods.
 * It will also automatically track and report the successes and failures recorded.
 * @author AFourTech
 */
public class ExecuteTestSuite {
	public static void main(String[] args) {
		new TestRunner();
		TestRunner.run(AllAPITestsSuite.class);
	}
}
