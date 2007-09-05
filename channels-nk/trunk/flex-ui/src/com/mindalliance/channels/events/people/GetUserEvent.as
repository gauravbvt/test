// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.mindalliance.channels.events.common.GetElementEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class GetUserEvent extends GetElementEvent
	{
		public static const GetUser_Event:String = "<GetUserEvent>";
		
		public function GetUserEvent(id : String, model : EditorModel=null) 
        {
            super( GetUser_Event, id, model );
        }
	}
}