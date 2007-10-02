// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.mindalliance.channels.events.common.DeleteElementEvent;

	public class DeleteAcquirementEvent extends DeleteElementEvent
	{

		public function DeleteAcquirementEvent(id : String, taskId : String) 
		{
			super( id );
			
		}
	}
}