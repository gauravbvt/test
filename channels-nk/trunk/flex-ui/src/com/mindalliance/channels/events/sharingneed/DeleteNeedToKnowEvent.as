// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;

    public class DeleteNeedToKnowEvent extends CairngormEvent
    {
        public static const DeleteNeedToKnow_Event:String = "<DeleteNeedToKnowEvent>";
        public var id : String;
        public function DeleteNeedToKnowEvent(id : String) 
        {
            super( DeleteNeedToKnow_Event );
            this.id = id;
        }
    }
}