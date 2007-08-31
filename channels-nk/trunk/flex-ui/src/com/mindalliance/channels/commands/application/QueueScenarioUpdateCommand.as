// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.commands.BaseCommand;
	import com.mindalliance.channels.events.application.QueueScenarioUpdateEvent;
	import com.mindalliance.channels.model.EditorModel;
	
	public class QueueScenarioUpdateCommand extends BaseCommand
	{
		override public function execute(event:CairngormEvent):void
		{
			var evt:QueueScenarioUpdateEvent = event as QueueScenarioUpdateEvent;
			channelsModel.projectScenarioBrowserModel.shouldUpdateScenario = true;
		}
	}
}