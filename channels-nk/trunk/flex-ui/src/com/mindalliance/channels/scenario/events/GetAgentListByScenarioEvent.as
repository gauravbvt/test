// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetAgentListByScenarioEvent extends GetElementListEvent
	{
		public function GetAgentListByScenarioEvent(scenarioId : String) 
		{
            super( "agentsInScenario", "agents", {"scenarioId" : scenarioId} );
		}
	}
}