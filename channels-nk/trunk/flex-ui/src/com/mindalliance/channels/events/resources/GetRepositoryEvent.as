// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.resources
{
	import com.mindalliance.channels.events.common.GetElementEvent;
	import com.mindalliance.channels.model.EditorModel;

	public class GetRepositoryEvent extends GetElementEvent
	{
		public static const GetRepository_Event:String = "<GetRepositoryEvent>";
		
		public function GetRepositoryEvent(id : String, model : EditorModel) 
        {
            super( GetRepository_Event, id, model );
        }
	}
}