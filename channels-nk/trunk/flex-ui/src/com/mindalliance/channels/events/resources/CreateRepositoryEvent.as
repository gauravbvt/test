// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.resources
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class CreateRepositoryEvent extends CairngormEvent
	{
		public static const CreateRepository_Event:String = "<CreateRepositoryEvent>";
		
		public function CreateRepositoryEvent() 
		{
			super( CreateRepository_Event );
		}
	}
}