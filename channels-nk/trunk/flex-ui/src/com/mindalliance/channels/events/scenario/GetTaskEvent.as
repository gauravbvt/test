// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.mindalliance.channels.events.common.GetElementEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class GetTaskEvent extends GetElementEvent
	{
		public static const GetTask_Event:String = "<GetTaskEvent>";
		public function GetTaskEvent(id : String, model : EditorModel) 
		{
			super( GetTask_Event, id, model );
		}
	}
}