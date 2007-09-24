// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.business.sharingneed.KnowDelegate;
    import com.mindalliance.channels.commands.BaseDelegateCommand;
    import com.mindalliance.channels.events.sharingneed.*;
    import com.mindalliance.channels.util.CairngormHelper;
    import com.mindalliance.channels.vo.KnowVO;
    
    public class CreateKnowSequenceCommand extends BaseDelegateCommand
    {
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:CreateKnowSequenceEvent = event as CreateKnowSequenceEvent;
            var delegate:KnowDelegate = new KnowDelegate( this );
            var param : Array = new Array();
            param["needToKnowWho"] = evt.needToKnowWho;
            param["needToKnowAbout"] = evt.needToKnowAbout;
            
            delegate.create(evt.knowWho, evt.knowAbout, param);
        }
        
        override public function result(data:Object):void
        {
            var result : KnowVO = data["data"] as KnowVO;
            if (result != null) {
                channelsModel.getElementModel(result.id).data = result;
                CairngormHelper.fireEvent(new CreateNeedToKnowSequenceEvent(
                                                data["needToKnowWho"], 
                                                data["needToKnowAbout"], 
                                                result.id));
                	
            }
        }
    }
}