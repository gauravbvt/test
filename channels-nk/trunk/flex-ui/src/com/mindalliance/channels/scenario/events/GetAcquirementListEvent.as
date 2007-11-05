// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetAcquirementListEvent extends GetElementListEvent
	{
		
		public function GetAcquirementListEvent(scenarioId : String) 
		{

            super( "acquirementsInScenario", "acquirements", {"scenarioId" : scenarioId} );		    
		}
	}
}