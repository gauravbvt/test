// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.people.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetPersonListEvent extends GetElementListEvent
	{
		public function GetPersonListEvent() 
		{
			super( "allPersons", "people", new Object() );
		}
	}
}