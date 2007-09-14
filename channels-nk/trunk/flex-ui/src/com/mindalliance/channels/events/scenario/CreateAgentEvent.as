// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class CreateAgentEvent extends CairngormEvent
	{
		public static const CreateAgent_Event:String = "<CreateAgentEvent>";
		public var name : String;
		public var taskId : String;
		public var roleId : String;
		public function CreateAgentEvent(name : String, taskId : String, roleId : String) 
		{
			super( CreateAgent_Event );
			this.name = name;
			this.taskId = taskId;
			this.roleId = roleId;
		}
	}
}