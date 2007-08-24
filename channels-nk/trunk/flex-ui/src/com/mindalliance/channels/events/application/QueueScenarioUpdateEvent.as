// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.application
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class QueueScenarioUpdateEvent extends CairngormEvent
	{
		public static const QueueScenarioUpdate_Event:String = "<QueueScenarioUpdateEvent>";
		
		public function QueueScenarioUpdateEvent() 
		{
			super( QueueScenarioUpdate_Event );
		}
	}
}