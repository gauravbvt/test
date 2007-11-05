// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetTaskListEvent extends GetElementListEvent
	{
		
		public function GetTaskListEvent(scenarioId : String) 
		{
			super( "tasksInScenario","tasks",{"scenarioId" : scenarioId} );
		}
	}
}