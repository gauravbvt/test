// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.sharingneed.events
{
    import com.mindalliance.channels.common.events.CreateElementEvent;

    public class CreateSharingNeedSequenceEvent extends CreateElementEvent
    {
        
        public function CreateSharingNeedSequenceEvent(knowId : String, needToKnowId : String) 
        {
            super( "sharingNeed", {"knowId" : knowId, "needToKnowId" : needToKnowId} );
        }
    }
}