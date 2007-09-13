// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class DeletePersonEvent extends CairngormEvent
	{
		public static const DeletePerson_Event:String = "<DeletePersonEvent>";
		public var id : String;
		public function DeletePersonEvent(id : String) 
		{
			super( DeletePerson_Event );
			this.id = id;
		}
	}
}