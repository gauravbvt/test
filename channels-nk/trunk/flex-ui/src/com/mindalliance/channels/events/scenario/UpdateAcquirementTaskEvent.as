// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class UpdateAcquirementTaskEvent extends CairngormEvent
	{
		public static const UpdateAcquirementTask_Event:String = "<UpdateAcquirementTaskEvent>";
		public var id : String;
		public var taskId : String;
		public function UpdateAcquirementTaskEvent(id : String, taskId : String) 
		{
			super( UpdateAcquirementTask_Event );
			this.id = id;
			this.taskId = taskId;
		}
	}
}