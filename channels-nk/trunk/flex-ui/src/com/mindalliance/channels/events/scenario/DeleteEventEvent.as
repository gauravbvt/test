// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.mindalliance.channels.events.common.DeleteElementEvent;

	public class DeleteEventEvent extends DeleteElementEvent
	{

		public function DeleteEventEvent(id : String) 
		{
			super( id );
		}
	}
}