// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.commands.BaseCommand;
	import com.mindalliance.channels.events.scenario.*;
	import com.mindalliance.channels.model.EditorModel;
	import com.mindalliance.channels.util.CairngormHelper;
	import com.mindalliance.channels.util.ElementHelper;
	import com.mindalliance.channels.vo.AcquirementVO;
	
	public class UpdateAcquirementTaskCommand extends BaseCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:UpdateAcquirementTaskEvent = event as UpdateAcquirementTaskEvent;
 			var el : AcquirementVO = channelsModel.getElementModel(evt.id).data as AcquirementVO;
			var model : EditorModel  =channelsModel.getEditorModel();
			model.id=evt.id;
			model.isChanged = true;
			
            CairngormHelper.fireEvent(new UpdateAcquirementEvent(model, el.name,el.description,el.categories,ElementHelper.findElementById(evt.taskId, channelsModel.getElementListModel('tasks').data)));
		}
		
	}
}