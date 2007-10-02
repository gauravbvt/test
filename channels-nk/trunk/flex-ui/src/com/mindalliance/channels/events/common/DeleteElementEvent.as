// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.common
{
    import com.adobe.cairngorm.control.CairngormEvent;

    public class DeleteElementEvent extends CairngormEvent
    {
        public static const DeleteElement_Event:String = "<DeleteElementEvent>";
        public var id : String;
        public function DeleteElementEvent(id : String) 
        {
            super( DeleteElement_Event );
            this.id = id;
        }
    }
}