// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.scenario.TaskDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.scenario.*;
	import com.mindalliance.channels.vo.TaskVO;
	import com.mindalliance.channels.vo.common.ElementVO;
	
	public class CreateTaskCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:CreateTaskEvent = event as CreateTaskEvent;
			var delegate:TaskDelegate = new TaskDelegate( this );
			delegate.create(evt.name, evt.scenarioId);
		}
		
        override public function result(data:Object):void
        {
            var result:TaskVO = data["data"] as TaskVO;
            if (result!=null) {
                log.info("Task created");
                channelsModel.getElementListModel('tasks').data.addItem(new ElementVO(result.id, result.name));
                //CairngormEventDispatcher.getInstance().dispatchEvent( new GetOrganizationListEvent() );
            }
        }
	}
}