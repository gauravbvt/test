// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class DeleteEventEvent extends CairngormEvent
	{
		public static const DeleteEvent_Event:String = "<DeleteEventEvent>";
		public var id : String
		public function DeleteEventEvent(id : String) 
		{
			super( DeleteEvent_Event );
			this.id = id;
		}
	}
}