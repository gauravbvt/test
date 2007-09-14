package com.mindalliance.ui.test
{
	import com.mindalliance.channels.business.application.ProjectCreateTest;
	import com.mindalliance.channels.business.scenario.AgentCreateTest;
	import com.mindalliance.channels.business.scenario.EventCreateTest;
	import com.mindalliance.channels.business.scenario.TaskCreateTest;
	
	import flexunit.framework.Test;
	import flexunit.framework.TestSuite;
	
	public class AllTests
	{
		public static function suite() : Test
		{
			var testSuite : TestSuite = new TestSuite();
			
            testSuite.addTest( ProjectCreateTest.suite());
            testSuite.addTest( AgentCreateTest.suite());
            testSuite.addTest( TaskCreateTest.suite());
            testSuite.addTest( EventCreateTest.suite());
			
		
			return testSuite;
		}
	}
}