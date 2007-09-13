// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class DeleteRoleEvent extends CairngormEvent
	{
		public static const DeleteRole_Event:String = "<DeleteRoleEvent>";
		public var id : String;
		public function DeleteRoleEvent(id : String) 
		{
			super( DeleteRole_Event );
			this.id = id;
		}
	}
}