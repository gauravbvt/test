// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.vo.common.Knowable;
    import com.mindalliance.channels.vo.common.SourceOrSink;

    public class CreateNeedToKnowEvent extends CairngormEvent
    {
        public static const CreateNeedToKnow_Event:String = "<CreateNeedToKnowEvent>";
        public var about : Knowable;
        public var who : SourceOrSink;
        public function CreateNeedToKnowEvent(who : SourceOrSink, about : Knowable) 
        {
            super( CreateNeedToKnow_Event );
            this.who = who;
            this.about = about;
        }
    }
}