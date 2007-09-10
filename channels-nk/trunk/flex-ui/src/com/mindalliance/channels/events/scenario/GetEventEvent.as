// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.mindalliance.channels.events.common.GetElementEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class GetEventEvent extends GetElementEvent
	{
		public static const GetEvent_Event:String = "<GetEventEvent>";
		
		public function GetEventEvent(id : String, model : EditorModel) 
        {
            super( GetEvent_Event, id, model );
        }
	}
}