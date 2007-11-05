// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.mindalliance.channels.common.events.CreateElementEvent;

	public class CreateAcquirementEvent extends CreateElementEvent
	{

		public function CreateAcquirementEvent(name : String, taskId : String) 
		{
			super( "acquirement", {"name" : name, "taskId" : taskId} );
		}
	}
}