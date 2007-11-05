// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.mindalliance.channels.common.events.CreateElementEvent;

	public class CreateArtifactEvent extends CreateElementEvent
	{
		public function CreateArtifactEvent(name : String, taskId : String) 
		{
			super( "artifact", {"name" : name, "taskId": taskId} );
            
		}
	}
}