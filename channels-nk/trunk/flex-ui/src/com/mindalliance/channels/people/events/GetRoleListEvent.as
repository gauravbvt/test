// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.people.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetRoleListEvent extends GetElementListEvent
	{
		public function GetRoleListEvent() 
		{
			super( "allRoles", "roles", new Object() );
		}
	}
}