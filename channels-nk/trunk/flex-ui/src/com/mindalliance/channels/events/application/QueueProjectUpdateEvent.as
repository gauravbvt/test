// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class QueueProjectUpdateEvent extends CairngormEvent
	{
		public static const QueueProjectUpdate_Event:String = "<QueueProjectUpdateEvent>";
		
		public function QueueProjectUpdateEvent() 
		{
			super( QueueProjectUpdate_Event );
		}
	}
}