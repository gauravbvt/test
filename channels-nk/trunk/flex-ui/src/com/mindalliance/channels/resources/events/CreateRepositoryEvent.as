// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.resources.events
{
	import com.mindalliance.channels.common.events.CreateElementEvent;

	public class CreateRepositoryEvent extends CreateElementEvent
	{
		public function CreateRepositoryEvent(name : String, organizationId : String) 
		{
			super( "repository", {"name" : name, "organizationId" : organizationId} );
		}
	}
}