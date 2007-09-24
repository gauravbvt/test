// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.vo.common.Knowable;
    import com.mindalliance.channels.vo.common.SourceOrSink;

    public class CreateNeedToKnowSequenceEvent extends CairngormEvent
    {
        public static const CreateNeedToKnowSequence_Event:String = "<CreateNeedToKnowSequenceEvent>";
        public var about : Knowable;
        public var who : SourceOrSink;
        public var knowId : String;
        public function CreateNeedToKnowSequenceEvent(who : SourceOrSink, about : Knowable, knowId : String) 
        {
            super( CreateNeedToKnowSequence_Event );
            this.about = about;
            this.who = who;
            this.knowId = knowId;
        }
    }
}