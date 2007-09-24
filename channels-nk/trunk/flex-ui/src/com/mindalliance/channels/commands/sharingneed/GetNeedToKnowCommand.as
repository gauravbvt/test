// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.sharingneed
{
    import com.mindalliance.channels.business.sharingneed.NeedToKnowDelegate;
    import com.mindalliance.channels.commands.common.GetElementCommand;
    import com.mindalliance.channels.events.sharingneed.*;
    
    public class GetNeedToKnowCommand extends GetElementCommand
    {
    
        public function GetNeedToKnowCommand() {
        	super(NeedToKnowDelegate);
        
        }
    }
}