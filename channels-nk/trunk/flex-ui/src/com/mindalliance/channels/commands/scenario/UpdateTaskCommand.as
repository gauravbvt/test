// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.scenario.TaskDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.scenario.*;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.model.ElementModel;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.vo.TaskVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class UpdateTaskCommand extends BaseDelegateCommand
	{
	
     override public function execute(task:CairngormEvent):void
        {
            var evt:UpdateTaskEvent = task as UpdateTaskEvent;
            
            var model : EditorModel = evt.model;
            var element :ElementModel = model.getElementModel(model.id);
            var data : TaskVO = (element.data as TaskVO);   
            if (model.isChanged) {
                log.debug("Updating Task model");

                data.name=evt.name;
                data.description = evt.description;
                data.categories = evt.categories; 
                data.where = evt.where;
                data.cause = evt.cause;
                data.scenario = evt.scenario;
                data.duration = evt.duration;
                
                model.isChanged = false;
                element.dirty=true;
            }
            if (element.dirty == true) {
                log.debug("Updating Task");
                var delegate:TaskDelegate = new TaskDelegate( this );
                delegate.updateElement(data);
            }      
            
        }
        
        override public function result(data:Object):void
        {
            if (data["data"] == true) {
                log.debug("Task successfully updated");
                var id : String = (data["id"] as String);
                channelsModel.getElementModel(id).dirty = false;
                // Update the element name in the task list
                var el : ElementVO = ElementHelper.findElementById(id, channelsModel.getElementListModel('tasks').data);
                el.name = channelsModel.getElementModel(id).data.name;
            } else {
                log.error("Update of Task " + result + " failed");
            }
        }
	}
}