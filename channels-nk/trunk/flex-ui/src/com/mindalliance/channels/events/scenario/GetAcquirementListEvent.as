// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetAcquirementListEvent extends CairngormEvent
	{
		public static const GetAcquirementList_Event:String = "<GetAcquirementListEvent>";
		public var scenarioId : String;
		
		public function GetAcquirementListEvent(scenarioId : String) 
		{
			super( GetAcquirementList_Event );
            this.scenarioId = scenarioId;				    
		}
	}
}