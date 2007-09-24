// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.sharingneed
{
	import com.mindalliance.channels.events.common.GetElementEvent;
	import com.mindalliance.channels.model.EditorModel;
	
    

    public class GetKnowEvent extends GetElementEvent
    {
        public static const GetKnow_Event:String = "<GetKnowEvent>";
        
        public function GetKnowEvent(id : String, model : EditorModel = null) 
        {
            super( GetKnow_Event, id, model );
        }
    }
}