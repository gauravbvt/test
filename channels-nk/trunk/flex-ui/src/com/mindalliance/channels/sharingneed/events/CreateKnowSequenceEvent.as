// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.sharingneed.events
{
    import com.mindalliance.channels.common.events.CreateElementEvent;
    import com.mindalliance.channels.vo.common.Knowable;
    import com.mindalliance.channels.vo.common.SourceOrSink;

    public class CreateKnowSequenceEvent extends CreateElementEvent
    {
        public function CreateKnowSequenceEvent(knowWho : SourceOrSink,
                                                knowAbout : Knowable,
                                                needToKnowWho : SourceOrSink,
                                                needToKnowAbout : Knowable) 
        {
            super( "know", {"knowWho" : knowWho,
                            "knowAbout" : knowAbout,
                            "needToKnowWho" : needToKnowWho,
                             "needToKnowAbout" : needToKnowAbout});
        }
    }
}