// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.mindalliance.channels.events.common.GetElementEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class GetAcquirementEvent extends GetElementEvent
	{
		public static const GetAcquirement_Event:String = "<GetAcquirementEvent>";
		
		public function GetAcquirementEvent(id : String, model : EditorModel) 
        {
            super( GetAcquirement_Event, id, model );
        }
	}
}