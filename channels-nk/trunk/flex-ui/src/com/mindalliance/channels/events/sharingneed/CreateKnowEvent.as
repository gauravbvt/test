// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.vo.common.Knowable;
    import com.mindalliance.channels.vo.common.SourceOrSink;

    public class CreateKnowEvent extends CairngormEvent
    {
        public static const CreateKnow_Event:String = "<CreateKnowEvent>";
        public var about : Knowable;
        public var who : SourceOrSink;
        public function CreateKnowEvent(who : SourceOrSink, about : Knowable) 
        {
            super( CreateKnow_Event );
            this.who = who;
            this.about = about;
        }
    }
}