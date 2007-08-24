// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.events.application.QueueProjectUpdateEvent;
	import mx.logging.ILogger;
	import mx.logging.Log;
	
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.model.application.ProjectScenarioBrowserModel;
	public class QueueProjectUpdateCommand implements ICommand
	{
		private var log : ILogger = Log.getLogger("com.mindalliance.channels.commands.application.QueueProjectUpdateCommand");
		private var model : ProjectScenarioBrowserModel = ChannelsModelLocator.getInstance().projectScenarioBrowserModel;
		
		public function execute(event:CairngormEvent):void
		{
			var evt:QueueProjectUpdateEvent = event as QueueProjectUpdateEvent;
			model.shouldUpdateProject = true;
		}
	}
}