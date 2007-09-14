// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class DeleteAgentEvent extends CairngormEvent
	{
		public static const DeleteAgent_Event:String = "<DeleteAgentEvent>";
		
		public var id : String;
		public var taskId : String;
		
		public function DeleteAgentEvent(id : String, taskId : String) 
		{
			super( DeleteAgent_Event );
			this.id = id;
		    this.taskId = taskId;
		}
	}
}