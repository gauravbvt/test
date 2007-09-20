// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetArtifactListByTaskEvent extends CairngormEvent
	{
		public static const GetArtifactListByTask_Event:String = "<GetArtifactListByTaskEvent>";
		public var taskId : String;
		public function GetArtifactListByTaskEvent(taskId : String) 
		{
			super( GetArtifactListByTask_Event );
			this.taskId = taskId;
		}
	}
}