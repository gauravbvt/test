// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.mindalliance.channels.common.events.CreateElementEvent;

	public class CreateTaskEvent extends CreateElementEvent
	{
		public function CreateTaskEvent(name : String, scenarioId : String) 
		{
			super( "task", {"name" : name, "scenarioId" : scenarioId} );
			
		}
	}
}