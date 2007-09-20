// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class DeleteArtifactEvent extends CairngormEvent
	{
		public static const DeleteArtifact_Event:String = "<DeleteArtifactEvent>";
		public var id : String;
		public var taskId : String;
		public function DeleteArtifactEvent(id : String, taskId : String) 
		{
			super( DeleteArtifact_Event );
			this.id = id;
			this.taskId = taskId;
		}
	}
}