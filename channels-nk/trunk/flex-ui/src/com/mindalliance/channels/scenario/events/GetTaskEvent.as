// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.scenario.events
{
	import com.mindalliance.channels.common.events.GetElementEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class GetTaskEvent extends GetElementEvent
	{
		public static const GetTask_Event:String = "<GetTaskEvent>";
		public function GetTaskEvent(id : String, model : EditorModel=null) 
		{
			super( id, model,  GetTask_Event );
		}
	}
}