// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class CreateTaskEvent extends CairngormEvent
	{
		public static const CreateTask_Event:String = "<CreateTaskEvent>";
		public var name : String;
        public var scenarioId : String;
		public function CreateTaskEvent(name : String, scenarioId : String) 
		{
			super( CreateTask_Event );
			this.name = name;
            this.scenarioId = scenarioId;
		}
	}
}