// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package @namespace@.@commands@.@submodule@
{
    import com.adobe.cairngorm.commands.ICommand;
    import com.adobe.cairngorm.control.CairngormEvent;
    import @namespace@.@commands@.BaseCommand;
    import @namespace@.@events@.@submodule@.@sequence@Event;
    
    public class @sequence@Command extends BaseCommand
    {
        override public function execute(event:CairngormEvent):void
        {
            var evt:@sequence@Event = event as @sequence@Event;
        }
    }
}