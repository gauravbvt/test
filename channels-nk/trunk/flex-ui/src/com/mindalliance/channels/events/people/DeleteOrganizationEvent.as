// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class DeleteOrganizationEvent extends CairngormEvent
	{
		public static const DeleteOrganization_Event:String = "<DeleteOrganizationEvent>";
		public var id : String;
		public function DeleteOrganizationEvent(id : String) 
		{
			super( DeleteOrganization_Event );
			this.id = id;
		}
	}
}