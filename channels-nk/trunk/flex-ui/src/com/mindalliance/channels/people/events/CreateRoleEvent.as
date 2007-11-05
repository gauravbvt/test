// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.people.events
{
	import com.mindalliance.channels.common.events.CreateElementEvent;

	public class CreateRoleEvent extends CreateElementEvent
	{
		public function CreateRoleEvent(name : String, organizationId : String) 
		{
			super( "role", {"name" : name, "organizationId" : organizationId} );
		}
	}
}