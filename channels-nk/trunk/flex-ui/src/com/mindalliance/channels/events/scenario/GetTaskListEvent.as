// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetTaskListEvent extends CairngormEvent
	{
		public static const GetTaskList_Event:String = "<GetTaskListEvent>";
		
		public var scenarioId : String;
		
		public function GetTaskListEvent(scenarioId : String) 
		{
			super( GetTaskList_Event );
			this.scenarioId = scenarioId;
		}
	}
}