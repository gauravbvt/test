// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class CreateAcquirementEvent extends CairngormEvent
	{
		public static const CreateAcquirement_Event:String = "<CreateAcquirementEvent>";
		
		public function CreateAcquirementEvent() 
		{
			super( CreateAcquirement_Event );
		}
	}
}