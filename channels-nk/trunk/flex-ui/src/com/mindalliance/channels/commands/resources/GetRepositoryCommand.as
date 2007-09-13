// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.resources
{
	import com.mindalliance.channels.business.resources.RepositoryDelegate;
	import com.mindalliance.channels.commands.common.GetElementCommand;
	import com.mindalliance.channels.events.resources.*;
	
	public class GetRepositoryCommand extends GetElementCommand
    {
    
        public function GetRepositoryCommand() {
            super(RepositoryDelegate);
            
        }
	}
}