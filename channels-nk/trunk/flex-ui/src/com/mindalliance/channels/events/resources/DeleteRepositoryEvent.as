// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.resources
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class DeleteRepositoryEvent extends CairngormEvent
	{
		public static const DeleteRepository_Event:String = "<DeleteRepositoryEvent>";
		
		public function DeleteRepositoryEvent() 
		{
			super( DeleteRepository_Event );
		}
	}
}