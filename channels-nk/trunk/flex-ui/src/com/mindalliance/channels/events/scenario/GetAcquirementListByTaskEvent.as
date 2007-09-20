// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetAcquirementListByTaskEvent extends CairngormEvent
	{
		public static const GetAcquirementListByTask_Event:String = "<GetAcquirementListByTaskEvent>";
		public var taskId : String;
		public function GetAcquirementListByTaskEvent(taskId : String) 
		{
			super( GetAcquirementListByTask_Event );
			this.taskId = taskId;
		}
	}
}