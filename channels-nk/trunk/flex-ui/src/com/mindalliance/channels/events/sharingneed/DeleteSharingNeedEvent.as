// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;

    public class DeleteSharingNeedEvent extends CairngormEvent
    {
        public static const DeleteSharingNeed_Event:String = "<DeleteSharingNeedEvent>";
        public var id : String
        public function DeleteSharingNeedEvent(id : String) 
        {
            super( DeleteSharingNeed_Event );
            this.id = id;
        }
    }
}