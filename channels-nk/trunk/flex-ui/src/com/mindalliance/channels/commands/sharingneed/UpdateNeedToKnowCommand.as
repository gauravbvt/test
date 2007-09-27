// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.business.sharingneed.NeedToKnowDelegate;
    import com.mindalliance.channels.commands.BaseDelegateCommand;
    import com.mindalliance.channels.events.scenario.*;
    import com.mindalliance.channels.events.sharingneed.UpdateNeedToKnowEvent;
    
    public class UpdateNeedToKnowCommand extends BaseDelegateCommand
    {
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:UpdateNeedToKnowEvent = event as UpdateNeedToKnowEvent;
            var delegate:NeedToKnowDelegate = new NeedToKnowDelegate( this );
        }
        
        override public function result(data:Object):void
        {
            
        }
    }
}