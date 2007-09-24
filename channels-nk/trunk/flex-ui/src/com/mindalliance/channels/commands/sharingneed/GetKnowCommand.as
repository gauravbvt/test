// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.sharingneed
{
    import com.mindalliance.channels.business.sharingneed.KnowDelegate;
    import com.mindalliance.channels.commands.common.GetElementCommand;
    import com.mindalliance.channels.events.sharingneed.*;
    
    public class GetKnowCommand extends GetElementCommand
    {
    
        public function GetKnowCommand() {
            super(KnowDelegate);	
        }
    }
}