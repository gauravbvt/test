// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.people.events
{
	import com.mindalliance.channels.common.events.CreateElementEvent;

	public class CreatePersonEvent extends CreateElementEvent
	{
		public function CreatePersonEvent(firstName : String, lastName : String) 
		{
			super( "person", {"firstName" : firstName, "lastName" : lastName});
		}
	}
}