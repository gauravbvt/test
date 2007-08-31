// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.commands.BaseCommand;
	import com.mindalliance.channels.events.application.QueueProjectUpdateEvent;
	
	public class QueueProjectUpdateCommand extends BaseCommand
	{
		override public function execute(event:CairngormEvent):void
		{
			var evt:QueueProjectUpdateEvent = event as QueueProjectUpdateEvent;
			channelsModel.projectScenarioBrowserModel.shouldUpdateProject = true;
		}
	}
}