// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.mindalliance.channels.events.common.GetElementEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class GetRoleEvent extends GetElementEvent
	{
		public static const GetRole_Event:String = "<GetRoleEvent>";

		public function GetRoleEvent(id : String, model : EditorModel) 
		{
			super( GetRole_Event, id, model );
		}
	}
}