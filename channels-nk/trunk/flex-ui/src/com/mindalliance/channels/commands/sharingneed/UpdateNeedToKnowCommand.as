// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.business.sharingneed.NeedToKnowDelegate;
    import com.mindalliance.channels.commands.BaseDelegateCommand;
    import com.mindalliance.channels.events.scenario.*;
    import com.mindalliance.channels.events.sharingneed.UpdateNeedToKnowEvent;
    import com.mindalliance.channels.model.EditorModel;
    import com.mindalliance.channels.model.ElementModel;
    import com.mindalliance.channels.vo.NeedToKnowVO;
    
    public class UpdateNeedToKnowCommand extends BaseDelegateCommand
    {
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:UpdateNeedToKnowEvent = event as UpdateNeedToKnowEvent; 
            var model : EditorModel = evt.model;
            var element :ElementModel = model.getElementModel(model.id);
            var data : NeedToKnowVO = (element.data as NeedToKnowVO);   
            if (model.isChanged) {
                log.debug("Updating NeedToKnow model");
 
                data.who = evt.who;
                data.about = evt.about;
                data.what = evt.what;
                data.criticality = evt.criticality;
                data.urgency = evt.urgency;
                data.deliveryMode = evt.deliveryMode;
                data.updateOnChange = evt.updateOnChange;
                data.updateEvery = evt.updateEvery;
                data.format = evt.format;
                model.isChanged = false;
                element.dirty=true;
            }
            if (element.dirty == true) {
                log.debug("Updating NeedToKnow");
                var delegate:NeedToKnowDelegate = new NeedToKnowDelegate( this );
                delegate.updateElement(data);
            }      
        }
        
        override public function result(data:Object):void
        {
            if (data["data"] == true) {
                log.debug("NeedToKnow successfully updated");
                var id : String = (data["id"] as String);
                channelsModel.getElementModel(id).dirty = false;
            } else {
                log.error("Update of NeedToKnow " + result + " failed");
            }
        }
    }
}