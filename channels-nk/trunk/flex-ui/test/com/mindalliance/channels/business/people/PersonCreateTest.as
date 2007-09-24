package com.mindalliance.channels.business.people
{
    import com.mindalliance.channels.vo.common.ElementVO;
    
    import flexunit.framework.TestCase;
    import flexunit.framework.TestSuite;
    
    import mx.rpc.IResponder;
    
    

    public class PersonCreateTest extends TestCase implements IResponder
    {
        public function PersonCreateTest(method : String) {
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
          var delegate : PersonDelegate = new PersonDelegate(this   .addResponder(this,5000));

          delegate.create("Test", "Person");
        }
        
        public static function suite():TestSuite
        {
            var ts:TestSuite = new TestSuite();
 
            ts.addTest( new PersonCreateTest( "create" ) );
            return ts;
        }

    }
}