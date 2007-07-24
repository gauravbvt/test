package com.mindalliance.ui.test
{
	import flexunit.framework.TestSuite;
	import flexunit.framework.Test;
	
	public class AllTests
	{
		public static function suite() : Test
		{
			var testSuite : TestSuite = new TestSuite();
			
			testSuite.addTest( new TestSuite( TestDummy ) );
		
			return testSuite;
		}
	}
}