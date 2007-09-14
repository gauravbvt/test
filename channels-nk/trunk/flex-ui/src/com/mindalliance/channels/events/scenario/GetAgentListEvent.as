// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetAgentListEvent extends CairngormEvent
	{
		public static const GetAgentList_Event:String = "<GetAgentListEvent>";
		public var taskId : String;
		public function GetAgentListEvent(taskId : String) 
		{
			super( GetAgentList_Event );
			this.taskId = taskId;
		}
	}
}