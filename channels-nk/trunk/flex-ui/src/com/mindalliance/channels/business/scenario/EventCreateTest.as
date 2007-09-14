package com.mindalliance.channels.business.scenario
{
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import flexunit.framework.TestCase;
	import flexunit.framework.TestSuite;
	
	import mx.rpc.IResponder;
	
	

	public class EventCreateTest extends TestCase implements IResponder
	{
		public function EventCreateTest(method : String) {
		  super(method);	
		}
		
		public function result(data:Object):void
		{
			assertTrue(data is String);
		}
		
        public function fault(info:Object):void
        {
            fail(info as String);
        }
		public function create() {
		  var delegate : EventDelegate = new EventDelegate(this	.addResponder(this,5000));
		  delegate.create("tst", "scenario1");
		}
		
		public static function suite():TestSuite
        {
            var ts:TestSuite = new TestSuite();
 
            ts.addTest( new EventCreateTest( "create" ) );
            return ts;
        }

	}
}