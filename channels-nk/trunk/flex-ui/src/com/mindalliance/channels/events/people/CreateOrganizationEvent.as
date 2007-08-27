// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class CreateOrganizationEvent extends CairngormEvent
	{
		public static const CreateOrganization_Event:String = "<CreateOrganizationEvent>";
		public var name : String;
		public function CreateOrganizationEvent(name : String) 
		{
			super( CreateOrganization_Event );
			this.name = name;
		}
	}
}