// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package @namespace@.@commands@.@submodule@
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.adobe.cairngorm.control.CairngormEventDispatcher;
    import @namespace@.@business@.@submodule@.@delegate@Delegate;
    import @namespace@.@events@.@submodule@.*;
    import @namespace@.@commands@.BaseDelegateCommand;
    
    public class @sequence@Command extends BaseDelegateCommand
    {
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:@sequence@Event = event as @sequence@Event;
            var delegate:@delegate@Delegate = new @delegate@Delegate( this );
        }
        
        override public function result(data:Object):void
        {
            
        }
    }
}