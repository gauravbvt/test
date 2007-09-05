// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.mindalliance.channels.events.common.GetElementEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class GetPersonEvent extends GetElementEvent
	{
		public static const GetPerson_Event:String = "<GetPersonEvent>";

		public function GetPersonEvent(id : String, model : EditorModel) 
		{
			super( GetPerson_Event, id, model );
		}
	}
}