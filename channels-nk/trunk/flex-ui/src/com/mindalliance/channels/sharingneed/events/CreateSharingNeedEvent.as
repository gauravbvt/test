// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.sharingneed.events
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.vo.common.Knowable;
    import com.mindalliance.channels.vo.common.SourceOrSink;

    public class CreateSharingNeedEvent extends CairngormEvent
    {
        public static const CreateSharingNeed_Event:String = "<CreateSharingNeedEvent>";
        public var knowWho : SourceOrSink;
        public var knowAbout : Knowable;
        public var needToKnowWho : SourceOrSink;
        public var needToKnowAbout : Knowable;
        public function CreateSharingNeedEvent(knowWho : SourceOrSink,
                                                knowAbout : Knowable,
                                                needToKnowWho : SourceOrSink,
                                                needToKnowAbout : Knowable) 
        {
            super( CreateSharingNeed_Event );
            this.knowWho = knowWho;
            this.knowAbout  = knowAbout;
            this.needToKnowWho = needToKnowWho;
            this.needToKnowAbout = needToKnowAbout;
        }
    }
}