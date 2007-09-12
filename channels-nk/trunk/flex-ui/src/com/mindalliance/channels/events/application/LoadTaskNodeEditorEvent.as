// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class LoadTaskNodeEditorEvent extends CairngormEvent
	{
		public static const LoadTaskNodeEditor_Event:String = "<LoadTaskNodeEditorEvent>";
		public var taskId : String;
		public var roleId : String;
		public function LoadTaskNodeEditorEvent(taskId : String, roleId : String) 
		{
			super( LoadTaskNodeEditor_Event );
			this.taskId = taskId;
			this.roleId = roleId;
		}
	}
}