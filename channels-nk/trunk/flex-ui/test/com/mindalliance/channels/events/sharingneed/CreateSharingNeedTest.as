package com.mindalliance.channels.events.sharingneed
{
	import flexunit.framework.TestCase;
	
	public class CreateSharingNeedTest extends TestCase
	{
		
		public function CreateSharingNeedTest(method : String) {
          super(method);    
        }
		public static function suite():TestSuite
        {
            var ts:TestSuite = new TestSuite();
 
            ts.addTest( new CreateSharingNeedTest( "create" ) );
            return ts;
        }
	}
}