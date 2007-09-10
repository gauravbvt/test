// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetEventListEvent extends CairngormEvent
	{
		public static const GetEventList_Event:String = "<GetEventListEvent>";
		public var scenarioId : String;
		
		public function GetEventListEvent(scenarioId : String) 
		{
			super( GetEventList_Event );
            this.scenarioId = scenarioId;				    
		}
	}
}