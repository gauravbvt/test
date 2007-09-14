package com.mindalliance.channels.business.scenario
{
	import com.mindalliance.channels.vo.common.ElementVO;
	
	import flexunit.framework.TestCase;
	import flexunit.framework.TestSuite;
	
	import mx.rpc.IResponder;
	
	

	public class AgentCreateTest extends TestCase implements IResponder
	{
		public function AgentCreateTest(method : String) {
		  super(method);	
		}
		
		public function result(data:Object):void
		{
			assertTrue(data is ElementVO);
		}
		
        public function fault(info:Object):void
        {
            fail(info as String);
        }
		public function create() {
		  var delegate : AgentDelegate = new AgentDelegate(this	.addResponder(this,5000));
		  delegate.create("Test Role", "task1", "role1");
		}
		
		public static function suite():TestSuite
        {
            var ts:TestSuite = new TestSuite();
 
            ts.addTest( new AgentCreateTest( "create" ) );
            return ts;
        }

	}
}