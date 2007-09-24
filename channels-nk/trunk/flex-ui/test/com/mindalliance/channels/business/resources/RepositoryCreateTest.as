package com.mindalliance.channels.business.resources
{
    import com.mindalliance.channels.vo.common.ElementVO;
    
    import flexunit.framework.TestCase;
    import flexunit.framework.TestSuite;
    
    import mx.rpc.IResponder;
    
    

    public class RepositoryCreateTest extends TestCase implements IResponder
    {
        public function RepositoryCreateTest(method : String) {
          super(method);    
        }
        
        public function result(data:Object):void
        {
            assertTrue(data);
        }
        
        public function fault(info:Object):void
        {
            fail(info as String);
        }
        public function create() : void {
          var delegate : RepositoryDelegate = new RepositoryDelegate(this   .addResponder(this,5000));
          delegate.create("Repository", "organization1");
        }
        
        public static function suite():TestSuite
        {
            var ts:TestSuite = new TestSuite();
 
            ts.addTest( new RepositoryCreateTest( "create" ) );
            return ts;
        }

    }
}