// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class QueueUpdateEvent extends CairngormEvent
	{
		public static const QueueUpdate_Event:String = "<QueueUpdateEvent>";
		
		public var model : EditorModel;
		
		public function QueueUpdateEvent(model : EditorModel) 
		{
			super( QueueUpdate_Event );
			this.model = model;
		}
	}
}