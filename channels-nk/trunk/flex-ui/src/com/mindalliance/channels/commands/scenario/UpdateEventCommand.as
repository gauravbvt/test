// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
    import com.adobe.cairngorm.control.CairngormEvent;
    import com.mindalliance.channels.business.scenario.EventDelegate;
    import com.mindalliance.channels.commands.BaseDelegateCommand;
    import com.mindalliance.channels.events.people.*;
    import com.mindalliance.channels.events.scenario.UpdateEventEvent;
    import com.mindalliance.channels.model.*;
    import com.mindalliance.channels.util.ElementHelper;
    import com.mindalliance.channels.vo.EventVO;
    import com.mindalliance.channels.vo.common.ElementVO;
    
    public class UpdateEventCommand extends BaseDelegateCommand
    {
    
        override public function execute(event:CairngormEvent):void
        {
            var evt:UpdateEventEvent = event as UpdateEventEvent;
            
            var model : EditorModel = evt.model;
            var element :ElementModel = model.getElementModel(model.id);
            var data : EventVO = (element.data as EventVO);   
            if (model.isChanged) {
                log.debug("Updating Event model");

                data.name=evt.name;
                data.description = evt.description;
                data.categories = evt.categories; 
	            data.where = evt.where;
	            data.cause = evt.cause;
	            data.scenario = evt.scenario;
	            data.duration = evt.duration;
	            data.taskCompletions = evt.taskCompletions;
                
                model.isChanged = false;
                element.dirty=true;
            }
            if (element.dirty == true) {
                log.debug("Updating Event");
                var delegate:EventDelegate = new EventDelegate( this );
                delegate.updateElement(data);
            }      
            
        }
        
        override public function result(data:Object):void
        {
            if (data["data"] == true) {
                log.debug("Event successfully updated");
                var id : String = (data["id"] as String);
                channelsModel.getElementModel(id).dirty = false;
                // Update the element name in the event list
                var el : ElementVO = ElementHelper.findElementById(id, channelsModel.getElementListModel('events').data);
                el.name = channelsModel.getElementModel(id).data.name;
            } else {
                log.error("Update of Event " + result + " failed");
            }
        }
	}
}