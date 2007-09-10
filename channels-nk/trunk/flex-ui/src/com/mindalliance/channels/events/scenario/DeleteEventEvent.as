// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class DeleteEventEvent extends CairngormEvent
	{
		public static const DeleteEvent_Event:String = "<DeleteEventEvent>";
		
		public function DeleteEventEvent() 
		{
			super( DeleteEvent_Event );
		}
	}
}