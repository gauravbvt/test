// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.mindalliance.channels.events.common.DeleteElementEvent;

	public class DeleteOrganizationEvent extends DeleteElementEvent
	{

		public function DeleteOrganizationEvent(id : String) 
		{
			super( id );
		}
	}
}