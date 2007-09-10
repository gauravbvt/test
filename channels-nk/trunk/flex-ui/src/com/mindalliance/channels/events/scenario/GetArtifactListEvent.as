// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class GetArtifactListEvent extends CairngormEvent
	{
		public static const GetArtifactList_Event:String = "<GetArtifactListEvent>";
		public var scenarioId : String;
		public function GetArtifactListEvent(scenarioId : String) 
		{
			super( GetArtifactList_Event );
			this.scenarioId = scenarioId;
		}
	}
}