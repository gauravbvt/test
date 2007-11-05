// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.people.events
{
	import com.mindalliance.channels.common.events.GetElementEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class GetUserEvent extends GetElementEvent
	{
		public static const GetUser_Event:String = "<GetUserEvent>";
		
		public function GetUserEvent(id : String, model : EditorModel=null) 
        {
            super(id, model,  GetUser_Event );
        }
	}
}