// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.events.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.model.EditorModel;
    import com.mindalliance.channels.vo.common.CategorySetVO;
    import com.mindalliance.channels.vo.common.DurationVO;
    import com.mindalliance.channels.vo.common.InformationVO;
    import com.mindalliance.channels.vo.common.Knowable;
    import com.mindalliance.channels.vo.common.SourceOrSink;

    public class UpdateNeedToKnowEvent extends CairngormEvent
    {
        public static const UpdateNeedToKnow_Event:String = "<UpdateNeedToKnowEvent>";
        public var model : EditorModel;
        public var who : SourceOrSink;
        public var about : Knowable;
        public var what : InformationVO;
        public var criticality : String;
        public var urgency : DurationVO;
        public var deliveryMode : String;
        public var updateOnChange : Boolean;
        public var updateEvery : DurationVO;
        public var format : CategorySetVO;
        public function UpdateNeedToKnowEvent(model : EditorModel,
                                        who : SourceOrSink,
                                        about : Knowable,
                                        what : InformationVO,
                                        criticality : String,
                                        urgency : DurationVO,
                                        deliveryMode : String,
                                        updateOnChange : Boolean,
                                        updateEvery : DurationVO,
                                        format : CategorySetVO) 
        {
            super( UpdateNeedToKnow_Event );
            this.model = model ;
            this.who = who;
            this.about = about;
            this.what = what;
            this.criticality = criticality;
            this.urgency = urgency;
            this.deliveryMode = deliveryMode;
            this.updateOnChange = updateOnChange;
            this.updateEvery = updateEvery;
            this.format = format;

        }
    }
}