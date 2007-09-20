// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.scenario.ArtifactDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.scenario.*;
	
	import mx.collections.ArrayCollection;
	
	public class GetArtifactListByTaskCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:GetArtifactListByTaskEvent = event as GetArtifactListByTaskEvent;
			if (evt.taskId != null) {
                var delegate:ArtifactDelegate = new ArtifactDelegate( this );
				delegate.getArtifactListByTask(evt.taskId);
			}
		}
		
		override public function result(data:Object):void
		{
			channelsModel.getElementListModel("artifacts" + data["taskId"]).data = (data["data"] as ArrayCollection);
            log.debug("Successfully retrieved task artifact list");
		}
	}
}