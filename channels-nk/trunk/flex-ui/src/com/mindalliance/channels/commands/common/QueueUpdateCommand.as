// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.common
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.commands.BaseCommand;
	import com.mindalliance.channels.events.common.QueueUpdateEvent;
	import com.mindalliance.channels.model.EditorModel;
	
	public class QueueUpdateCommand extends BaseCommand
	{
		override public function execute(event:CairngormEvent):void
		{
			var evt:QueueUpdateEvent = event as QueueUpdateEvent;
			var model : EditorModel = evt.model;
			model.isChanged = true;
		}
	}
}