// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.resources.events
{
	import com.mindalliance.channels.common.events.GetElementListEvent;

	public class GetRepositoryListEvent extends GetElementListEvent
	{
		public function GetRepositoryListEvent() 
		{
			super( "allRepositories", "repositories", new Object() );
		}
	}
}