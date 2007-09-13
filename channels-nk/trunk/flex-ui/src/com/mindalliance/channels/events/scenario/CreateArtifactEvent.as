// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class CreateArtifactEvent extends CairngormEvent
	{
		public static const CreateArtifact_Event:String = "<CreateArtifactEvent>";
		public var name : String;
        public var taskId : String;
		public function CreateArtifactEvent(name : String, taskId : String) 
		{
			super( CreateArtifact_Event );
            this.name = name;
            this.taskId = taskId;
		}
	}
}