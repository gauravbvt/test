// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class QueueOrganizationUpdateEvent extends CairngormEvent
	{
		public static const QueueOrganizationUpdate_Event:String = "<QueueOrganizationUpdateEvent>";
		
		public function QueueOrganizationUpdateEvent() 
		{
			super( QueueOrganizationUpdate_Event );
		}
	}
}