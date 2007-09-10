// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.scenario
{
	import com.mindalliance.channels.events.common.GetElementEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class GetArtifactEvent extends GetElementEvent
	{
		public static const GetArtifact_Event:String = "<GetArtifactEvent>";
		
		public function GetArtifactEvent(id : String, model : EditorModel) 
        {
            super( GetArtifact_Event, id, model );
        }
	}
}