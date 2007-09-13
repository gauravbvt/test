// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class CreateEventEvent extends CairngormEvent
	{
		public static const CreateEvent_Event:String = "<CreateEventEvent>";
		public var name : String;
		public var scenarioId : String;
		public function CreateEventEvent(name : String, scenarioId : String) 
		{
			super( CreateEvent_Event );
			this.name = name;
			this.scenarioId = scenarioId;
		}
	}
}