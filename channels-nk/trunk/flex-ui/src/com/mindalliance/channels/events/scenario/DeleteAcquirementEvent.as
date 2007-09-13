// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class DeleteAcquirementEvent extends CairngormEvent
	{
		public static const DeleteAcquirement_Event:String = "<DeleteAcquirementEvent>";
		public var id : String;
		public function DeleteAcquirementEvent(id : String) 
		{
			super( DeleteAcquirement_Event );
			this.id = id;
		}
	}
}