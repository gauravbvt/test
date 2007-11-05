// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetEventListEvent extends GetElementListEvent
	{
		
		public function GetEventListEvent(scenarioId : String) 
		{
			super( "eventsInScenario", "events", {"scenarioId" : scenarioId} );
		}
	}
}