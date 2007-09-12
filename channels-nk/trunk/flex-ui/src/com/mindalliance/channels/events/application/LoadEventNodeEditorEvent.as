// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class LoadEventNodeEditorEvent extends CairngormEvent
	{
		public static const LoadEventNodeEditor_Event:String = "<LoadEventNodeEditorEvent>";
		public var eventId : String;
		public function LoadEventNodeEditorEvent(eventId : String) 
		{
			super( LoadEventNodeEditor_Event );
			this.eventId = eventId;
		}
	}
}