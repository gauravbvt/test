// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.sharingneed.commands
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.common.commands.BaseCommand;
    import com.mindalliance.channels.sharingneed.events.*;
    import com.mindalliance.channels.util.CairngormHelper;
    
    public class CreateSharingNeedCommand extends BaseCommand
    {
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:CreateSharingNeedEvent = event as CreateSharingNeedEvent;
            CairngormHelper.fireEvent(new CreateKnowSequenceEvent(evt.knowWho, 
                                                                    evt.knowAbout,
                                                                    evt.needToKnowWho,
                                                                    evt.needToKnowAbout));
        }

    }
}