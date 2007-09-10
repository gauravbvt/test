// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class GetPersonByUserEvent extends CairngormEvent
	{
		public static const GetPersonByUser_Event:String = "<GetPersonByUserEvent>";
		public var userId : String;
		public function GetPersonByUserEvent(id : String) 
		{
			super( GetPersonByUser_Event );
			this.userId = id;
		}
	}
}