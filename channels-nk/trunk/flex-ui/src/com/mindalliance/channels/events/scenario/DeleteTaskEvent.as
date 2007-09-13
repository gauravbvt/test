// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class DeleteTaskEvent extends CairngormEvent
	{
		public static const DeleteTask_Event:String = "<DeleteTaskEvent>";
		public var id : String;
		public function DeleteTaskEvent(id : String) 
		{
			super( DeleteTask_Event );
			this.id = id;
		}
	}
}