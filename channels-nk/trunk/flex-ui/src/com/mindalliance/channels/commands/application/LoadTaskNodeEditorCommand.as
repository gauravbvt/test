// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.commands.BaseCommand;
	import com.mindalliance.channels.events.application.LoadTaskNodeEditorEvent;
	import com.mindalliance.channels.events.people.GetRoleEvent;
	import com.mindalliance.channels.events.scenario.GetTaskEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	
	public class LoadTaskNodeEditorCommand extends BaseCommand
	{
		override public function execute(event:CairngormEvent):void
		{
			var evt:LoadTaskNodeEditorEvent = event as LoadTaskNodeEditorEvent;
            CairngormHelper.fireEvent(new GetTaskEvent(evt.taskId, channelsModel.propertyEditorModel.taskNodeEditorModel.taskModel));
            CairngormHelper.fireEvent(new GetRoleEvent(evt.roleId, channelsModel.propertyEditorModel.taskNodeEditorModel.roleModel));
        
		}
	}
}