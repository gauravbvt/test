// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.business.sharingneed.NeedToKnowDelegate;
    import com.mindalliance.channels.commands.BaseDelegateCommand;
    import com.mindalliance.channels.events.sharingneed.*;
    import com.mindalliance.channels.util.CairngormHelper;
    import com.mindalliance.channels.vo.NeedToKnowVO;
    
    public class CreateNeedToKnowSequenceCommand extends BaseDelegateCommand
    {
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:CreateNeedToKnowSequenceEvent = event as CreateNeedToKnowSequenceEvent;
            var delegate:NeedToKnowDelegate = new NeedToKnowDelegate( this );
            var param : Array = new Array();
            param["knowId"] = evt.knowId;
            delegate.create(evt.who,evt.about,param);
        }
        
        override public function result(data:Object):void
        {
            var result : NeedToKnowVO = data["data"] as NeedToKnowVO;
            if (result != null) {
                channelsModel.getElementModel(result.id).data = result;
                CairngormHelper.fireEvent(new CreateSharingNeedSequenceEvent(
                                                data["knowId"],
                                                result.id));
                    
            }
        }
    }
}