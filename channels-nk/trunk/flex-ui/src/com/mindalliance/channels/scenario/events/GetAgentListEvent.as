// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetAgentListEvent extends GetElementListEvent
	{
		public function GetAgentListEvent(taskId : String) 
		{
            super( "taskAgents", "agents" + taskId, {"taskId" : taskId} );
		}
	}
}