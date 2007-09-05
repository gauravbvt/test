// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.mindalliance.channels.events.common.GetElementEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class GetOrganizationEvent extends GetElementEvent
	{
		public static const GetOrganization_Event:String = "<GetOrganizationEvent>";
		
		public function GetOrganizationEvent(id : String, model : EditorModel) 
		{
			super( GetOrganization_Event,id,model );
		}
	}
}