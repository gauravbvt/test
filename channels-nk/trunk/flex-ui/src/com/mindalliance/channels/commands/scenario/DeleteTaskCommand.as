// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.scenario.TaskDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.scenario.*;
	import com.mindalliance.channels.util.ElementHelper;
	
	import mx.collections.ArrayCollection;
	
	public class DeleteTaskCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:DeleteTaskEvent = event as DeleteTaskEvent;
			var delegate:TaskDelegate = new TaskDelegate( this );
			delegate.deleteElement(evt.id);
		}
		
		override public function result(data:Object):void
		{
            var result:Boolean = data["data"] as Boolean;
            if (result == true) {
            	var col : ArrayCollection = channelsModel.getElementListModel("tasks").data;
            	channelsModel.deleteElementModel(data["id"]);
            	if (col != null) {
            		var inx: int = ElementHelper.findElementIndexById(data["id"], col);
            		if (inx > 0) col.removeItemAt(inx);
            	}
                //CairngormEventDispatcher.getInstance().dispatchEvent( new GetTaskListEvent(channelsModel.currentScenario.id) );
                log.info("Task successfully deleted");
            } else {
                log.warn("Task Deletion failed");   
            }       			
		}
	}
}