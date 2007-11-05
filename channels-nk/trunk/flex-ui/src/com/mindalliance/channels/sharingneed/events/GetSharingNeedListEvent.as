// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.sharingneed.events
{
    import com.mindalliance.channels.common.events.GetElementListEvent;

    public class GetSharingNeedListEvent extends GetElementListEvent
    {
        public static const GetSharingNeedList_Event:String = "<GetSharingNeedListEvent>";
        public function GetSharingNeedListEvent(scenarioId : String) 
        {
            super( "sharingNeedsInScenario", "sharingNeeds", {"scenarioId" : scenarioId}, GetSharingNeedList_Event );
        }
    }
}