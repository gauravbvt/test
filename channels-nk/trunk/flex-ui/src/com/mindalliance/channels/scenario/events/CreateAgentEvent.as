// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.mindalliance.channels.common.events.CreateElementEvent;

	public class CreateAgentEvent extends CreateElementEvent
	{
		public function CreateAgentEvent(name : String, taskId : String, roleId : String) 
		{
			super( "agent", {"name":name, "taskId" : taskId, "roleId": roleId} );
		}
	}
}