// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.mindalliance.channels.common.events.CreateElementEvent;

	public class CreateEventEvent extends CreateElementEvent
	{
		public function CreateEventEvent(name : String, scenarioId : String) 
		{
			super( "event", {"name" : name, "scenarioId" : scenarioId} );
		}
	}
}