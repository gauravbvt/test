// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.events.application.QueueScenarioUpdateEvent;
	import mx.logging.ILogger;
	import mx.logging.Log;
	
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	public class QueueScenarioUpdateCommand implements ICommand
	{
		private var log : ILogger = Log.getLogger("com.mindalliance.channels.commands.application.QueueScenarioUpdateCommand");
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		
		
		public function execute(event:CairngormEvent):void
		{
			var evt:QueueScenarioUpdateEvent = event as QueueScenarioUpdateEvent;
			model.shouldUpdateScenario = true;
		}
	}
}