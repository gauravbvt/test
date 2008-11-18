package com.mindalliance.channels;

import junit.framework.TestCase;
import org.apache.wicket.util.tester.WicketTester;

/**
 * Simple test using the WicketTester
 */
public class TestScenarioPage extends TestCase
{
	private WicketTester tester;

	@Override
	public void setUp()
	{
		tester = new WicketTester(new Channels());
	}

	public void testRenderMyPage()
	{
		//start and render the test page
		tester.startPage(ScenarioPage.class);

		//assert rendered page class
		tester.assertRenderedPage(ScenarioPage.class);

		//assert rendered label component
		//tester.assertLabel("message", "If you see this message wicket is properly configured and running");

    }
}
