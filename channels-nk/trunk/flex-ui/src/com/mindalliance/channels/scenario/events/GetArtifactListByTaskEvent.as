// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetArtifactListByTaskEvent extends GetElementListEvent
	{
		public function GetArtifactListByTaskEvent(taskId : String) 
		{
            super( "taskArtifacts", "artifacts" + taskId, {"taskId" : taskId} );
		}
	}
}