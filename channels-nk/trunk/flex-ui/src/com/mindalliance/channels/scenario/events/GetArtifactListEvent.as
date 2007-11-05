// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetArtifactListEvent extends GetElementListEvent
	{
		public function GetArtifactListEvent(scenarioId : String) 
		{

            super( "artifactsInScenario", "artifacts", {"scenarioId" : scenarioId} );
		}
	}
}