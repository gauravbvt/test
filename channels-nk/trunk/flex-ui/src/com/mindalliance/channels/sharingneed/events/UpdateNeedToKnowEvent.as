// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.sharingneed.events
{
    import com.mindalliance.channels.common.events.UpdateElementEvent;
    import com.mindalliance.channels.model.EditorModel;
    import com.mindalliance.channels.vo.common.CategorySetVO;
    import com.mindalliance.channels.vo.common.DurationVO;
    import com.mindalliance.channels.vo.common.InformationVO;
    import com.mindalliance.channels.vo.common.Knowable;
    import com.mindalliance.channels.vo.common.SourceOrSink;

    public class UpdateNeedToKnowEvent extends UpdateElementEvent
    {

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
            super( model, {
                "who" : who,
                "about" : about,
                "what" : what,
                "criticality" : criticality,
                "urgency" : urgency,
                "deliveryMode" : deliveryMode,
                "updateOnChange" : updateOnChange,
                "updateEvery" : updateEvery,
                "format" : format
            });

        }
    }
}