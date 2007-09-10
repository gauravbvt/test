// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.scenario.ArtifactDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.scenario.*;
	
	import mx.collections.ArrayCollection;
	
	public class GetArtifactListCommand extends BaseDelegateCommand
	{
        override public function execute(event:CairngormEvent):void
        {
            var evt:GetArtifactListEvent = event as GetArtifactListEvent;
            var delegate:ArtifactDelegate = new ArtifactDelegate( this );
            log.debug("Retrieving artifact list");
            delegate.getArtifactList(evt.scenarioId);
        }
        
        override public function result(data:Object):void
        {
            channelsModel.getElementListModel("artifacts").data = (data["data"] as ArrayCollection);
            log.debug("Successfully retrieved artifact list");
        }
        
        override public function fault(info:Object):void
        {
            channelsModel.getElementListModel("artifacts").data  = null;
            super.fault(info);
        }
	}
}