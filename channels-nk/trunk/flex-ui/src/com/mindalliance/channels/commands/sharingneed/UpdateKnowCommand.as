// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.adobe.cairngorm.control.CairngormEventDispatcher;
    import com.mindalliance.channels.business.sharingneed.KnowDelegate;
    import com.mindalliance.channels.events.sharingneed.*;
    import com.mindalliance.channels.commands.BaseDelegateCommand;
    
    public class UpdateKnowCommand extends BaseDelegateCommand
    {
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:UpdateKnowEvent = event as UpdateKnowEvent;
            var delegate:KnowDelegate = new KnowDelegate( this );
        }
        
        override public function result(data:Object):void
        {
            
        }
    }
}