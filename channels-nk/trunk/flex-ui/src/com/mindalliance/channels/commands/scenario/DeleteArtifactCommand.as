// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	import com.mindalliance.channels.business.scenario.ArtifactDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.scenario.*;
	
	public class DeleteArtifactCommand extends BaseDelegateCommand
	{
	
		override public function execute(event:CairngormEvent):void
		{
			var evt:DeleteArtifactEvent = event as DeleteArtifactEvent;
			var delegate:ArtifactDelegate = new ArtifactDelegate( this );
			delegate.deleteElement(evt.id);
		}
		
		override public function result(data:Object):void
		{
            var result:Boolean = data["data"] as Boolean;
            if (result == true) {
                CairngormEventDispatcher.getInstance().dispatchEvent( new GetArtifactListEvent(channelsModel.currentScenario.id) );
                log.info("Artifact successfully deleted");
            } else {
                log.warn("Artifact Deletion failed");   
            }       			
		}
	}
}