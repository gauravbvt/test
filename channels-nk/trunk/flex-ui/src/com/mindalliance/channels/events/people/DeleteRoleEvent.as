// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.mindalliance.channels.events.common.DeleteElementEvent;

	public class DeleteRoleEvent extends DeleteElementEvent
	{
		public function DeleteRoleEvent(id : String) 
		{
			super( id );
		}
	}
}