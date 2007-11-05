// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.sharingneed.events
{
    import com.mindalliance.channels.common.events.CreateElementEvent;
    import com.mindalliance.channels.vo.common.Knowable;
    import com.mindalliance.channels.vo.common.SourceOrSink;

    public class CreateNeedToKnowSequenceEvent extends CreateElementEvent
    {
        public function CreateNeedToKnowSequenceEvent(who : SourceOrSink, about : Knowable, knowId : String) 
        {
            super( "needToKnow", {"about" : about,
                                    "who" : who,
                                    "knowId" : knowId});
        }
    }
}