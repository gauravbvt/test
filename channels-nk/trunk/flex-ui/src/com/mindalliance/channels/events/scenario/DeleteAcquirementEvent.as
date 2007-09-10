// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class DeleteAcquirementEvent extends CairngormEvent
	{
		public static const DeleteAcquirement_Event:String = "<DeleteAcquirementEvent>";
		
		public function DeleteAcquirementEvent() 
		{
			super( DeleteAcquirement_Event );
		}
	}
}