// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.resources
{
	import com.mindalliance.channels.events.common.DeleteElementEvent;

	public class DeleteRepositoryEvent extends DeleteElementEvent
	{
		public function DeleteRepositoryEvent(id : String) 
		{
			super( id );
		}
	}
}