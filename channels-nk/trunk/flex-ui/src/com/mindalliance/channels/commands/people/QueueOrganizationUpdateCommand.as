// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.people
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.commands.BaseCommand;
	import com.mindalliance.channels.events.people.QueueOrganizationUpdateEvent;
	
	public class QueueOrganizationUpdateCommand extends BaseCommand
	{
		override public function execute(event:CairngormEvent):void
		{
			var evt:QueueOrganizationUpdateEvent = event as QueueOrganizationUpdateEvent;
			model.propertyEditorModel.shouldUpdateOrganization = true;
		}
	}
}