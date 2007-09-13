// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.people
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class CreatePersonEvent extends CairngormEvent
	{
		public static const CreatePerson_Event:String = "<CreatePersonEvent>";
		public var firstName : String;
		public var lastName : String;
		public function CreatePersonEvent(firstName : String, lastName : String) 
		{
			super( CreatePerson_Event );
            this.firstName = firstName;
            this.lastName = lastName;
		}
	}
}