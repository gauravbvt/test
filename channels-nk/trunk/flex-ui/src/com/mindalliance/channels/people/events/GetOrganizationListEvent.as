// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.people.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetOrganizationListEvent extends GetElementListEvent
	{
		public function GetOrganizationListEvent() 
		{
			super( "allOrganizations", "organizations", new Object() );
		}
	}
}