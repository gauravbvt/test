// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class CreateRoleEvent extends CairngormEvent
	{
		public static const CreateRole_Event:String = "<CreateRoleEvent>";
		public var name : String;
		public var organizationId : String;
		public function CreateRoleEvent(name : String, organizationId : String) 
		{
			super( CreateRole_Event );
			this.name = name;
			this.organizationId = organizationId;
		}
	}
}