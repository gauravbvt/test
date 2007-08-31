// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.commands.BaseCommand;
	import com.mindalliance.channels.events.people.QueueOrganizationUpdateEvent;
	import com.mindalliance.channels.model.EditorModel;
	
	public class QueueOrganizationUpdateCommand extends BaseCommand
	{
		override public function execute(event:CairngormEvent):void
		{
			var evt:QueueOrganizationUpdateEvent = event as QueueOrganizationUpdateEvent;
			var model : EditorModel = evt.model;
			model.isChanged = true;
		}
	}
}