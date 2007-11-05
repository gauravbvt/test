// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.sharingneed.events
{
    import com.mindalliance.channels.common.events.UpdateElementEvent;
    import com.mindalliance.channels.model.EditorModel;
    import com.mindalliance.channels.vo.common.InformationVO;
    import com.mindalliance.channels.vo.common.Knowable;
    import com.mindalliance.channels.vo.common.SourceOrSink;

    public class UpdateKnowEvent extends UpdateElementEvent
    {
        public function UpdateKnowEvent(model : EditorModel,
                                        who : SourceOrSink,
                                        about : Knowable,
                                        what : InformationVO) 
        {
            super( model, {
                "who" : who,
                "about" : about,
                "what" : what
            });
            
        }
    }
}