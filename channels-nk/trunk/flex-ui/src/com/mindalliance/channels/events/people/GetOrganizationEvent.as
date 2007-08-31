// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class GetOrganizationEvent extends CairngormEvent
	{
		public static const GetOrganization_Event:String = "<GetOrganizationEvent>";
		public var model : EditorModel;
		public var id : String;
		
		public function GetOrganizationEvent(id : String, model : EditorModel) 
		{
			super( GetOrganization_Event );
			this.id = id;
			this.model = model;
		}
	}
}