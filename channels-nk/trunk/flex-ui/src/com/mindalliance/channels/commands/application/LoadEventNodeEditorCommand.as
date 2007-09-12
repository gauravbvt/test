// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.commands.BaseCommand;
	import com.mindalliance.channels.events.application.LoadEventNodeEditorEvent;
	import com.mindalliance.channels.events.scenario.GetEventEvent;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.util.CairngormHelper;
	
	public class LoadEventNodeEditorCommand extends BaseCommand
	{
		override public function execute(event:CairngormEvent):void
		{
			var evt:LoadEventNodeEditorEvent = event as LoadEventNodeEditorEvent;
			CairngormHelper.fireEvent(new GetEventEvent(evt.eventId, channelsModel.propertyEditorModel.eventNodeEditorModel.eventModel));
		}
	}
}