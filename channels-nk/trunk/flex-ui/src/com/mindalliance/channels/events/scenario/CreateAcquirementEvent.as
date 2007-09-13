// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class CreateAcquirementEvent extends CairngormEvent
	{
		public static const CreateAcquirement_Event:String = "<CreateAcquirementEvent>";
		
		public var name : String;
		public var taskId : String;
		public function CreateAcquirementEvent(name : String, taskId : String) 
		{
			super( CreateAcquirement_Event );
			this.name = name;
			this.taskId = taskId;
		}
	}
}