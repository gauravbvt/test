// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.vo.common.Knowable;
    import com.mindalliance.channels.vo.common.SourceOrSink;

    public class CreateKnowSequenceEvent extends CairngormEvent
    {
        public static const CreateKnowSequence_Event:String = "<CreateKnowSequenceEvent>";
        public var knowWho : SourceOrSink;
        public var knowAbout : Knowable;
        public var needToKnowWho : SourceOrSink;
        public var needToKnowAbout : Knowable;
        public function CreateKnowSequenceEvent(knowWho : SourceOrSink,
                                                knowAbout : Knowable,
                                                needToKnowWho : SourceOrSink,
                                                needToKnowAbout : Knowable) 
        {
            super( CreateKnowSequence_Event );
            this.knowWho = knowWho;
            this.knowAbout  = knowAbout;
            this.needToKnowWho = needToKnowWho;
            this.needToKnowAbout = needToKnowAbout;
        }
    }
}