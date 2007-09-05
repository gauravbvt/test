// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.resources.TaskDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.scenario.*;
	
	import mx.collections.ArrayCollection;
	
	public class GetTaskListCommand extends BaseDelegateCommand
	{
	
        override public function execute(event:CairngormEvent):void
        {
            var evt:GetTaskListEvent = event as GetTaskListEvent;
            var delegate:TaskDelegate = new TaskDelegate( this );
            log.debug("Retrieving task list");
            delegate.getTaskList(evt.scenarioId);
        }
        
        override public function result(data:Object):void
        {
            channelsModel.getElementListModel("tasks").data = (data["data"] as ArrayCollection);
            log.debug("Successfully retrieved task list");
        }
        
        override public function fault(info:Object):void
        {
            channelsModel.getElementListModel("tasks").data  = null;
            super.fault(info);
        }
	}
}