// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.model.EditorModel;
    import com.mindalliance.channels.vo.common.InformationVO;
    import com.mindalliance.channels.vo.common.Knowable;
    import com.mindalliance.channels.vo.common.SourceOrSink;

    public class UpdateKnowEvent extends CairngormEvent
    {
        public static const UpdateKnow_Event:String = "<UpdateKnowEvent>";
        public var model : EditorModel;
        public var who : SourceOrSink;
        public var about : Knowable;
        public var what : InformationVO;
        public function UpdateKnowEvent(model : EditorModel,
                                        who : SourceOrSink,
                                        about : Knowable,
                                        what : InformationVO) 
        {
            super( UpdateKnow_Event );
            this.model = model ;
            this.who = who;
            this.about = about;
            this.what = what;
            
        }
    }
}