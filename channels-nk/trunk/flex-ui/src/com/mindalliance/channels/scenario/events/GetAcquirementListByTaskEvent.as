// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetAcquirementListByTaskEvent extends GetElementListEvent
	{
		public function GetAcquirementListByTaskEvent(taskId : String) 
		{
            super( "taskAcquirements", "acquirements" + taskId, {"taskId" : taskId} );
		}
	}
}