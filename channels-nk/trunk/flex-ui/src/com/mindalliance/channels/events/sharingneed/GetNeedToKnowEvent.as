// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.sharingneed
{
	import com.mindalliance.channels.events.common.GetElementEvent;
	import com.mindalliance.channels.model.EditorModel;
	
    

    public class GetNeedToKnowEvent extends GetElementEvent
    {
        public static const GetNeedToKnow_Event:String = "<GetNeedToKnowEvent>";
        
        public function GetNeedToKnowEvent(id : String, model : EditorModel = null) 
        {
            super( GetNeedToKnow_Event, id, model );
        }
    }
}