// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;

    public class CreateSharingNeedSequenceEvent extends CairngormEvent
    {
        public static const CreateSharingNeedSequence_Event:String = "<CreateSharingNeedSequenceEvent>";
        
        public var knowId : String;
        public var needToKnowId : String;
        
        public function CreateSharingNeedSequenceEvent(knowId : String, needToKnowId : String) 
        {
            super( CreateSharingNeedSequence_Event );
            this.knowId = knowId;
            this.needToKnowId = needToKnowId;
        }
    }
}