// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.resources
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class CreateRepositoryEvent extends CairngormEvent
	{
		public static const CreateRepository_Event:String = "<CreateRepositoryEvent>";
		public var name : String;
		public var organizationId : String;
		public function CreateRepositoryEvent(name : String, organizationId : String) 
		{
			super( CreateRepository_Event );
			this.name = name;
			this.organizationId = organizationId;
		}
	}
}