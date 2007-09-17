// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetAgentListByScenarioEvent extends CairngormEvent
	{
		public static const GetAgentListByScenario_Event:String = "<GetAgentListByScenarioEvent>";
		public var scenarioId : String;
		public function GetAgentListByScenarioEvent(scenarioId : String) 
		{
			super( GetAgentListByScenario_Event );
			this.scenarioId = scenarioId;
		}
	}
}