// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;

    public class GetSharingNeedListEvent extends CairngormEvent
    {
        public static const GetSharingNeedList_Event:String = "<GetSharingNeedListEvent>";
        public var scenarioId : String;
        public function GetSharingNeedListEvent(scenarioId : String) 
        {
            super( GetSharingNeedList_Event );
            this.scenarioId = scenarioId;
        }
    }
}