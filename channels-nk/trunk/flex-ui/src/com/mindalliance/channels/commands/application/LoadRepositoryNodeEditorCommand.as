// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.application
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.commands.BaseCommand;
	import com.mindalliance.channels.events.application.LoadRepositoryNodeEditorEvent;
	import com.mindalliance.channels.events.people.GetOrganizationEvent;
	import com.mindalliance.channels.events.resources.GetRepositoryEvent;
	import com.mindalliance.channels.util.CairngormHelper;
	
	public class LoadRepositoryNodeEditorCommand extends BaseCommand
	{
		override public function execute(event:CairngormEvent):void
		{
			var evt:LoadRepositoryNodeEditorEvent = event as LoadRepositoryNodeEditorEvent;
            CairngormHelper.fireEvent(new GetRepositoryEvent(evt.repositoryId, channelsModel.propertyEditorModel.repositoryNodeEditorModel.repositoryModel));
        
            CairngormHelper.fireEvent(new GetOrganizationEvent(evt.organizationId, channelsModel.propertyEditorModel.repositoryNodeEditorModel.organizationModel));
        
		}
	}
}