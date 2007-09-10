// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.resources
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class UpdateRepositoryEvent extends CairngormEvent
	{
		public static const UpdateRepository_Event:String = "<UpdateRepositoryEvent>";
		
		public function UpdateRepositoryEvent() 
		{
			super( UpdateRepository_Event );
		}
	}
}