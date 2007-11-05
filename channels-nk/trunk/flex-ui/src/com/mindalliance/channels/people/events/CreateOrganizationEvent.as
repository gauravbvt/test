// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.people.events
{
	import com.mindalliance.channels.common.events.CreateElementEvent;

	public class CreateOrganizationEvent extends CreateElementEvent
	{
		public function CreateOrganizationEvent(name : String) 
		{
			super("organization", {"name" : name});
		}
	}
}