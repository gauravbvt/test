// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.commands.scenario
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.mindalliance.channels.business.scenario.EventDelegate;
	import com.mindalliance.channels.commands.BaseDelegateCommand;
	import com.mindalliance.channels.events.scenario.*;
	
	import mx.collections.ArrayCollection;
	
	public class GetEventListCommand extends BaseDelegateCommand
	{
	
        override public function execute(event:CairngormEvent):void
        {
            var evt:GetEventListEvent = event as GetEventListEvent;
            var delegate:EventDelegate = new EventDelegate( this );
            log.debug("Retrieving event list");
            delegate.getEventList(evt.scenarioId);
        }
        
        override public function result(data:Object):void
        {
            channelsModel.getElementListModel("events").data = (data["data"] as ArrayCollection);
            log.debug("Successfully retrieved event list");
        }
        
        override public function fault(info:Object):void
        {
            channelsModel.getElementListModel("events").data  = null;
            super.fault(info);
        }
	}
}