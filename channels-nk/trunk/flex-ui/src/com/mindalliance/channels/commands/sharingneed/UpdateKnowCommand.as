// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.sharingneed
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.business.sharingneed.KnowDelegate;
    import com.mindalliance.channels.commands.BaseDelegateCommand;
    import com.mindalliance.channels.events.sharingneed.*;
    import com.mindalliance.channels.model.EditorModel;
    import com.mindalliance.channels.model.ElementModel;
    import com.mindalliance.channels.vo.KnowVO;
    
    public class UpdateKnowCommand extends BaseDelegateCommand
    {
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:UpdateKnowEvent = event as UpdateKnowEvent;
            var model : EditorModel = evt.model;
            var element :ElementModel = model.getElementModel(model.id);
            var data : KnowVO = (element.data as KnowVO);   
            if (model.isChanged) {
                log.debug("Updating Know model");
 
                data.who = evt.who;
                data.about = evt.about;
                data.what = evt.what;
                
                model.isChanged = false;
                element.dirty=true;
            }
            if (element.dirty == true) {
                log.debug("Updating Know");
                var delegate:KnowDelegate = new KnowDelegate( this );
                delegate.updateElement(data);
            }      
        }
        
        override public function result(data:Object):void
        {
            if (data["data"] == true) {
                log.debug("Know successfully updated");
                var id : String = (data["id"] as String);
                channelsModel.getElementModel(id).dirty = false;
            } else {
                log.error("Update of NeedToKnow " + result + " failed");
            }
        }
    }
}