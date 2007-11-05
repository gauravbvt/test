// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class UpdateArtifactTaskEvent extends CairngormEvent
	{
		public static const UpdateArtifactTask_Event:String = "<UpdateArtifactTaskEvent>";
		public var id : String;
		public var taskId : String;
		public function UpdateArtifactTaskEvent(id : String, taskId : String) 
		{
			super( UpdateArtifactTask_Event );
			this.id = id;
			this.taskId = taskId;
		}
	}
}