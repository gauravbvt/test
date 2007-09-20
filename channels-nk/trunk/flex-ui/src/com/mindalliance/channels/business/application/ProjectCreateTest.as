package com.mindalliance.channels.business.application
{
	import com.mindalliance.channels.vo.common.ElementVO;
    
    import flexunit.framework.TestCase;
    import flexunit.framework.TestSuite;
    
    import mx.rpc.IResponder;
    
    

    public class ProjectCreateTest extends TestCase implements IResponder
    {
        public function ProjectCreateTest(method : String) {
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
        public function create() : void {
          var delegate : ProjectDelegate = new ProjectDelegate(this .addResponder(this,5000));
          delegate.createProject("Test Project");
        }
        
        public static function suite():TestSuite
        {
            var ts:TestSuite = new TestSuite();
 
            ts.addTest( new ProjectCreateTest( "create" ) );
            return ts;
        }

    }
	

}